package com.vex.security.security

import android.content.Context
import com.vex.security.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PolicyManager {

    private const val PREFS = "vex_policies"

    private val defaults = mapOf(
        "CRITICAL" to "QUARANTINE",
        "HIGH" to "BLOCK",
        "MEDIUM" to "NOTIFY",
        "LOW" to "TAG",
        "SAFE" to "ALLOW",
        // Backward compatibility
        "QUARANTINE" to "QUARANTINE",
        "BLOCK" to "BLOCK",
        "WARN" to "NOTIFY"
    )

    suspend fun refresh(context: Context) {
        try {
            val policies = withContext(Dispatchers.IO) {
                ApiClient.apiService.getPolicies()
            }

            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val editor = prefs.edit()

            policies.forEach { (level, action) ->
                val normalizedKey = RiskCalculator.normalizeRiskLevel(level)
                editor.putString(normalizedKey, action.uppercase())
                editor.putString(level.uppercase(), action.uppercase())
            }

            editor.apply()
        } catch (_: Exception) {
            // Keep last known policy
        }
    }

    fun getAction(
        context: Context,
        riskLevel: String
    ): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val normalized = RiskCalculator.normalizeRiskLevel(riskLevel)

        return prefs.getString(
            normalized,
            prefs.getString(riskLevel.uppercase(), defaults[normalized] ?: "ALLOW")
        ) ?: "ALLOW"
    }
}
