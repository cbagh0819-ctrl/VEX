package com.vex.security.model

data class Alert(
    val alertId: String,
    val deviceId: String,
    val title: String,
    val message: String,
    val severity: String,
    val threatType: String,
    val timestamp: Long,
    val acknowledged: Boolean
)
