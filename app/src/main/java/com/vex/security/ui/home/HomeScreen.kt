package com.vex.security.ui.home

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.pm.PackageManager
import com.vex.security.monitor.AutomaticFileAnalyzer
import com.vex.security.monitor.StorageScanner
import com.vex.security.scanner.ApkScanManager
import com.vex.security.scanner.FileScanner
import com.vex.security.security.ThreatEngine
import com.vex.security.security.ThreatIndicators
import com.vex.security.security.RiskCalculator
import com.vex.security.security.SecurityPolicy
import com.vex.security.repository.ThreatRepository
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    onNavigateToApkCheck: () -> Unit = {},
    onNavigateToAiCheck: () -> Unit = {},
    onNavigateToAiVideoCheck: () -> Unit = {},
    onNavigateToNoMetadataCheck: () -> Unit = {},
    onNavigateToUsbCheck: () -> Unit = {},
    onNavigateToQrCheck: () -> Unit = {},
    onNavigateToFileScan: () -> Unit = {},
    onNavigateToUrlScan: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToBrowser: () -> Unit = {},
    onNavigateToBombShield: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scanning by remember { mutableStateOf(false) }
    var riskStatus by remember { mutableStateOf("SAFE") }
    var riskColor by remember { mutableStateOf(Color(0xFF35E08A)) } // Green
    var riskMessage by remember { mutableStateOf("Everything is good\nNo threats detected") }
    var lastScanText by remember { mutableStateOf("2 hours ago") }

    val startQuickScan = {
        if (!scanning) {
            scanning = true
            riskMessage = "Scanning storage & applications..."
            scope.launch {
                val files = withContext(Dispatchers.IO) {
                    StorageScanner.getRecentFiles(context)
                }

                delay(1200)

                var maxScore = 0
                val foundThreatIndicators = mutableListOf<String>()
                var mostThreateningItem = ""

                // 1. Scan storage files with AutomaticFileAnalyzer
                for (file in files) {
                    val metadata = FileScanner.scanFile(context, file.uri, "QUICK_SCAN")
                    val indicators = AutomaticFileAnalyzer.analyze(metadata)
                    val analysis = ThreatEngine.analyze(indicators, context)
                    if (analysis.riskScore > maxScore) {
                        maxScore = analysis.riskScore
                        mostThreateningItem = metadata.fileName
                        foundThreatIndicators.clear()
                        foundThreatIndicators.addAll(indicators)
                    }
                }

                // 2. Scan installed third-party apps for risky permission combinations
                try {
                    val pm = context.packageManager
                    val pkgs = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
                    for (pkg in pkgs) {
                        val isSystem = (pkg.applicationInfo != null &&
                            (pkg.applicationInfo!!.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0)
                        if (isSystem && (pkg.packageName.startsWith("android") || pkg.packageName.startsWith("com.android"))) {
                            continue
                        }
                        val appResult = ApkScanManager.scanInstalledPackage(context, pkg)
                        if (appResult.riskScore > maxScore) {
                            maxScore = appResult.riskScore
                            mostThreateningItem = pkg.packageName
                            foundThreatIndicators.clear()
                            foundThreatIndicators.addAll(appResult.indicators)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val standardLvl = RiskCalculator.getRiskLevel(maxScore)
                when (standardLvl) {
                    "CRITICAL" -> {
                        riskStatus = "CRITICAL"
                        riskColor = Color(0xFFFF4D6D) // Red
                        riskMessage = "Critical threat: $mostThreateningItem\nImmediate action required."
                    }
                    "HIGH" -> {
                        riskStatus = "HIGH"
                        riskColor = Color(0xFFFF9F43) // Orange
                        riskMessage = "Suspicious item: $mostThreateningItem\nThreat blocked."
                    }
                    "MEDIUM" -> {
                        riskStatus = "MEDIUM"
                        riskColor = Color(0xFFFFD166) // Yellow
                        riskMessage = "Warning: Review activity for $mostThreateningItem"
                    }
                    "LOW" -> {
                        riskStatus = "LOW"
                        riskColor = Color(0xFF00E5FF) // Cyan
                        riskMessage = "Notice: Low risk item: $mostThreateningItem"
                    }
                    else -> {
                        riskStatus = "SAFE"
                        riskColor = Color(0xFF35E08A) // Green
                        riskMessage = "Everything is good\nNo threats detected across storage & apps."
                    }
                }

                scanning = false
                lastScanText = "Just now"

                try {
                    val finalScore = if (maxScore > 0) maxScore else 10
                    ThreatRepository.sendThreatEvent(
                        deviceId = DeviceIdentity.getDeviceId(context),
                        threatType = "FILE",
                        riskScore = finalScore,
                        riskLevel = riskStatus,
                        action = SecurityPolicy.getAction(finalScore),
                        status = if (riskStatus == "SAFE") "RESOLVED" else "OPEN",
                        indicators = if (foundThreatIndicators.isNotEmpty()) foundThreatIndicators else listOf(ThreatIndicators.CLEAN_FILE),
                        source = "ANDROID_QUICK_SCAN",
                        explanation = riskMessage.replace("\n", " "),
                        recommendation = if (maxScore >= 60) "Quarantine or isolate item immediately" else "Device verified secure"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VEX",
                        color = Color(0xFF00FFE0),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "CYBER GUARD",
                        color = Color(0xFFA0B0C0),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Device Protected Pill
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(20.dp))
                            .background(Color(0xFF0A1F28), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(riskColor, shape = RoundedCornerShape(50))
                            )
                            Text(
                                text = if (riskStatus == "SAFE") "Device Protected" else "Threat Alert",
                                color = riskColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Notification Bell
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(50))
                            .background(Color(0xFF0A1F28), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Risk Status Card
        item {
            CyberCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your Device Risk",
                            color = Color(0xFF80A0B0),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = riskStatus,
                            color = riskColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = riskMessage,
                            color = Color(0xFFB0C4DE),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .border(1.dp, Color(0xFF005566), RoundedCornerShape(10.dp))
                                .background(Color(0xFF0A141A), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Shield",
                                    tint = Color(0xFF00FFE0),
                                    modifier = Modifier.size(16.dp)
                                )
                                Column {
                                    Text("Last Scan", color = Color(0xFF80A0B0), fontSize = 10.sp)
                                    Text(lastScanText, color = Color(0xFFFFFFFF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    // Risk Gauge Visual
                    Box(
                        modifier = Modifier.size(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(110.dp)) {
                            drawArc(
                                color = Color(0xFF0A2530),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Safe segment
                            drawArc(
                                color = if (riskStatus == "SAFE") Color(0xFF35E08A) else Color(0xFF35E08A).copy(alpha = 0.3f),
                                startAngle = 135f,
                                sweepAngle = 67.5f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Warn segment
                            drawArc(
                                color = if (riskStatus == "WARN") Color(0xFFFFD166) else Color(0xFFFFD166).copy(alpha = 0.3f),
                                startAngle = 202.5f,
                                sweepAngle = 67.5f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Block segment
                            drawArc(
                                color = if (riskStatus == "BLOCK") Color(0xFFFF9F43) else Color(0xFFFF9F43).copy(alpha = 0.3f),
                                startAngle = 270f,
                                sweepAngle = 67.5f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Quarantine segment
                            drawArc(
                                color = if (riskStatus == "QUARANTINE") Color(0xFFFF4D6D) else Color(0xFFFF4D6D).copy(alpha = 0.3f),
                                startAngle = 337.5f,
                                sweepAngle = 67.5f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = when (riskStatus) {
                                    "QUARANTINE" -> Icons.Default.Warning
                                    "BLOCK" -> Icons.Default.Block
                                    "WARN" -> Icons.Default.Info
                                    else -> Icons.Default.CheckCircle
                                },
                                contentDescription = riskStatus,
                                tint = riskColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = riskStatus,
                                color = riskColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Quick Scan Banner ("CHECK YOUR PHONE")
        item {
            CyberCard(
                modifier = Modifier.clickable {
                    startQuickScan()
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(12.dp))
                                .background(Color(0xFF0A1F28), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Quick Scan",
                                tint = Color(0xFF00FFE0),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column {
                            Text("QUICK SCAN", color = Color(0xFF80A0B0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("CHECK YOUR PHONE", color = Color(0xFFFFFFFF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (scanning) "Scanning everything automatically..." else "Tap to quick scan everything automatically",
                                color = if (scanning) Color(0xFF00FFE0) else Color(0xFFA0B0C0),
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (scanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Color(0xFF00FFE0),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF00FFE0), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Go",
                                tint = Color(0xFF000810),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Feature Grid (2x3)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Android,
                            title = "APK CHECK",
                            subtitle = "Scan and analyze apps and APK files",
                            onClick = onNavigateToApkCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.QrCode,
                            title = "QR PHOTOS CHECK",
                            subtitle = "Scan QR codes from camera or gallery",
                            onClick = onNavigateToQrCheck
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Image,
                            title = "AI PHOTOS CHECK",
                            subtitle = "Detect AI generated or manipulated images",
                            onClick = onNavigateToAiCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Videocam,
                            title = "AI VIDEO CHECK",
                            subtitle = "Detect AI generated videos & deepfakes",
                            onClick = onNavigateToAiVideoCheck
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Description,
                            title = "NO METADATA CHECK",
                            subtitle = "Find photos with missing metadata",
                            onClick = onNavigateToNoMetadataCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Usb,
                            title = "USB/OTG CHECK",
                            subtitle = "Scan USB devices and external storage",
                            onClick = onNavigateToUsbCheck
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Language,
                            title = "URL CHECK",
                            subtitle = "Check and detect malicious links",
                            onClick = onNavigateToUrlScan
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.FolderOpen,
                            title = "FILE DEEP SCAN",
                            subtitle = "Upload and scan any file for malware",
                            onClick = onNavigateToFileScan
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Language,
                            title = "VEX BROWSER (VB)",
                            subtitle = "Zero login, incognito ad & threat shield",
                            onClick = onNavigateToBrowser
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Shield,
                            title = "BOMB SHIELD",
                            subtitle = "Auto block flood SMS & robocalls",
                            onClick = onNavigateToBombShield
                        )
                    }
                }
            }
        }

        // Recent Activity Card
        item {
            CyberCard(
                modifier = Modifier.clickable { onNavigateToDashboard() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Activity",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text("Recent Activity", color = Color(0xFFFFFFFF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = when (riskStatus) {
                                    "QUARANTINE" -> "Harmful threat detected in automatic scan"
                                    "BLOCK" -> "Suspicious file blocked"
                                    "WARN" -> "Warning items found"
                                    else -> "No threats detected today"
                                },
                                color = riskColor,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = Color(0xFF80A0B0),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF005566).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .border(1.dp, Color(0xFF005566).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
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
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, Color(0xFF00FFE0).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color(0xFF00FFE0),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = Color(0xFF7090A0),
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color(0xFFFFFFFF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF80A0B0),
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }
        }
    }
}
