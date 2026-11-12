package com.vex.security.security

object RiskCalculator {

    fun getIndicatorWeight(indicator: String): Int {
        return when (indicator) {
            // Controlled fixtures for OTG demo & backward compatibility
            "FILE_SAFE_DEMO" -> 10
            "FILE_WARN_DEMO" -> 35
            "FILE_HARMFUL_DEMO" -> 85

            "CONTROLLED_SAFE_URL" -> 10
            "CONTROLLED_WARN_URL" -> 35
            "CONTROLLED_BLOCK_URL" -> 60
            "CONTROLLED_QUARANTINE_URL" -> 85

            "CONTROLLED_APK_SAFE" -> 10
            "CONTROLLED_APK_WARN" -> 35
            "CONTROLLED_APK_BLOCK" -> 60
            "CONTROLLED_APK_QUARANTINE" -> 85

            // Clean / Safe indicators
            ThreatIndicators.NORMAL_URL -> 10
            ThreatIndicators.CAMERA_AUTHENTIC -> 10
            ThreatIndicators.CAMERA_RECORDED_VIDEO -> 10
            ThreatIndicators.CLEAN_FILE -> 10
            ThreatIndicators.CLEAN_APP -> 10

            // File & Payload indicators
            ThreatIndicators.EXECUTABLE_PAYLOAD -> 50
            ThreatIndicators.SUSPICIOUS_EXTENSION -> 25
            ThreatIndicators.DOUBLE_EXTENSION -> 35
            ThreatIndicators.HIDDEN_EXTENSION -> 30
            ThreatIndicators.UNKNOWN_FILE_TYPE -> 20
            ThreatIndicators.SUSPICIOUS_FILENAME -> 25
            ThreatIndicators.LARGE_EXECUTABLE -> 25
            ThreatIndicators.LARGE_FILE -> 15
            ThreatIndicators.UNKNOWN_MIME_TYPE -> 15
            ThreatIndicators.EMPTY_FILE_NAME -> 20
            ThreatIndicators.MALICIOUS_TEST_INDICATOR -> 60

            // URL & Web indicators
            ThreatIndicators.PHISHING_URL -> 45
            ThreatIndicators.MALICIOUS_DOWNLOAD -> 50
            ThreatIndicators.EMBEDDED_CREDENTIALS -> 40
            ThreatIndicators.SUSPICIOUS_URL -> 25
            ThreatIndicators.IP_BASED_URL -> 25
            ThreatIndicators.URL_SHORTENER -> 15
            ThreatIndicators.SUSPICIOUS_TLD -> 25
            ThreatIndicators.EXCESSIVE_SUBDOMAINS -> 25
            ThreatIndicators.PUNYCODE_DOMAIN -> 35
            ThreatIndicators.INSECURE_HTTP -> 15

            // Image & Metadata indicators
            ThreatIndicators.AI_SYNTHETIC_GENERATOR -> 85
            ThreatIndicators.AI_IMAGE_INDICATOR -> 35
            ThreatIndicators.AI_ENHANCED_IMAGE -> 35
            ThreatIndicators.MISSING_METADATA -> 35

            // AI Video & Deepfake indicators
            ThreatIndicators.AI_GENERATED_VIDEO -> 85
            ThreatIndicators.DEEPFAKE_FACE_SYNTHESIS -> 80
            ThreatIndicators.AI_LIPSYNC_MANIPULATION -> 65
            ThreatIndicators.SYNTHETIC_AUDIO_TRACK -> 40
            ThreatIndicators.AI_VIDEO_INDICATOR -> 35

            // APK & Application indicators
            ThreatIndicators.DANGEROUS_PERMISSIONS_COMBO -> 50
            ThreatIndicators.ACCESSIBILITY_ABUSE -> 45
            ThreatIndicators.OVERLAY_PERMISSION -> 30
            ThreatIndicators.HIGH_RISK_PERMISSION -> 25
            ThreatIndicators.SUSPICIOUS_APK -> 30
            ThreatIndicators.DEBUG_SIGNATURE -> 25
            ThreatIndicators.OUTDATED_TARGET_SDK -> 20

            // Call & SMS Bombing indicators
            ThreatIndicators.TELECOM_DOS_ATTACK -> 90
            ThreatIndicators.SMS_BOMB_BURST -> 85
            ThreatIndicators.CALL_FLOOD_ATTACK -> 85
            ThreatIndicators.OTP_FLOOD_PATTERN -> 75
            ThreatIndicators.UNKNOWN_SENDER_FLOOD -> 70

            else -> 10
        }
    }

    fun calculate(indicators: List<String>): Int {
        if (indicators.isEmpty()) return 10
        
        // If it only has clean/authentic indicators, return a safe score
        val safeOnly = indicators.all { 
            it == ThreatIndicators.NORMAL_URL || 
            it == ThreatIndicators.CAMERA_AUTHENTIC || 
            it == ThreatIndicators.CAMERA_RECORDED_VIDEO ||
            it == ThreatIndicators.CLEAN_FILE || 
            it == ThreatIndicators.CLEAN_APP || 
            it == "FILE_SAFE_DEMO" || 
            it == "CONTROLLED_SAFE_URL" || 
            it == "CONTROLLED_APK_SAFE" 
        }
        if (safeOnly) return 10

        // If high critical indicator is present, guarantee quarantine / block score
        if (indicators.contains(ThreatIndicators.AI_SYNTHETIC_GENERATOR) || 
            indicators.contains(ThreatIndicators.AI_GENERATED_VIDEO) ||
            indicators.contains(ThreatIndicators.DEEPFAKE_FACE_SYNTHESIS) ||
            indicators.contains(ThreatIndicators.DANGEROUS_PERMISSIONS_COMBO) ||
            indicators.contains(ThreatIndicators.ACCESSIBILITY_ABUSE)) {
            return 85
        }

        var score = 0
        for (indicator in indicators) {
            score += getIndicatorWeight(indicator)
        }
        return score.coerceIn(10, 100)
    }

    fun getRiskLevel(score: Int): String {
        return when (score) {
            in 0..19 -> "SAFE"
            in 20..39 -> "LOW"
            in 40..59 -> "MEDIUM"
            in 60..79 -> "HIGH"
            else -> "CRITICAL"
        }
    }

    fun normalizeRiskLevel(level: String?): String {
        return when (level?.uppercase()?.trim()) {
            "CRITICAL", "QUARANTINE" -> "CRITICAL"
            "HIGH", "BLOCK" -> "HIGH"
            "MEDIUM", "WARN" -> "MEDIUM"
            "LOW" -> "LOW"
            "SAFE" -> "SAFE"
            else -> "SAFE"
        }
    }
}
