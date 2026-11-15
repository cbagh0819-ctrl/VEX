package com.vex.security.repository

import android.content.Context
import com.vex.security.network.ApiClient

object SecurityRepository {

    suspend fun syncWithBackend(context: Context): Boolean {
        return try {
            val response = ApiClient.apiService.healthCheck()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
