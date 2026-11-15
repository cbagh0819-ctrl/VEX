package com.vex.security.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threat_events")
data class ThreatEventEntity(
    @PrimaryKey
    val eventId: String,
    val deviceId: String,
    val threatType: String,
    val riskScore: Int,
    val riskLevel: String,
    val action: String,
    val status: String,
    val indicatorsCsv: String,
    val explanation: String,
    val recommendation: String,
    val source: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)
