package com.vex.security.security

import android.content.Context

object ThreatEngine {

    fun analyze(indicators: List<String>): ThreatAnalysis {

        val score = RiskCalculator.calculate(indicators)
        val level = RiskCalculator.getRiskLevel(score)
        val action = SecurityPolicy.getAction(score)

        return ThreatAnalysis(
            score = score,
            level = level,
            action = action,
            indicators = indicators
        )
    }

    fun analyze(indicators: List<String>, context: Context?): ThreatAnalysis {
        val score = RiskCalculator.calculate(indicators)
        val level = RiskCalculator.getRiskLevel(score)
        val action = if (context != null) {
            PolicyManager.getAction(context, level)
        } else {
            SecurityPolicy.getAction(score)
        }

        return ThreatAnalysis(
            score = score,
            level = level,
            action = action,
            indicators = indicators
        )
    }

    fun analyze(context: Context, indicators: List<String>): ThreatAnalysis {
        return analyze(indicators, context)
    }
}

data class ThreatAnalysis(
    val score: Int,
    val level: String,
    val action: String,
    val indicators: List<String>
) {
    val riskScore: Int get() = score
    val riskLevel: String get() = level
    val explanation: ThreatExplanation by lazy {
        ThreatExplanationEngine.explain(
            indicators = indicators,
            riskScore = score,
            riskLevel = level,
            action = action
        )
    }
}
