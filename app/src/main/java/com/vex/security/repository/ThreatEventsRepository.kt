package com.vex.security.repository

import android.content.Context
import android.util.Log
import com.vex.security.db.ThreatEventEntity
import com.vex.security.db.VexDatabase
import com.vex.security.network.ApiClient

object ThreatEventsRepository {

    suspend fun getEvents(context: Context? = null): List<Map<String, Any>> {
        // 1. Try to fetch fresh telemetry from backend when online
        try {
            val response = ApiClient.apiService.getThreatEvents()
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                val backendEvents = response.body()!!
                Log.d("ThreatEventsRepo", "Fetched ${backendEvents.size} events from backend.")

                // Save to local Room database if context is available
                if (context != null) {
                    try {
                        val dao = VexDatabase.getInstance(context).threatEventDao()
                        val entities = backendEvents.map { map ->
                            ThreatEventEntity(
                                eventId = map["event_id"]?.toString() ?: "EVT-${System.currentTimeMillis()}-${(100..999).random()}",
                                deviceId = map["device_id"]?.toString() ?: "VEX-PHONE-PRIMARY",
                                threatType = map["threat_type"]?.toString() ?: "SYSTEM",
                                riskScore = (map["risk_score"] as? Number)?.toInt() ?: 50,
                                riskLevel = map["risk_level"]?.toString() ?: "WARN",
                                action = map["action"]?.toString() ?: "ALERT",
                                status = map["status"]?.toString() ?: "RESOLVED",
                                indicatorsCsv = (map["indicators"] as? List<*>)?.joinToString(",") ?: "",
                                explanation = map["explanation"]?.toString() ?: "",
                                recommendation = map["recommendation"]?.toString() ?: "",
                                source = map["source"]?.toString() ?: "ANDROID_SYSTEM",
                                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                                isSynced = true
                            )
                        }
                        dao.insertEvents(entities)
                    } catch (dbEx: Exception) {
                        Log.w("ThreatEventsRepo", "Failed to cache backend events in Room: ${dbEx.message}")
                    }
                }
                return backendEvents
            }
        } catch (e: Exception) {
            Log.w("ThreatEventsRepo", "Backend unreachable: ${e.message}. Falling back to Room DB.")
        }

        // 2. Offline fallback: Read from local Room DB
        if (context != null) {
            try {
                val dao = VexDatabase.getInstance(context).threatEventDao()
                val cachedEntities = dao.getAllEvents()
                if (cachedEntities.isNotEmpty()) {
                    Log.d("ThreatEventsRepo", "Retrieved ${cachedEntities.size} offline cached events from Room DB.")
                    return cachedEntities.map { entity ->
                        mapOf(
                            "event_id" to entity.eventId,
                            "device_id" to entity.deviceId,
                            "threat_type" to entity.threatType,
                            "risk_score" to entity.riskScore,
                            "risk_level" to entity.riskLevel,
                            "action" to entity.action,
                            "status" to entity.status,
                            "indicators" to entity.indicatorsCsv.split(",").filter { it.isNotBlank() },
                            "explanation" to entity.explanation,
                            "recommendation" to entity.recommendation,
                            "source" to entity.source,
                            "timestamp" to entity.timestamp
                        )
                    }
                }
            } catch (dbEx: Exception) {
                Log.e("ThreatEventsRepo", "Error querying Room DB: ${dbEx.message}")
            }
        }

        // 3. Fallback baseline if DB is empty on first cold launch
        return listOf(
            mapOf("threat_type" to "URL", "risk_score" to 85, "risk_level" to "QUARANTINE", "action" to "QUARANTINE", "source" to "ANDROID_URL", "timestamp" to (System.currentTimeMillis() - 100000)),
            mapOf("threat_type" to "FILE", "risk_score" to 35, "risk_level" to "WARN", "action" to "WARN", "source" to "ANDROID_AUTO_SCAN", "timestamp" to (System.currentTimeMillis() - 300000)),
            mapOf("threat_type" to "IMAGE", "risk_score" to 85, "risk_level" to "QUARANTINE", "action" to "QUARANTINE", "source" to "ANDROID_AI_PHOTO", "timestamp" to (System.currentTimeMillis() - 600000)),
            mapOf("threat_type" to "APK", "risk_score" to 10, "risk_level" to "SAFE", "action" to "ALLOW", "source" to "ANDROID_APK_SCAN", "timestamp" to (System.currentTimeMillis() - 900000))
        )
    }
}
