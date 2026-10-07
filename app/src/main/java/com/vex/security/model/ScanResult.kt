package com.vex.security.model

data class ScanResult(
    val scanId: String,
    val deviceId: String,
    val targetType: String,
    val targetName: String,
    val riskScore: Int,
    val riskLevel: String,
    val action: String,
    val detected: Boolean,
    val indicators: List<String>,
    val timestamp: Long
)
