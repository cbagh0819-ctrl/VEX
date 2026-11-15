package com.vex.security.notification

import android.content.Context
import android.util.Log

object SecurityNotificationManager {

    private const val TAG = "VEX_NOTIFICATION_MGR"

    fun init(context: Context) {
        try {
            SecurityNotification.show(context, "VEX Active Guard", "Endpoint real-time security protection active.")
            Log.d(TAG, "Security notification channels initialized.")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing notification channels: ${e.message}")
        }
    }

    fun postAlert(context: Context, title: String, message: String) {
        SecurityNotification.show(context, title, message)
    }

    fun playSiren(context: Context) {
        AlarmPlayer.playAlarm(context)
    }

    fun stopSiren(context: Context? = null) {
        // Safe no-op or pass context if available
    }
}
