package com.vex.security.repository

import com.vex.security.network.ApiClient

object SocRepository {

    suspend fun getSummary(): Map<String, Any> {
        try {
            val response = ApiClient.apiService.getReportSummary()
            if (response.isSuccessful && response.body() != null) {
                return response.body()!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback telemetry when offline or network unreachable
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
