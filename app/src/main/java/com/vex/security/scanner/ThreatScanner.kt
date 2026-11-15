package com.vex.security.scanner

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log

data class ScannedThreat(
    val packageName: String,
    val appName: String,
    val riskScore: Int,
    val isSystemApp: Boolean,
    val suspiciousPermissions: List<String>
)

object ThreatScanner {

    private const val TAG = "VEX_THREAT_SCANNER"

    fun scanInstalledApps(context: Context): List<ScannedThreat> {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        val threats = mutableListOf<ScannedThreat>()

        val highRiskPermissions = setOf(
            "android.permission.RECEIVE_SMS",
            "android.permission.READ_SMS",
            "android.permission.RECORD_AUDIO",
            "android.permission.CAMERA",
            "android.permission.READ_CALL_LOG",
            "android.permission.ANSWER_PHONE_CALLS"
        )

        for (pkg in packages) {
            val appInfo = pkg.applicationInfo ?: continue
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            if (isSystem) continue // Skip core OS system packages

            val requested = pkg.requestedPermissions?.toList() ?: emptyList()
            val matchedRisks = requested.filter { it in highRiskPermissions }

            if (matchedRisks.size >= 2) {
                val score = (matchedRisks.size * 20).coerceAtMost(100)
                threats.add(
                    ScannedThreat(
                        packageName = pkg.packageName,
                        appName = appInfo.loadLabel(pm).toString(),
                        riskScore = score,
                        isSystemApp = false,
                        suspiciousPermissions = matchedRisks
                    )
                )
            }
        }

        Log.d(TAG, "Completed app threat scan. Flagged ${threats.size} high-risk apps.")
        return threats
    }
}
