package com.vex.security.telecom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SmsBombReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            try {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                for (sms in messages) {
                    val sender = sms.displayOriginatingAddress ?: sms.originatingAddress ?: "UNKNOWN"
                    val body = sms.displayMessageBody ?: sms.messageBody ?: ""

                    val isAttack = BombShieldManager.processIncomingSms(context, sender, body)
                    if (isAttack) {
                        try {
                            if (isOrderedBroadcast) {
                                abortBroadcast()
                            }
                        } catch (e: Exception) {
                            Log.w("SmsBombReceiver", "Could not abort SMS broadcast: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SmsBombReceiver", "Error inspecting incoming SMS: ${e.message}", e)
            }
        }
    }
}
