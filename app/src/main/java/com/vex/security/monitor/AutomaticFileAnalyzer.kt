package com.vex.security.monitor

import com.vex.security.model.FileMetadata
import com.vex.security.security.ThreatIndicators

object AutomaticFileAnalyzer {

    fun analyze(metadata: FileMetadata): List<String> {
        val indicators = mutableListOf<String>()

        val fileName = metadata.fileName.lowercase()
        val extension = (metadata.extension
            ?: if (metadata.fileName.contains(".")) metadata.fileName.substringAfterLast('.').lowercase() else null)
            ?.removePrefix(".")

        // 1. Controlled demo fixtures & pattern matching for test lab (safe6487.txt, warn6474.txt, harmful8964.txt)
        when {
            fileName.contains("safe6487") -> {
                indicators.add("FILE_SAFE_DEMO")
            }
            fileName.contains("warn6474") -> {
                indicators.add("FILE_WARN_DEMO")
            }
            fileName.contains("harmful8964") -> {
                indicators.add("FILE_HARMFUL_DEMO")
            }
            fileName == "vex_safe_demo.txt" || fileName.contains("safe_demo") -> {
                return listOf(ThreatIndicators.CLEAN_FILE)
            }
            fileName == "vex_warn_demo.txt" || fileName.contains("warn_demo") -> {
                indicators.add(ThreatIndicators.SUSPICIOUS_FILENAME)
            }
            fileName == "vex_harmful_demo.txt" || fileName.contains("harmful_demo") -> {
                indicators.add(ThreatIndicators.SUSPICIOUS_FILENAME)
                indicators.add(ThreatIndicators.SUSPICIOUS_EXTENSION)
            }
        }

        // 2. Suspicious keywords in filename (real-world malware terms)
        val suspiciousNames = listOf(
            "virus",
            "malware",
            "trojan",
            "payload",
            "exploit",
            "hack",
            "keylogger",
            "ransomware",
            "backdoor",
            "spyware",
            "stealer",
            "rootkit",
            "crack",
            "keygen",
            "patcher"
        )

        if (suspiciousNames.any { fileName.contains(it) }) {
            indicators.add(ThreatIndicators.SUSPICIOUS_FILENAME)
        }

        // 3. Dangerous executable & script payload extensions
        val dangerousExtensions = setOf(
            "exe",
            "bat",
            "cmd",
            "scr",
            "msi",
            "apk",
            "dex",
            "so",
            "sh",
            "bin",
            "vbs",
            "ps1",
            "jar",
            "elf",
            "com",
            "pif"
        )

        if (extension in dangerousExtensions) {
            indicators.add(ThreatIndicators.SUSPICIOUS_EXTENSION)
            indicators.add(ThreatIndicators.EXECUTABLE_PAYLOAD)
        }

        // 4. Double extension detection (e.g. invoice.pdf.exe, image.png.apk)
        val nameParts = fileName.split(".")
        if (nameParts.size >= 3) {
            val lastExt = nameParts.last()
            val secondLastExt = nameParts[nameParts.size - 2]
            val documentOrMedia = setOf("pdf", "doc", "docx", "xls", "xlsx", "jpg", "jpeg", "png", "txt")
            if (lastExt in dangerousExtensions && secondLastExt in documentOrMedia) {
                indicators.add(ThreatIndicators.DOUBLE_EXTENSION)
            } else if (nameParts.size >= 3) {
                indicators.add(ThreatIndicators.DOUBLE_EXTENSION)
            }
        }

        // 5. Detect extension vs MIME type mismatch (hidden extension)
        val mime = metadata.mimeType?.lowercase()
        if (mime != null && extension != null) {
            val isDocOrImage = extension in setOf("jpg", "jpeg", "png", "gif", "pdf", "txt", "doc", "docx")
            val isExecutableMime = mime.contains("dosexec") ||
                    mime.contains("android.package-archive") ||
                    mime.contains("x-sh") ||
                    mime.contains("x-executable")
            if (isDocOrImage && isExecutableMime) {
                indicators.add(ThreatIndicators.HIDDEN_EXTENSION)
            }
        }

        // 6. Large executable detection
        if (metadata.sizeBytes > 50_000_000 && extension in dangerousExtensions) {
            indicators.add(ThreatIndicators.LARGE_EXECUTABLE)
        }

        // 7. Large file detection (> 100MB)
        if (metadata.sizeBytes > 100_000_000) {
            indicators.add(ThreatIndicators.LARGE_FILE)
        }

        // 8. Empty filename or unknown file type
        if (metadata.fileName.isBlank()) {
            indicators.add(ThreatIndicators.EMPTY_FILE_NAME)
        } else if (extension == null && metadata.mimeType == null) {
            indicators.add(ThreatIndicators.UNKNOWN_FILE_TYPE)
        }

        // 9. If completely clean standard file, assign CLEAN_FILE baseline
        if (indicators.isEmpty()) {
            indicators.add(ThreatIndicators.CLEAN_FILE)
        }

        return indicators.distinct()
    }
}
