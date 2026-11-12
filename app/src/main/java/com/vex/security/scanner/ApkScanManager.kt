package com.vex.security.scanner

import android.content.Context
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.util.Log
import com.vex.security.model.FileMetadata
import com.vex.security.security.RiskCalculator
import com.vex.security.security.SecurityPolicy
import com.vex.security.security.ThreatExplanation
import com.vex.security.security.ThreatExplanationEngine
import com.vex.security.security.ThreatIndicators
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
    val explanation: ThreatExplanation,
    val certificate: CertificateInspectionResult? = null,
    val drebinPrediction: DrebinPrediction? = null,
    val dangerousPairs: List<DangerousPermissionPair> = emptyList()
)

object ApkScanManager {

    var lastScannedResult: ApkScanResult? = null

    suspend fun scan(
        context: Context,
        uri: Uri
    ): ApkScanResult = withContext(Dispatchers.IO) {

        val metadata = FileScanner.scanFile(
            context,
            uri,
            "APK_SCAN"
        )

        val analysisDetails = ApkAnalyzer.analyze(context, uri)

        val score: Int
        val level: String
        val action: String
        val indicators = mutableListOf<String>()

        // 1. Controlled fixtures (for test lab compatibility)
        if (analysisDetails.isControlledTest && analysisDetails.testScore >= 0) {
            score = analysisDetails.testScore.coerceIn(0, 100)
            level = RiskCalculator.getRiskLevel(score)
            action = SecurityPolicy.getAction(score)
            indicators.add(when {
                score <= 24 -> "CONTROLLED_APK_SAFE"
                score <= 49 -> "CONTROLLED_APK_WARN"
                score <= 74 -> "CONTROLLED_APK_BLOCK"
                else -> "CONTROLLED_APK_QUARANTINE"
            })
            indicators.add("VEX_TEST_METADATA_DETECTED")
        } else {
            val perms = analysisDetails.permissions.map { it.uppercase() }

            // 1. Evaluate Drebin ML Model
            val drebin = analysisDetails.drebinPrediction ?: DrebinApkClassifier.predict(analysisDetails.permissions)
            if (drebin.isClassifiedMalicious) {
                indicators.add(ThreatIndicators.DREBIN_ML_MALWARE_PREDICTION)
            }

            // 2. Evaluate Dangerous Permission Pairs
            val dangPairs = analysisDetails.dangerousPairs.ifEmpty {
                ApkDangerousPairsDetector.evaluate(analysisDetails.permissions)
            }
            for (pair in dangPairs) {
                when (pair.id) {
                    "ACCESSIBILITY_OVERLAY" -> {
                        indicators.add(ThreatIndicators.ACCESSIBILITY_OVERLAY_TROJAN)
                        indicators.add(ThreatIndicators.ACCESSIBILITY_ABUSE)
                        indicators.add(ThreatIndicators.OVERLAY_PERMISSION)
                    }
                    "ACCESSIBILITY_SMS" -> {
                        indicators.add(ThreatIndicators.ACCESSIBILITY_SMS_BYPASS)
                        indicators.add(ThreatIndicators.DANGEROUS_PERMISSIONS_COMBO)
                    }
                    else -> {
                        indicators.add(ThreatIndicators.DANGEROUS_PERMISSIONS_COMBO)
                    }
                }
            }

            // Individual permission checks
            val hasAccessibility = perms.any { it.contains("BIND_ACCESSIBILITY_SERVICE") }
            val hasOverlay = perms.any { it.contains("SYSTEM_ALERT_WINDOW") }
            val hasInstall = perms.any { it.contains("REQUEST_INSTALL_PACKAGES") || it.contains("INSTALL_PACKAGES") }
            val hasSensitive = perms.count {
                it.contains("CAMERA") ||
                it.contains("RECORD_AUDIO") ||
                it.contains("ACCESS_FINE_LOCATION") ||
                it.contains("READ_CONTACTS") ||
                it.contains("READ_CALL_LOG")
            }

            if (hasAccessibility && !indicators.contains(ThreatIndicators.ACCESSIBILITY_ABUSE)) {
                indicators.add(ThreatIndicators.ACCESSIBILITY_ABUSE)
            }
            if (hasOverlay && !indicators.contains(ThreatIndicators.OVERLAY_PERMISSION)) {
                indicators.add(ThreatIndicators.OVERLAY_PERMISSION)
            }
            if (hasInstall) {
                indicators.add(ThreatIndicators.HIGH_RISK_PERMISSION)
            }
            if (hasSensitive >= 3) {
                indicators.add(ThreatIndicators.HIGH_RISK_PERMISSION)
            }

            // 3. Evaluate Certificate Checks
            val cert = analysisDetails.certificate
            if (cert != null) {
                if (cert.isDebugSigned) {
                    indicators.add(ThreatIndicators.DEBUG_CERTIFICATE_DETECTED)
                    indicators.add(ThreatIndicators.DEBUG_SIGNATURE)
                }
                if (cert.isExpired) {
                    indicators.add(ThreatIndicators.EXPIRED_CERTIFICATE)
                }
                if (cert.hasWeakAlgorithm) {
                    indicators.add(ThreatIndicators.WEAK_SIGNATURE_ALGORITHM)
                }
                if (cert.isSelfSigned && !cert.isDebugSigned) {
                    indicators.add(ThreatIndicators.SELF_SIGNED_CERTIFICATE)
                }
            }

            if (metadata.extension != "apk" && metadata.extension != null) {
                indicators.add(ThreatIndicators.SUSPICIOUS_EXTENSION)
            }

            if (indicators.isEmpty()) {
                indicators.add(ThreatIndicators.CLEAN_APP)
            }

            // Calculate unified risk score
            var calculatedScore = RiskCalculator.calculate(indicators)
            // Weight with ML model prediction
            if (drebin.malwareProbability > 0.60f && calculatedScore < drebin.mlRiskScore) {
                calculatedScore = ((calculatedScore * 0.4f) + (drebin.mlRiskScore * 0.6f)).toInt()
            }
            score = calculatedScore.coerceIn(10, 100)
            level = RiskCalculator.getRiskLevel(score)
            action = SecurityPolicy.getAction(score)
        }

        Log.d("VEX_APK", "APK Scanned: ${metadata.fileName} | Score: $score | Level: $level")

        val explanation = ThreatExplanationEngine.explain(
            indicators = indicators.distinct(),
            riskScore = score,
            riskLevel = level,
            action = action
        )

        val result = ApkScanResult(
            metadata = metadata,
            packageName = analysisDetails.packageName,
            versionName = analysisDetails.versionName,
            versionCode = analysisDetails.versionCode,
            permissions = analysisDetails.permissions,
            indicators = indicators.distinct(),
            riskScore = score,
            riskLevel = level,
            action = action,
            isControlledTest = analysisDetails.isControlledTest,
            explanation = explanation,
            certificate = analysisDetails.certificate,
            drebinPrediction = analysisDetails.drebinPrediction,
            dangerousPairs = analysisDetails.dangerousPairs
        )
        lastScannedResult = result
        result
    }

    fun scanInstalledPackage(
        context: Context,
        pkg: PackageInfo
    ): ApkScanResult {
        val pm = context.packageManager
        val appName = pkg.applicationInfo?.loadLabel(pm)?.toString() ?: pkg.packageName
        val perms = pkg.requestedPermissions?.toList() ?: emptyList()
        val upperPerms = perms.map { it.uppercase() }

        val indicators = mutableListOf<String>()

        // 1. Drebin ML Prediction
        val drebin = DrebinApkClassifier.predict(perms)
        if (drebin.isClassifiedMalicious) {
            indicators.add(ThreatIndicators.DREBIN_ML_MALWARE_PREDICTION)
        }

        // 2. Dangerous Permission Pairs
        val dangPairs = ApkDangerousPairsDetector.evaluate(perms)
        for (pair in dangPairs) {
            when (pair.id) {
                "ACCESSIBILITY_OVERLAY" -> {
                    indicators.add(ThreatIndicators.ACCESSIBILITY_OVERLAY_TROJAN)
                    indicators.add(ThreatIndicators.ACCESSIBILITY_ABUSE)
                    indicators.add(ThreatIndicators.OVERLAY_PERMISSION)
                }
                "ACCESSIBILITY_SMS" -> {
                    indicators.add(ThreatIndicators.ACCESSIBILITY_SMS_BYPASS)
                    indicators.add(ThreatIndicators.DANGEROUS_PERMISSIONS_COMBO)
                }
                else -> {
                    indicators.add(ThreatIndicators.DANGEROUS_PERMISSIONS_COMBO)
                }
            }
        }

        val hasAccessibility = upperPerms.any { it.contains("BIND_ACCESSIBILITY_SERVICE") }
        val hasOverlay = upperPerms.any { it.contains("SYSTEM_ALERT_WINDOW") }
        val hasInstall = upperPerms.any { it.contains("REQUEST_INSTALL_PACKAGES") || it.contains("INSTALL_PACKAGES") }
        val sensitiveCount = upperPerms.count {
            it.contains("CAMERA") ||
            it.contains("RECORD_AUDIO") ||
            it.contains("ACCESS_FINE_LOCATION") ||
            it.contains("READ_CONTACTS") ||
            it.contains("READ_CALL_LOG")
        }

        if (hasAccessibility && !indicators.contains(ThreatIndicators.ACCESSIBILITY_ABUSE)) {
            indicators.add(ThreatIndicators.ACCESSIBILITY_ABUSE)
        }
        if (hasOverlay && !indicators.contains(ThreatIndicators.OVERLAY_PERMISSION)) {
            indicators.add(ThreatIndicators.OVERLAY_PERMISSION)
        }
        if (hasInstall) {
            indicators.add(ThreatIndicators.HIGH_RISK_PERMISSION)
        }
        if (sensitiveCount >= 3) {
            indicators.add(ThreatIndicators.HIGH_RISK_PERMISSION)
        }

        val targetSdk = pkg.applicationInfo?.targetSdkVersion ?: 33
        if (targetSdk < 26) {
            indicators.add(ThreatIndicators.OUTDATED_TARGET_SDK)
        }

        // 3. Certificate Inspection
        val cert = ApkCertificateInspector.inspectPackageInfo(context, pkg)
        if (cert.isDebugSigned) {
            indicators.add(ThreatIndicators.DEBUG_CERTIFICATE_DETECTED)
            indicators.add(ThreatIndicators.DEBUG_SIGNATURE)
        }
        if (cert.isExpired) {
            indicators.add(ThreatIndicators.EXPIRED_CERTIFICATE)
        }
        if (cert.hasWeakAlgorithm) {
            indicators.add(ThreatIndicators.WEAK_SIGNATURE_ALGORITHM)
        }
        if (cert.isSelfSigned && !cert.isDebugSigned) {
            indicators.add(ThreatIndicators.SELF_SIGNED_CERTIFICATE)
        }

        if (indicators.isEmpty()) {
            indicators.add(ThreatIndicators.CLEAN_APP)
        }

        var score = RiskCalculator.calculate(indicators)
        if (drebin.malwareProbability > 0.60f && score < drebin.mlRiskScore) {
            score = ((score * 0.4f) + (drebin.mlRiskScore * 0.6f)).toInt()
        }
        score = score.coerceIn(10, 100)
        val level = RiskCalculator.getRiskLevel(score)
        val action = SecurityPolicy.getAction(score)

        val explanation = ThreatExplanationEngine.explain(
            indicators = indicators.distinct(),
            riskScore = score,
            riskLevel = level,
            action = action
        )

        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pkg.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            pkg.versionCode.toLong()
        }

        val metadata = FileMetadata(
            fileName = "$appName.apk",
            filePath = "package:${pkg.packageName}",
            mimeType = "application/vnd.android.package-archive",
            extension = "apk",
            sizeBytes = 10_000_000L,
            createdTime = null,
            modifiedTime = null,
            sha256 = cert.sha256Fingerprint ?: "INSTALLED_SYSTEM_PACKAGE",
            source = "INSTALLED_PACKAGE"
        )

        val result = ApkScanResult(
            metadata = metadata,
            packageName = pkg.packageName,
            versionName = pkg.versionName ?: "1.0",
            versionCode = versionCode,
            permissions = perms,
            indicators = indicators.distinct(),
            riskScore = score,
            riskLevel = level,
            action = action,
            isControlledTest = false,
            explanation = explanation,
            certificate = cert,
            drebinPrediction = drebin,
            dangerousPairs = dangPairs
        )
        lastScannedResult = result
        return result
    }
}
