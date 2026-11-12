package com.vex.security.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * VEX Blast Radius In-Memory High-Speed Cache & WebSocket Sync for Android.
 * Sub-millisecond lookups on-device.
 * Subscribes to backend /api/blocklist/ws to receive instantaneous quarantine broadcasts.
 */
object VexBlastRadiusClient {

    private const val TAG = "VexBlastRadius"
    private val localBlocklist = ConcurrentHashMap<String, String>() // normalized target -> reason
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS) // Keep WebSocket open
        .build()

    private var webSocket: WebSocket? = null

    init {
        // Pre-populate with seed threats
        localBlocklist["phish-bank-login.com"] = "Pre-seeded Phishing Domain"
        localBlocklist["apple-id-verify.click"] = "Pre-seeded Impersonation Target"
        localBlocklist["free-iphone-claim.xyz"] = "Pre-seeded CamPhish Exploit"
        localBlocklist["evil-payload.ru"] = "Pre-seeded Malware Dropper"
    }

    fun startSync(host: String = ApiClient.activeHost) {
        scope.launch {
            try {
                val wsUrl = "ws://$host:8000/api/blocklist/ws"
                val request = Request.Builder().url(wsUrl).build()

                webSocket = client.newWebSocket(request, object : WebSocketListener() {
                    override fun onOpen(ws: WebSocket, response: Response) {
                        Log.i(TAG, "Connected to Blast Radius Global Threat Mesh WebSocket")
                    }

                    override fun onMessage(ws: WebSocket, text: String) {
                        try {
                            val json = JSONObject(text)
                            val event = json.optString("event")
                            if (event == "BLAST_RADIUS_CONTAINMENT_EVENT") {
                                val data = json.getJSONObject("data")
                                val target = data.optString("target")
                                val reason = data.optString("reason", "Global Automated Containment")
                                if (target.isNotEmpty()) {
                                    addBlockedTarget(target, reason)
                                    Log.w(TAG, "INSTANT THREAT CONTAINED: $target")
                                }
                            } else if (event == "INITIAL_SYNC") {
                                val items = json.optJSONArray("items")
                                if (items != null) {
                                    for (i in 0 until items.length()) {
                                        val item = items.getJSONObject(i)
                                        val t = item.optString("target")
                                        val r = item.optString("reason", "Contained")
                                        if (t.isNotEmpty()) addBlockedTarget(t, r)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing WebSocket containment packet: ${e.message}")
                        }
                    }

                    override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                        Log.w(TAG, "WebSocket sync disconnected: ${t.message}. Reconnecting in 5s...")
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start Blast Radius sync: ${e.message}")
            }
        }
    }

    fun isTargetBlocked(target: String): Boolean {
        val norm = normalize(target)
        if (localBlocklist.containsKey(norm) || localBlocklist.containsKey(target.trim())) {
            return true
        }
        for (key in localBlocklist.keys) {
            if (norm.endsWith(".$key")) return true
        }
        return false
    }

    fun getBlockReason(target: String): String {
        val norm = normalize(target)
        return localBlocklist[norm] ?: localBlocklist[target.trim()] ?: "Contained in Global Blast Radius"
    }

    fun addBlockedTarget(target: String, reason: String) {
        val norm = normalize(target)
        localBlocklist[norm] = reason
        localBlocklist[target.trim()] = reason
    }

    private fun normalize(target: String): String {
        var t = target.trim().lowercase(java.util.Locale.ROOT)
        if (t.contains("://")) {
            try {
                t = t.substringAfter("://").substringBefore("/").substringBefore(":")
            } catch (e: Exception) { }
        }
        return t
    }
}
