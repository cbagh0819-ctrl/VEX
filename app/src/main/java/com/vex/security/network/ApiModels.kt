package com.vex.security.network

data class HealthResponse(
    val status: String
)

data class DeviceRequest(
    val device_id: String,
    val device_name: String,
    val android_version: String,
    val model: String,
    val registered_at: Long
)

data class ThreatEventRequest(
    val event_id: String,
    val device_id: String,
    val threat_type: String,
    val risk_score: Int,
    val risk_level: String,
    val action: String,
    val status: String,
    val indicators: List<String>,
    val source: String,
    val timestamp: Long
)
