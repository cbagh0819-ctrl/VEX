package com.vex.security.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import java.util.Locale

data class UsbAnalysisResult(
    val deviceName: String,
    val vendorId: Int,
    val productId: Int,
    val deviceClass: Int,
    val interfaceCount: Int,
    val riskLevel: String,     // SAFE / SUSPICIOUS / DANGEROUS
    val riskScore: Int,        // 0-100
    val findings: List<String>,
    val recommendation: String
)

object UsbSecurityManager {

    // Known attack device vendor+product IDs
    private val knownAttackDevices = mapOf(
        (0x03EB to 0x2042) to "Hak5 USB Rubber Ducky",
        (0x0665 to 0x5161) to "USB Killer Device",
        (0x1D6B to 0x0104) to "Generic BadUSB HID",
        (0x16C0 to 0x05DC) to "Teensy BadUSB",
        (0x2341 to 0x0043) to "Arduino HID Attack"
    )

    // USB Device Class codes
    private const val CLASS_HID = 3        // Keyboard/Mouse
    private const val CLASS_MASS_STORAGE = 8
    private const val CLASS_HUB = 9
    private const val CLASS_VENDOR_SPECIFIC = 255

    fun analyzeDevice(device: UsbDevice): UsbAnalysisResult {
        val findings = mutableListOf<String>()
        var riskScore = 0

        val vendorId = device.vendorId
        val productId = device.productId
        val deviceClass = device.deviceClass
        val interfaceCount = device.interfaceCount

        // Check 1: Known attack device
        val attackMatch = knownAttackDevices[vendorId to productId]
        if (attackMatch != null) {
            findings.add("🔴 Known attack device: $attackMatch")
            riskScore += 90
        }

        // Check 2: HID device (potential BadUSB/Rubber Ducky)
        var hasHID = false
        var hasMassStorage = false
        for (i in 0 until interfaceCount) {
            val iface = device.getInterface(i)
            if (iface.interfaceClass == CLASS_HID) hasHID = true
            if (iface.interfaceClass == CLASS_MASS_STORAGE) hasMassStorage = true
        }

        if (deviceClass == CLASS_HID || hasHID) {
            findings.add("⚠️ HID Device detected — can inject keyboard/mouse input")
            riskScore += 40
        }

        // Check 3: Combo HID + Storage = classic BadUSB
        if (hasHID && hasMassStorage) {
            findings.add("🔴 HID + Mass Storage combined — classic BadUSB signature")
            riskScore += 30
        }

        // Check 4: Vendor-specific class (unknown protocol)
        if (deviceClass == CLASS_VENDOR_SPECIFIC) {
            findings.add("⚠️ Vendor-specific USB class — unknown protocol")
            riskScore += 20
        }

        // Check 5: Unknown vendor ID (not in common hardware lists)
        if (vendorId == 0 || vendorId == 0xFFFF) {
            findings.add("⚠️ Unknown/unregistered USB vendor ID")
            riskScore += 25
        }

        riskScore = riskScore.coerceAtMost(100)

        val riskLevel = when {
            riskScore >= 70 -> "DANGEROUS"
            riskScore >= 35 -> "SUSPICIOUS"
            else            -> "SAFE"
        }

        val recommendation = when (riskLevel) {
            "DANGEROUS"  -> "Disconnect immediately. This device matches known attack hardware patterns."
            "SUSPICIOUS" -> "Use with caution. This device has unusual characteristics."
            else         -> "Device appears safe. Standard USB storage or peripheral."
        }

        return UsbAnalysisResult(
            deviceName = device.deviceName,
            vendorId = vendorId,
            productId = productId,
            deviceClass = deviceClass,
            interfaceCount = interfaceCount,
            riskLevel = riskLevel,
            riskScore = riskScore,
            findings = findings,
            recommendation = recommendation
        )
    }

    fun getConnectedDevices(context: Context): List<UsbDevice> {
        val usbManager = context.getSystemService(Context.USB_SERVICE)
            as? UsbManager ?: return emptyList()
        return usbManager.deviceList.values.toList()
    }

    fun registerUsbReceiver(
        context: Context,
        onDeviceAttached: (UsbDevice) -> Unit,
        onDeviceDetached: (UsbDevice) -> Unit
    ): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val device = if (android.os.Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra(
                        UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                } ?: return
                when (intent.action) {
                    UsbManager.ACTION_USB_DEVICE_ATTACHED -> onDeviceAttached(device)
                    UsbManager.ACTION_USB_DEVICE_DETACHED -> onDeviceDetached(device)
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        context.registerReceiver(receiver, filter)
        return receiver
    }

    fun init(context: Context) {
        // Registered for app-wide USB monitoring
    }
}
