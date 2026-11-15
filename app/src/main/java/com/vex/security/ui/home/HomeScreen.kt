package com.vex.security.ui.home

import android.content.pm.PackageManager
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.vex.security.security.ThreatEngine
import com.vex.security.security.ThreatIndicators
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val CoralPink = Color(0xFFFF3358)
private val DarkPlumCard = Color(0xFF241239)
private val LavenderText = Color(0xFFA998C4)
private val EmeraldSafe = Color(0xFF35E08A)

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
    onNavigateToPhoneCheck: () -> Unit = {},
    onNavigateToBrowser: () -> Unit = {},
    onNavigateToBombShield: () -> Unit = {},
    onNavigateToWifiCheck: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scanning by remember { mutableStateOf(false) }
    var riskStatus by remember { mutableStateOf("SAFE") }
    var riskColor by remember { mutableStateOf(EmeraldSafe) }
    var riskMessage by remember { mutableStateOf("Everything is good\nNo threats detected") }
    var lastScanText by remember { mutableStateOf("JUST NOW") }

    val startQuickScan = {
        if (!scanning) {
            scanning = true
            riskMessage = "Scanning storage & applications..."
            scope.launch {
                val files = withContext(Dispatchers.IO) {
                    StorageScanner.getRecentFiles(context)
                }

                delay(900)

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

                // 2. Scan installed third-party apps
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
                            mostThreateningItem = appResult.packageName ?: "Suspicious App"
                            foundThreatIndicators.clear()
                            foundThreatIndicators.addAll(appResult.indicators)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                when {
                    maxScore >= 75 -> {
                        riskStatus = "QUARANTINE"
                        riskColor = CoralPink
                        riskMessage = "Harmful threat: $mostThreateningItem\nImmediate action required."
                    }
                    maxScore >= 50 -> {
                        riskStatus = "BLOCK"
                        riskColor = Color(0xFFFF9F43)
                        riskMessage = "Suspicious item: $mostThreateningItem\nThreat blocked."
                    }
                    maxScore >= 25 -> {
                        riskStatus = "WARN"
                        riskColor = Color(0xFFFFD166)
                        riskMessage = "Warning: Review activity for $mostThreateningItem"
                    }
                    else -> {
                        riskStatus = "SAFE"
                        riskColor = EmeraldSafe
                        riskMessage = "Everything is good\nNo threats detected across storage & apps."
                    }
                }

                scanning = false
                lastScanText = "JUST NOW"

                try {
                    ThreatRepository.sendThreatEvent(
                        context = context,
                        deviceId = DeviceIdentity.getDeviceId(context),
                        threatType = "FILE",
                        riskScore = if (maxScore > 0) maxScore else 5,
                        riskLevel = riskStatus,
                        action = if (riskStatus == "SAFE") "ALLOW" else riskStatus,
                        status = if (riskStatus == "SAFE") "RESOLVED" else "OPEN",
                        indicators = if (foundThreatIndicators.isNotEmpty()) foundThreatIndicators else listOf(ThreatIndicators.CLEAN_FILE),
                        source = "STANDALONE_QUICK_SCAN",
                        explanation = riskMessage.replace("\n", " "),
                        recommendation = if (maxScore >= 50) "Action required" else "Device verified secure"
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
                        Color(0xFF140D1E),
                        Color(0xFF1B1126),
                        Color(0xFF100818)
                    )
                )
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Header Bar (Matching design)
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
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VE",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text = "X",
                                color = CoralPink,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1).sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(DarkPlumCard, RoundedCornerShape(12.dp))
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

                    Box(
                        modifier = Modifier
                            .width(1.5.dp)
                            .height(28.dp)
                            .background(Color(0xFF381A56))
                    )
                }

                // Right: Two Badges (STANDALONE & Device Protected)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Badge 1: 🛡️ STANDALONE Pill
                    Box(
                        modifier = Modifier
                            .background(DarkPlumCard, RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFF381A56), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(EmeraldSafe, CircleShape)
                            )
                            Text(
                                text = "ON-DEVICE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Badge 2: 🛡️ Device Protected
                    Box(
                        modifier = Modifier
                            .border(1.5.dp, CoralPink, RoundedCornerShape(20.dp))
                            .background(Color(0xFF1E0E30), RoundedCornerShape(20.dp))
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
                                tint = CoralPink,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (riskStatus == "SAFE") "Device Protected" else "Threat Alert",
                                color = CoralPink,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Hero Mascot Card ("YOUR DEVICE RISK // SAFE")
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = DarkPlumCard),
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
                        // Left: Mascot Robot
                        Box(
                            modifier = Modifier.size(width = 120.dp, height = 115.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            RobotMascot(modifier = Modifier.fillMaxSize())
                        }

                        // Right: Risk Status & Quick Action
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = "YOUR DEVICE RISK",
                                color = LavenderText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = riskStatus,
                                color = Color.White,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Check",
                                    tint = CoralPink,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = if (riskStatus == "SAFE") "No threats detected" else "Action required",
                                    color = Color.White,
                                    fontSize = 11.5.sp,
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
                                    tint = CoralPink,
                                    modifier = Modifier.size(16.dp)
                                )
                                Column {
                                    Text(
                                        text = "Last Scan",
                                        color = LavenderText,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Text(
                                        text = lastScanText,
                                        color = Color.White,
                                        fontSize = 11.sp,
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
                                .background(CoralPink, RoundedCornerShape(14.dp))
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
                                        fontSize = 12.sp,
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

        // 3. Middle Callout Banner ("CHECK YOUR PHONE") -> Opens PhoneCheckScreen
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onNavigateToPhoneCheck() },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(CoralPink, Color(0xFFFF486C))
                            )
                        )
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(width = 58.dp, height = 58.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            PhoneScanIllustration(modifier = Modifier.fillMaxSize())
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CHECK YOUR PHONE",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Tap to run full 5-pillar on-device audit",
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Start Audit",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Feature Grid (6 Rows, matching user screenshot style exactly)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Row 1: APK CHECK & QR PHOTOS CHECK
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Android,
                            title = "APK CHECK",
                            subtitle = "Scan and analyze apps",
                            onClick = onNavigateToApkCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.QrCode,
                            title = "QR PHOTOS CHECK",
                            subtitle = "Scan QR codes from camera",
                            onClick = onNavigateToQrCheck
                        )
                    }
                }

                // Row 2: PHOTO SCAN & VIDEO SCAN
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Image,
                            title = "PHOTO SCAN",
                            subtitle = "Check for hidden threats",
                            onClick = onNavigateToAiCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Videocam,
                            title = "VIDEO SCAN",
                            subtitle = "Analyze videos for risks",
                            onClick = onNavigateToAiVideoCheck
                        )
                    }
                }

                // Row 3: NO METADATA & USB / OTG CHECK
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Description,
                            title = "NO METADATA",
                            subtitle = "Find missing metadata photos",
                            onClick = onNavigateToNoMetadataCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Usb,
                            title = "USB / OTG CHECK",
                            subtitle = "Scan external USB storage",
                            onClick = onNavigateToUsbCheck
                        )
                    }
                }

                // Row 4: URL CHECK & FILE DEEP SCAN
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Language,
                            title = "URL CHECK",
                            subtitle = "Detect malicious web links",
                            onClick = onNavigateToUrlScan
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.FolderOpen,
                            title = "FILE DEEP SCAN",
                            subtitle = "Scan any file for malware",
                            onClick = onNavigateToFileScan
                        )
                    }
                }

                // Row 5: VEX BROWSER & BOMB SHIELD
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Shield,
                            title = "VEX BROWSER",
                            subtitle = "Zero login, incognito shield",
                            onClick = onNavigateToBrowser
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Phone,
                            title = "BOMB SHIELD",
                            subtitle = "Auto block flood SMS & calls",
                            onClick = onNavigateToBombShield
                        )
                    }
                }

                // Row 6: WI-FI CHECK & PHONE AUDIT (Newly added and prominent!)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Wifi,
                            title = "WI-FI CHECK",
                            subtitle = "Inspect network & rogue APs",
                            onClick = onNavigateToWifiCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.PhoneAndroid,
                            title = "PHONE AUDIT",
                            subtitle = "Root & OS security scan",
                            onClick = onNavigateToPhoneCheck
                        )
                    }
                }
            }
        }

        // 5. Recent Activity Card (Matching user screenshot)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onNavigateToPhoneCheck() },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkPlumCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(CoralPink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Activity",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "RECENT ACTIVITY",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "No threats detected today",
                                color = LavenderText,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(CoralPink, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
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
 * Dark plum card (#241239), white icon, coral circular chevron button (#FF3358), bold italic white title, soft lavender subtitle.
 */
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
            .height(118.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkPlumCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
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
                        .size(32.dp)
                        .background(CoralPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = LavenderText,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Cute vector drawing of Cyber Robot Mascot with shield
 */
@Composable
fun RobotMascot(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val headCenter = Offset(w * 0.45f, h * 0.42f)
        val headRadius = w * 0.30f

        // Cute antenna
        drawLine(
            color = Color.White,
            start = Offset(headCenter.x, headCenter.y - headRadius),
            end = Offset(headCenter.x, headCenter.y - headRadius * 1.45f),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = CoralPink,
            radius = headRadius * 0.18f,
            center = Offset(headCenter.x, headCenter.y - headRadius * 1.45f)
        )

        // Head
        drawCircle(
            color = Color.White,
            radius = headRadius,
            center = headCenter
        )

        // Visor
        drawRoundRect(
            color = DarkPlumCard,
            topLeft = Offset(headCenter.x - headRadius * 0.72f, headCenter.y - headRadius * 0.42f),
            size = Size(headRadius * 1.44f, headRadius * 0.72f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(headRadius * 0.32f)
        )

        // Happy Cyan eyes
        drawCircle(
            color = Color(0xFF00FFE0),
            radius = headRadius * 0.14f,
            center = Offset(headCenter.x - headRadius * 0.35f, headCenter.y - headRadius * 0.06f)
        )
        drawCircle(
            color = Color(0xFF00FFE0),
            radius = headRadius * 0.14f,
            center = Offset(headCenter.x + headRadius * 0.35f, headCenter.y - headRadius * 0.06f)
        )

        // Cute smile arc
        drawArc(
            color = CoralPink,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(headCenter.x - headRadius * 0.35f, headCenter.y + headRadius * 0.12f),
            size = Size(headRadius * 0.70f, headRadius * 0.38f),
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        // Little body
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(headCenter.x - headRadius * 0.65f, headCenter.y + headRadius * 0.78f),
            size = Size(headRadius * 1.30f, headRadius * 0.70f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(headRadius * 0.35f)
        )

        // Coral chest collar
        drawRoundRect(
            color = CoralPink,
            topLeft = Offset(headCenter.x - headRadius * 0.50f, headCenter.y + headRadius * 0.76f),
            size = Size(headRadius * 1.00f, headRadius * 0.16f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(headRadius * 0.1f)
        )

        // Shield on right
        val shieldCenter = Offset(w * 0.80f, h * 0.68f)
        val sW = w * 0.34f
        val sH = h * 0.36f

        val shieldPath = Path().apply {
            moveTo(shieldCenter.x, shieldCenter.y - sH * 0.48f)
            lineTo(shieldCenter.x + sW * 0.48f, shieldCenter.y - sH * 0.30f)
            quadraticTo(shieldCenter.x + sW * 0.45f, shieldCenter.y + sH * 0.15f, shieldCenter.x, shieldCenter.y + sH * 0.50f)
            quadraticTo(shieldCenter.x - sW * 0.45f, shieldCenter.y + sH * 0.15f, shieldCenter.x - sW * 0.48f, shieldCenter.y - sH * 0.30f)
            close()
        }

        drawPath(path = shieldPath, color = Color.White)

        val innerPath = Path().apply {
            moveTo(shieldCenter.x, shieldCenter.y - sH * 0.42f)
            lineTo(shieldCenter.x + sW * 0.40f, shieldCenter.y - sH * 0.26f)
            quadraticTo(shieldCenter.x + sW * 0.38f, shieldCenter.y + sH * 0.12f, shieldCenter.x, shieldCenter.y + sH * 0.44f)
            quadraticTo(shieldCenter.x - sW * 0.38f, shieldCenter.y + sH * 0.12f, shieldCenter.x - sW * 0.40f, shieldCenter.y - sH * 0.26f)
            close()
        }
        drawPath(path = innerPath, color = CoralPink)

        // Checkmark inside shield
        val checkPath = Path().apply {
            moveTo(shieldCenter.x - sW * 0.16f, shieldCenter.y - sH * 0.02f)
            lineTo(shieldCenter.x - sW * 0.03f, shieldCenter.y + sH * 0.12f)
            lineTo(shieldCenter.x + sW * 0.18f, shieldCenter.y - sH * 0.12f)
        }
        drawPath(path = checkPath, color = Color.White, style = Stroke(width = 6f, cap = StrokeCap.Round))
    }
}

/**
 * Vector drawing of phone outline with magnifying glass lens inside
 */
@Composable
fun PhoneScanIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val phoneLeft = w * 0.15f
        val phoneTop = h * 0.08f
        val phoneW = w * 0.60f
        val phoneH = h * 0.82f

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(phoneLeft, phoneTop),
            size = Size(phoneW, phoneH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f),
            style = Stroke(width = 6f)
        )

        val magCenter = Offset(phoneLeft + phoneW * 0.50f, phoneTop + phoneH * 0.46f)
        val magRadius = phoneW * 0.28f

        drawCircle(
            color = Color.White,
            radius = magRadius,
            center = magCenter,
            style = Stroke(width = 6f)
        )

        val handleStart = Offset(magCenter.x + magRadius * 0.68f, magCenter.y + magRadius * 0.68f)
        val handleEnd = Offset(handleStart.x + magRadius * 0.70f, handleStart.y + magRadius * 0.70f)
        drawLine(
            color = Color.White,
            start = handleStart,
            end = handleEnd,
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
    }
}
