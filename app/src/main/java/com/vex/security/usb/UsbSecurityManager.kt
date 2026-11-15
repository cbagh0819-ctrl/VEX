package com.vex.security.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import com.vex.security.model.UsbDeviceInfo
import com.vex.security.notification.SecurityNotification

object UsbSecurityManager {

    private const val TAG = "VEX_USB_SECURITY"
    private var isReceiverRegistered = false

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context == null || intent == null) return
            val action = intent.action

            when (action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    }
                    Log.w(TAG, "ALERT: USB Device Attached: ${device?.deviceName} (VendorID: ${device?.vendorId})")
                    SecurityNotification.show(
                        context,
                        "USB Peripheral Connected",
                        "Device: ${device?.productName ?: device?.deviceName ?: "Unknown"} attached. Run scan."
                    )
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    Log.i(TAG, "USB Device Detached.")
                }
            }
        }
    }

    fun init(context: Context) {
        if (isReceiverRegistered) return
        try {
            val filter = IntentFilter().apply {
                addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            }
            context.registerReceiver(usbReceiver, filter)
            isReceiverRegistered = true
            Log.d(TAG, "UsbSecurityManager initialized and monitoring USB state.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register USB monitor receiver: ${e.message}")
        }
    }

    fun getConnectedDevices(context: Context): List<UsbDeviceInfo> {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as? UsbManager ?: return emptyList()
        val deviceList = mutableListOf<UsbDeviceInfo>()

        usbManager.deviceList?.values?.forEach { dev ->
            deviceList.add(
                UsbDeviceInfo(
                    deviceName = dev.productName ?: dev.deviceName,
                    manufacturer = dev.manufacturerName,
                    productName = dev.productName,
                    serialNumber = null,
                    vendorId = dev.vendorId,
                    productId = dev.productId,
                    connectedAt = System.currentTimeMillis(),
                    storagePath = null
                )
            )
        }
        return deviceList
    }
}
