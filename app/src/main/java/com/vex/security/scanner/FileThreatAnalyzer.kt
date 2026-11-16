package com.vex.security.scanner

data class FileThreatReport(
    val trueType: String,
    val isMasquerading: Boolean,
    val riskScore: Float,
    val verdict: String,
    val findings: List<String>
)

object FileThreatAnalyzer {

    // Magic byte signatures → true file type
    private val magicBytes = mapOf(
        "PDF"   to byteArrayOf(0x25, 0x50, 0x44, 0x46),
        "ZIP"   to byteArrayOf(0x50, 0x4B, 0x03, 0x04),
        "EXE"   to byteArrayOf(0x4D, 0x5A),
        "ELF"   to byteArrayOf(0x7F, 0x45, 0x4C, 0x46),
        "DEX"   to byteArrayOf(0x64, 0x65, 0x78, 0x0A),
        "CLASS" to byteArrayOf(0xCA.toByte(), 0xFE.toByte(),
                               0xBA.toByte(), 0xBE.toByte()),
        "JPEG"  to byteArrayOf(0xFF.toByte(), 0xD8.toByte(),
                               0xFF.toByte()),
        "PNG"   to byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)
    )

    fun detectTrueType(headerBytes: ByteArray): String {
        for ((type, sig) in magicBytes) {
            if (headerBytes.size >= sig.size &&
                headerBytes.take(sig.size).toByteArray()
                    .contentEquals(sig)) return type
        }
        return "UNKNOWN"
    }

    fun isMasquerading(declaredExt: String?, trueType: String): Boolean {
        val ext = declaredExt?.uppercase() ?: return false
        val execTypes = setOf("EXE", "ELF", "DEX", "CLASS")
        return trueType in execTypes && ext != trueType
    }

    fun analyzeContent(
        context: android.content.Context,
        uri: android.net.Uri,
        extension: String?
    ): FileThreatReport {
        val findings = mutableListOf<String>()
        var score = 0f

        val headerBytes = try {
            if (uri.scheme == "file") {
                val f = java.io.File(uri.path ?: "")
                if (f.exists()) f.inputStream().use { it.readNBytes(16) } else ByteArray(0)
            } else {
                context.contentResolver.openInputStream(uri)?.use { 
                    it.readNBytes(16) 
                } ?: ByteArray(0)
            }
        } catch (e: Exception) { ByteArray(0) }

        val trueType = detectTrueType(headerBytes)
        val masq = isMasquerading(extension, trueType)

        if (masq) {
            findings.add("🔴 File is masquerading: extension .${extension} but actually $trueType")
            score += 0.8f
        }

        if (trueType in setOf("EXE", "ELF", "DEX", "CLASS")) {
            findings.add("🔴 Executable file detected")
            score += 0.7f
        }

        // PDF analysis
        if (trueType == "PDF" || extension?.lowercase() == "pdf") {
            val content = try {
                if (uri.scheme == "file") {
                    val f = java.io.File(uri.path ?: "")
                    if (f.exists()) f.inputStream().use { it.readBytes().toString(Charsets.ISO_8859_1) } else ""
                } else {
                    context.contentResolver.openInputStream(uri)?.use {
                        it.readBytes().toString(Charsets.ISO_8859_1)
                    } ?: ""
                }
            } catch (e: Exception) { "" }
            if (content.contains("/JavaScript") || content.contains("/JS")) {
                findings.add("🔴 JavaScript found inside PDF")
                score += 0.4f
            }
            if (content.contains("/OpenAction") || content.contains("/AA")) {
                findings.add("🔴 Auto-open action in PDF — executes on open")
                score += 0.3f
            }
            if (content.contains("/EmbeddedFile")) {
                findings.add("⚠️ Embedded file attachment in PDF")
                score += 0.2f
            }
            if (content.contains("/Launch")) {
                findings.add("🔴 PDF can launch external programs")
                score += 0.4f
            }
        }

        // ZIP bomb detection
        if (trueType == "ZIP") {
            try {
                val tempFile = java.io.File.createTempFile(
                    "vex_zip_", ".zip", context.cacheDir)
                if (uri.scheme == "file") {
                    val f = java.io.File(uri.path ?: "")
                    f.copyTo(tempFile, overwrite = true)
                } else {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        java.io.FileOutputStream(tempFile).use { it.write(input.readBytes()) }
                    }
                }
                val zipFile = java.util.zip.ZipFile(tempFile)
                var totalUncompressed = 0L
                val compressed = tempFile.length()
                zipFile.entries().asIterator().forEach { e ->
                    totalUncompressed += e.size
                }
                zipFile.close()
                tempFile.delete()
                val ratio = if (compressed > 0) 
                    totalUncompressed.toDouble() / compressed else 0.0
                if (ratio > 100.0 || totalUncompressed > 500_000_000L) {
                    findings.add("🔴 ZIP Bomb detected — ${ratio.toInt()}x compression ratio")
                    score += 0.9f
                }
            } catch (e: Exception) { /* ignore */ }
        }

        // Script analysis
        val scriptExts = setOf("html", "htm", "js", "py", "sh",
                               "php", "bat", "ps1", "vbs")
        if (extension?.lowercase() in scriptExts) {
            val content = try {
                if (uri.scheme == "file") {
                    val f = java.io.File(uri.path ?: "")
                    if (f.exists()) f.inputStream().use { it.readBytes().toString(Charsets.UTF_8) } else ""
                } else {
                    context.contentResolver.openInputStream(uri)?.use {
                        it.readBytes().toString(Charsets.UTF_8)
                    } ?: ""
                }
            } catch (e: Exception) { "" }
            val lc = content.lowercase()
            if (lc.contains("eval("))       { findings.add("🔴 eval() found — code execution"); score += 0.4f }
            if (lc.contains("wscript.shell"))  { findings.add("🔴 WScript.Shell found"); score += 0.5f }
            if (lc.contains("powershell"))     { findings.add("🔴 PowerShell command found"); score += 0.5f }
            if (lc.contains("cmd.exe"))        { findings.add("🔴 cmd.exe reference found"); score += 0.5f }
            if (Regex("[A-Za-z0-9+/]{100,}={0,2}").containsMatchIn(content)) {
                findings.add("⚠️ Large base64 payload found — possible hidden code")
                score += 0.3f
            }
        }

        score = score.coerceAtMost(1.0f)
        val verdict = when {
            score >= 0.8f -> "DANGEROUS"
            score >= 0.5f -> "SUSPICIOUS"
            score >= 0.2f -> "LOW_RISK"
            else          -> "SAFE"
        }

        return FileThreatReport(
            trueType = trueType,
            isMasquerading = masq,
            riskScore = score,
            verdict = verdict,
            findings = findings
        )
    }
}
