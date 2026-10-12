package com.vex.security.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.repository.SocRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun formatMetric(value: Any?): String {
    if (value == null) return "0"
    val str = value.toString().trim()
    return try {
        val num = str.toDouble()
        if (num == num.toLong().toDouble()) {
            num.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.1f", num)
        }
    } catch (e: Exception) {
        str
    }
}

@Composable
fun SocDashboardScreen() {
    val scope = rememberCoroutineScope()
    var summary by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }

    val loadData = {
        scope.launch {
            loading = true
            summary = SocRepository.getSummary()
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            val data = SocRepository.getSummary()
            summary = data
            loading = false
            delay(3000)
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SOC OPERATIONS CENTER",
                        color = Color(0xFF00FFE0),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Real-time threat monitoring & telemetry",
                        color = Color(0xFF80A0B0),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(20.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(20.dp))
                        .clickable { loadData() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Refresh",
                            color = Color(0xFF00FFE0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (loading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00FFE0))
                }
            }
        } else {
            val totalThreats = summary["total_threats"] ?: summary["total_events"] ?: 0
            val quarantine = summary["quarantine"] ?: summary["critical"] ?: 0
            val block = summary["block"] ?: summary["high"] ?: 0
            val warn = summary["warn"] ?: summary["medium"] ?: 0
            val safe = summary["safe"] ?: summary["low"] ?: 0
            val blocked = summary["blocked"] ?: 0
            val devices = summary["devices"] ?: 1
            val filesScanned = summary["files_scanned"] ?: 0
            val urlThreats = summary["url_threats"] ?: 0

            // Row 1: Total Threats & Quarantine
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(
                            title = "Total Threats",
                            value = totalThreats,
                            color = Color(0xFF00FFE0),
                            icon = Icons.Default.Security
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(
                            title = "Quarantine",
                            value = quarantine,
                            color = Color(0xFFFF4D6D),
                            icon = Icons.Default.Warning
                        )
                    }
                }
            }

            // Row 2: Block & Warn
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(
                            title = "Block",
                            value = block,
                            color = Color(0xFFFF9F43),
                            icon = Icons.Default.Block
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(
                            title = "Warn",
                            value = warn,
                            color = Color(0xFFFFD166),
                            icon = Icons.Default.Info
                        )
                    }
                }
            }

            // Row 3: Safe & Blocked Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(
                            title = "Safe",
                            value = safe,
                            color = Color(0xFF35E08A),
                            icon = Icons.Default.CheckCircle
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(
                            title = "Blocked",
                            value = blocked,
                            color = Color(0xFF38BDF8),
                            icon = Icons.Default.Shield
                        )
                    }
                }
            }

            // Row 4: Files Scanned & URL Threats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(
                            title = "Files Scanned",
                            value = filesScanned,
                            color = Color(0xFF80A0B0),
                            icon = Icons.Default.Description
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(
                            title = "URL Threats",
                            value = urlThreats,
                            color = Color(0xFFC084FC),
                            icon = Icons.Default.Language
                        )
                    }
                }
            }

            // Row 5: Registered Devices (Full Width Card)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF00FFE0).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF07141C))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .border(1.dp, Color(0xFF00FFE0).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .background(Color(0xFF00FFE0).copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = Color(0xFF00FFE0),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Registered Devices",
                                    color = Color(0xFFD0E0EC),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Active SOC telemetry endpoints",
                                    color = Color(0xFF608090),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Text(
                            text = formatMetric(devices),
                            color = Color(0xFF00FFE0),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CyberMetricCard(
    title: String,
    value: Any?,
    color: Color,
    icon: ImageVector = Icons.Default.Security,
    modifier: Modifier = Modifier
) {
    val formatted = formatMetric(value)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF07141C))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color, RoundedCornerShape(50))
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = formatted,
                    color = color,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = title,
                    color = Color(0xFF80A0B0),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}
