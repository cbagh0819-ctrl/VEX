package com.vex.security.security

import android.content.Context
import android.util.Log

object SecurityManager {

    private const val TAG = "VEX_SECURITY_MANAGER"

    fun enforcePolicy(context: Context) {
        val action = PolicyManager.getAction(context, "WARN")
        Log.d(TAG, "Enforcing active security policy. Default action: $action")
    }

    fun isFeaturePermitted(context: Context? = null, feature: String = ""): Boolean {
        return true
    }
}
