package com.vex.security.defense

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Base64
import android.util.Log
import androidx.core.app.NotificationCompat
import com.vex.security.network.ApiClient
import com.vex.security.utils.DeviceIdentity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class VexLiveCallInterceptionService : Service() {

    companion object {
        const val CHANNEL_ID = "VEX_LIVE_INTERCEPTION_CHANNEL"
        const val NOTIFICATION_ID = 8841
        private const val TAG = "VexLiveInterception"
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private val httpClient = OkHttpClient.Builder().build()
    private var lastAnalyzedTime = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "VEX Live Call Shield",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🛡️ VEX Live Deepfake Shield Active")
            .setContentText("Monitoring live video frames for mesoscopic face-swap artifacts...")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        val resultCode = intent?.getIntExtra("EXTRA_RESULT_CODE", Activity.RESULT_CANCELED) ?: Activity.RESULT_CANCELED
        val resultData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra("EXTRA_RESULT_DATA", Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra("EXTRA_RESULT_DATA")
        }

        if (resultCode == Activity.RESULT_OK && resultData != null) {
            val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = projectionManager.getMediaProjection(resultCode, resultData)
            startScreenCapture()
        }

        return START_STICKY
    }

    private fun startScreenCapture() {
        val metrics = resources.displayMetrics
        val width = 720
        val height = 1280
        val density = metrics.densityDpi

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "VexCallCapture",
            width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            val now = System.currentTimeMillis()
            // Sample 1 frame every 1.5 seconds to conserve battery and avoid network flood
            if (now - lastAnalyzedTime >= 1500) {
                lastAnalyzedTime = now
                val image = reader.acquireLatestImage()
                if (image != null) {
                    processFrame(image, width, height)
                    image.close()
                }
            } else {
                val img = reader.acquireLatestImage()
                img?.close()
            }
        }, null)
    }

    private fun processFrame(image: android.media.Image, width: Int, height: Int) {
        try {
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width

            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)

            // Scale to 256x256 for MesoNet consumption
            val scaled = Bitmap.createScaledBitmap(bitmap, 256, 256, true)
            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            val b64Frame = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

            dispatchFrameToMesoNet(b64Frame)
        } catch (e: Exception) {
            Log.e(TAG, "Error encoding frame: ${e.message}")
        }
    }

    private fun dispatchFrameToMesoNet(b64Frame: String) {
        val deviceId = DeviceIdentity.getDeviceId(this)
        val payload = JSONObject().apply {
            put("device_id", deviceId)
            put("frame_base64", b64Frame)
            put("caller_package", "com.whatsapp")
        }

        val requestBody = payload.toString().toRequestBody("application/json".toMediaType())
        val url = "https://rid-compressed-ministries-menu.trycloudflare.com/ai/mesonet/live-frame"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.w(TAG, "MesoNet live frame dispatch failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val body = it.body?.string() ?: return
                        val json = JSONObject(body)
                        val isDeepfake = json.optBoolean("is_deepfake", false)
                        val threatScore = json.optInt("threat_score", 0)
                        val pHash = json.optString("perceptual_hash", "")

                        // Pillar 4: Trigger SYSTEM_ALERT_WINDOW Overlay immediately on fake detection
                        if (isDeepfake) {
                            val overlayIntent = Intent(this@VexLiveCallInterceptionService, VexAlertOverlayService::class.java).apply {
                                putExtra("EXTRA_THREAT_SCORE", threatScore)
                                putExtra("EXTRA_CALLER_APP", "WhatsApp / Video Call")
                                putExtra("EXTRA_PHASH", pHash)
                            }
                            startService(overlayIntent)
                        }
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
    }
}
