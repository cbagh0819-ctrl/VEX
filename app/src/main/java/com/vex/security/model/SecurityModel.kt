package com.vex.security.model

data class SecurityModel(
    val deviceId: String,
    val securityLevel: String,
    val activeThreatCount: Int,
    val lastScanTimestamp: Long
)
