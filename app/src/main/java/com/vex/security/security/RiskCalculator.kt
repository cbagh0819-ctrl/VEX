package com.vex.security.security

object RiskCalculator {

    fun calculate(indicators: List<String>): Int {
        var score = 0

        for (indicator in indicators) {
            score += when (indicator) {
                "SUSPICIOUS_EXTENSION" -> 25
                "DOUBLE_EXTENSION" -> 30
                "UNKNOWN_FILE_TYPE" -> 20
                "HIGH_RISK_PERMISSION" -> 25
                "SUSPICIOUS_APK" -> 35
                "MALICIOUS_TEST_INDICATOR" -> 60
                "SUSPICIOUS_FILENAME" -> 25
                "LARGE_EXECUTABLE" -> 20
                "HIDDEN_EXTENSION" -> 20
                "UNKNOWN_MIME_TYPE" -> 15
                "EMPTY_FILE_NAME" -> 10
                "LARGE_FILE" -> 10
                "SUSPICIOUS_URL" -> 25
                "PHISHING_URL" -> 40
                "IP_BASED_URL" -> 20
                "URL_SHORTENER" -> 15
                else -> 5
            }
        }

        return score.coerceIn(0, 100)
    }

    fun getRiskLevel(score: Int): String {
        return when {
            score >= 80 -> "CRITICAL"
            score >= 60 -> "HIGH"
            score >= 30 -> "MEDIUM"
            else -> "LOW"
        }
    }
}
