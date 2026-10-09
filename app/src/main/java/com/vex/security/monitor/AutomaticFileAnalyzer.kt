package com.vex.security.monitor

import com.vex.security.model.FileMetadata
import com.vex.security.security.ThreatIndicators

object AutomaticFileAnalyzer {

    fun analyze(metadata: FileMetadata): List<String> {
        val indicators = mutableListOf<String>()

        val fileName = metadata.fileName.lowercase()
        val extension = metadata.extension
            ?.lowercase()
            ?.removePrefix(".")

        // Controlled demo fixtures & pattern matching for safe, warn, harmful files (e.g. safe6487.txt, warn6474.txt, harmful8964.txt)
        when {
            fileName.contains("safe") -> {
                indicators.add("FILE_SAFE_DEMO")
            }
            fileName.contains("warn") -> {
                indicators.add("FILE_WARN_DEMO")
            }
            fileName.contains("harmful") -> {
                indicators.add("FILE_HARMFUL_DEMO")
            }
            fileName == "vex_safe_demo.txt" -> return emptyList()

            fileName == "vex_warn_demo.txt" -> {
                indicators.add(ThreatIndicators.SUSPICIOUS_FILENAME)
            }

            fileName == "vex_harmful_demo.txt" -> {
                indicators.add(ThreatIndicators.SUSPICIOUS_FILENAME)
                indicators.add(ThreatIndicators.SUSPICIOUS_EXTENSION)
            }
        }

        // Suspicious filename indicators
        val suspiciousNames = listOf(
            "virus",
            "malware",
            "trojan",
            "payload",
            "exploit",
            "hack"
        )

        if (suspiciousNames.any { fileName.contains(it) }) {
            indicators.add(ThreatIndicators.SUSPICIOUS_FILENAME)
        }

        // Double extension, e.g. report.pdf.exe
        val nameParts = fileName.split(".")
        if (nameParts.size >= 3) {
            indicators.add(ThreatIndicators.DOUBLE_EXTENSION)
        }

        // Unknown MIME type
        if (metadata.mimeType == null) {
            indicators.add(ThreatIndicators.UNKNOWN_FILE_TYPE)
        }

        // Suspicious executable extensions
        val suspiciousExtensions = setOf(
            "exe",
            "bat",
            "cmd",
            "scr",
            "msi"
        )

        if (extension in suspiciousExtensions) {
            indicators.add(ThreatIndicators.SUSPICIOUS_EXTENSION)
        }

        // Large executable detection
        if (
            metadata.sizeBytes > 50_000_000 &&
            extension in suspiciousExtensions
        ) {
            indicators.add(ThreatIndicators.LARGE_EXECUTABLE)
        }

        // Empty filename
        if (metadata.fileName.isBlank()) {
            indicators.add(ThreatIndicators.EMPTY_FILE_NAME)
        }

        // Detect a possible extension mismatch
        if (
            extension != null &&
            !fileName.endsWith(".$extension")
        ) {
            indicators.add(ThreatIndicators.HIDDEN_EXTENSION)
        }

        return indicators.distinct()
    }
}
