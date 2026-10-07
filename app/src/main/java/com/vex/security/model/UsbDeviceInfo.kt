package com.vex.security.model

data class UsbDeviceInfo(
    val deviceName: String,
    val manufacturer: String?,
    val productName: String?,
    val serialNumber: String?,
    val vendorId: Int,
    val productId: Int,
    val connectedAt: Long,
    val storagePath: String?
)
