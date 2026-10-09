package com.vex.security.notification

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.os.Vibrator
import android.os.VibrationEffect

object SecurityNotification {

    private const val CHANNEL_ID = "VEX_SECURITY"
    private const val CHANNEL_NAME = "VEX Security Alerts"

    private const val ALARM_CHANNEL_ID = "VEX_ALARM"
    private const val ALARM_CHANNEL_NAME = "VEX Critical Alarms"

    fun show(
        context: Context,
        title: String,
        message: String
    ) {
        showWarning(context, message)
    }

    fun showWarning(context: Context, message: String) {
        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(channel)

        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_dialog_alert
                )
                .setContentTitle("VEX Warning Alert")
                .setContentText(message)
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        manager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }

    fun showHarmfulAlert(context: Context, fileName: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            ALARM_CHANNEL_ID,
            ALARM_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)

        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle("VEX WARNING!")
            .setContentText("Suspicious test file detected ($fileName). This file has been flagged as high risk. Do not open it.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("VEX WARNING! Suspicious test file detected ($fileName). This file has been flagged as high risk. Do not open it."))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()

        manager.notify(fileName.hashCode(), notification)

        // Play alarm sound
        AlarmPlayer.playAlarm(context)

        // Vibrate
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), 0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
