package com.vex.security.browser

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DetectedMedia(
    val url: String,
    val mimeType: String,
    val filename: String,
    val isVideo: Boolean
)

object VexBrowserEngine {
    private const val TAG = "VEX_BROWSER"
    
    // Privacy DNS: AdGuard DoH Endpoint
    const val PRIVATE_DNS_HOST = "dns.adguard.com"
    const val PRIVATE_DNS_DOH = "https://dns.adguard.com/dns-query"
    
    // Reactive Media Sniffer State
    private val _detectedMediaList = MutableStateFlow<List<DetectedMedia>>(emptyList())
    val detectedMediaList: StateFlow<List<DetectedMedia>> = _detectedMediaList.asStateFlow()

    fun configureSecureSettings(webView: WebView) {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = false
            databaseEnabled = false
            cacheMode = WebSettings.LOAD_NO_CACHE
            allowFileAccess = false
            allowContentAccess = false
            setGeolocationEnabled(false)
            displayZoomControls = false
            builtInZoomControls = true
            useWideViewPort = true
            loadWithOverviewMode = true
            userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile; VEX-Browser/2.4; ZeroTrace) AppleWebKit/537.36"
        }

        // Zero-Trace in-memory cookies
        CookieManager.getInstance().apply {
            setAcceptCookie(false)
            setAcceptThirdPartyCookies(webView, false)
        }

        // In-Flight Media Sniffer via DownloadListener
        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            Log.d(TAG, "Media download intercepted: $url ($mimeType)")
            sniffMedia(url, mimeType)
        }
    }

    fun sniffMedia(url: String, mimeType: String? = null) {
        val lowerUrl = url.lowercase()
        val isVideo = lowerUrl.contains(".mp4") || lowerUrl.contains(".m3u8") || 
                      lowerUrl.contains(".webm") || (mimeType?.startsWith("video/") == true)
        val isImage = lowerUrl.contains(".jpg") || lowerUrl.contains(".png") || 
                      lowerUrl.contains(".webp") || (mimeType?.startsWith("image/") == true)

        if (isVideo || isImage) {
            val filename = try {
                Uri.parse(url).lastPathSegment ?: if (isVideo) "video_stream.mp4" else "image.jpg"
            } catch (e: Exception) {
                "downloaded_media"
            }

            val media = DetectedMedia(
                url = url,
                mimeType = mimeType ?: if (isVideo) "video/mp4" else "image/jpeg",
                filename = filename,
                isVideo = isVideo
            )

            val current = _detectedMediaList.value.toMutableList()
            if (current.none { it.url == url }) {
                current.add(0, media)
                _detectedMediaList.value = current
                Log.i(TAG, "Media Sniffer Captured: $filename (Total: ${current.size})")
            }
        }
    }

    fun clearAllBrowserData(context: Context? = null) {
        try {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            WebStorage.getInstance().deleteAllData()
            _detectedMediaList.value = emptyList()
            Log.d(TAG, "All browser caches, cookies, and sniffed media purged from memory.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear browser data: ${e.message}")
        }
    }
}
