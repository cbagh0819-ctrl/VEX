package com.vex.security.ui.scan

import android.content.BroadcastReceiver
import android.content.Context
import android.hardware.usb.UsbDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.notification.AlarmPlayer
import com.vex.security.notification.SecurityNotification
import com.vex.security.repository.ThreatRepository
import com.vex.security.usb.UsbAnalysisResult
import com.vex.security.usb.UsbSecurityManager
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsbOtgCheckScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var connectedDevices by remember { mutableStateOf<List<UsbDevice>>(emptyList()) }
    var deviceReports by remember { mutableStateOf<Map<String, UsbAnalysisResult>>(emptyMap()) }
    var alarmActive by remember { mutableStateOf(false) }
    var lastScannedAt by remember { mutableStateOf(System.currentTimeMillis()) }

    fun refreshUsbDevices() {
        val devices = UsbSecurityManager.getConnectedDevices(context)
        val reports = mutableMapOf<String, UsbAnalysisResult>()
        var foundDangerous = false

        for (device in devices) {
            val report = UsbSecurityManager.analyzeDevice(device)
            reports[device.deviceName] = report

            if (report.riskLevel == "DANGEROUS" || report.riskScore >= 70) {
                foundDangerous = true
                scope.launch {
                    try {
                        ThreatRepository.sendThreatEvent(
                            context = context,
                            deviceId = DeviceIdentity.getDeviceId(context),
                            threatType = "USB_HARDWARE_ATTACK",
                            riskScore = report.riskScore,
                            riskLevel = report.riskLevel,
                            action = "BLOCK",
                            status = "OPEN",
                            indicators = report.findings,
                            source = "USB_OTG_SHIELD",
                            explanation = "Malicious USB hardware signature detected (${report.deviceName})",
                            recommendation = report.recommendation
                        )
                    } catch (_: Exception) {}
                }
            }
        }

        connectedDevices = devices
        deviceReports = reports
        lastScannedAt = System.currentTimeMillis()

        if (foundDangerous) {
            alarmActive = true
            AlarmPlayer.playAlarm(context)
            SecurityNotification.showHarmfulAlert(context, "Malicious USB Hardware Detected! Disconnect immediately.")
        }
    }

    // Register live USB hardware insertion/removal broadcast listener
    DisposableEffect(context) {
        refreshUsbDevices()

        val receiver: BroadcastReceiver = UsbSecurityManager.registerUsbReceiver(
            context = context,
            onDeviceAttached = {
                refreshUsbDevices()
            },
            onDeviceDetached = {
                refreshUsbDevices()
                if (alarmActive) {
                    AlarmPlayer.stopAlarm(context)
                    alarmActive = false
                }
            }
        )

        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
            if (alarmActive) {
                AlarmPlayer.stopAlarm(context)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF03141C),
                        Color(0xFF01080C),
                        Color(0xFF000305)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(50))
                            .background(Color(0xFF0A1F28), RoundedCornerShape(50))
                            .clickable { onBackPressed() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "USB / OTG HARDWARE SHIELD",
                            color = Color(0xFFFFFFFF),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "BadUSB, Rubber Ducky & HID Defense",
                            color = Color(0xFF80A0B0),
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(20.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(20.dp))
                        .clickable { refreshUsbDevices() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "SCAN",
                            color = Color(0xFF00FFE0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Alarm Warning Banner if active
        if (alarmActive) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFFF3358), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E0914))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alarm",
                                tint = Color(0xFFFF3358),
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text("ATTACK HARDWARE ALERT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Malicious USB device detected!", color = Color(0xFFFF88A0), fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = {
                                AlarmPlayer.stopAlarm(context)
                                alarmActive = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3358))
                        ) {
                            Text("SILENCE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live USB Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF00FFE0).copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(12.dp))
                                .background(Color(0xFF0A1F28), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Usb,
                                contentDescription = "USB",
                                tint = if (connectedDevices.isNotEmpty()) Color(0xFF00FFE0) else Color(0xFF80A0B0),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (connectedDevices.isEmpty()) "NO USB DEVICES ATTACHED" else "${connectedDevices.size} USB DEVICE(S) CONNECTED",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (connectedDevices.isEmpty()) "Host controller ready. Plug in an OTG cable/drive." else "Live hardware inspection running",
                                color = Color(0xFF80A0B0),
                                fontSize = 11.sp
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFF162A38), thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Active OTG Controller: READY", color = Color(0xFF00FFE0), fontSize = 11.sp)
                        Text("Signature DB: 5 Attack Profiles", color = Color(0xFF80A0B0), fontSize = 11.sp)
                    }
                }
            }
        }

        // Connected Device Details
        if (connectedDevices.isNotEmpty()) {
            items(connectedDevices) { device ->
                val report = deviceReports[device.deviceName] ?: UsbSecurityManager.analyzeDevice(device)
                val badgeColor = when (report.riskLevel) {
                    "DANGEROUS" -> Color(0xFFFF3358)
                    "SUSPICIOUS" -> Color(0xFFFFB800)
                    else -> Color(0xFF35E08A)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1924))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = device.deviceName.ifBlank { "Generic USB Peripheral" },
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "VID: ${String.format("0x%04X", device.vendorId)}  |  PID: ${String.format("0x%04X", device.productId)}",
                                    color = Color(0xFF00FFE0),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .border(1.dp, badgeColor, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = report.riskLevel,
                                    color = badgeColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFF162A38))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Device Class: ${device.deviceClass}", color = Color(0xFF80A0B0), fontSize = 11.sp)
                            Text("Interfaces: ${device.interfaceCount}", color = Color(0xFF80A0B0), fontSize = 11.sp)
                            Text("Risk Score: ${report.riskScore}/100", color = badgeColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (report.findings.isNotEmpty()) {
                            Text("Hardware Findings:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                report.findings.forEach { finding ->
                                    Text("• $finding", color = Color(0xFFA0C0D0), fontSize = 11.sp)
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF061018), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "🛡️ Recommendation: ${report.recommendation}",
                                color = Color(0xFFD0E0E8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF005566).copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1822))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Help",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "How to test USB / OTG Security",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Attach any USB drive, mouse, keyboard, or OTG accessory. VEX will instantly read the hardware vendor/product IDs and analyze interface descriptors for BadUSB keyboard injection or malicious firmware.",
                            color = Color(0xFF80A0B0),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
