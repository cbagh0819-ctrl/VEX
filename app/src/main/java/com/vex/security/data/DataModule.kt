package com.vex.security.data

import android.content.Context
import com.vex.security.network.ApiClient
import com.vex.security.network.ApiService

object DataModule {

    fun provideApiService(): ApiService {
        return ApiClient.apiService
    }

    fun isOnline(): Boolean {
        return ApiClient.activeHost.isNotBlank()
    }
}
