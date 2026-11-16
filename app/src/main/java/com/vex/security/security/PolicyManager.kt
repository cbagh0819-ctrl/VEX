package com.vex.security.security

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PolicyManager {
    private const val PREFS_NAME = "vex_policies_pref"

    suspend fun refresh(context: Context) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains("SAFE")) {
            prefs.edit()
                .putString("SAFE", "ALLOW")
                .putString("WARN", "WARN")
                .putString("BLOCK", "BLOCK")
                .putString("QUARANTINE", "QUARANTINE")
                .apply()
        }
        Log.d("VEX_POLICY", "Local offline policy active.")
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
