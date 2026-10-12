package com.vex.security.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val DEFAULT_HOST = "10.0.2.2"
    private const val BASE_URL = "http://$DEFAULT_HOST:8000/"

    @Volatile
    var activeHost: String = DEFAULT_HOST

    private val candidateHosts = listOf("10.0.2.2", "127.0.0.1", "192.168.31.2")

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val request = chain.request()
                val originalUrl = request.url()

                // Order candidate hosts with the last known working host first
                val orderedHosts = mutableListOf<String>()
                orderedHosts.add(activeHost)
                candidateHosts.forEach { host ->
                    if (!orderedHosts.contains(host)) {
                        orderedHosts.add(host)
                    }
                }

                var lastException: IOException? = null
                for (host in orderedHosts) {
                    val newUrl = originalUrl.newBuilder().host(host).build()
                    val newRequest = request.newBuilder().url(newUrl).build()
                    try {
                        val response = chain.proceed(newRequest)
                        if (response.isSuccessful || response.code() < 500) {
                            if (activeHost != host) {
                                activeHost = host
                                Log.i("ApiClient", "Switched active backend host to: $host:8000")
                            }
                            return@addInterceptor response
                        }
                        return@addInterceptor response
                    } catch (e: IOException) {
                        lastException = e
                        Log.w("ApiClient", "Connection attempt to $host:8000 failed: ${e.message}")
                    }
                }

                throw lastException ?: IOException("Failed to connect to backend on any host: $candidateHosts")
            }
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
