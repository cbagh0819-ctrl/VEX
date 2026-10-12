package com.vex.security.ui.threats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.repository.ThreatEventsRepository
import kotlinx.coroutines.launch

@Composable
fun ThreatListScreen() {
    val scope = rememberCoroutineScope()
    var events by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val loadEvents = {
        scope.launch {
            loading = true
            events = ThreatEventsRepository.getEvents()
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        loadEvents()
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
                        text = "SECURITY REPORTS & HISTORY",
                        color = Color(0xFF00FFE0),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Recorded threat events & incident logs",
                        color = Color(0xFF80A0B0),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(20.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(20.dp))
                        .clickable { loadEvents() }
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
        } else if (events.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                ) {
                    Box(modifier = Modifier.padding(30.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No threat events recorded yet.", color = Color(0xFF80A0B0))
                    }
                }
            }
        } else {
            items(events) { event ->
                val level = "${event["risk_level"] ?: "UNKNOWN"}"
                val levelColor = when (level) {
                    "SAFE" -> Color(0xFF35E08A)
                    "WARN" -> Color(0xFFFFD166)
                    "BLOCK" -> Color(0xFFFF9F43)
                    else -> Color(0xFFFF4D6D)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, levelColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Threat Type: ${event["threat_type"] ?: "UNKNOWN"}",
                                color = Color(0xFFFFFFFF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Box(
                                modifier = Modifier
                                    .background(levelColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = level,
                                    color = levelColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        val rawScore = event["risk_score"] ?: 0
                        val formattedScore = try {
                            val d = rawScore.toString().toDouble()
                            d.toLong().toString()
                        } catch (e: Exception) {
                            rawScore.toString()
                        }

                        Text("Risk Score: $formattedScore / 100", color = Color(0xFF00FFE0), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Action: ${event["action"] ?: "UNKNOWN"}", color = Color(0xFFFFFFFF), fontSize = 12.sp)
                        Text("Source: ${event["source"] ?: "UNKNOWN"}", color = Color(0xFF80A0B0), fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
