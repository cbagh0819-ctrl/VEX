package com.vex.security.telecom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.vex.security.notification.SecurityNotification
import com.vex.security.repository.ThreatRepository
import com.vex.security.security.ThreatIndicators
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsBombReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        try {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isNullOrEmpty()) return

            val firstMsg = messages[0]
            val sender = firstMsg.displayOriginatingAddress ?: "Unknown"
            val body = messages.joinToString("") { it.displayMessageBody ?: "" }

            Log.d("VEX_SMS_SHIELD", "SMS received from $sender (Length: ${body.length})")

            val isBlast = BombShieldManager.evaluateSmsEvent(context, sender, body)
            if (isBlast) {
                Log.w("VEX_SMS_SHIELD", "SMS Bombing attack intercepted from sender: $sender")
                SecurityNotification.showHarmfulAlert(
                    context,
                    "SMS OTP / Flood Attack Intercepted! Multiple burst messages from $sender."
                )

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        ThreatRepository.sendThreatEvent(
                            context = context,
                            deviceId = DeviceIdentity.getDeviceId(context),
                            threatType = "SMS_BOMB_BURST",
                            riskScore = 85,
                            riskLevel = "QUARANTINE",
                            action = "BLOCK",
                            status = "OPEN",
                            indicators = listOf(ThreatIndicators.TELECOM_DOS_ATTACK, ThreatIndicators.SMS_BOMB_BURST),
                            source = "SMS_SHIELD",
                            explanation = "Rapid burst frequency detected from $sender",
                            recommendation = "DoS / OTP flood attack mitigated. Silence unknown sender notifications."
                        )
                    } catch (_: Exception) {}
                }
            }
        } catch (e: Exception) {
            Log.e("VEX_SMS_SHIELD", "Error parsing SMS intent: ${e.message}")
        }
    }
}
