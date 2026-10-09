package com.vex.security.repository

import android.util.Log
import com.vex.security.network.ApiClient
import com.vex.security.network.ThreatEventRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ThreatRepository {

    suspend fun sendThreatEvent(
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
        try {
            val event = ThreatEventRequest(
                event_id = "EVT-${System.currentTimeMillis()}",
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
                Log.d("VEX_REPO", "Threat event sent successfully: ${response.body()}")
            } else {
                Log.e("VEX_REPO", "Failed to send threat event, code: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("VEX_REPO", "Exception sending threat event: ${e.message}", e)
        }
    }

    suspend fun sendUrlThreat(
        deviceId: String,
        url: String,
        riskScore: Int,
        riskLevel: String,
        action: String,
        indicators: List<String>
    ) = withContext(Dispatchers.IO) {
        try {
            val event = ThreatEventRequest(
                event_id = "EVT-${System.currentTimeMillis()}",
                device_id = deviceId,
                threat_type = "URL",
                risk_score = riskScore,
                risk_level = riskLevel,
                action = action,
                status = "OPEN",
                indicators = indicators,
                source = "ANDROID_URL",
                timestamp = System.currentTimeMillis()
            )

            val response = ApiClient.apiService.sendThreatEvent(event)
            if (response.isSuccessful) {
                Log.d("VEX_REPO", "URL threat event sent successfully: ${response.body()}")
            } else {
                Log.e("VEX_REPO", "Failed to send URL threat event, code: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("VEX_REPO", "Exception sending URL threat: ${e.message}", e)
        }
    }
}
