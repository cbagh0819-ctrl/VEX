package com.vex.security.scanner

import android.content.Context
import android.net.Uri
import com.vex.security.model.FileMetadata
import com.vex.security.security.ThreatEngine
import com.vex.security.security.ThreatIndicators
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ApkScanResult(
    val metadata: FileMetadata,
    val permissions: List<String>,
    val indicators: List<String>,
    val riskScore: Int,
    val riskLevel: String,
    val action: String
)

object ApkScanManager {

    suspend fun scan(
        context: Context,
        uri: Uri
    ): ApkScanResult = withContext(Dispatchers.IO) {

        val metadata =
            FileScanner.scanFile(
                context,
                uri,
                "APK_SCAN"
            )

        val permissions =
            ApkAnalyzer.getPermissions(
                context,
                uri
            )

        val indicators = mutableListOf<String>()

        if (metadata.extension == "apk") {
            indicators.add(
                ThreatIndicators.SUSPICIOUS_APK
            )
        }

        if (permissions.isNotEmpty()) {
            val dangerous = permissions.count {
                it.contains("CAMERA") ||
                it.contains("RECORD_AUDIO") ||
                it.contains("ACCESS_FINE_LOCATION") ||
                it.contains("READ_CONTACTS") ||
                it.contains("SEND_SMS")
            }

            if (dangerous >= 2) {
                indicators.add(
                    ThreatIndicators.HIGH_RISK_PERMISSION
                )
            }
        }

        val analysis =
            ThreatEngine.analyze(indicators)

        ApkScanResult(
            metadata = metadata,
            permissions = permissions,
            indicators = indicators,
            riskScore = analysis.riskScore,
            riskLevel = analysis.riskLevel,
            action = analysis.action
        )
    }
}
