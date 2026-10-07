package com.vex.security.monitor

import com.vex.security.model.FileMetadata
import com.vex.security.security.ThreatIndicators

object AutomaticFileAnalyzer {

    fun analyze(
        metadata: FileMetadata
    ): List<String> {

        val indicators = mutableListOf<String>()

        val fileName = metadata.fileName.lowercase()
        val extension = metadata.extension?.lowercase()

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
            indicators.add(
                ThreatIndicators.SUSPICIOUS_FILENAME
            )
        }

        // Double extension
        if (fileName.count { it == '.' } >= 2) {
            indicators.add(
                ThreatIndicators.DOUBLE_EXTENSION
            )
        }

        // Unknown MIME type
        if (metadata.mimeType == null) {
            indicators.add(
                ThreatIndicators.UNKNOWN_FILE_TYPE
            )
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
            indicators.add(
                ThreatIndicators.SUSPICIOUS_EXTENSION
            )
        }

        // Large executable detection
        if (
            metadata.sizeBytes > 50_000_000 &&
            metadata.extension?.lowercase() in setOf(
                "exe",
                "msi",
                "bat",
                "cmd",
                "scr"
            )
        ) {
            indicators.add(
                ThreatIndicators.LARGE_EXECUTABLE
            )
        }

        if (metadata.fileName.isBlank()) {
            indicators.add(ThreatIndicators.EMPTY_FILE_NAME)
        }

        if (metadata.mimeType == null) {
            indicators.add(ThreatIndicators.UNKNOWN_MIME_TYPE)
        }

        if (
            metadata.extension != null &&
            !fileName.endsWith(".${metadata.extension.lowercase()}")
        ) {
            indicators.add(ThreatIndicators.HIDDEN_EXTENSION)
        }

        return indicators.distinct()
    }
}
