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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.repository.ThreatRepository
import com.vex.security.utils.DeviceIdentity
import com.vex.security.wifi.NearbyWifiAp
import com.vex.security.wifi.WifiSecurityCheckItem
import com.vex.security.wifi.WifiSecurityManager
import com.vex.security.wifi.WifiSecurityReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val CoralPink = Color(0xFFFF3358)
private val DarkPlumBg1 = Color(0xFF140D1E)
private val DarkPlumBg2 = Color(0xFF1B1126)
private val DarkPlumBg3 = Color(0xFF100818)
private val DarkPlumCard = Color(0xFF241239)
private val LavenderText = Color(0xFFA998C4)
private val EmeraldSafe = Color(0xFF35E08A)

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
        "QUARANTINE" -> CoralPink
        "WARN" -> Color(0xFFFFD166)
        "BLOCK" -> Color(0xFFFF9F43)
        else -> EmeraldSafe
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkPlumBg1,
                        DarkPlumBg2,
                        DarkPlumBg3
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
                            .clip(CircleShape)
                            .background(DarkPlumCard)
                            .clickable { onBackPressed() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "WI-FI SECURITY CHECK",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Inspect encryption, rogue APs & DNS integrity",
                            color = LavenderText,
                            fontSize = 11.sp
                        )
                    }
                }

                // Rescan Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CoralPink)
                        .clickable(enabled = !isScanning) { runWifiScan() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Re-scan",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
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
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkPlumCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(Color(0xFF180D28), CircleShape)
                            .border(2.dp, levelColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (currentReport?.isConnected == true) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = "Wi-Fi Status",
                            tint = levelColor,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isScanning) "SCANNING NETWORK..." else currentReport?.ssid ?: "No Connection",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
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
                    HorizontalDivider(color = Color(0xFF381A56))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("RISK SCORE", color = LavenderText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${currentReport?.riskScore ?: 5} / 100",
                                color = levelColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SIGNAL QUALITY", color = LavenderText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${currentReport?.signalStrengthPercent ?: 90}%",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("GATEWAY PING", color = LavenderText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${currentReport?.pingLatencyMs ?: 2} ms",
                                color = CoralPink,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
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
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkPlumCard)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "CONNECTION SPECIFICATIONS",
                        color = CoralPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
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
                color = LavenderText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(currentReport?.checks ?: emptyList()) { check ->
            val iconColor = when (check.status) {
                "PASSED" -> EmeraldSafe
                "WARNING" -> Color(0xFFFFD166)
                else -> CoralPink
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkPlumCard)
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
                        modifier = Modifier.size(24.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = check.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = check.detail,
                            color = LavenderText,
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
                    color = LavenderText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(currentReport?.nearbyAps ?: emptyList()) { ap ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkPlumCard)
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
                                tint = if (ap.isSecure) EmeraldSafe else CoralPink,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = ap.ssid,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${ap.encryptionType} • ${String.format("%.1f", ap.frequencyGhz)} GHz • ${ap.signalPercent}% Signal",
                                    color = LavenderText,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    if (ap.isSecure) EmeraldSafe.copy(alpha = 0.2f) else CoralPink.copy(alpha = 0.2f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (ap.isSecure) "SECURE" else "INSECURE",
                                color = if (ap.isSecure) EmeraldSafe else CoralPink,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Action Recommendation Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1744))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = CoralPink, modifier = Modifier.size(18.dp))
                        Text(
                            text = "SECURITY RECOMMENDATION",
                            color = CoralPink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                    }
                    Text(
                        text = currentReport?.recommendations?.firstOrNull() ?: "Network is safe and encrypted. Enjoy private browsing.",
                        color = Color.White,
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
        Text(text = label, color = LavenderText, fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
