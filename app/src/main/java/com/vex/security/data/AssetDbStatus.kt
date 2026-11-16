package com.vex.security.data

import android.content.Context
import java.io.File

object AssetDbStatus {
    fun isPhishingDbReady(context: Context) =
        File(context.getDatabasePath("phishing_domains.db").path).exists()

    fun isSpamDbReady(context: Context) =
        File(context.getDatabasePath("spam_numbers.db").path).exists()

    fun isUsbDbReady(context: Context) =
        File(context.getDatabasePath("usb_devices.db").path).exists()

    fun isMalwareHashDbReady(context: Context) =
        File(context.getDatabasePath("malware_hashes.db").path).exists()
}
