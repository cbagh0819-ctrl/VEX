package com.vex.security.security

import java.net.URI
import java.util.Locale

object UrlAnalyzer {

    // Controlled test URLs for test engine backwards compatibility
    private const val SAFE_URL = "https://example.com/?vex_test=SAFE_10"
    private const val WARN_URL = "https://example.com/?vex_test=WARN_35"
    private const val BLOCK_URL = "https://example.com/?vex_test=BLOCK_60"
    private const val QUARANTINE_URL = "https://example.com/?vex_test=QUARANTINE_85"

    private val shorteners = setOf(
        "bit.ly", "goo.gl", "tinyurl.com", "t.co", "ow.ly", "is.gd", "buff.ly", "adf.ly", "cutt.ly", "rb.gy", "shorturl.at"
    )

    private val suspiciousTlds = setOf(
        "top", "xyz", "club", "work", "icu", "zip", "mov", "tk", "ml", "ga", "cf", "gq", "buzz", "cam", "fit", "rest", "monster", "click", "link", "stream", "download", "racing", "bid", "country", "loan"
    )

    private val officialBrandDomains = mapOf(
        "paypal" to "paypal.com",
        "google" to "google.com",
        "apple" to "apple.com",
        "microsoft" to "microsoft.com",
        "netflix" to "netflix.com",
        "amazon" to "amazon.com",
        "binance" to "binance.com",
        "metamask" to "metamask.io",
        "coinbase" to "coinbase.com",
        "chase" to "chase.com",
        "facebook" to "facebook.com",
        "instagram" to "instagram.com",
        "whatsapp" to "whatsapp.com"
    )

    private val suspiciousKeywords = listOf(
        "login", "verify", "update", "account", "secure", "banking", "signin",
        "wallet", "seed-phrase", "airdrop", "lottery", "prize", "free-money",
        "password", "auth", "credential", "suspend", "unlock"
    )

    private val dangerousFileExtensions = listOf(
        ".apk", ".exe", ".scr", ".bat", ".cmd", ".ps1", ".vbs", ".sh", ".msi", ".jar"
    )

    fun analyze(urlInput: String): List<String> {
        val indicators = mutableListOf<String>()
        val url = urlInput.trim()

        if (url.isBlank()) {
            return listOf(ThreatIndicators.NORMAL_URL)
        }

        // 1. Check controlled test fixtures
        when (url) {
            SAFE_URL -> return listOf("CONTROLLED_SAFE_URL")
            WARN_URL -> return listOf("CONTROLLED_WARN_URL")
            BLOCK_URL -> return listOf("CONTROLLED_BLOCK_URL")
            QUARANTINE_URL -> return listOf("CONTROLLED_QUARANTINE_URL")
        }

        val lowerUrl = url.lowercase(Locale.ROOT)

        // 2. Protocol Check
        if (lowerUrl.startsWith("http://")) {
            indicators.add(ThreatIndicators.INSECURE_HTTP)
        } else if (lowerUrl.startsWith("javascript:") || lowerUrl.startsWith("data:") || lowerUrl.startsWith("file:")) {
            indicators.add(ThreatIndicators.SUSPICIOUS_URL)
        }

        // 3. Embedded Credentials Check (e.g. http://admin:password@evil.com)
        if (lowerUrl.contains("://") && lowerUrl.substringAfter("://").contains("@")) {
            indicators.add(ThreatIndicators.EMBEDDED_CREDENTIALS)
        }

        // 4. Extract Host and Path safely
        var host = ""
        var path = ""
        try {
            val uri = URI(if (!url.contains("://")) "http://$url" else url)
            host = uri.host?.lowercase(Locale.ROOT) ?: ""
            path = (uri.path ?: "").lowercase(Locale.ROOT)
        } catch (_: Exception) {
            val withoutScheme = lowerUrl.substringAfter("://")
            host = withoutScheme.substringBefore("/").substringBefore("?").substringBefore(":")
            path = "/" + withoutScheme.substringAfter("/", "")
        }

        if (host.isNotBlank()) {
            // Raw numerical IP host check
            val ipRegex = Regex("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")
            if (ipRegex.matches(host)) {
                indicators.add(ThreatIndicators.IP_BASED_URL)
            }

            // Punycode (IDN homograph attack) check
            if (host.startsWith("xn--") || host.contains(".xn--")) {
                indicators.add(ThreatIndicators.PUNYCODE_DOMAIN)
            }

            // URL Shortener check
            if (shorteners.any { host == it || host.endsWith(".$it") }) {
                indicators.add(ThreatIndicators.URL_SHORTENER)
            }

            // Suspicious TLD check
            val tld = host.substringAfterLast(".", "")
            if (suspiciousTlds.contains(tld)) {
                indicators.add(ThreatIndicators.SUSPICIOUS_TLD)
            }

            // Excessive subdomains check (e.g. a.b.c.d.evil.com)
            val domainParts = host.split(".")
            if (domainParts.size >= 5) {
                indicators.add(ThreatIndicators.EXCESSIVE_SUBDOMAINS)
            }

            // Phishing / Brand Impersonation check
            for ((brand, authenticDomain) in officialBrandDomains) {
                if (host.contains(brand) && host != authenticDomain && !host.endsWith(".$authenticDomain")) {
                    indicators.add(ThreatIndicators.PHISHING_URL)
                    break
                }
            }
        }

        // 5. Direct Executable / Malware Download Check
        if (dangerousFileExtensions.any { lowerUrl.contains(it) }) {
            indicators.add(ThreatIndicators.MALICIOUS_DOWNLOAD)
        }

        // 6. Suspicious path/query keywords
        if (suspiciousKeywords.any { path.contains(it) || lowerUrl.contains(it) }) {
            indicators.add(ThreatIndicators.SUSPICIOUS_URL)
        }

        if (indicators.isEmpty()) {
            indicators.add(ThreatIndicators.NORMAL_URL)
        }

        return indicators.distinct()
    }
}
