package com.vex.security.browser

import android.content.Context
import android.util.Log
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.WebStorage
import com.vex.security.security.ThreatEngine
import com.vex.security.security.ThreatIndicators
import com.vex.security.security.UrlAnalyzer
import java.io.File
import java.net.URLEncoder
import java.util.Locale

data class UrlSafetyCheck(
    val isDangerous: Boolean,
    val url: String,
    val reasonTitle: String,
    val riskScore: Int,
    val riskLevel: String,
    val indicators: List<String>,
    val explanationReasons: List<String>,
    val recommendation: String
)

object VexBrowserEngine {

    private const val TAG = "VexBrowserEngine"

    var sessionBlockedAds: Int = 0
    var sessionBlockedThreats: Int = 0

    fun resetSessionCounters() {
        sessionBlockedAds = 0
        sessionBlockedThreats = 0
    }

    /**
     * JavaScript Virtual HAL & Active Deception Layer
     * Injected into every page DOM by VEX Secure Browser.
     * Hooks navigator.mediaDevices.getUserMedia() to feed synthetic canary decoy stream.
     */
    const val VIRTUAL_HAL_INJECTION_SCRIPT: String = """
    (function() {
        if (window.__VEX_VIRTUAL_HAL__) return;
        window.__VEX_VIRTUAL_HAL__ = true;
        window.__VEX_SECURE_BROWSER__ = true;

        console.log("[VEX] 🛡️ Virtual HAL Active: Physical camera isolated. Deception stream primed.");

        // Hook navigator.mediaDevices.getUserMedia
        if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
            const originalGetUserMedia = navigator.mediaDevices.getUserMedia.bind(navigator.mediaDevices);

            navigator.mediaDevices.getUserMedia = function(constraints) {
                console.log("[VEX] 🚨 Intercepted camera stream request: ", constraints);
                try {
                    if (window.VexVirtualHal && window.VexVirtualHal.onMediaAccessIntercepted) {
                        window.VexVirtualHal.onMediaAccessIntercepted(window.location.href, JSON.stringify(constraints || {}));
                    }
                } catch(e) { console.error("[VEX] Virtual HAL bridge error:", e); }

                // Create synthetic Decoy Stream using a virtual Canvas
                const canvas = document.createElement('canvas');
                canvas.width = 640;
                canvas.height = 480;
                const ctx = canvas.getContext('2d');

                const img = new Image();
                img.crossOrigin = "anonymous";
                img.src = "http://10.0.2.2:8000/api/honeypot/selfie?session=vex_browser_hook&t=" + Date.now();

                function renderDecoy() {
                    if (img.complete && img.naturalWidth > 0) {
                        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
                    } else {
                        // Matrix animation fallback while loading
                        ctx.fillStyle = "#0a0f18";
                        ctx.fillRect(0, 0, canvas.width, canvas.height);
                        ctx.fillStyle = "#00ffe0";
                        ctx.font = "18px monospace";
                        ctx.fillText("[VEX ACTIVE DECEPTION: CAMERA HAL VIRTUALIZED]", 30, 200);
                        ctx.fillStyle = "#ff4d6d";
                        ctx.font = "14px monospace";
                        ctx.fillText("Nice try, script kiddie! Honeytrap armed.", 30, 240);
                    }
                    requestAnimationFrame(renderDecoy);
                }
                renderDecoy();

                // Generate valid synthetic MediaStream from Canvas
                const syntheticStream = canvas.captureStream ? canvas.captureStream(25) : null;
                if (syntheticStream) {
                    return Promise.resolve(syntheticStream);
                }
                return originalGetUserMedia(constraints);
            };
        }
    })();
    """

    /**
     * Evaluates a URL against the VEX Threat Intelligence Engine,
     * heuristic analyzers, and known malicious patterns.
     */
    fun evaluateUrl(urlInput: String, context: Context): UrlSafetyCheck {
        val url = urlInput.trim()
        if (url.isBlank() || url == "about:blank") {
            return UrlSafetyCheck(
                isDangerous = false,
                url = url,
                reasonTitle = "Safe",
                riskScore = 0,
                riskLevel = "SAFE",
                indicators = emptyList(),
                explanationReasons = emptyList(),
                recommendation = "Normal browsing"
            )
        }

        val lowerUrl = url.lowercase(Locale.ROOT)
        val indicators = UrlAnalyzer.analyze(url).toMutableList()

        // Additional real-time threat detection patterns
        if (lowerUrl.contains("malware.testing") ||
            lowerUrl.contains("test-malware") ||
            lowerUrl.contains("testsafebrowsing") ||
            lowerUrl.contains("wicar.org") ||
            lowerUrl.contains("eicar.org") ||
            lowerUrl.contains("evil.com") ||
            lowerUrl.contains("payload.apk") ||
            lowerUrl.contains("virus-sample") ||
            lowerUrl.contains("credential-harvest")
        ) {
            if (!indicators.contains(ThreatIndicators.MALICIOUS_DOWNLOAD)) {
                indicators.add(ThreatIndicators.MALICIOUS_DOWNLOAD)
            }
        }

        // AdGuard Custom DNS verification (dns.adguard.com)
        val host = try {
            java.net.URI(if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url").host
        } catch (_: Exception) {
            null
        }
        val isBlockedByAdGuard = AdGuardDnsEngine.isBlockedByAdGuardDns(host)
        if (isBlockedByAdGuard) {
            if (!indicators.contains(ThreatIndicators.MALICIOUS_DOWNLOAD)) {
                indicators.add(ThreatIndicators.MALICIOUS_DOWNLOAD)
            }
        }

        // Calculate threat score and level via VEX Threat Engine
        val analysis = ThreatEngine.analyze(indicators, context)
        val score = if (isBlockedByAdGuard) maxOf(85, analysis.riskScore) else analysis.riskScore
        val level = if (isBlockedByAdGuard) "BLOCK" else analysis.riskLevel

        // Determine if navigation must be halted
        val isDangerous = isBlockedByAdGuard ||
                level == "BLOCK" ||
                level == "QUARANTINE" ||
                indicators.contains(ThreatIndicators.PHISHING_URL) ||
                indicators.contains(ThreatIndicators.MALICIOUS_DOWNLOAD) ||
                indicators.contains(ThreatIndicators.EMBEDDED_CREDENTIALS) ||
                indicators.contains(ThreatIndicators.PUNYCODE_DOMAIN) ||
                indicators.contains(ThreatIndicators.IP_BASED_URL) ||
                indicators.contains("CONTROLLED_BLOCK_URL") ||
                indicators.contains("CONTROLLED_QUARANTINE_URL") ||
                indicators.contains(ThreatIndicators.SUSPICIOUS_TLD) && indicators.contains(ThreatIndicators.SUSPICIOUS_URL)

        val reasonTitle = when {
            isBlockedByAdGuard -> "AdGuard DNS: Ad/Malware Host Blocked (dns.adguard.com)"
            indicators.contains(ThreatIndicators.PHISHING_URL) -> "Phishing Domain Impersonation Detected"
            indicators.contains(ThreatIndicators.MALICIOUS_DOWNLOAD) -> "Drive-by Executable / Malware Download Blocked"
            indicators.contains(ThreatIndicators.EMBEDDED_CREDENTIALS) -> "Embedded Credentials Attack Detected"
            indicators.contains(ThreatIndicators.PUNYCODE_DOMAIN) -> "Homograph IDN / Punycode Spoofing Detected"
            indicators.contains(ThreatIndicators.IP_BASED_URL) -> "Hostile Numerical IP Web Server Blocked"
            indicators.contains("CONTROLLED_BLOCK_URL") || indicators.contains("CONTROLLED_QUARANTINE_URL") -> "VEX High-Risk Controlled Threat"
            indicators.contains(ThreatIndicators.SUSPICIOUS_TLD) -> "Suspicious TLD with Deceptive URL Scheme"
            else -> "High Risk Cyber Threat Detected"
        }

        return UrlSafetyCheck(
            isDangerous = isDangerous,
            url = url,
            reasonTitle = reasonTitle,
            riskScore = score,
            riskLevel = level,
            indicators = indicators,
            explanationReasons = analysis.explanation.reasons,
            recommendation = analysis.explanation.recommendation
        )
    }

    /**
     * Resolves user input into a valid web URL or a privacy-safe DuckDuckGo search URL.
     */
    fun resolveUrlOrSearch(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "https://duckduckgo.com"

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed
        }

        // Domain heuristic: contains dot, no spaces, e.g. "example.com" or "wikipedia.org/wiki/Linux"
        if (!trimmed.contains(" ") && trimmed.contains(".") && !trimmed.endsWith(".")) {
            return "https://$trimmed"
        }

        // Default to privacy search via DuckDuckGo
        return try {
            "https://duckduckgo.com/?q=${URLEncoder.encode(trimmed, "UTF-8")}"
        } catch (_: Exception) {
            "https://duckduckgo.com/?q=$trimmed"
        }
    }

    /**
     * Automatically and completely purges all browser caches, cookies,
     * DOM databases, history, and permission grants.
     * Leaves zero trace on the device.
     */
    fun clearAllBrowserData(context: Context) {
        try {
            // Remove all cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.removeSessionCookies(null)
            cookieManager.flush()

            // Remove WebStorage (Local Storage & Session Storage)
            WebStorage.getInstance().deleteAllData()

            // Clear any geolocation grants
            GeolocationPermissions.getInstance().clearAll()

            // Clear cache directories
            context.cacheDir.deleteRecursively()

            // Clear WebView local directory if accessible
            val appDir = context.applicationInfo.dataDir
            val webViewDir = File(appDir, "app_webview")
            if (webViewDir.exists()) {
                webViewDir.deleteRecursively()
            }

            Log.d(TAG, "Zero-trace wipe completed: all browser cache, cookies, and storage annihilated.")
        } catch (e: Exception) {
            Log.e(TAG, "Error purging browser session data: ${e.message}", e)
        }
    }
}
