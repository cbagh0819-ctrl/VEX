package com.vex.security.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("health")
    suspend fun healthCheck(): Response<Map<String, Any>>

    @POST("devices/")
    suspend fun registerDevice(
        @Body device: DeviceRequest
    ): Response<Map<String, Any>>

    @POST("events/")
    suspend fun sendThreatEvent(
        @Body event: ThreatEventRequest
    ): Response<Map<String, Any>>

    @GET("reports/summary")
    suspend fun getReportSummary(): Response<Map<String, Any>>

    @GET("events/")
    suspend fun getThreatEvents(): Response<List<Map<String, Any>>>

    @GET("policies/")
    suspend fun getPolicies(): Map<String, String>
}
