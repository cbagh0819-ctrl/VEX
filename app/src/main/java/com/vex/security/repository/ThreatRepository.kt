package com.vex.security.repository

import android.content.Context
import android.util.Log
import com.vex.security.db.ThreatEventEntity
import com.vex.security.db.VexDatabase
import com.vex.security.network.ApiClient
import com.vex.security.network.ThreatEventRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ThreatRepository {

    suspend fun sendThreatEvent(
        context: Context? = null,
        deviceId: String,
        threatType: String,
        riskScore: Int,
        riskLevel: String,
        action: String,
        status: String,
        indicators: List<String>,
        source: String,
        explanation: String = "",
        recommendation: String = ""
    ) = withContext(Dispatchers.IO) {
        val eventId = "EVT-${System.currentTimeMillis()}"
        var isSynced = false

        // 1. Attempt sending to remote backend
        try {
            val event = ThreatEventRequest(
                event_id = eventId,
                device_id = deviceId,
                threat_type = threatType,
                risk_score = riskScore,
                risk_level = riskLevel,
                action = action,
                status = status,
                indicators = indicators,
                explanation = explanation,
                recommendation = recommendation,
                source = source,
                timestamp = System.currentTimeMillis()
            )

            val response = ApiClient.apiService.sendThreatEvent(event)
            if (response.isSuccessful) {
                isSynced = true
                Log.d("VEX_REPO", "Threat event synced to backend: ${response.body()}")
            } else {
                Log.w("VEX_REPO", "Backend rejected threat event, code: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.w("VEX_REPO", "Failed to reach backend: ${e.message}. Queuing locally in Room.")
        }

        // 2. Persist locally in Room DB so data is never lost offline
        if (context != null) {
            try {
                val dao = VexDatabase.getInstance(context).threatEventDao()
                val entity = ThreatEventEntity(
                    eventId = eventId,
                    deviceId = deviceId,
                    threatType = threatType,
                    riskScore = riskScore,
                    riskLevel = riskLevel,
                    action = action,
                    status = status,
                    indicatorsCsv = indicators.joinToString(","),
                    explanation = explanation,
                    recommendation = recommendation,
                    source = source,
                    timestamp = System.currentTimeMillis(),
                    isSynced = isSynced
                )
                dao.insertEvent(entity)
                Log.d("VEX_REPO", "Saved threat event to local Room DB ($eventId, synced=$isSynced)")
            } catch (dbEx: Exception) {
                Log.e("VEX_REPO", "Room database insertion error: ${dbEx.message}")
            }
        }
    }

    suspend fun sendUrlThreat(
        context: Context? = null,
        deviceId: String,
        url: String,
        riskScore: Int,
        riskLevel: String,
        action: String,
        indicators: List<String>
    ) = withContext(Dispatchers.IO) {
        sendThreatEvent(
            context = context,
            deviceId = deviceId,
            threatType = "URL",
            riskScore = riskScore,
            riskLevel = riskLevel,
            action = action,
            status = "OPEN",
            indicators = indicators,
            source = "ANDROID_URL",
            explanation = "Suspicious URL detected: $url",
            recommendation = "Block navigation to avoid credential theft or malware download."
        )
    }
}
