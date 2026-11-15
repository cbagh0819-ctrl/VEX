package com.vex.security.repository

import android.content.Context
import android.util.Log
import com.vex.security.db.VexDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SocRepository {

    suspend fun getSummary(context: Context? = null): Map<String, Any> = withContext(Dispatchers.IO) {
        if (context != null) {
            try {
                val dao = VexDatabase.getInstance(context).threatEventDao()
                val total = dao.getThreatCount()
                val quarantine = dao.getCountByRiskLevel("QUARANTINE")
                val block = dao.getCountByRiskLevel("BLOCK")
                val warn = dao.getCountByRiskLevel("WARN")
                val safe = dao.getCountByRiskLevel("SAFE")

                return@withContext mapOf(
                    "total_threats" to total,
                    "quarantine" to quarantine,
                    "block" to block,
                    "warn" to warn,
                    "safe" to safe,
                    "blocked" to (block + quarantine),
                    "devices" to 1
                )
            } catch (dbEx: Exception) {
                Log.e("SocRepository", "Error calculating summary from Room DB: ${dbEx.message}")
            }
        }

        return@withContext mapOf(
            "total_threats" to 0,
            "quarantine" to 0,
            "block" to 0,
            "warn" to 0,
            "safe" to 1,
            "blocked" to 0,
            "devices" to 1
        )
    }
}
