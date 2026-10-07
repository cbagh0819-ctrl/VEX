package com.vex.security.security

import java.util.Locale

object UrlAnalyzer {

    private val shorteners = listOf(
        "bit.ly", "goo.gl", "tinyurl.com", "t.co", "ow.ly", "is.gd", "buff.ly", "adf.ly"
    )

    fun analyze(urlInput: String): List<String> {
        val indicators = mutableListOf<String>()
        val url = urlInput.trim().lowercase(Locale.ROOT)

        if (url.isEmpty()) return indicators

        // Check IP based URL
        val ipRegex = Regex("https?://\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")
        if (ipRegex.containsMatchIn(url)) {
            indicators.add(ThreatIndicators.IP_BASED_URL)
        }

        // Check URL shorteners
        if (shorteners.any { url.contains(it) }) {
            indicators.add(ThreatIndicators.URL_SHORTENER)
        }

        // Check phishing keywords
        val phishingKeywords = listOf("login", "verify", "update", "account", "secure", "banking", "signin")
        if (phishingKeywords.any { url.contains(it) }) {
            indicators.add(ThreatIndicators.PHISHING_URL)
        }

        // Check suspicious patterns
        if (url.contains("free") || url.contains("bonus") || url.contains("win")) {
            indicators.add(ThreatIndicators.SUSPICIOUS_URL)
        }

        return indicators.distinct()
    }
}
