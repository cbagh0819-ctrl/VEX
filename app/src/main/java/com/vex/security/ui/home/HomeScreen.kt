package com.vex.security.ui.home

import android.content.pm.PackageManager
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.monitor.AutomaticFileAnalyzer
import com.vex.security.monitor.StorageScanner
import com.vex.security.repository.ThreatRepository
import com.vex.security.scanner.ApkScanManager
import com.vex.security.scanner.FileScanner
import com.vex.security.security.RiskCalculator
import com.vex.security.security.SecurityPolicy
import com.vex.security.security.ThreatEngine
import com.vex.security.security.ThreatIndicators
import com.vex.security.ui.theme.ThemeState
import com.vex.security.ui.theme.VexPalette
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
    var riskColor by remember { mutableStateOf(Color(0xFFFF3358)) }
    var lastScanText by remember { mutableStateOf("2 HOURS AGO") }

    val startQuickScan = {
        if (!scanning) {
            scanning = true
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
                        riskColor = Color(0xFFFF3358)
                    }
                    "HIGH" -> {
                        riskStatus = "QUARANTINE"
                        riskColor = Color(0xFFFF3358)
                    }
                    "MEDIUM" -> {
                        riskStatus = "WARN"
                        riskColor = Color(0xFFFFB800)
                    }
                    "LOW" -> {
                        riskStatus = "CAUTION"
                        riskColor = Color(0xFFFFB800)
                    }
                    else -> {
                        riskStatus = "SAFE"
                        riskColor = Color(0xFFFF3358)
                    }
                }

                lastScanText = "JUST NOW"
                scanning = false

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
                        explanation = if (riskStatus == "SAFE") "No threats detected. Device verified secure." else "Threat item detected: $mostThreateningItem",
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
            .background(Color(0xFFF6F4F9))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Header Bar (Matching the exact screenshot)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: VEX Logo + CYBER GUARD Pill + Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VE",
                                color = Color(0xFF1C1133),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text = "X",
                                color = Color(0xFFFF3358),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1).sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFF241239), RoundedCornerShape(12.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "CYBER GUARD",
                                color = Color.White,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Subtle vertical divider line
                    Box(
                        modifier = Modifier
                            .width(1.5.dp)
                            .height(28.dp)
                            .background(Color(0xFFDCD6E8))
                    )
                }

                // Right: Two Badges (EXECUTIVE & Device Protected)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Badge 1: 👑 EXECUTIVE Pill
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF241239), RoundedCornerShape(20.dp))
                            .clickable { ThemeState.cycleNextTheme(context) }
                            .padding(horizontal = 11.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(text = "👑", fontSize = 11.sp)
                            Text(
                                text = "EXECUTIVE",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Badge 2: 🛡️ Device Protected
                    Box(
                        modifier = Modifier
                            .border(1.5.dp, Color(0xFFFF3358), RoundedCornerShape(20.dp))
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Protected",
                                tint = Color(0xFFFF3358),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (riskStatus == "SAFE") "Device Protected" else "Threat Alert",
                                color = Color(0xFFFF3358),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 1.5 Unique Device ID Badge (Cloud Linked to SOC Dashboard)
        item {
            val deviceId = remember { DeviceIdentity.getDeviceId(context) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { DeviceIdentity.copyDeviceId(context) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF241239)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B1E5C))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF341754), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "ID",
                                tint = Color(0xFFFF3358),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "UNIQUE DEVICE ID",
                                    color = Color(0xFFA998C4),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF059669).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "● CLOUD LINKED",
                                        color = Color(0xFF34D399),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = deviceId,
                                color = Color.White,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFF3358), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "COPY",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Hero Card ("YOUR DEVICE RISK // SAFE")
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF241239)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Cyber Robot Mascot
                        Box(
                            modifier = Modifier.size(width = 130.dp, height = 120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            RobotMascot(modifier = Modifier.fillMaxSize())
                        }

                        // Right: Risk Status & Checklist
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = "YOUR DEVICE RISK",
                                color = Color(0xFFA998C4),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Large Bold Italic SAFE with Red Smile Arc
                            Column {
                                Text(
                                    text = riskStatus,
                                    color = Color.White,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black,
                                    fontStyle = FontStyle.Italic,
                                    letterSpacing = 1.sp
                                )

                                Canvas(modifier = Modifier.size(width = 65.dp, height = 6.dp)) {
                                    val smilePath = Path().apply {
                                        moveTo(0f, 2f)
                                        quadraticTo(size.width * 0.5f, size.height, size.width, 2f)
                                    }
                                    drawPath(
                                        path = smilePath,
                                        color = Color(0xFFFF3358),
                                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Checklist bullets
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Check",
                                    tint = Color(0xFFFF3358),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (riskStatus == "SAFE") "Everything is good" else "Attention required",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Check",
                                    tint = Color(0xFFFF3358),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (riskStatus == "SAFE") "No threats detected" else "Threat item isolated",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Row: Last Scan Pill + Quick Scan Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Last Scan Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFF381A56), RoundedCornerShape(14.dp))
                                .background(Color(0xFF1A0C2B), RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 9.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Calendar",
                                    tint = Color(0xFFFF3358),
                                    modifier = Modifier.size(16.dp)
                                )
                                Column {
                                    Text(
                                        text = "Last Scan",
                                        color = Color(0xFFA998C4),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Text(
                                        text = lastScanText,
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Right: QUICK SCAN Red Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFFF3358), RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { startQuickScan() }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (scanning) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "SCANNING...",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "QUICK SCAN",
                                        color = Color.White,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Middle Callout Banner ("CHECK YOUR PHONE")
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { startQuickScan() },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF3358), Color(0xFFFF486C))
                            )
                        )
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Cute Phone Scan Illustration
                        Box(
                            modifier = Modifier.size(width = 62.dp, height = 62.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            PhoneScanIllustration(modifier = Modifier.fillMaxSize())
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CHECK YOUR PHONE",
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Tap to scan everything automatically",
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 4. Feature Grid (Matching screenshot: 2 columns, dark cards, white icons, red circular chevron)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Row 1: APK CHECK & QR PHOTOS CHECK
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItemExecutive(
                            icon = Icons.Default.Android,
                            title = "APK CHECK",
                            subtitle = "Scan and analyze apps",
                            onClick = onNavigateToApkCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItemExecutive(
                            icon = Icons.Default.QrCode,
                            title = "QR PHOTOS CHECK",
                            subtitle = "Scan QR codes from camera",
                            onClick = onNavigateToQrCheck
                        )
                    }
                }

                // Row 2: PHOTO SCAN & VIDEO SCAN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItemExecutive(
                            icon = Icons.Default.Image,
                            title = "PHOTO SCAN",
                            subtitle = "Check for hidden threats",
                            onClick = onNavigateToAiCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItemExecutive(
                            icon = Icons.Default.Videocam,
                            title = "VIDEO SCAN",
                            subtitle = "Analyze videos for risks",
                            onClick = onNavigateToAiVideoCheck
                        )
                    }
                }

                // Row 3: METADATA SCAN & USB/OTG CHECK
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItemExecutive(
                            icon = Icons.Default.Description,
                            title = "METADATA SCAN",
                            subtitle = "Photos missing EXIF info",
                            onClick = onNavigateToNoMetadataCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItemExecutive(
                            icon = Icons.Default.Usb,
                            title = "USB/OTG CHECK",
                            subtitle = "Scan external pen drives",
                            onClick = onNavigateToUsbCheck
                        )
                    }
                }

                // Row 4: VEX BROWSER & BOMB SHIELD
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItemExecutive(
                            icon = Icons.Default.Language,
                            title = "VEX BROWSER",
                            subtitle = "Zero-login incognito VB",
                            onClick = onNavigateToBrowser
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItemExecutive(
                            icon = Icons.Default.Shield,
                            title = "BOMB SHIELD",
                            subtitle = "Block SMS flood & spam",
                            onClick = onNavigateToBombShield
                        )
                    }
                }

                // Row 5: FILE DEEP SCAN & URL CHECK
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItemExecutive(
                            icon = Icons.Default.FolderOpen,
                            title = "FILE SCAN",
                            subtitle = "Deep malware inspection",
                            onClick = onNavigateToFileScan
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItemExecutive(
                            icon = Icons.Default.Link,
                            title = "URL SCAN",
                            subtitle = "Detect malicious links",
                            onClick = onNavigateToUrlScan
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

/**
 * 2-Column Action Feature Card styled exactly to the design reference:
 * Dark plum card (#241239), white icon, red circular chevron button (#FF3358), bold white title, soft lavender subtitle.
 */
@Composable
fun FeatureItemExecutive(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF241239)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: White Icon + Red Circular Chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFFF3358), RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // Bottom Column: Bold Title + Lavender Subtitle
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFFA998C4),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * High-definition vector rendering of the cute VEX Cyber Robot mascot holding the security shield.
 */
@Composable
fun RobotMascot(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Sparkle / dynamic accent marks (top)
        drawLine(
            color = Color(0xFFFF3358),
            start = Offset(w * 0.45f, h * 0.05f),
            end = Offset(w * 0.40f, h * 0.12f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFFFF3358),
            start = Offset(w * 0.55f, h * 0.05f),
            end = Offset(w * 0.53f, h * 0.13f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // Robot Head (white rounded rect / oval)
        val headRadius = w * 0.28f
        val headCenter = Offset(w * 0.40f, h * 0.42f)

        // Ear muff left
        drawCircle(
            color = Color(0xFFFF3358),
            radius = headRadius * 0.38f,
            center = Offset(headCenter.x - headRadius * 0.95f, headCenter.y)
        )
        drawCircle(
            color = Color.White,
            radius = headRadius * 0.20f,
            center = Offset(headCenter.x - headRadius * 0.95f, headCenter.y)
        )

        // Ear muff right
        drawCircle(
            color = Color(0xFFFF3358),
            radius = headRadius * 0.38f,
            center = Offset(headCenter.x + headRadius * 0.95f, headCenter.y)
        )
        drawCircle(
            color = Color.White,
            radius = headRadius * 0.20f,
            center = Offset(headCenter.x + headRadius * 0.95f, headCenter.y)
        )

        // Head base
        drawCircle(
            color = Color.White,
            radius = headRadius,
            center = headCenter
        )

        // Red top crest on helmet
        drawArc(
            color = Color(0xFFFF3358),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = true,
            topLeft = Offset(headCenter.x - headRadius * 0.4f, headCenter.y - headRadius * 1.05f),
            size = Size(headRadius * 0.8f, headRadius * 0.35f)
        )

        // Dark Visor / face plate
        drawRoundRect(
            color = Color(0xFF190B2A),
            topLeft = Offset(headCenter.x - headRadius * 0.72f, headCenter.y - headRadius * 0.45f),
            size = Size(headRadius * 1.44f, headRadius * 0.95f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(headRadius * 0.45f)
        )

        // Cheerful curved eyes ^ ^
        drawArc(
            color = Color.White,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(headCenter.x - headRadius * 0.48f, headCenter.y - headRadius * 0.15f),
            size = Size(headRadius * 0.32f, headRadius * 0.22f),
            style = Stroke(width = 4.5f, cap = StrokeCap.Round)
        )
        drawArc(
            color = Color.White,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(headCenter.x + headRadius * 0.16f, headCenter.y - headRadius * 0.15f),
            size = Size(headRadius * 0.32f, headRadius * 0.22f),
            style = Stroke(width = 4.5f, cap = StrokeCap.Round)
        )

        // Cute rosy cheeks (red dots)
        drawCircle(
            color = Color(0x66FF3358),
            radius = headRadius * 0.12f,
            center = Offset(headCenter.x - headRadius * 0.50f, headCenter.y + headRadius * 0.25f)
        )
        drawCircle(
            color = Color(0x66FF3358),
            radius = headRadius * 0.12f,
            center = Offset(headCenter.x + headRadius * 0.50f, headCenter.y + headRadius * 0.25f)
        )

        // Robot Body (white capsule)
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(headCenter.x - headRadius * 0.65f, headCenter.y + headRadius * 0.75f),
            size = Size(headRadius * 1.15f, headRadius * 0.85f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(headRadius * 0.4f)
        )

        // Red collar / band
        drawRoundRect(
            color = Color(0xFFFF3358),
            topLeft = Offset(headCenter.x - headRadius * 0.55f, headCenter.y + headRadius * 0.72f),
            size = Size(headRadius * 0.95f, headRadius * 0.18f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(headRadius * 0.1f)
        )

        // Left hand giving thumbs up!
        drawCircle(
            color = Color.White,
            radius = headRadius * 0.22f,
            center = Offset(headCenter.x - headRadius * 0.75f, headCenter.y + headRadius * 1.1f)
        )

        // Glowing Shield with Checkmark (Right side of robot)
        val shieldCenter = Offset(w * 0.78f, h * 0.70f)
        val shieldWidth = w * 0.36f
        val shieldHeight = h * 0.38f

        val shieldPath = Path().apply {
            moveTo(shieldCenter.x, shieldCenter.y - shieldHeight * 0.48f)
            lineTo(shieldCenter.x + shieldWidth * 0.48f, shieldCenter.y - shieldHeight * 0.32f)
            quadraticTo(
                shieldCenter.x + shieldWidth * 0.46f, shieldCenter.y + shieldHeight * 0.15f,
                shieldCenter.x, shieldCenter.y + shieldHeight * 0.52f
            )
            quadraticTo(
                shieldCenter.x - shieldWidth * 0.46f, shieldCenter.y + shieldHeight * 0.15f,
                shieldCenter.x - shieldWidth * 0.48f, shieldCenter.y - shieldHeight * 0.32f
            )
            close()
        }

        // Shield outer border highlight
        drawPath(path = shieldPath, color = Color.White)

        // Shield inner coral body
        val innerShieldPath = Path().apply {
            moveTo(shieldCenter.x, shieldCenter.y - shieldHeight * 0.44f)
            lineTo(shieldCenter.x + shieldWidth * 0.42f, shieldCenter.y - shieldHeight * 0.30f)
            quadraticTo(
                shieldCenter.x + shieldWidth * 0.40f, shieldCenter.y + shieldHeight * 0.12f,
                shieldCenter.x, shieldCenter.y + shieldHeight * 0.46f
            )
            quadraticTo(
                shieldCenter.x - shieldWidth * 0.40f, shieldCenter.y + shieldHeight * 0.12f,
                shieldCenter.x - shieldWidth * 0.42f, shieldCenter.y - shieldHeight * 0.30f
            )
            close()
        }
        drawPath(path = innerShieldPath, color = Color(0xFFFF3358))

        // White checkmark inside shield
        val checkPath = Path().apply {
            moveTo(shieldCenter.x - shieldWidth * 0.18f, shieldCenter.y - shieldHeight * 0.02f)
            lineTo(shieldCenter.x - shieldWidth * 0.04f, shieldCenter.y + shieldHeight * 0.14f)
            lineTo(shieldCenter.x + shieldWidth * 0.20f, shieldCenter.y - shieldHeight * 0.14f)
        }
        drawPath(
            path = checkPath,
            color = Color.White,
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )
    }
}

/**
 * Cute vector drawing of a phone outline with a magnifying glass search lens inside and sparkle motion rays.
 */
@Composable
fun PhoneScanIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Dynamic motion rays on top-right: ヾ
        drawLine(
            color = Color.White,
            start = Offset(w * 0.75f, h * 0.12f),
            end = Offset(w * 0.86f, h * 0.05f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.88f, h * 0.25f),
            end = Offset(w * 0.98f, h * 0.22f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        // Phone outline
        val phoneLeft = w * 0.12f
        val phoneTop = h * 0.10f
        val phoneW = w * 0.62f
        val phoneH = h * 0.80f
        val corner = 20f

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(phoneLeft, phoneTop),
            size = Size(phoneW, phoneH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner),
            style = Stroke(width = 6f)
        )

        // Magnifying glass inside phone
        val magCenter = Offset(phoneLeft + phoneW * 0.52f, phoneTop + phoneH * 0.48f)
        val magRadius = phoneW * 0.27f

        drawCircle(
            color = Color.White,
            radius = magRadius,
            center = magCenter,
            style = Stroke(width = 6f)
        )

        // Handle of magnifying glass
        val handleStart = Offset(magCenter.x + magRadius * 0.7f, magCenter.y + magRadius * 0.7f)
        val handleEnd = Offset(handleStart.x + magRadius * 0.75f, handleStart.y + magRadius * 0.75f)
        drawLine(
            color = Color.White,
            start = handleStart,
            end = handleEnd,
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
    }
}
