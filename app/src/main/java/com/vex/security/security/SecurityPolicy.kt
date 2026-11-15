package com.vex.security.security

object SecurityPolicy {

    const val PREFS_NAME = "vex_admin_policy_prefs"

    fun getAction(riskScore: Int): String {
        return when (riskScore) {
            in 0..24 -> "ALLOW"
            in 25..49 -> "WARN"
            in 50..74 -> "BLOCK"
            else -> "QUARANTINE"
        }
    }
}
