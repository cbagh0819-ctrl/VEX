package com.vex.security.ui.threats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.repository.ThreatEventsRepository
import com.vex.security.security.RiskCalculator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ThreatListScreen() {
    val scope = rememberCoroutineScope()
    var events by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("ALL") }

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

    // Filter events
    val filteredEvents = remember(events, selectedFilter) {
        if (selectedFilter == "ALL") events
        else events.filter {
            val lvl = RiskCalculator.normalizeRiskLevel("${it["risk_level"] ?: ""}")
            lvl == selectedFilter
        }
    }

    // Calculate 5-tier counts
    val counts = remember(events) {
        val map = mutableMapOf(
            "CRITICAL" to 0,
            "HIGH" to 0,
            "MEDIUM" to 0,
            "LOW" to 0,
            "SAFE" to 0
        )
        events.forEach { e ->
            val lvl = RiskCalculator.normalizeRiskLevel("${e["risk_level"] ?: ""}")
            map[lvl] = (map[lvl] ?: 0) + 1
        }
        map
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
                        text = "INCIDENT LIFE CYCLE & XAI",
                        color = Color(0xFF00FFE0),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Operational response flow & Explainable AI telemetry",
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

        // 5-Tier Metric Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    TierFilterChip(
                        label = "ALL",
                        count = events.size,
                        color = Color(0xFF00FFE0),
                        isSelected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" }
                    )
                }
                item {
                    TierFilterChip(
                        label = "CRITICAL",
                        count = counts["CRITICAL"] ?: 0,
                        color = Color(0xFFFF4D6D),
                        isSelected = selectedFilter == "CRITICAL",
                        onClick = { selectedFilter = "CRITICAL" }
                    )
                }
                item {
                    TierFilterChip(
                        label = "HIGH",
                        count = counts["HIGH"] ?: 0,
                        color = Color(0xFFFF9F43),
                        isSelected = selectedFilter == "HIGH",
                        onClick = { selectedFilter = "HIGH" }
                    )
                }
                item {
                    TierFilterChip(
                        label = "MEDIUM",
                        count = counts["MEDIUM"] ?: 0,
                        color = Color(0xFFFFD166),
                        isSelected = selectedFilter == "MEDIUM",
                        onClick = { selectedFilter = "MEDIUM" }
                    )
                }
                item {
                    TierFilterChip(
                        label = "LOW",
                        count = counts["LOW"] ?: 0,
                        color = Color(0xFF00E5FF),
                        isSelected = selectedFilter == "LOW",
                        onClick = { selectedFilter = "LOW" }
                    )
                }
                item {
                    TierFilterChip(
                        label = "SAFE",
                        count = counts["SAFE"] ?: 0,
                        color = Color(0xFF35E08A),
                        isSelected = selectedFilter == "SAFE",
                        onClick = { selectedFilter = "SAFE" }
                    )
                }
            }
        }

        if (loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00FFE0))
                }
            }
        } else if (filteredEvents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                ) {
                    Box(modifier = Modifier.padding(32.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (selectedFilter == "ALL") "No threat events recorded yet." else "No events in $selectedFilter tier.",
                            color = Color(0xFF80A0B0),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(filteredEvents) { event ->
                IncidentLifeCycleCard(event = event)
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun TierFilterChip(
    label: String,
    count: Int,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .border(
                1.dp,
                if (isSelected) color else color.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            )
            .background(
                if (isSelected) color.copy(alpha = 0.2f) else Color(0xFF0A1F28),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = "$label ($count)",
                color = if (isSelected) Color.White else Color(0xFF80A0B0),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun IncidentLifeCycleCard(event: Map<String, Any>) {
    var expanded by remember { mutableStateOf(false) }

    val rawLevel = "${event["risk_level"] ?: "SAFE"}"
    val standardLevel = RiskCalculator.normalizeRiskLevel(rawLevel)
    val levelColor = when (standardLevel) {
        "CRITICAL" -> Color(0xFFFF4D6D)
        "HIGH" -> Color(0xFFFF9F43)
        "MEDIUM" -> Color(0xFFFFD166)
        "LOW" -> Color(0xFF00E5FF)
        else -> Color(0xFF35E08A)
    }

    val threatType = "${event["threat_type"] ?: "UNKNOWN"}"
    val rawScore = event["risk_score"] ?: 10
    val formattedScore = try {
        val d = rawScore.toString().toDouble()
        d.toLong().toString()
    } catch (_: Exception) {
        rawScore.toString()
    }

    val action = "${event["action"] ?: "ALLOW"}"
    val source = "${event["source"] ?: "MOBILE_CLIENT"}"
    val explanation = "${event["explanation"] ?: "Heuristic evaluation verified standard integrity baseline."}"
    val recommendation = "${event["recommendation"] ?: "Item verified clean under current security baseline."}"

    val timestamp = try {
        val t = event["timestamp"].toString().toLong()
        val sdf = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
        sdf.format(Date(t))
    } catch (_: Exception) {
        "Recent"
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Threat Type & 5-Tier Risk Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(levelColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                "BOMB" in threatType || "TELECOM" in threatType -> Icons.Default.PhoneCallback
                                "VIDEO" in threatType || "DEEPFAKE" in threatType -> Icons.Default.Videocam
                                "PHOTO" in threatType || "IMAGE" in threatType -> Icons.Default.Image
                                "APK" in threatType || "APP" in threatType -> Icons.Default.Android
                                "DNS" in threatType || "ADGUARD" in threatType -> Icons.Default.Public
                                else -> Icons.Default.Security
                            },
                            contentDescription = null,
                            tint = levelColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = threatType,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "$source · $timestamp",
                            color = Color(0xFF80A0B0),
                            fontSize = 10.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "$formattedScore/100",
                        color = Color(0xFF00FFE0),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Box(
                        modifier = Modifier
                            .background(levelColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .border(1.dp, levelColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = standardLevel,
                            color = levelColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF002233))

            // ==========================================
            // PRIORITY 2: OPERATIONAL LIFE CYCLE STEPPER
            // 1. Detected -> 2. Action Taken -> 3. User Response
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF07141D), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF00384D), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "OPERATIONAL LIFE CYCLE FLOW",
                        color = Color(0xFF00FFE0),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    // Step 1: Ingestion / Detection
                    LifeCycleStepRow(
                        stepNumber = "1",
                        stepTitle = "INCIDENT DETECTED",
                        stepDetail = "Sensor telemetry triggered with severity: $standardLevel ($formattedScore pts)",
                        icon = Icons.Default.Search,
                        badgeColor = Color(0xFF00FFE0)
                    )

                    // Step 2: Automated Action Taken
                    LifeCycleStepRow(
                        stepNumber = "2",
                        stepTitle = "AUTOMATED ACTION ENFORCED",
                        stepDetail = "Admin SOC policy triggered: $action",
                        icon = Icons.Default.Shield,
                        badgeColor = levelColor
                    )

                    // Step 3: Response / User Recommendation
                    LifeCycleStepRow(
                        stepNumber = "3",
                        stepTitle = "USER REMEDIATION / RESPONSE",
                        stepDetail = recommendation,
                        icon = Icons.Default.CheckCircle,
                        badgeColor = Color(0xFF35E08A)
                    )
                }
            }

            // ==========================================
            // PRIORITY 4: EXPLAINABLE AI (XAI) REASONING
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF081822), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF005566), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "AI",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "EXPLAINABLE AI (XAI) FORENSIC REASONING",
                            color = Color(0xFF00FFE0),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = explanation,
                        color = Color(0xFFD0E0E8),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            // Expandable details toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Hide Technical Forensics" else "Inspect MITRE ATT&CK & Feature Breakdown",
                    color = Color(0xFF80A0B0),
                    fontSize = 11.sp
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF80A0B0),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Expandable Deep Forensics
            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val rawIndicators = event["indicators"]
                    val indicatorsList: List<String> = when (rawIndicators) {
                        is List<*> -> rawIndicators.mapNotNull { it?.toString() }
                        is String -> rawIndicators.replace("[", "").replace("]", "").replace("\"", "").split(",").map { it.trim() }.filter { it.isNotBlank() }
                        else -> emptyList()
                    }

                    if (indicatorsList.isNotEmpty()) {
                        Text(
                            text = "Observed Attack Indicators (${indicatorsList.size}):",
                            color = Color(0xFF80A0B0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            indicatorsList.forEach { ind ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF0F2633), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = ind,
                                        color = Color(0xFF00FFE0),
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Framework: MITRE ATT&CK Mobile Enterprise v14  ·  Defense Strategy: Zero-Trust",
                        color = Color(0xFF608090),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LifeCycleStepRow(
    stepNumber: String,
    stepTitle: String,
    stepDetail: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(badgeColor.copy(alpha = 0.2f), CircleShape)
                .border(1.dp, badgeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = badgeColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stepTitle,
                color = badgeColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
            Text(
                text = stepDetail,
                color = Color(0xFFB0C4D0),
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }
    }
}
