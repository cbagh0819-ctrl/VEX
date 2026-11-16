package com.vex.security.security

import android.content.Context
import android.util.Log
import com.vex.security.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PolicyManager {
    private const val PREFS_NAME = "vex_policies_pref"

    suspend fun refresh(context: Context) = withContext(Dispatchers.IO) {
        try {
            val policies = ApiClient.apiService.getPolicies()
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            for ((level, action) in policies) {
                editor.putString(level.uppercase(), action.uppercase())
            }
            editor.apply()
            Log.d("VEX_POLICY", "Policies refreshed: $policies")
        } catch (e: Exception) {
            // Load local fallback — silently use hardcoded defaults already defined in SecurityPolicy.kt
            Log.d("VEX_POLICY", "Using local offline policy defaults: ${e.message}")
        }
    }

    fun getAction(context: Context, level: String): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val defaultAction = SecurityPolicy.getAction(
            when (level.uppercase()) {
                "SAFE" -> 10
                "WARN" -> 35
                "BLOCK" -> 60
                else -> 90
            }
        )
        return prefs.getString(level.uppercase(), defaultAction) ?: defaultAction
    }
}
