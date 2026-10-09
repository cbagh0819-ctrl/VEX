package com.vex.security.security

import java.util.Locale

object UrlAnalyzer {

    private const val SAFE_URL = "https://example.com/?vex_test=SAFE_10"
    private const val WARN_URL = "https://example.com/?vex_test=WARN_35"
    private const val BLOCK_URL = "https://example.com/?vex_test=BLOCK_60"
    private const val QUARANTINE_URL = "https://example.com/?vex_test=QUARANTINE_85"

    private val shorteners = listOf(
        "bit.ly", "goo.gl", "tinyurl.com", "t.co", "ow.ly", "is.gd", "buff.ly", "adf.ly"
    )

    fun analyze(urlInput: String): List<String> {
        val indicators = mutableListOf<String>()
        val url = urlInput.trim()

        when (url) {
            SAFE_URL -> {
                indicators.add("CONTROLLED_SAFE_URL")
                return indicators
            }
            WARN_URL -> {
                indicators.add("CONTROLLED_WARN_URL")
                return indicators
            }
            BLOCK_URL -> {
                indicators.add("CONTROLLED_BLOCK_URL")
                return indicators
            }
            QUARANTINE_URL -> {
                indicators.add("CONTROLLED_QUARANTINE_URL")
                return indicators
            }
        }

        val lowerUrl = url.lowercase(Locale.ROOT)
        if (lowerUrl.isEmpty()) {
            indicators.add("NORMAL_URL")
            return indicators
        }

        // Check IP based URL
        val ipRegex = Regex("https?://\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")
        if (ipRegex.containsMatchIn(lowerUrl)) {
            indicators.add(ThreatIndicators.IP_BASED_URL)
        }

        // Check URL shorteners
        if (shorteners.any { lowerUrl.contains(it) }) {
            indicators.add(ThreatIndicators.URL_SHORTENER)
        }

        // Check phishing keywords
        val phishingKeywords = listOf("login", "verify", "update", "account", "secure", "banking", "signin")
        if (phishingKeywords.any { lowerUrl.contains(it) }) {
            indicators.add(ThreatIndicators.PHISHING_URL)
        }

        // Check suspicious patterns
        if (lowerUrl.contains("free") || lowerUrl.contains("bonus") || lowerUrl.contains("win")) {
            indicators.add(ThreatIndicators.SUSPICIOUS_URL)
        }

        if (indicators.isEmpty()) {
            indicators.add("NORMAL_URL")
        }

        return indicators.distinct()
    }
}
