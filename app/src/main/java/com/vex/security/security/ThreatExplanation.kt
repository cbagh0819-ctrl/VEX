package com.vex.security.security

data class ThreatExplanation(
    val reasons: List<String> = emptyList(),
    val recommendation: String = ""
)

object ThreatExplanationEngine {
    fun explain(
        indicators: List<String>,
        riskScore: Int,
        riskLevel: String,
        action: String
    ): ThreatExplanation {
        val reasons = mutableListOf<String>()
        for (ind in indicators) {
            when (ind) {
                "FILE_SAFE_DEMO" -> reasons.add("Original Safe Folder / File Detected")
                "FILE_WARN_DEMO" -> reasons.add("Suspicious file attributes detected")
                "FILE_HARMFUL_DEMO" -> reasons.add("High-risk malicious payload pattern identified")
                "DOUBLE_EXTENSION" -> reasons.add("Dangerous double file extension detected (e.g. .pdf.exe)")
                "SUSPICIOUS_EXTENSION" -> reasons.add("Executable or script extension found in download")
                "UNKNOWN_FILE_TYPE" -> reasons.add("File signature does not match declared MIME extension")
                "HIGH_RISK_PERMISSION" -> reasons.add("Application requests critical sensitive system permissions")
                "SUSPICIOUS_APK" -> reasons.add("Unverified APK signature or third-party sideload source")
                "MALICIOUS_TEST_INDICATOR" -> reasons.add("Controlled security lab threat test indicator triggered")
                "NO_METADATA" -> reasons.add("Complete lack of standard camera EXIF metadata in media")
                "AI_GENERATION_MARKER" -> reasons.add("Synthetic generator signatures found in image header")
                "SUSPICIOUS_URL" -> reasons.add("Target URL flagged in domain blacklist or IP pattern")
                "PHISHING_HEURISTIC" -> reasons.add("Deceptive login credential harvesting page pattern")
                "QR_EMBEDDED_EXPLOIT" -> reasons.add("QR code contains encoded malicious payload or redirect")
                "USB_AUTORUN_DETECTED" -> reasons.add("OTG/USB storage contains autorun script or suspicious binaries")
                else -> reasons.add("Triggered security indicator: $ind")
            }
        }
        if (reasons.isEmpty()) {
            reasons.add(if (riskScore == 0) "No suspicious indicators detected." else "Security policy evaluated with score $riskScore.")
        }

        val recommendation = when (riskLevel.uppercase()) {
            "SAFE" -> "The analyzed item is safe and allowed for use."
            "WARN" -> "Review the item carefully before opening or installing."
            "BLOCK" -> "The file or activity was blocked to protect system integrity."
            "QUARANTINE", "CRITICAL" -> "Isolate immediately. Payload poses severe risk to device and data."
            else -> "Follow standard organizational security policies."
        }

        return ThreatExplanation(reasons = reasons, recommendation = recommendation)
    }
}
