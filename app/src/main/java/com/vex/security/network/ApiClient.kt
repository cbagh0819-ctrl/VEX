package com.vex.security.network

import android.util.Log
import com.vex.security.BuildConfig
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {

    private val configuredUrl: String = BuildConfig.BACKEND_URL.let {
        if (it.endsWith("/")) it else "$it/"
    }

    @Volatile
    var customBaseUrl: String? = null

    fun getServerUrl(): String {
        return customBaseUrl?.let { if (it.endsWith("/")) it else "$it/" } ?: configuredUrl
    }

    fun setServerUrl(url: String) {
        val trimmed = url.trim()
        val formatted = if (trimmed.endsWith("/")) trimmed else "$trimmed/"
        if (formatted.startsWith("http://") || formatted.startsWith("https://")) {
            customBaseUrl = formatted
            HttpUrl.parse(formatted)?.let {
                activeEndpoint = it
                activeHost = it.host()
            }
            Log.i("ApiClient", "Custom backend URL set to: $formatted")
        }
    }

    private val candidateUrls: List<HttpUrl> by lazy {
        val list = mutableListOf<String>()
        list.add(configuredUrl)
        list.add("https://vex-security-soc.onrender.com/")
        list.add("http://10.0.2.2:8000/")
        list.add("http://192.168.31.2:8000/")
        list.add("http://192.168.1.100:8000/")
        list.add("http://127.0.0.1:8000/")

        list.mapNotNull { HttpUrl.parse(it) }.distinct()
    }

    @Volatile
    var activeHost: String = (HttpUrl.parse(configuredUrl)?.host() ?: "10.0.2.2")

    @Volatile
    private var activeEndpoint: HttpUrl = (HttpUrl.parse(configuredUrl)
        ?: HttpUrl.parse("http://10.0.2.2:8000/")!!)

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val request = chain.request()
                val originalUrl = request.url()

                val targetCandidates = mutableListOf<HttpUrl>()
                customBaseUrl?.let { HttpUrl.parse(it) }?.let { targetCandidates.add(it) }
                targetCandidates.add(activeEndpoint)

                candidateUrls.forEach { cand ->
                    if (!targetCandidates.any { it.host() == cand.host() && it.port() == cand.port() && it.scheme() == cand.scheme() }) {
                        targetCandidates.add(cand)
                    }
                }

                var lastException: IOException? = null
                for (candidate in targetCandidates) {
                    val newUrl = originalUrl.newBuilder()
                        .scheme(candidate.scheme())
                        .host(candidate.host())
                        .port(candidate.port())
                        .build()
                    val newRequest = request.newBuilder().url(newUrl).build()

                    try {
                        val response = chain.proceed(newRequest)
                        if (response.isSuccessful || response.code() < 500) {
                            if (activeEndpoint.host() != candidate.host() || activeEndpoint.port() != candidate.port()) {
                                activeEndpoint = candidate
                                activeHost = candidate.host()
                                Log.i("ApiClient", "Backend connected: ${candidate.scheme()}://${candidate.host()}:${candidate.port()}")
                            }
                            return@addInterceptor response
                        }
                        return@addInterceptor response
                    } catch (e: IOException) {
                        lastException = e
                        Log.w("ApiClient", "Connection to ${candidate.host()}:${candidate.port()} failed: ${e.message}")
                    }
                }

                throw lastException ?: IOException("Failed to connect to VEX backend on any candidate endpoint")
            }
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(configuredUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    suspend fun isAvailable(): Boolean {
        return try {
            val response = apiService.healthCheck()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
