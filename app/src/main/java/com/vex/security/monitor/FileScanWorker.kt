package com.vex.security.monitor

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.vex.security.repository.ThreatRepository
import com.vex.security.scanner.FileScanner
import com.vex.security.security.ThreatEngine
import com.vex.security.security.ThreatIndicators
import com.vex.security.notification.SecurityNotification
import com.vex.security.metadata.MetadataExtractor
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.runBlocking

class FileScanWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private fun createFileKey(
        fileName: String,
        sha256: String?
    ): String {
        return "$fileName:${sha256 ?: "NO_HASH"}"
    }

    override fun doWork(): Result {

        return try {

            val prefs = applicationContext.getSharedPreferences(
                "vex_scan_history",
                Context.MODE_PRIVATE
            )

            val files = StorageScanner.getRecentFiles(
                applicationContext
            )

            val deviceId = DeviceIdentity.getDeviceId(
                applicationContext
            )

            for (file in files) {

                val metadata = FileScanner.scanFile(
                    context = applicationContext,
                    uri = file.uri,
                    source = "AUTO_SCAN"
                )

                val extractedMetadata = MetadataExtractor.extract(
                    applicationContext,
                    file.uri
                )

                println(
                    "VEX_METADATA: " +
                        "name=${extractedMetadata.name}, " +
                        "size=${extractedMetadata.size}, " +
                        "mime=${extractedMetadata.mimeType}"
                )

                val fileKey = createFileKey(
                    metadata.fileName,
                    metadata.sha256
                )

                if (prefs.getBoolean(fileKey, false)) {
                    continue
                }

                val indicators =
                    AutomaticFileAnalyzer.analyze(metadata).toMutableList()

                if (extractedMetadata.size > 50_000_000) {
                    indicators.add(ThreatIndicators.LARGE_FILE)
                }

                val analysis =
                    ThreatEngine.analyze(indicators)

                // Send event to backend
                runBlocking {

                    ThreatRepository.sendThreatEvent(
                        deviceId = deviceId,
                        threatType = "FILE",
                        riskScore = analysis.riskScore,
                        riskLevel = analysis.riskLevel,
                        action = analysis.action,
                        status = "OPEN",
                        indicators = indicators,
                        source = "ANDROID_AUTO_SCAN"
                    )
                }

                prefs.edit()
                    .putBoolean(fileKey, true)
                    .apply()

                // Security alert
                if (analysis.riskScore >= 30) {

                    SecurityNotification.show(
                        applicationContext,
                        "VEX Security Alert",
                        "${metadata.fileName} detected as ${analysis.riskLevel}"
                    )
                }
            }

            Result.success()

        } catch (e: Exception) {

            println(
                "VEX AUTO SCAN ERROR: ${e.message}"
            )

            Result.retry()
        }
    }
}
