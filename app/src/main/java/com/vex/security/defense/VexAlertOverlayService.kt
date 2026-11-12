package com.vex.security.defense

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.vex.security.R

class VexAlertOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val threatScore = intent?.getIntExtra("EXTRA_THREAT_SCORE", 95) ?: 95
        val appName = intent?.getStringExtra("EXTRA_CALLER_APP") ?: "Live Video Call"
        val pHash = intent?.getStringExtra("EXTRA_PHASH") ?: "None"

        showSystemAlertOverlay(threatScore, appName, pHash)
        return START_NOT_STICKY
    }

    private fun showSystemAlertOverlay(threatScore: Int, appName: String, pHash: String) {
        if (overlayView != null) return // Already showing

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 100
        }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.parseColor("#E68B0000")) // Solid Dark Red Glass
            setPadding(40, 40, 40, 40)
        }

        val title = TextView(this).apply {
            text = "🚨 CRITICAL: DEEPFAKE FACE SWAP INTERCEPTED"
            textSize = 18f
            setTextColor(android.graphics.Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val body = TextView(this).apply {
            text = "Target: $appName\nMesoNet Mesoscopic Confidence: $threatScore%\nPerceptual Hash: ${pHash.take(16)}...\nRisk: Impersonation / Biometric Hijack"
            textSize = 14f
            setTextColor(android.graphics.Color.YELLOW)
            setPadding(0, 16, 0, 24)
        }

        val dismissBtn = Button(this).apply {
            text = "TERMINATE & QUARANTINE CALL"
            setBackgroundColor(android.graphics.Color.RED)
            setTextColor(android.graphics.Color.WHITE)
            setOnClickListener {
                stopSelf()
            }
        }

        container.addView(title)
        container.addView(body)
        container.addView(dismissBtn)

        overlayView = container
        try {
            windowManager?.addView(overlayView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
