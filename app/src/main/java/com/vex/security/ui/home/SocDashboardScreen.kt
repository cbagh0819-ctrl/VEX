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
import kotlinx.coroutines.launch

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
        loadData()
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
                        fontSize = 20.sp,
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
            val totalThreats = "${summary["total_threats"] ?: summary["total_events"] ?: 4}"
            val quarantine = "${summary["quarantine"] ?: summary["critical"] ?: 1}"
            val block = "${summary["block"] ?: summary["high"] ?: 1}"
            val warn = "${summary["warn"] ?: summary["medium"] ?: 1}"
            val safe = "${summary["safe"] ?: summary["low"] ?: 1}"
            val blocked = "${summary["blocked"] ?: 1}"
            val devices = "${summary["devices"] ?: 1}"

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(title = "Total Threats", value = totalThreats, color = Color(0xFF00FFE0), icon = Icons.Default.Security)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(title = "Quarantine", value = quarantine, color = Color(0xFFFF4D6D), icon = Icons.Default.Warning)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(title = "Block", value = block, color = Color(0xFFFF9F43), icon = Icons.Default.Block)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(title = "Warn", value = warn, color = Color(0xFFFFD166), icon = Icons.Default.Info)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(title = "Safe", value = safe, color = Color(0xFF35E08A), icon = Icons.Default.CheckCircle)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CyberMetricCard(title = "Blocked", value = blocked, color = Color(0xFF7090A0), icon = Icons.Default.Shield)
                    }
                }
            }

            item {
                CyberMetricCard(title = "Registered Devices", value = devices, color = Color(0xFF00FFE0), icon = Icons.Default.PhoneAndroid)
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
    value: String,
    color: Color,
    icon: ImageVector = Icons.Default.Security
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
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
                        .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    color = Color(0xFFFFFFFF),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = value,
                color = color,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
