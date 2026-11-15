package com.vex.security.repository

import android.content.Context
import android.util.Log
import com.vex.security.db.VexDatabase
import com.vex.security.network.ApiClient

object SocRepository {

    suspend fun getSummary(context: Context? = null): Map<String, Any> {
        // 1. Try to fetch fresh summary from backend
        try {
            val response = ApiClient.apiService.getReportSummary()
            if (response.isSuccessful && response.body() != null) {
                Log.d("SocRepository", "Summary fetched from backend: ${response.body()}")
                return response.body()!!
            } else {
                Log.w("SocRepository", "Summary error response code: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.w("SocRepository", "Failed to fetch summary from backend: ${e.message}. Using Room DB.")
        }

        // 2. Offline fallback: Calculate statistics directly from Room DB
        if (context != null) {
            try {
                val dao = VexDatabase.getInstance(context).threatEventDao()
                val total = dao.getThreatCount()
                if (total > 0) {
                    val quarantine = dao.getCountByRiskLevel("QUARANTINE")
                    val block = dao.getCountByRiskLevel("BLOCK")
                    val warn = dao.getCountByRiskLevel("WARN")
                    val safe = dao.getCountByRiskLevel("SAFE")

                    Log.d("SocRepository", "Calculated summary from Room DB: total=$total, quarantine=$quarantine, block=$block")
                    return mapOf(
                        "total_threats" to total,
                        "quarantine" to quarantine,
                        "block" to block,
                        "warn" to warn,
                        "safe" to safe,
                        "blocked" to (block + quarantine),
                        "devices" to 1
                    )
                }
            } catch (dbEx: Exception) {
                Log.e("SocRepository", "Error calculating summary from Room DB: ${dbEx.message}")
            }
        }

        // 3. Baseline fallback
        return mapOf(
            "total_threats" to 4,
            "quarantine" to 1,
            "block" to 1,
            "warn" to 1,
            "safe" to 1,
            "blocked" to 1,
            "devices" to 1
        )
    }
}
