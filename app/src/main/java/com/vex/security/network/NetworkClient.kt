package com.vex.security.network

import com.vex.security.network.ApiClient
import com.vex.security.network.ApiService

object NetworkClient {
    val service: ApiService
        get() = ApiClient.apiService
}
