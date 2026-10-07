package com.vex.security.model

data class ThreatEvent(
    val eventId: String,
    val deviceId: String,
    val threatType: String,
    val riskScore: Int,
    val riskLevel: String,
    val action: String,
    val status: String,
    val indicators: List<String>,
    val source: String,
    val timestamp: Long
)
