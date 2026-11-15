package com.vex.security.repository

import android.content.Context
import android.util.Log
import com.vex.security.db.ThreatEventEntity
import com.vex.security.db.VexDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ThreatRepository {

    suspend fun sendThreatEvent(
        context: Context? = null,
        deviceId: String,
        threatType: String,
        riskScore: Int,
        riskLevel: String,
        action: String,
        status: String,
        indicators: List<String>,
        source: String,
        explanation: String = "",
        recommendation: String = ""
    ) = withContext(Dispatchers.IO) {
        val eventId = "EVT-${System.currentTimeMillis()}-${(100..999).random()}"

        // 100% On-Device Standalone Storage in local Room DB
        if (context != null) {
            try {
                val dao = VexDatabase.getInstance(context).threatEventDao()
                val entity = ThreatEventEntity(
                    eventId = eventId,
                    deviceId = deviceId,
                    threatType = threatType,
                    riskScore = riskScore,
                    riskLevel = riskLevel,
                    action = action,
                    status = status,
                    indicatorsCsv = indicators.joinToString(","),
                    explanation = explanation,
                    recommendation = recommendation,
                    source = source,
                    timestamp = System.currentTimeMillis(),
                    isSynced = false
                )
                dao.insertEvent(entity)
                Log.d("VEX_REPO", "Threat event saved locally in Room DB: $eventId ($threatType)")
            } catch (dbEx: Exception) {
                Log.e("VEX_REPO", "Failed to persist threat event in Room DB: ${dbEx.message}")
            }
        }
    }
}
