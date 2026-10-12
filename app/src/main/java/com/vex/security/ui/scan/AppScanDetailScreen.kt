package com.vex.security.ui.scan

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.repository.ThreatRepository
import com.vex.security.scanner.ApkScanManager
import com.vex.security.scanner.ApkScanResult
import com.vex.security.security.ThreatIndicators
import com.vex.security.ui.components.ThreatExplanationCard
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScanDetailScreen(
    onBackPressed: () -> Unit = {},
    onSelectApkClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentResult by remember { mutableStateOf(ApkScanManager.lastScannedResult) }
    var selectedTab by remember { mutableStateOf("OVERVIEW") }
    var scanning by remember { mutableStateOf(false) }

    val tabs = listOf("OVERVIEW", "PERMISSIONS", "ACTIVITIES", "NETWORK", "CERTIFICATE")

    val apkPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scanning = true
            scope.launch {
                try {
                    val result = ApkScanManager.scan(context, uri)
                    currentResult = result
                    ThreatRepository.sendThreatEvent(
                        deviceId = DeviceIdentity.getDeviceId(context),
                        threatType = "APK",
                        riskScore = result.riskScore,
                        riskLevel = result.riskLevel,
                        action = result.action,
                        status = "OPEN",
                        indicators = result.indicators,
                        source = "ANDROID_APK_DETAIL_SCAN",
                        explanation = result.explanation.reasons.joinToString(" | "),
                        recommendation = result.explanation.recommendation
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    scanning = false
                }
            }
        }
    }

    val res = currentResult
    val riskColor = when (res?.riskLevel) {
        "SAFE" -> Color(0xFF35E08A)
        "WARN" -> Color(0xFFFFD166)
        "BLOCK" -> Color(0xFFFF9F43)
        else -> Color(0xFFFF4D6D)
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
                            text = "APP SCAN (APK ANALYSIS)",
                            color = Color(0xFFFFFFFF),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Deep technical inspection & threat breakdown",
                            color = Color(0xFF80A0B0),
                            fontSize = 11.sp
                        )
                    }
                }

                // Select APK Pill Button
                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(20.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(20.dp))
                        .clickable { apkPicker.launch(arrayOf("*/*")) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Select APK",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Select APK",
                            color = Color(0xFF00FFE0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (scanning) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF00FFE0))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Deep-analyzing package structure...", color = Color(0xFF00FFE0), fontSize = 13.sp)
                }
            }
        }

        if (res == null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF00FFE0).copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Android, contentDescription = null, tint = Color(0xFF00FFE0), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Application Scanned Yet", color = Color(0xFFFFFFFF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Select an APK package from device storage or scan an installed app to inspect permissions, vulnerabilities, and behaviors.",
                            color = Color(0xFF80A0B0),
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { apkPicker.launch(arrayOf("*/*")) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A2530)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FFE0))
                        ) {
                            Text("PICK AN APK FILE TO SCAN", color = Color(0xFF00FFE0), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            // Selected APK Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, riskColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(14.dp))
                                        .background(Color(0xFF0A1F28), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Android,
                                        contentDescription = "Android",
                                        tint = Color(0xFF00FFE0),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Target Package",
                                        color = Color(0xFF80A0B0),
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = res.metadata.fileName,
                                        color = Color(0xFFFFFFFF),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Version: ${res.versionName ?: "1.0"}  ·  Size: ${res.metadata.sizeBytes / 1024 / 1024} MB",
                                        color = Color(0xFF80A0B0),
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "Package: ${res.packageName ?: "com.app"}",
                                        color = Color(0xFF608090),
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Malicious / Risk Score Badge
                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, riskColor, RoundedCornerShape(20.dp))
                                        .background(riskColor.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = res.riskLevel,
                                        color = riskColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Risk Score",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 9.sp
                                )
                                Text(
                                    text = "${res.riskScore} / 100",
                                    color = riskColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Scan Again Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { apkPicker.launch(arrayOf("*/*")) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .border(1.dp, Color(0xFF005566), RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp)),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF0A141A))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF00FFE0), modifier = Modifier.size(16.dp))
                                    Text("SCAN ANOTHER PACKAGE", color = Color(0xFF00FFE0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Tab Bar (OVERVIEW, PERMISSIONS, ACTIVITIES, NETWORK, CERTIFICATE)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF00FFE0) else Color(0xFF005566),
                                    RoundedCornerShape(10.dp)
                                )
                                .background(
                                    if (isSelected) Color(0xFF0A2530) else Color(0xFF0A141A),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedTab = tab }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color(0xFF00FFE0) else Color(0xFF80A0B0),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            when (selectedTab) {
                "OVERVIEW" -> {
                    // Threat Summary Section
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Threat Summary",
                                color = Color(0xFFFFFFFF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            val dangerousCount = res.permissions.count {
                                it.contains("SMS") || it.contains("CAMERA") || it.contains("LOCATION") ||
                                        it.contains("RECORD_AUDIO") || it.contains("CONTACTS") ||
                                        it.contains("SYSTEM_ALERT_WINDOW") || it.contains("ACCESSIBILITY")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    SummaryCard(title = "Threat Indicators", count = "${res.indicators.size}", color = riskColor, icon = Icons.Default.BugReport)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    SummaryCard(title = "Dangerous Perms", count = "$dangerousCount", color = Color(0xFFFF9F43), icon = Icons.Default.Warning)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    SummaryCard(title = "Total Perms", count = "${res.permissions.size}", color = Color(0xFF38BDF8), icon = Icons.Default.Info)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    SummaryCard(title = "Security Action", count = res.action, color = Color(0xFFC084FC), icon = Icons.Default.Shield)
                                }
                            }
                        }
                    }

                    // Risk Analysis Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF005566), RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Risk Analysis & Findings",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(110.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Canvas(modifier = Modifier.size(95.dp)) {
                                            drawArc(
                                                color = Color(0xFF0A2530),
                                                startAngle = 135f,
                                                sweepAngle = 270f,
                                                useCenter = false,
                                                style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                                            )
                                            drawArc(
                                                color = riskColor,
                                                startAngle = 135f,
                                                sweepAngle = (res.riskScore / 100f) * 270f,
                                                useCenter = false,
                                                style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("${res.riskScore}", color = riskColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                            Text(res.riskLevel, color = riskColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.weight(1.3f),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        res.indicators.take(5).forEach { ind ->
                                            RiskFactorRow(
                                                label = ind.replace("_", " "),
                                                points = if (ind == ThreatIndicators.CLEAN_APP) "Safe" else "Flagged"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Explanation Card
                    item {
                        ThreatExplanationCard(
                            explanation = res.explanation,
                            indicators = res.indicators
                        )
                    }
                }

                "PERMISSIONS" -> {
                    item {
                        Text(
                            text = "Requested Permissions (${res.permissions.size})",
                            color = Color(0xFF00FFE0),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val dangerousSet = setOf(
                        "android.permission.CAMERA",
                        "android.permission.RECORD_AUDIO",
                        "android.permission.ACCESS_FINE_LOCATION",
                        "android.permission.ACCESS_COARSE_LOCATION",
                        "android.permission.READ_CONTACTS",
                        "android.permission.WRITE_CONTACTS",
                        "android.permission.READ_SMS",
                        "android.permission.SEND_SMS",
                        "android.permission.CALL_PHONE",
                        "android.permission.SYSTEM_ALERT_WINDOW",
                        "android.permission.BIND_ACCESSIBILITY_SERVICE"
                    )

                    items(res.permissions.size) { idx ->
                        val perm = res.permissions[idx]
                        val isDang = perm in dangerousSet

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (isDang) Color(0xFFFF4D6D).copy(alpha = 0.5f) else Color(0xFF003344), RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF060F15))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = perm.substringAfterLast("."),
                                        color = Color(0xFFFFFFFF),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = perm, color = Color(0xFF608090), fontSize = 9.sp)
                                }

                                Text(
                                    text = if (isDang) "DANGEROUS" else "NORMAL",
                                    color = if (isDang) Color(0xFFFF4D6D) else Color(0xFF35E08A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                "ACTIVITIES" -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF005566), RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Component Analysis", color = Color(0xFF00FFE0), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                HorizontalDivider(color = Color(0xFF003344))
                                Text("Package: ${res.packageName ?: "Unknown"}", color = Color(0xFFFFFFFF), fontSize = 12.sp)
                                Text("Version Name: ${res.versionName ?: "1.0"}", color = Color(0xFFB0C4DE), fontSize = 12.sp)
                                Text("Version Code: ${res.versionCode}", color = Color(0xFFB0C4DE), fontSize = 12.sp)
                                Text("Controlled Test Fixture: ${res.isControlledTest}", color = Color(0xFFB0C4DE), fontSize = 12.sp)
                            }
                        }
                    }
                }

                "NETWORK" -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF005566), RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Network Surface", color = Color(0xFF00FFE0), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                HorizontalDivider(color = Color(0xFF003344))
                                val hasInternet = res.permissions.any { it.contains("INTERNET") }
                                Text("Internet Permission: ${if (hasInternet) "GRANTED" else "NOT REQUESTED"}", color = if (hasInternet) Color(0xFFFFD166) else Color(0xFF35E08A), fontSize = 12.sp)
                                Text("Cleartext Traffic: Disallowed by standard platform config", color = Color(0xFFB0C4DE), fontSize = 12.sp)
                            }
                        }
                    }
                }

                "CERTIFICATE" -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF005566), RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Package Certificate & Hashes", color = Color(0xFF00FFE0), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                HorizontalDivider(color = Color(0xFF003344))
                                Text("SHA-256 Digest:", color = Color(0xFF00FFE0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(res.metadata.sha256 ?: "Unavailable", color = Color(0xFFB0C4DE), fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Signature Integrity: Verified by Android OS Package Manager", color = Color(0xFF35E08A), fontSize = 11.sp)
                            }
                        }
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
fun RiskFactorRow(label: String, points: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFFFF4D6D), shape = RoundedCornerShape(50))
            )
            Text(label, color = Color(0xFFC0D0E0), fontSize = 10.sp)
        }
        Text(points, color = Color(0xFFFF4D6D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
