package com.vex.security.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.vex.security.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object DeviceIdentity {

    private const val TAG = "VEX_IDENTITY"
    private const val PREFS_NAME = "vex_identity"
    private const val DEVICE_ID_KEY = "device_id"

    /**
     * Retrieves or generates a human-readable, high-entropy unique device ID.
     * Format: VEX-XXXX-XXXX (e.g. VEX-8K92-4F2A)
     */
    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var deviceId = prefs.getString(DEVICE_ID_KEY, null)

        // Generate clean VEX-XXXX-XXXX if not present or if using legacy long raw UUID format
        if (deviceId.isNullOrEmpty() || (deviceId.length > 15 && deviceId.contains("-"))) {
            deviceId = generateHumanUniqueId()
            prefs.edit().putString(DEVICE_ID_KEY, deviceId).apply()
            Log.i(TAG, "Assigned new Unique Device ID: $deviceId")
        }

        return deviceId
    }

    /**
     * Generates a sleek, high-entropy, unambiguous Crockford-style ID: VEX-XXXX-XXXX
     */
    private fun generateHumanUniqueId(): String {
        val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        val part1 = (1..4).map { chars.random() }.joinToString("")
        val part2 = (1..4).map { chars.random() }.joinToString("")
        return "VEX-$part1-$part2"
    }

    /**
     * Copies the unique device ID to clipboard and shows an executive confirmation toast.
     */
    fun copyDeviceId(context: Context) {
        val id = getDeviceId(context)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("VEX Device ID", id)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied VEX Device ID: $id", Toast.LENGTH_SHORT).show()
    }

    /**
     * Asynchronously registers this device on the central SOC backend.
     */
    fun registerDeviceAsync(context: Context) {
        val deviceId = getDeviceId(context)
        val model = "${Build.MANUFACTURER} ${Build.MODEL}".trim()
        val osVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        val clientVersion = "2.4.0"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val json = """
                    {
                        "device_id": "$deviceId",
                        "device_name": "$model",
                        "platform": "ANDROID",
                        "os_version": "$osVersion",
                        "android_version": "$osVersion",
                        "model": "$model",
                        "client_version": "$clientVersion",
                        "registered_at": ${System.currentTimeMillis()}
                    }
                """.trimIndent()

                val candidateUrls = listOf(
                    "https://rid-compressed-ministries-menu.trycloudflare.com/devices/",
                    "https://networking-tender-infrared-newly.trycloudflare.com/devices/",
                    "https://questionnaire-solution-tri-move.trycloudflare.com/devices/",
                    "http://127.0.0.1:8000/devices/",
                    "http://${ApiClient.activeHost}:8000/devices/",
                    "http://192.168.64.235:8000/devices/",
                    "http://10.0.2.2:8000/devices/"
                ).distinct()

                for (url in candidateUrls) {
                    try {
                        val request = Request.Builder()
                            .url(url)
                            .post(json.toRequestBody("application/json".toMediaType()))
                            .build()
                        val response = client.newCall(request).execute()
                        if (response.isSuccessful || response.code == 200) {
                            Log.i(TAG, "Device $deviceId registered successfully at $url")
                            break
                        }
                    } catch (e: IOException) {
                        // try next candidate
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not register device automatically: ${e.message}")
            }
        }
    }
}
