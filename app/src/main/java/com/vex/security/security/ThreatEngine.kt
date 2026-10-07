package com.vex.security.security

data class ThreatAnalysis(
    val riskScore: Int,
    val riskLevel: String,
    val action: String
)

object ThreatEngine {

    fun analyze(indicators: List<String>): ThreatAnalysis {

        val score = RiskCalculator.calculate(indicators)
        val level = RiskCalculator.getRiskLevel(score)
        val action = SecurityPolicy.getAction(score)

        return ThreatAnalysis(
            riskScore = score,
            riskLevel = level,
            action = action
        )
    }
}
