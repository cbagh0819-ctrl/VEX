package com.vex.security.model

data class ApkScanResult(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val permissions: List<String>,
    val riskScore: Int,
    val riskLevel: String,
    val isMalicious: Boolean,
    val indicators: List<String>
)
