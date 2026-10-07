package com.vex.security.repository

import com.vex.security.network.ApiClient

object ThreatEventsRepository {

    suspend fun getEvents(): List<Map<String, Any>> {
        val response = ApiClient.apiService.getThreatEvents()

        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }
}
