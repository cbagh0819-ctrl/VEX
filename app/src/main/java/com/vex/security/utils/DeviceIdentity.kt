package com.vex.security.utils

import android.content.Context
import java.util.UUID

object DeviceIdentity {

    private const val PREFS_NAME = "vex_identity"
    private const val DEVICE_ID_KEY = "device_id"

    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        var deviceId = prefs.getString(DEVICE_ID_KEY, null)

        if (deviceId == null) {
            deviceId = "VEX-" + UUID.randomUUID().toString()

            prefs.edit()
                .putString(DEVICE_ID_KEY, deviceId)
                .apply()
        }

        return deviceId
    }
}
