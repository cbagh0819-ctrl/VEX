package com.vex.security.scanner

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileOutputStream

data class ApkAnalysisDetails(
    val packageName: String?,
    val versionName: String?,
    val versionCode: Long,
    val permissions: List<String>,
    val isControlledTest: Boolean,
    val testScore: Int,
    val testLevel: String?,
    val testAction: String?,
    val riskScore: Int = 0,
    val riskLevel: String = "SAFE",
    val dangerousFindings: List<String> = emptyList()
)

object ApkAnalyzer {

    private val dangerousPermissions = setOf(
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.READ_CALL_LOG",
        "android.permission.PROCESS_OUTGOING_CALLS",
        "android.permission.READ_CONTACTS",
        "android.permission.RECORD_AUDIO",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.CAMERA",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.SYSTEM_ALERT_WINDOW",
        "android.permission.REQUEST_INSTALL_PACKAGES",
        "android.permission.BIND_ACCESSIBILITY_SERVICE",
        "android.permission.BIND_DEVICE_ADMIN",
        "android.permission.GET_ACCOUNTS",
        "android.permission.USE_BIOMETRIC",
        "android.permission.CHANGE_NETWORK_STATE",
        "android.permission.DISABLE_KEYGUARD"
    )

    private val criticalCombinations = listOf(
        setOf(
            "android.permission.BIND_ACCESSIBILITY_SERVICE",
            "android.permission.SYSTEM_ALERT_WINDOW"
        ),
        setOf(
            "android.permission.READ_SMS",
            "android.permission.SEND_SMS",
            "android.permission.RECEIVE_SMS"
        ),
        setOf(
            "android.permission.RECORD_AUDIO",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_CONTACTS"
        )
    )

    fun scorePermissions(permissions: List<String>): Triple<Int, String, List<String>> {
        val dangerous = permissions.filter { it in dangerousPermissions }
        val findings = mutableListOf<String>()
        var score = 0

        // Base score from dangerous permission count
        score += (dangerous.size * 8).coerceAtMost(60)

        // Critical combinations
        for (combo in criticalCombinations) {
            if (permissions.containsAll(combo)) {
                score += 25
                findings.add("🔴 Dangerous permission combination: ${combo.joinToString(" + ") { it.substringAfterLast('.') }}")
            }
        }

        dangerous.forEach { perm ->
            findings.add("⚠️ ${perm.substringAfterLast('.')}")
        }

        score = score.coerceAtMost(100)
        val level = when {
            score >= 70 -> "DANGEROUS"
            score >= 40 -> "SUSPICIOUS"
            score >= 15 -> "LOW_RISK"
            else        -> "SAFE"
        }
        return Triple(score, level, findings)
    }

    fun analyze(
        context: Context,
        apkUri: Uri
    ): ApkAnalysisDetails {
        var tempFile: File? = null
        try {
            val resolver = context.contentResolver
            tempFile = File.createTempFile("vex_apk_", ".apk", context.cacheDir)
            resolver.openInputStream(apkUri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            } ?: run {
                Log.e("VEX_APK", "Failed to open input stream for URI: $apkUri")
            }

            val packageManager = context.packageManager
            val flags = PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
            val packageInfo: PackageInfo = packageManager.getPackageArchiveInfo(
                tempFile.absolutePath,
                flags
            ) ?: run {
                Log.e("VEX_APK", "Failed to get PackageArchiveInfo for path: ${tempFile.absolutePath}")
                return ApkAnalysisDetails(null, null, 0L, emptyList(), false, -1, null, null)
            }

            val packageName = packageInfo.packageName
            val versionName = packageInfo.versionName
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }

            val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
            val (scoredRisk, scoredLevel, findings) = scorePermissions(permissions)

            val appInfo = packageInfo.applicationInfo
            val metaData = appInfo?.metaData

            val isFixture = metaData?.getBoolean("vex.test.fixture", false) ?: false
            val testScore = metaData?.getInt("vex.test.score", -1) ?: -1
            val testLevel = metaData?.getString("vex.test.level")
            val testAction = metaData?.getString("vex.test.action")

            Log.d("VEX_APK", "Selected URI: $apkUri")
            Log.d("VEX_APK", "Package: $packageName")
            Log.d("VEX_APK", "Scored risk: $scoredRisk ($scoredLevel)")

            return ApkAnalysisDetails(
                packageName = packageName,
                versionName = versionName,
                versionCode = versionCode,
                permissions = permissions,
                isControlledTest = isFixture,
                testScore = testScore,
                testLevel = testLevel,
                testAction = testAction,
                riskScore = scoredRisk,
                riskLevel = scoredLevel,
                dangerousFindings = findings
            )
        } catch (e: Exception) {
            Log.e("VEX_APK", "Error analyzing APK: ${e.message}", e)
            return ApkAnalysisDetails(null, null, 0L, emptyList(), false, -1, null, null)
        } finally {
            try {
                tempFile?.delete()
            } catch (_: Exception) {}
        }
    }

    fun getPermissions(
        context: Context,
        apkUri: Uri
    ): List<String> {
        return analyze(context, apkUri).permissions
    }
}
