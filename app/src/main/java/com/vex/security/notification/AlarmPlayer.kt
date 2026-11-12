package com.vex.security.notification

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Vibrator
import com.vex.security.R

object AlarmPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun playAlarm(context: Context) {
        try {
            stopAlarm(context)
            mediaPlayer = MediaPlayer.create(context, R.raw.emergency_alarm)?.apply {
                isLooping = true
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                mediaPlayer = MediaPlayer.create(context, alertUri)?.apply {
                    isLooping = true
                    start()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun stopAlarm(context: Context? = null) {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (context != null) {
            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
