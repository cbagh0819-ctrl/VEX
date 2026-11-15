package com.vex.security.telecom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log

class CallBombReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            Log.d("VEX_CALL_SHIELD", "Phone State: $state, Incoming: $incomingNumber")

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val isBlocked = BombShieldManager.evaluateCallEvent(context, incomingNumber)
                if (isBlocked) {
                    safeSilenceOrRejectCall(context)
                }
            }
        }
    }

    private fun safeSilenceOrRejectCall(context: Context) {
        // Safe Mitigation 1: Silence ringer immediately (Always permitted with MODIFY_AUDIO_SETTINGS)
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.let {
                it.ringerMode = AudioManager.RINGER_MODE_SILENT
                Log.i("VEX_CALL_SHIELD", "Ringer silenced to protect against telecom DOS blast.")
            }
        } catch (e: Exception) {
            Log.w("VEX_CALL_SHIELD", "Failed to silence audio: ${e.message}")
        }

        // Safe Mitigation 2: TelecomManager.endCall() on Android 9+ (Requires ANSWER_PHONE_CALLS permission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
                if (context.checkSelfPermission(android.Manifest.permission.ANSWER_PHONE_CALLS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    val ended = telecomManager?.endCall() ?: false
                    Log.i("VEX_CALL_SHIELD", "TelecomManager endCall result: $ended")
                } else {
                    Log.w("VEX_CALL_SHIELD", "ANSWER_PHONE_CALLS not granted. Falling back to ringer suppression.")
                }
            } catch (e: SecurityException) {
                Log.w("VEX_CALL_SHIELD", "SecurityException on endCall (safely caught): ${e.message}")
            } catch (e: Exception) {
                Log.w("VEX_CALL_SHIELD", "endCall error: ${e.message}")
            }
        }
    }
}
