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
    val testAction: String?
)

object ApkAnalyzer {

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

            val appInfo = packageInfo.applicationInfo
            val metaData = appInfo?.metaData

            val isFixture = metaData?.getBoolean("vex.test.fixture", false) ?: false
            val testScore = metaData?.getInt("vex.test.score", -1) ?: -1
            val testLevel = metaData?.getString("vex.test.level")
            val testAction = metaData?.getString("vex.test.action")

            Log.d("VEX_APK", "Selected URI: $apkUri")
            Log.d("VEX_APK", "Package: $packageName")
            Log.d("VEX_APK", "Controlled fixture: $isFixture")
            Log.d("VEX_APK", "Metadata level: $testLevel")
            Log.d("VEX_APK", "Metadata score: $testScore")
            Log.d("VEX_APK", "Metadata action: $testAction")

            return ApkAnalysisDetails(
                packageName = packageName,
                versionName = versionName,
                versionCode = versionCode,
                permissions = permissions,
                isControlledTest = isFixture,
                testScore = testScore,
                testLevel = testLevel,
                testAction = testAction
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
