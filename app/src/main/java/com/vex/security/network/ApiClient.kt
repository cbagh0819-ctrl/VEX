package com.vex.security.network

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val TAG = "ApiClient"
    const val PUBLIC_CLOUD_HOST = "rid-compressed-ministries-menu.trycloudflare.com"
    const val PUBLIC_CLOUD_URL = "https://$PUBLIC_CLOUD_HOST/"

    private const val DEFAULT_HOST = "127.0.0.1"
    private const val BASE_URL = "http://$DEFAULT_HOST:8000/"

    @Volatile
    var activeHost: String = DEFAULT_HOST

    @Volatile
    var customCloudEndpoint: String? = null

    fun getCandidateEndpoints(): List<String> {
        val list = mutableListOf<String>()
        customCloudEndpoint?.let { if (it.isNotBlank()) list.add(it.trim()) }
        list.add("127.0.0.1")
        list.add(PUBLIC_CLOUD_HOST)
        list.add("questionnaire-solution-tri-move.trycloudflare.com")
        list.add("192.168.64.235")
        list.add("10.0.2.2")
        return list.distinct()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .writeTimeout(6, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val request = chain.request()
                val originalUrl = request.url

                val endpoints = mutableListOf<String>()
                // Prioritize active host if valid
                if (activeHost.isNotBlank()) endpoints.add(activeHost)
                getCandidateEndpoints().forEach { ep ->
                    if (!endpoints.contains(ep)) endpoints.add(ep)
                }

                var lastException: IOException? = null
                for (target in endpoints) {
                    val isHttps = target.contains("trycloudflare.com") || target.startsWith("https://")
                    val cleanHost = target.removePrefix("https://").removePrefix("http://").substringBefore("/").substringBefore(":")
                    val targetPort = if (isHttps) 443 else 8000
                    val targetScheme = if (isHttps) "https" else "http"

                    val newUrl = originalUrl.newBuilder()
                        .scheme(targetScheme)
                        .host(cleanHost)
                        .port(targetPort)
                        .build()

                    val newRequest = request.newBuilder()
                        .url(newUrl)
                        .header("User-Agent", "VEX-Mobile-RASP/2.4")
                        .build()

                    try {
                        val response = chain.proceed(newRequest)
                        if (response.isSuccessful || response.code < 500) {
                            if (activeHost != target) {
                                activeHost = target
                                Log.i(TAG, "Switched active backend target to: $targetScheme://$cleanHost:$targetPort")
                            }
                            return@addInterceptor response
                        }
                        return@addInterceptor response
                    } catch (e: IOException) {
                        lastException = e
                        Log.w(TAG, "Connection to $targetScheme://$cleanHost:$targetPort failed: ${e.message}")
                    }
                }

                throw lastException ?: IOException("Failed to connect to backend on any endpoint: $endpoints")
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
