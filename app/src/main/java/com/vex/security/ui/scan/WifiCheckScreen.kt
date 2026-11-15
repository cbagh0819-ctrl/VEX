package com.vex.security.ui.scan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.repository.ThreatRepository
import com.vex.security.ui.theme.*
import com.vex.security.utils.DeviceIdentity
import com.vex.security.wifi.WifiSecurityManager
import com.vex.security.wifi.WifiSecurityReport
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiCheckScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isScanning by remember { mutableStateOf(false) }
    var report by remember { mutableStateOf<WifiSecurityReport?>(null) }

    val runScan = {
        scope.launch {
            isScanning = true
            delay(600) // Brief aesthetic scan transition
            val result = WifiSecurityManager.analyzeWifiNetwork(context)
            report = result
            isScanning = false

            // Report to Room DB and SOC
            try {
                ThreatRepository.sendThreatEvent(
                    context = context,
                    deviceId = DeviceIdentity.getDeviceId(context),
                    threatType = "WIFI",
                    riskScore = result.riskScore,
                    riskLevel = result.riskLevel,
                    action = result.action,
                    status = if (result.riskLevel == "SAFE") "RESOLVED" else "OPEN",
                    indicators = listOf("WIFI_${result.riskLevel}", result.securityProtocol),
                    source = "ANDROID_WIFI_SCAN",
                    explanation = result.summary,
                    recommendation = result.recommendations.firstOrNull() ?: "Network evaluated secure"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        runScan()
    }

    val currentReport = report
    val levelColor = when (currentReport?.riskLevel) {
        "QUARANTINE" -> VexCrimson
        "WARN" -> VexAmber
        "BLOCK" -> VexOrange
        else -> VexEmerald
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        VexBackgroundTop,
                        VexBackgroundMid,
                        VexVoid
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
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
                            .border(1.dp, VexCardBorder, CircleShape)
                            .background(VexCardElevated, CircleShape)
                            .clickable { onBackPressed() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = VexCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "WI-FI SECURITY CHECK",
                            color = VexCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Inspect encryption, rogue APs & DNS integrity",
                            color = VexTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Rescan Icon Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, VexCyan, CircleShape)
                        .background(VexCardElevated, CircleShape)
                        .clickable { runScan() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = VexCyan,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Re-scan",
                            tint = VexCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Hero Wi-Fi Radar Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, levelColor.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = VexCardDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(VexNavIndicator, CircleShape)
                            .border(1.5.dp, levelColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (currentReport?.isConnected == true) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = "Wi-Fi Status",
                            tint = levelColor,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentReport?.ssid ?: "Scanning Network...",
                        color = VexTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .background(levelColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .border(1.dp, levelColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = currentReport?.summary ?: "Analyzing encryption & gateway...",
                            color = levelColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = VexCardBorder.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("RISK SCORE", color = VexTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${currentReport?.riskScore ?: 10} / 100",
                                color = levelColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SIGNAL QUALITY", color = VexTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${currentReport?.signalStrengthPercent ?: 85}%",
                                color = VexTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("LINK SPEED", color = VexTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${currentReport?.linkSpeedMbps ?: 0} Mbps",
                                color = VexCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4-Card Forensic Parameters Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        WifiMetricCard(
                            icon = Icons.Default.Lock,
                            label = "ENCRYPTION PROTOCOL",
                            value = currentReport?.securityProtocol ?: "WPA2/WPA3",
                            statusColor = levelColor
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        WifiMetricCard(
                            icon = Icons.Default.Router,
                            label = "ROUTER GATEWAY",
                            value = currentReport?.gatewayIp ?: "192.168.1.1",
                            statusColor = VexCyan
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        WifiMetricCard(
                            icon = Icons.Default.Dns,
                            label = "DNS INTEGRITY",
                            value = if (currentReport?.isDnsSecure == true) "Secure / Trusted" else "Suspicious DNS",
                            statusColor = if (currentReport?.isDnsSecure == true) VexEmerald else VexAmber
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        WifiMetricCard(
                            icon = Icons.Default.Speed,
                            label = "FREQUENCY BAND",
                            value = String.format(java.util.Locale.US, "%.1f GHz Band", currentReport?.frequencyGhz ?: 2.4),
                            statusColor = VexTextSecondary
                        )
                    }
                }
            }
        }

        // Security Recommendations Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VexCardBorderSubtle, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = VexCardDark)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = VexCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Security Audit & Recommendations",
                            color = VexTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    currentReport?.recommendations?.forEach { rec ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = VexEmerald,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Text(
                                text = rec,
                                color = VexTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        // Action Re-Scan Button
        item {
            Button(
                onClick = { runScan() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VexCyan,
                    contentColor = VexVoid
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Scan",
                        tint = VexVoid,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isScanning) "ANALYZING WI-FI PACKETS..." else "RUN DEEP WI-FI AUDIT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun WifiMetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    statusColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .border(1.dp, VexCardBorderSubtle, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = VexCardDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = VexTextDim,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = value,
                color = VexTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
