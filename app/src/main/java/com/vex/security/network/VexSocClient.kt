package com.vex.security.network

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object VexSocClient {
    private const val TAG = "VEX_SOC_CLIENT"
    private val client = OkHttpClient.Builder()
        .connectTimeout(3, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    
    // Configurable endpoint (local IP, Cloudflare Tunnel, or Render cloud URL)
    var socBaseUrl: String = "https://questionnaire-solution-tri-move.trycloudflare.com"

    fun reportThreatToSoc(deviceId: String, decision: com.vex.security.ai.SecurityDecision, callback: ((Boolean) -> Unit)? = null) {
        val jsonPayload = """
            {
               "device_id": "$deviceId",
               "threat_score": ${decision.threatScore},
               "category": "${decision.attackCategory}",
               "mitre_id": "${decision.mitreId}",
               "deception_engaged": ${decision.engageDeception},
               "explanation": "${decision.reason}",
               "timestamp": ${System.currentTimeMillis()}
            }
        """.trimIndent()

        // Prioritize Cloudflare Tunnel, then USB reverse tunnel, then LAN
        val targetUrls = listOfNotNull(
            "$socBaseUrl/api/canary/track/$deviceId",
            "https://questionnaire-solution-tri-move.trycloudflare.com/api/canary/track/$deviceId",
            "http://127.0.0.1:8000/api/canary/track/$deviceId",
            "http://192.168.64.235:8000/api/canary/track/$deviceId",
            "http://10.0.2.2:8000/api/canary/track/$deviceId"
        ).distinct()

        dispatchPayloadWithFallback(targetUrls, 0, jsonPayload, callback)
    }

    private fun dispatchPayloadWithFallback(
        urls: List<String>,
        index: Int,
        jsonPayload: String,
        callback: ((Boolean) -> Unit)?
    ) {
        if (index >= urls.size) {
            Log.e(TAG, "All SOC telemetry endpoints failed.")
            callback?.invoke(false)
            return
        }

        val url = urls[index]
        val request = Request.Builder()
            .url(url)
            .post(jsonPayload.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.w(TAG, "Failed delivery to $url: ${e.message}. Trying next candidate...")
                dispatchPayloadWithFallback(urls, index + 1, jsonPayload, callback)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i(TAG, "SOC Telemetry updated successfully via $url. Code: ${response.code}")
                    callback?.invoke(true)
                } else {
                    Log.w(TAG, "SOC Telemetry returned HTTP ${response.code} from $url. Trying fallback...")
                    dispatchPayloadWithFallback(urls, index + 1, jsonPayload, callback)
                }
                response.close()
            }
        })
    }
}
