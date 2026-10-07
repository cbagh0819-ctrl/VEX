package com.vex.security.repository

import com.vex.security.network.ApiClient
import com.vex.security.network.ThreatEventRequest

object ThreatRepository {

    suspend fun sendThreatEvent(
        deviceId: String,
        threatType: String,
        riskScore: Int,
        riskLevel: String,
        action: String,
        status: String,
        indicators: List<String>,
        source: String
    ) {
        val event = ThreatEventRequest(
            event_id = "EVT-${System.currentTimeMillis()}",
            device_id = deviceId,
            threat_type = threatType,
            risk_score = riskScore,
            risk_level = riskLevel,
            action = action,
            status = status,
            indicators = indicators,
            source = source,
            timestamp = System.currentTimeMillis()
        )

        ApiClient.apiService.sendThreatEvent(event)
    }

    suspend fun sendUrlThreat(
        deviceId: String,
        url: String,
        riskScore: Int,
        riskLevel: String,
        action: String,
        indicators: List<String>
    ) {
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

        ApiClient.apiService.sendThreatEvent(event)
    }
}
