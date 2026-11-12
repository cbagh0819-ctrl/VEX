package com.vex.security.interceptor

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.vex.security.network.ApiClient
import com.vex.security.network.VexBlastRadiusClient
import com.vex.security.notification.AlarmPlayer
import com.vex.security.notification.SecurityNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.regex.Pattern

/**
 * VEX Deep-Defense Notification & Message Interceptor
 * Solves Android 4.4 - 14 SMS Interception Limitation:
 * Since non-default SMS apps cannot abort SMS_RECEIVED broadcasts,
 * this Service intercepts incoming notifications from SMS, WhatsApp, Telegram,
 * extracts embedded links/impersonation markers, checks the Blast Radius Blocklist,
 * and cancels malicious notifications before the victim can click them.
 */
class VexNotificationInterceptorService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val httpClient = OkHttpClient()

    private val monitoredPackages = setOf(
        "com.google.android.apps.messaging",
        "com.samsung.android.messaging",
        "com.android.mms",
        "com.whatsapp",
        "org.telegram.messenger",
        "com.facebook.orca"
    )

    private val urlPattern = Pattern.compile(
        "(https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}[^\\s]*)",
        Pattern.CASE_INSENSITIVE
    )

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val packageName = sbn.packageName
        if (!monitoredPackages.contains(packageName)) return

        val extras: Bundle? = sbn.notification?.extras
        val title = extras?.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras?.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        val fullContent = "$text $bigText".trim()

        if (fullContent.isEmpty()) return

        Log.i("VexInterceptor", "Intercepted message from $packageName | Sender: $title")

        // Extract URLs
        val matcher = urlPattern.matcher(fullContent)
        val extractedUrls = mutableListOf<String>()
        while (matcher.find()) {
            val url = matcher.group()
            extractedUrls.add(url)
        }

        serviceScope.launch {
            for (url in extractedUrls) {
                inspectAndContainUrl(url, title, packageName, sbn.key)
            }
        }
    }

    private fun inspectAndContainUrl(
        url: String,
        sender: String,
        pkg: String,
        notificationKey: String
    ) {
        try {
            // 1. Check local fast-path in-memory blocklist first (< 0.1ms)
            if (VexBlastRadiusClient.isTargetBlocked(url)) {
                containThreat(url, sender, notificationKey, VexBlastRadiusClient.getBlockReason(url))
                return
            }

            // 2. Query SOC Blast Radius Mesh
            val baseUrl = "http://${ApiClient.activeHost}:8000"
            val checkUrl = "$baseUrl/api/blocklist/check?target=${java.net.URLEncoder.encode(url, "UTF-8")}"
            val checkReq = Request.Builder().url(checkUrl).get().build()
            val response = httpClient.newCall(checkReq).execute()

            var isMalicious = false
            var reason = "Phishing Link Detected"

            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                if (json.optBoolean("blocked", false)) {
                    isMalicious = true
                    reason = json.optString("reason", "Contained in Global Blast Radius")
                }
            }

            // Heuristic fallback for obvious phishing lures
            if (!isMalicious) {
                val lower = url.lowercase(java.util.Locale.ROOT)
                if (lower.contains("iphone") || lower.contains("claim") || lower.contains("verify-bank") || lower.contains("bit.ly")) {
                    isMalicious = true
                    reason = "High-Confidence Social Engineering Lure"
                }
            }

            if (isMalicious) {
                containThreat(url, sender, notificationKey, reason)
            }
        } catch (e: Exception) {
            Log.e("VexInterceptor", "Error inspecting message URL: ${e.message}")
        }
    }

    private fun containThreat(url: String, sender: String, notificationKey: String, reason: String) {
        Log.w("VexInterceptor", "CRITICAL THREAT IN MESSAGE! Dismissing notification: $url ($reason)")

        // CRITICAL DEFENSE: Dismiss notification immediately to shield victim
        cancelNotification(notificationKey)

        // Sound alarm siren
        AlarmPlayer.playAlarm(applicationContext)

        // Trigger alert banner
        SecurityNotification.show(
            applicationContext,
            "VEX BLAST RADIUS: PHISHING DISMISSED",
            "Blocked link from $sender: $url ($reason)"
        )

        // Report to Global Threat Mesh
        try {
            val reportUrl = "http://${ApiClient.activeHost}:8000/api/blocklist/report"
            val payload = JSONObject().apply {
                put("target", url)
                put("target_type", "URL")
                put("threat_type", "SMISHING_PHISHING")
                put("risk_level", "CRITICAL")
                put("source_device_id", "ANDROID_ENDPOINT")
                put("source_platform", "ANDROID")
                put("reason", "Intercepted in SMS/chat from $sender: $reason")
            }
            val jsonMedia = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(jsonMedia, payload.toString())
            val postReq = Request.Builder().url(reportUrl).post(body).build()
            httpClient.newCall(postReq).execute()
        } catch (_: Exception) {}
    }
}
