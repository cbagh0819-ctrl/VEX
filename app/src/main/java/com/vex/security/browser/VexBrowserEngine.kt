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
