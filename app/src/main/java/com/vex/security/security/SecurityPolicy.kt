package com.vex.security.security

import android.content.Context

object SecurityPolicy {

    const val PREFS_NAME = "vex_admin_policy_prefs"

    fun getAction(riskScore: Int): String {
        return when (riskScore) {
            in 0..19 -> "ALLOW"
            in 20..39 -> "TAG"
            in 40..59 -> "NOTIFY"
            in 60..79 -> "BLOCK"
            else -> "QUARANTINE"
        }
    }

    fun getActionForLevel(riskLevel: String?): String {
        return when (riskLevel?.uppercase()?.trim()) {
            "CRITICAL", "QUARANTINE" -> "QUARANTINE"
            "HIGH", "BLOCK" -> "BLOCK"
            "MEDIUM", "WARN" -> "NOTIFY"
            "LOW" -> "TAG"
            "SAFE" -> "ALLOW"
            else -> "ALLOW"
        }
    }

    fun getEnforcedAction(context: Context?, riskLevel: String?): String {
        if (context == null) return getActionForLevel(riskLevel)
        val normalized = RiskCalculator.normalizeRiskLevel(riskLevel)
        return PolicyManager.getAction(context, normalized)
    }
}
