package com.vex.security.telecom

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.vex.security.notification.SecurityNotification
import com.vex.security.repository.ThreatRepository
import com.vex.security.security.ThreatIndicators
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

enum class SensitivityMode(
    val title: String,
    val smsThreshold: Int,
    val callThreshold: Int,
    val description: String
) {
    AGGRESSIVE("AGGRESSIVE", 2, 2, "Blocks > 2 unknown SMS/calls in 60s (Maximum Shield)"),
    BALANCED("BALANCED", 4, 3, "Blocks > 4 unknown SMS or 3 calls in 60s (Recommended)"),
    RELAXED("RELAXED", 6, 4, "Blocks > 6 unknown SMS or 4 calls in 60s (Standard)")
}

data class BombIncident(
    val id: String = "INC-${System.currentTimeMillis()}-${(100..999).random()}",
    val type: String, // "SMS_BOMB" or "CALL_FLOOD"
    val sender: String,
    val burstCount: Int,
    val sampleContent: String,
    val actionTaken: String,
    val riskScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)

object BombShieldManager {

    private const val TAG = "BombShieldManager"
    private const val PREFS_NAME = "vex_bomb_shield_prefs"
    private const val KEY_ENABLED = "shield_enabled"
    private const val KEY_SENSITIVITY = "sensitivity_mode"
    private const val KEY_IGNORE_CONTACTS = "ignore_contacts"
    private const val KEY_DETECT_OTP = "detect_otp_floods"
    private const val KEY_AUTO_MUTE = "auto_mute_ringer"
    private const val KEY_AUTO_REJECT = "auto_reject_calls"

    private const val WINDOW_MS = 60_000L // 60 seconds burst detection window

    // In-memory sliding window timestamps
    private val smsTimestamps = mutableListOf<Long>()
    private val callTimestamps = mutableListOf<Long>()

    // Observable Compose state for reactive UI
    var isEnabledState = mutableStateOf(true)
    var sensitivityState = mutableStateOf(SensitivityMode.BALANCED)
    var ignoreContactsState = mutableStateOf(true)
    var detectOtpFloodsState = mutableStateOf(true)
    var autoMuteRingerState = mutableStateOf(true)
    var autoRejectCallsState = mutableStateOf(true)

    var blockedSmsCount = mutableIntStateOf(0)
    var blockedCallsCount = mutableIntStateOf(0)
    var attacksNeutralizedCount = mutableIntStateOf(0)

    val incidentLogs = mutableStateListOf<BombIncident>()

    private val otpKeywords = listOf(
        "otp", "verification code", "verify", "verification", "code is",
        "login code", "one-time", "secret code", "passcode", "auth code",
        "do not share", "security code", "password reset", "confirm your account"
    )

    private val scope = CoroutineScope(Dispatchers.IO)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isEnabledState.value = prefs.getBoolean(KEY_ENABLED, true)
        val sensName = prefs.getString(KEY_SENSITIVITY, SensitivityMode.BALANCED.name)
        sensitivityState.value = try {
            SensitivityMode.valueOf(sensName ?: SensitivityMode.BALANCED.name)
        } catch (_: Exception) {
            SensitivityMode.BALANCED
        }
        ignoreContactsState.value = prefs.getBoolean(KEY_IGNORE_CONTACTS, true)
        detectOtpFloodsState.value = prefs.getBoolean(KEY_DETECT_OTP, true)
        autoMuteRingerState.value = prefs.getBoolean(KEY_AUTO_MUTE, true)
        autoRejectCallsState.value = prefs.getBoolean(KEY_AUTO_REJECT, true)
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        isEnabledState.value = enabled
        savePref(context) { it.putBoolean(KEY_ENABLED, enabled) }
        Log.d(TAG, "Bomb Shield state set to: $enabled")
    }

    fun setSensitivity(context: Context, mode: SensitivityMode) {
        sensitivityState.value = mode
        savePref(context) { it.putString(KEY_SENSITIVITY, mode.name) }
    }

    fun setIgnoreContacts(context: Context, ignore: Boolean) {
        ignoreContactsState.value = ignore
        savePref(context) { it.putBoolean(KEY_IGNORE_CONTACTS, ignore) }
    }

    fun setDetectOtpFloods(context: Context, detect: Boolean) {
        detectOtpFloodsState.value = detect
        savePref(context) { it.putBoolean(KEY_DETECT_OTP, detect) }
    }

    fun setAutoMuteRinger(context: Context, mute: Boolean) {
        autoMuteRingerState.value = mute
        savePref(context) { it.putBoolean(KEY_AUTO_MUTE, mute) }
    }

    fun setAutoRejectCalls(context: Context, reject: Boolean) {
        autoRejectCallsState.value = reject
        savePref(context) { it.putBoolean(KEY_AUTO_REJECT, reject) }
    }

    private fun savePref(context: Context, block: (SharedPreferences.Editor) -> Unit) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        block(editor)
        editor.apply()
    }

    /**
     * Checks if a phone number or sender ID is in the device's address book.
     */
    fun isContact(context: Context, numberOrSender: String): Boolean {
        if (numberOrSender.isBlank()) return false
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(numberOrSender)
            )
            val projection = arrayOf(ContactsContract.PhoneLookup._ID)
            val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
            val found = cursor?.use { it.moveToFirst() } ?: false
            found
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Intercepts incoming SMS and triggers rate-limiting burst defense.
     * Returns true if the message was detected as part of an active bombing attack.
     */
    fun processIncomingSms(context: Context, sender: String, body: String): Boolean {
        if (!isEnabledState.value) return false
        if (ignoreContactsState.value && isContact(context, sender)) return false

        val now = System.currentTimeMillis()
        synchronized(smsTimestamps) {
            smsTimestamps.removeAll { now - it > WINDOW_MS }
            smsTimestamps.add(now)

            val lowerBody = body.lowercase(Locale.ROOT)
            val hasOtpPattern = otpKeywords.any { lowerBody.contains(it) }

            val threshold = sensitivityState.value.smsThreshold
            val effectiveThreshold = if (detectOtpFloodsState.value && hasOtpPattern) {
                maxOf(2, threshold - 1)
            } else {
                threshold
            }

            if (smsTimestamps.size >= effectiveThreshold) {
                val burstCount = smsTimestamps.size
                blockedSmsCount.intValue++
                attacksNeutralizedCount.intValue++

                val incident = BombIncident(
                    type = "SMS_BOMB",
                    sender = sender,
                    burstCount = burstCount,
                    sampleContent = if (body.length > 60) body.take(57) + "..." else body,
                    actionTaken = "BLOCKED & NOTIFIED",
                    riskScore = 85
                )
                incidentLogs.add(0, incident)

                // High priority heads-up alert
                SecurityNotification.showWarning(
                    context,
                    "🚨 SMS BOMB ATTACK BLOCKED! Intercepted burst of $burstCount flood messages from $sender."
                )

                // Send threat event to backend / SOC
                scope.launch {
                    val deviceId = DeviceIdentity.getDeviceId(context)
                    ThreatRepository.sendThreatEvent(
                        deviceId = deviceId,
                        threatType = "SMS_BOMB_ATTACK",
                        riskScore = 85,
                        riskLevel = "BLOCK",
                        action = "INTERCEPTED",
                        status = "OPEN",
                        indicators = listOf(
                            ThreatIndicators.SMS_BOMB_BURST,
                            ThreatIndicators.TELECOM_DOS_ATTACK,
                            if (hasOtpPattern) ThreatIndicators.OTP_FLOOD_PATTERN else ThreatIndicators.UNKNOWN_SENDER_FLOOD
                        ),
                        source = "BOMB_SHIELD",
                        explanation = "High-velocity SMS flooding burst detected ($burstCount messages in ${WINDOW_MS / 1000}s) from $sender.",
                        recommendation = "SMS flood blocked by VEX Bomb Shield. Senders throttled."
                    )
                }

                Log.w(TAG, "SMS Bomb Attack Intercepted: $burstCount messages from $sender")
                return true
            }
        }
        return false
    }

    /**
     * Intercepts incoming call and triggers rate-limiting burst defense.
     * Returns true if the call was detected as part of an active call bombing attack.
     */
    fun processIncomingCall(context: Context, incomingNumber: String?): Boolean {
        if (!isEnabledState.value) return false
        val number = incomingNumber ?: "UNKNOWN"
        if (ignoreContactsState.value && isContact(context, number)) return false

        val now = System.currentTimeMillis()
        synchronized(callTimestamps) {
            callTimestamps.removeAll { now - it > WINDOW_MS }
            callTimestamps.add(now)

            val threshold = sensitivityState.value.callThreshold

            if (callTimestamps.size >= threshold) {
                val burstCount = callTimestamps.size
                blockedCallsCount.intValue++
                attacksNeutralizedCount.intValue++

                var action = "RINGER MUTED"

                // 1. Auto-Mute Ringer to prevent device DoS disturbance
                if (autoMuteRingerState.value) {
                    try {
                        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                    } catch (e: Exception) {
                        Log.e(TAG, "Could not mute ringer: ${e.message}")
                    }
                }

                // 2. Auto-Reject / End Call if permission granted
                if (autoRejectCallsState.value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                            telecomManager.endCall()
                            action = "CALL TERMINATED"
                        } catch (e: Exception) {
                            Log.e(TAG, "Could not end call: ${e.message}")
                        }
                    }
                }

                val incident = BombIncident(
                    type = "CALL_FLOOD",
                    sender = number,
                    burstCount = burstCount,
                    sampleContent = "Incoming automated robocall bomb burst",
                    actionTaken = action,
                    riskScore = 85
                )
                incidentLogs.add(0, incident)

                SecurityNotification.showWarning(
                    context,
                    "🚨 CALL BOMB ATTACK INTERCEPTED! Blocked burst of $burstCount calls from $number ($action)."
                )

                scope.launch {
                    val deviceId = DeviceIdentity.getDeviceId(context)
                    ThreatRepository.sendThreatEvent(
                        deviceId = deviceId,
                        threatType = "CALL_BOMB_ATTACK",
                        riskScore = 85,
                        riskLevel = "BLOCK",
                        action = "TERMINATED",
                        status = "OPEN",
                        indicators = listOf(
                            ThreatIndicators.CALL_FLOOD_ATTACK,
                            ThreatIndicators.TELECOM_DOS_ATTACK,
                            ThreatIndicators.UNKNOWN_SENDER_FLOOD
                        ),
                        source = "BOMB_SHIELD",
                        explanation = "Rapid unknown robocall flood detected ($burstCount calls in ${WINDOW_MS / 1000}s) from $number.",
                        recommendation = "Call flood intercepted by VEX Bomb Shield. Audio muted & call terminated."
                    )
                }

                Log.w(TAG, "Call Bomb Attack Intercepted: $burstCount calls from $number")
                return true
            }
        }
        return false
    }

    /**
     * Real-time attack simulator for interactive user testing.
     * Simulates an authentic SMS bomber attack (10 OTPs from major services in 3 seconds).
     */
    suspend fun simulateSmsBombAttack(
        context: Context,
        onProgress: (Int, Int, String) -> Unit = { _, _, _ -> }
    ) {
        val simulatedSenders = listOf(
            Pair("VK-FLPKRT", "Your Flipkart verification OTP is 492019. Do not share this code."),
            Pair("AM-AMAZON", "409218 is your Amazon security OTP. Valid for 10 minutes."),
            Pair("VM-SWIGGY", "Your Swiggy login code is 881023. Happy ordering!"),
            Pair("BZ-ZOMATO", "719302 is your Zomato account confirmation code."),
            Pair("WA-WHATSAP", "WhatsApp code: 394-019. You can also tap on the link to verify."),
            Pair("AD-INSTAG", "Use 109284 to verify your Instagram account."),
            Pair("TD-UBERIN", "Your Uber verification PIN is 8412."),
            Pair("AX-AXISBK", "OTP 992014 for login to Axis Mobile. Valid for 5 mins."),
            Pair("JD-JIOMRT", "381029 is your JioMart order OTP. Do not disclose."),
            Pair("PAYTM-AUTH", "748192 is your Paytm login OTP. Never share your OTP.")
        )

        for (i in simulatedSenders.indices) {
            val (sender, body) = simulatedSenders[i]
            onProgress(i + 1, simulatedSenders.size, sender)
            processIncomingSms(context, sender, body)
            delay(300)
        }
    }

    /**
     * Real-time attack simulator for interactive user testing.
     * Simulates a rapid call bomb burst (5 rapid robocall rings from unknown spoofed numbers).
     */
    suspend fun simulateCallBombAttack(
        context: Context,
        onProgress: (Int, Int, String) -> Unit = { _, _, _ -> }
    ) {
        val simulatedNumbers = listOf(
            "+1-800-449-0112 (VoIP Robocall)",
            "+44-20-7946-0921 (Automated IVR)",
            "+91-140-998-1200 (Telemarketing Flood)",
            "+1-888-555-0199 (Spoofed PBX)",
            "+33-1-40-00-99-88 (Unknown International)"
        )

        for (i in simulatedNumbers.indices) {
            val num = simulatedNumbers[i]
            onProgress(i + 1, simulatedNumbers.size, num)
            processIncomingCall(context, num)
            delay(400)
        }
    }

    fun resetStats() {
        blockedSmsCount.intValue = 0
        blockedCallsCount.intValue = 0
        attacksNeutralizedCount.intValue = 0
        incidentLogs.clear()
        synchronized(smsTimestamps) { smsTimestamps.clear() }
        synchronized(callTimestamps) { callTimestamps.clear() }
    }
}
