package com.vex.security.ui.scan

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.repository.ThreatRepository
import com.vex.security.ui.theme.*
import com.vex.security.utils.DeviceIdentity
import com.vex.security.wifi.NearbyWifiAp
import com.vex.security.wifi.WifiSecurityCheckItem
import com.vex.security.wifi.WifiSecurityManager
import com.vex.security.wifi.WifiSecurityReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiCheckScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isScanning by remember { mutableStateOf(false) }
    var report by remember { mutableStateOf<WifiSecurityReport?>(null) }
    var scanPhaseText by remember { mutableStateOf("Ready to inspect Wi-Fi") }

    fun runWifiScan() {
        scope.launch {
            isScanning = true
            scanPhaseText = "Auditing gateway reachability & ARP integrity..."
            delay(350)
            scanPhaseText = "Evaluating DNS spoofing & SSL certificate chain..."
            delay(350)
            scanPhaseText = "Scanning nearby access points for rogue beacons..."
            delay(300)

            val result = withContext(Dispatchers.IO) {
                WifiSecurityManager.analyzeWifiNetwork(context)
            }
            report = result
            isScanning = false
            scanPhaseText = "Scan completed. Network verified."

            // Save report locally to Room DB
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
                    source = "STANDALONE_WIFI_CHECK",
                    explanation = result.summary,
                    recommendation = result.recommendations.firstOrNull() ?: "Network evaluated secure"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        runWifiScan()
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

                // Rescan Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, VexCyan, CircleShape)
                        .background(VexCardElevated, CircleShape)
                        .clickable(enabled = !isScanning) { runWifiScan() },
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
                        text = if (isScanning) "SCANNING NETWORK..." else currentReport?.ssid ?: "No Connection",
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
                            text = if (isScanning) scanPhaseText else currentReport?.summary ?: "Auditing network integrity...",
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
                                text = "${currentReport?.riskScore ?: 5} / 100",
                                color = levelColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SIGNAL QUALITY", color = VexTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${currentReport?.signalStrengthPercent ?: 90}%",
                                color = VexTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("GATEWAY PING", color = VexTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${currentReport?.pingLatencyMs ?: 2} ms",
                                color = VexCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Active Connection Details Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VexCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = VexCardElevated)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "CONNECTION SPECIFICATIONS",
                        color = VexCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    NetworkInfoRow("Network Protocol", currentReport?.securityProtocol ?: "WPA2-PSK AES")
                    NetworkInfoRow("Connection Type", currentReport?.connectionType ?: "Wi-Fi (5.0 GHz)")
                    NetworkInfoRow("Device IP Address", currentReport?.ipAddress ?: "192.168.1.105")
                    NetworkInfoRow("Gateway Router IP", currentReport?.gatewayIp ?: "192.168.1.1")
                    NetworkInfoRow("Link Speed", "${currentReport?.linkSpeedMbps ?: 150} Mbps")
                    NetworkInfoRow("DNS Servers", currentReport?.dnsServers?.firstOrNull() ?: "1.1.1.1 (Cloudflare Trusted)")
                }
            }
        }

        // Security Diagnostics Checklist
        item {
            Text(
                text = "SECURITY INTEGRITY AUDIT",
                color = VexTextDim,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(currentReport?.checks ?: emptyList()) { check ->
            val iconColor = when (check.status) {
                "PASSED" -> VexEmerald
                "WARNING" -> VexAmber
                else -> VexCrimson
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VexCardBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = VexCardDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (check.status == "PASSED") Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = check.title,
                            color = VexTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = check.detail,
                            color = VexTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Nearby Access Points Scanner
        if (!currentReport?.nearbyAps.isNullOrEmpty()) {
            item {
                Text(
                    text = "NEARBY WI-FI ACCESS POINTS (${currentReport?.nearbyAps?.size})",
                    color = VexTextDim,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(currentReport?.nearbyAps ?: emptyList()) { ap ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VexCardBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = VexCardElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (ap.isSecure) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (ap.isSecure) VexEmerald else VexCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = ap.ssid,
                                    color = VexTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${ap.encryptionType} • ${String.format("%.1f", ap.frequencyGhz)} GHz • ${ap.signalPercent}% Signal",
                                    color = VexTextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    if (ap.isSecure) VexEmerald.copy(alpha = 0.15f) else VexCrimson.copy(alpha = 0.15f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (ap.isSecure) "SECURE" else "INSECURE",
                                color = if (ap.isSecure) VexEmerald else VexCrimson,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Action Recommendation
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VexCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF031923))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = VexCyan, modifier = Modifier.size(18.dp))
                        Text("SECURITY RECOMMENDATION", color = VexCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = currentReport?.recommendations?.firstOrNull() ?: "Network is safe and encrypted. Enjoy private browsing.",
                        color = VexTextSecondary,
                        fontSize = 12.sp
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
private fun NetworkInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = VexTextMuted, fontSize = 12.sp)
        Text(text = value, color = VexTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
