package com.vex.security.telecom

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf

object BombShieldManager {

    private const val TAG = "VEX_BOMB_SHIELD"
    private val callTimestamps = mutableListOf<Long>()
    private val smsTimestamps = mutableListOf<Long>()
    private val perNumberCallTimestamps = mutableMapOf<String, MutableList<Long>>()

    // Threshold: more than 4 events in 10 seconds triggers DOS shield
    private const val WINDOW_MS = 10_000L
    private const val MAX_BURST = 4

    val isBombShieldActive = mutableStateOf(true)
    var callThreshold = 4

    fun init(context: Context) {
        Log.d(TAG, "Bomb Shield telecom defense engine initialized.")
    }

    @Synchronized
    fun evaluateCallEvent(context: Context, number: String?): Boolean {
        val now = System.currentTimeMillis()
        callTimestamps.removeAll { now - it > WINDOW_MS }
        callTimestamps.add(now)

        val isBlast = callTimestamps.size > MAX_BURST
        if (isBlast) {
            Log.w(TAG, "Call DOS blast detected! Count in window: ${callTimestamps.size} (Caller: $number)")
        }
        return isBlast
    }

    @Synchronized
    fun evaluateSmsEvent(context: Context, sender: String?, body: String?): Boolean {
        val now = System.currentTimeMillis()
        smsTimestamps.removeAll { now - it > WINDOW_MS }
        smsTimestamps.add(now)

        val isBlast = smsTimestamps.size > MAX_BURST
        if (isBlast) {
            Log.w(TAG, "SMS OTP DOS blast detected! Count in window: ${smsTimestamps.size} (Sender: $sender)")
        }
        return isBlast
    }

    @Synchronized
    fun shouldBlockCall(number: String): Boolean {
        if (!isBombShieldActive.value) return false
        val recentCalls = perNumberCallTimestamps.getOrPut(number) { mutableListOf() }
        val now = System.currentTimeMillis()
        recentCalls.removeAll { now - it >= 60_000 }
        recentCalls.add(now)
        return recentCalls.size >= callThreshold
    }
}
