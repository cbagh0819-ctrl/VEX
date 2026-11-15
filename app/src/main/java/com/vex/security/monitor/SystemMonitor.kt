package com.vex.security.monitor

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.util.Log

data class SystemStatus(
    val totalMemoryBytes: Long,
    val availableMemoryBytes: Long,
    val isLowMemory: Boolean,
    val batteryPercentage: Int,
    val isCharging: Boolean
)

object SystemMonitor {

    private const val TAG = "VEX_SYSTEM_MONITOR"
    private var isMonitoring = false

    fun start(context: Context) {
        if (isMonitoring) return
        isMonitoring = true
        Log.d(TAG, "SystemMonitor started: tracking real-time device health and memory.")
    }

    fun stop(context: Context) {
        isMonitoring = false
        Log.d(TAG, "SystemMonitor stopped.")
    }

    fun getSystemStatus(context: Context): SystemStatus {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memoryInfo)

        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val batteryPct = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
        val isCharging = bm?.isCharging ?: false

        return SystemStatus(
            totalMemoryBytes = memoryInfo.totalMem,
            availableMemoryBytes = memoryInfo.availMem,
            isLowMemory = memoryInfo.lowMemory,
            batteryPercentage = batteryPct,
            isCharging = isCharging
        )
    }
}
