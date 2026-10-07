package com.vex.security.model

data class Device(
    val deviceId: String,
    val deviceName: String,
    val androidVersion: String,
    val model: String,
    val registeredAt: Long
)
