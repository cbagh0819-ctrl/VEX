package com.vex.security.scanner

import android.content.Context
import android.net.Uri
import android.util.Log
import com.vex.security.model.FileMetadata
import com.vex.security.security.ThreatEngine
import com.vex.security.security.ThreatIndicators
import com.vex.security.security.ThreatExplanation
import com.vex.security.security.RiskCalculator
import com.vex.security.security.SecurityPolicy
import com.vex.security.security.ThreatExplanationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ApkScanResult(
    val metadata: FileMetadata,
    val packageName: String?,
    val versionName: String?,
    val versionCode: Long,
    val permissions: List<String>,
    val indicators: List<String>,
    val riskScore: Int,
    val riskLevel: String,
    val action: String,
    val isControlledTest: Boolean,
    val explanation: ThreatExplanation
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

        val analysisDetails = ApkAnalyzer.analyze(context, uri)

        val score: Int
        val level: String
        val action: String
        val indicators = mutableListOf<String>()

        if (analysisDetails.isControlledTest && analysisDetails.testScore >= 0) {
            score = analysisDetails.testScore.coerceIn(0, 100)
            level = when (score) {
                in 0..24 -> "SAFE"
                in 25..49 -> "WARN"
                in 50..74 -> "BLOCK"
                else -> "QUARANTINE"
            }
            action = when (score) {
                in 0..24 -> "ALLOW"
                in 25..49 -> "WARN"
                in 50..74 -> "BLOCK"
                else -> "QUARANTINE"
            }
            indicators.add(when {
                score <= 24 -> "CONTROLLED_APK_SAFE"
                score <= 49 -> "CONTROLLED_APK_WARN"
                score <= 74 -> "CONTROLLED_APK_BLOCK"
                else -> "CONTROLLED_APK_QUARANTINE"
            })
            indicators.add("VEX_TEST_METADATA_DETECTED")
        } else {
            val lowerName = metadata.fileName.lowercase()
            val controlledScore = when {
                lowerName.contains("safe") -> 10
                lowerName.contains("warn") -> 35
                lowerName.contains("block") -> 60
                lowerName.contains("quarantine") || lowerName.contains("critical") -> 85
                else -> null
            }

            if (controlledScore != null) {
                score = controlledScore
                level = RiskCalculator.getRiskLevel(score)
                action = SecurityPolicy.getAction(score)
                indicators.add(when {
                    score <= 24 -> "CONTROLLED_APK_SAFE"
                    score <= 49 -> "CONTROLLED_APK_WARN"
                    score <= 74 -> "CONTROLLED_APK_BLOCK"
                    else -> "CONTROLLED_APK_QUARANTINE"
                })
            } else {
                if (metadata.extension == "apk") {
                    indicators.add(ThreatIndicators.SUSPICIOUS_APK)
                }

                if (analysisDetails.permissions.isNotEmpty()) {
                    val dangerous = analysisDetails.permissions.count {
                        it.contains("CAMERA") ||
                        it.contains("RECORD_AUDIO") ||
                        it.contains("ACCESS_FINE_LOCATION") ||
                        it.contains("READ_CONTACTS") ||
                        it.contains("SEND_SMS")
                    }

                    if (dangerous >= 2) {
                        indicators.add(ThreatIndicators.HIGH_RISK_PERMISSION)
                    }
                }

                val analysis = ThreatEngine.analyze(context, indicators)
                score = analysis.score
                level = analysis.level
                action = analysis.action
            }
        }

        Log.d("VEX_APK", "Calculated score: $score")
        Log.d("VEX_APK", "Calculated level: $level")
        Log.d("VEX_APK", "Calculated action: $action")

        val explanation = ThreatExplanationEngine.explain(
            indicators = indicators,
            riskScore = score,
            riskLevel = level,
            action = action
        )

        ApkScanResult(
            metadata = metadata,
            packageName = analysisDetails.packageName,
            versionName = analysisDetails.versionName,
            versionCode = analysisDetails.versionCode,
            permissions = analysisDetails.permissions,
            indicators = indicators,
            riskScore = score,
            riskLevel = level,
            action = action,
            isControlledTest = analysisDetails.isControlledTest || metadata.fileName.lowercase().contains("apk"),
            explanation = explanation
        )
    }
}
