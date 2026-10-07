package com.vex.security.model

data class Incident(
    val incidentId: String,
    val deviceId: String,
    val title: String,
    val description: String,
    val severity: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
)
