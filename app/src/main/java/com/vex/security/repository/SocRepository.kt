package com.vex.security.repository

import com.vex.security.network.ApiClient

object SocRepository {

    suspend fun getSummary(): Map<String, Any> {
        val response = ApiClient.apiService.getReportSummary()

        if (response.isSuccessful) {
            return response.body() ?: emptyMap()
        }

        return emptyMap()
    }
}
