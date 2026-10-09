package com.vex.security.security

object RiskCalculator {

    fun getIndicatorWeight(indicator: String): Int {
        return when (indicator) {
            "FILE_SAFE_DEMO" -> 10
            "FILE_WARN_DEMO" -> 35
            "FILE_HARMFUL_DEMO" -> 85

            "CONTROLLED_SAFE_URL" -> 10
            "CONTROLLED_WARN_URL" -> 35
            "CONTROLLED_BLOCK_URL" -> 60
            "CONTROLLED_QUARANTINE_URL" -> 85
            "NORMAL_URL" -> 20

            "CONTROLLED_APK_SAFE" -> 10
            "CONTROLLED_APK_WARN" -> 35
            "CONTROLLED_APK_BLOCK" -> 60
            "CONTROLLED_APK_QUARANTINE" -> 85

            "SUSPICIOUS_EXTENSION" -> 25
            "DOUBLE_EXTENSION" -> 30
            "UNKNOWN_FILE_TYPE" -> 20
            "HIGH_RISK_PERMISSION" -> 25
            "SUSPICIOUS_APK" -> 35
            "MALICIOUS_TEST_INDICATOR" -> 60
            "SUSPICIOUS_FILENAME" -> 25
            "LARGE_EXECUTABLE" -> 20

            "SUSPICIOUS_URL" -> 25
            "PHISHING_URL" -> 40
            "IP_BASED_URL" -> 20
            "URL_SHORTENER" -> 15

            "MISSING_METADATA" -> 10
            "AI_IMAGE_INDICATOR" -> 35
            "QR_SUSPICIOUS_TARGET" -> 40
            "QR_PHISHING_TARGET" -> 60

            else -> 5
        }
    }

    fun calculate(indicators: List<String>): Int {
        var score = 0
        for (indicator in indicators) {
            score += getIndicatorWeight(indicator)
        }
        return score.coerceIn(0, 100)
    }

    fun getRiskLevel(score: Int): String {
        return when (score) {
            in 0..24 -> "SAFE"
            in 25..49 -> "WARN"
            in 50..74 -> "BLOCK"
            else -> "QUARANTINE"
        }
    }
}
