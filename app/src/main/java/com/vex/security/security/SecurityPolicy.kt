package com.vex.security.security

object SecurityPolicy {

    fun getAction(riskScore: Int): String {
        return when {
            riskScore >= 80 -> "BLOCK"
            riskScore >= 60 -> "QUARANTINE"
            riskScore >= 30 -> "WARN"
            else -> "ALLOW"
        }
    }
}
