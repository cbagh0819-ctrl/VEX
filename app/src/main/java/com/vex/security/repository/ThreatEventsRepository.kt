package com.vex.security.repository

import android.content.Context
import android.util.Log
import com.vex.security.db.ThreatEventEntity
import com.vex.security.db.VexDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ThreatEventsRepository {

    suspend fun getEvents(context: Context? = null): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        if (context != null) {
            try {
                val dao = VexDatabase.getInstance(context).threatEventDao()
                val cachedEntities = dao.getAllEvents()
                if (cachedEntities.isNotEmpty()) {
                    Log.d("ThreatEventsRepo", "Retrieved ${cachedEntities.size} standalone local events from Room DB.")
                    return@withContext cachedEntities.map { entity ->
                        mapOf(
                            "event_id" to entity.eventId,
                            "device_id" to entity.deviceId,
                            "threat_type" to entity.threatType,
                            "risk_score" to entity.riskScore,
                            "risk_level" to entity.riskLevel,
                            "action" to entity.action,
                            "status" to entity.status,
                            "indicators" to if (entity.indicatorsCsv.isNotBlank()) entity.indicatorsCsv.split(",") else emptyList(),
                            "explanation" to entity.explanation,
                            "recommendation" to entity.recommendation,
                            "source" to entity.source,
                            "timestamp" to entity.timestamp
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ThreatEventsRepo", "Error reading Room DB: ${e.message}")
            }
        }

        // Default initial clean record if Room is fresh
        return@withContext listOf(
            mapOf(
                "event_id" to "EVT-SYSTEM-INIT",
                "device_id" to "VEX-STANDALONE",
                "threat_type" to "SYSTEM_INITIALIZE",
                "risk_score" to 0,
                "risk_level" to "SAFE",
                "action" to "ALLOW",
                "status" to "RESOLVED",
                "indicators" to listOf("CLEAN_DEVICE", "STANDALONE_SECURE"),
                "explanation" to "VEX Standalone Engine initialized. 100% On-device privacy active.",
                "recommendation" to "Run 'Check Your Phone' to audit installed apps, system integrity and network.",
                "source" to "STANDALONE_INIT",
                "timestamp" to System.currentTimeMillis()
            )
        )
    }
}
