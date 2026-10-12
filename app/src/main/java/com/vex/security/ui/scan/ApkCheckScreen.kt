package com.vex.security.ui.scan

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.repository.ThreatRepository
import com.vex.security.scanner.ApkScanManager
import com.vex.security.scanner.ApkScanResult
import com.vex.security.ui.components.ThreatExplanationCard
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApkCheckScreen(
    onBackPressed: () -> Unit = {},
    onSelectApkClicked: () -> Unit = {},
    onNavigateToDetail: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf("APK_FILE") } // "APK_FILE" or "INSTALLED_APPS"
    var selectedApk by remember { mutableStateOf<Uri?>(null) }
    var scanning by remember { mutableStateOf(false) }
    var scanningMessage by remember { mutableStateOf("Scanning APK...") }
    var apkResult by remember { mutableStateOf<ApkScanResult?>(ApkScanManager.lastScannedResult) }
    var installedApps by remember { mutableStateOf<List<PackageInfo>>(emptyList()) }
    var loadingApps by remember { mutableStateOf(false) }

    // Load installed apps when switching to INSTALLED_APPS tab
    LaunchedEffect(activeTab) {
        if (activeTab == "INSTALLED_APPS" && installedApps.isEmpty()) {
            loadingApps = true
            withContext(Dispatchers.IO) {
                try {
                    val pm = context.packageManager
                    val pkgs = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
                    installedApps = pkgs.sortedWith(
                        compareBy<PackageInfo> {
                            (it.applicationInfo?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM != 0
                        }.thenBy { it.applicationInfo?.loadLabel(pm)?.toString() ?: it.packageName }
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            loadingApps = false
        }
    }

    val apkPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedApk = uri
            scanning = true
            scanningMessage = "Deep scanning APK package..."
            apkResult = null

            scope.launch {
                try {
                    val result = ApkScanManager.scan(context, uri)
                    apkResult = result

                    try {
                        ThreatRepository.sendThreatEvent(
                            deviceId = DeviceIdentity.getDeviceId(context),
                            threatType = "APK",
                            riskScore = result.riskScore,
                            riskLevel = result.riskLevel,
                            action = result.action,
                            status = "OPEN",
                            indicators = result.indicators,
                            source = "ANDROID_APK_SCAN",
                            explanation = result.explanation.reasons.joinToString(" | "),
                            recommendation = result.explanation.recommendation
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    scanning = false
                }
            }
        }
    }

    val scanApp: (PackageInfo) -> Unit = { pkg ->
        scanning = true
        scanningMessage = "Analyzing permissions & behavior for ${pkg.packageName}..."
        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    ApkScanManager.scanInstalledPackage(context, pkg)
                }
                apkResult = result

                try {
                    ThreatRepository.sendThreatEvent(
                        deviceId = DeviceIdentity.getDeviceId(context),
                        threatType = "APK",
                        riskScore = result.riskScore,
                        riskLevel = result.riskLevel,
                        action = result.action,
                        status = "OPEN",
                        indicators = result.indicators,
                        source = "ANDROID_INSTALLED_APP_SCAN",
                        explanation = result.explanation.reasons.joinToString(" | "),
                        recommendation = result.explanation.recommendation
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                scanning = false
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
                            text = "APK & APP CHECK",
                            color = Color(0xFFFFFFFF),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Scan APK files and installed device applications",
                            color = Color(0xFF80A0B0),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Sub Tabs: APK FILE vs INSTALLED APPS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp,
                            if (activeTab == "APK_FILE") Color(0xFF00FFE0) else Color(0xFF005566),
                            RoundedCornerShape(12.dp)
                        )
                        .background(
                            if (activeTab == "APK_FILE") Color(0xFF0A2530) else Color(0xFF0A141A),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { activeTab = "APK_FILE" }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "APK FILE SCAN",
                        color = if (activeTab == "APK_FILE") Color(0xFF00FFE0) else Color(0xFF80A0B0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp,
                            if (activeTab == "INSTALLED_APPS") Color(0xFF00FFE0) else Color(0xFF005566),
                            RoundedCornerShape(12.dp)
                        )
                        .background(
                            if (activeTab == "INSTALLED_APPS") Color(0xFF0A2530) else Color(0xFF0A141A),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { activeTab = "INSTALLED_APPS" }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "INSTALLED APPS",
                        color = if (activeTab == "INSTALLED_APPS") Color(0xFF00FFE0) else Color(0xFF80A0B0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (activeTab == "APK_FILE") {
            // Top Banner Card ("Scan any APK file")
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF00FFE0).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                                        text = "Scan Any APK Package",
                                        color = Color(0xFFFFFFFF),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Detect trojans, dangerous permissions,\nand banking overlay vulnerabilities.",
                                        color = Color(0xFF80A0B0),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                apkPicker.launch(arrayOf("*/*"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF007788), Color(0xFF00FFE0))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = "Folder",
                                        tint = Color(0xFF000810),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "CHOOSE APK FROM STORAGE",
                                        color = Color(0xFF000810),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Installed Apps Tab
            if (loadingApps) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00FFE0))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Reading installed packages...", color = Color(0xFF80A0B0), fontSize = 12.sp)
                    }
                }
            } else {
                item {
                    Text(
                        text = "Installed Packages (${installedApps.size})",
                        color = Color(0xFF00FFE0),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(installedApps.take(30)) { pkg ->
                    val pm = context.packageManager
                    val appName = pkg.applicationInfo?.loadLabel(pm)?.toString() ?: pkg.packageName
                    val isSystem = (pkg.applicationInfo?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM != 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF003344), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color(0xFF0A1F28), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Android,
                                        contentDescription = null,
                                        tint = if (isSystem) Color(0xFF608090) else Color(0xFF00FFE0),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = appName,
                                        color = Color(0xFFFFFFFF),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = pkg.packageName,
                                        color = Color(0xFF608090),
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "v${pkg.versionName ?: "1.0"} · ${if (isSystem) "System App" else "User App"}",
                                        color = if (isSystem) Color(0xFF608090) else Color(0xFF35E08A),
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { scanApp(pkg) },
                                modifier = Modifier.height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A2530)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FFE0)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("SCAN", color = Color(0xFF00FFE0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (scanning) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF00FFE0))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(scanningMessage, color = Color(0xFF00FFE0), fontSize = 12.sp)
                }
            }
        }

        // Scan Results Card
        apkResult?.let { result ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF005566), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1F28))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ANALYSIS RESULT",
                                color = Color(0xFF00FFE0),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            // Button to View Full Deep Report
                            Button(
                                onClick = { onNavigateToDetail() },
                                modifier = Modifier.height(32.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005566)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("Full Report", color = Color(0xFF00FFE0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF00FFE0), modifier = Modifier.size(12.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Target: ${result.metadata.fileName}", color = Color(0xFFFFFFFF), fontWeight = FontWeight.Bold)
                        result.packageName?.let {
                            Text("Package: $it", color = Color(0xFF80A0B0), fontSize = 12.sp)
                        }
                        result.versionName?.let {
                            Text("Version: $it", color = Color(0xFF80A0B0), fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("RISK SCORE", color = Color(0xFF80A0B0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("${result.riskScore} / 100", color = Color(0xFF00FFE0), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("RISK LEVEL", color = Color(0xFF80A0B0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    result.riskLevel,
                                    color = when (result.riskLevel) {
                                        "SAFE" -> Color(0xFF35E08A)
                                        "WARN" -> Color(0xFFFFD166)
                                        "BLOCK" -> Color(0xFFFF9F43)
                                        else -> Color(0xFFFF4D6D)
                                    },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ACTION: ${result.action}", color = Color(0xFFFFFFFF), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Requested Permissions (${result.permissions.size})",
                            color = Color(0xFF00FFE0),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val dangerousPermissions = setOf(
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

                        result.permissions.take(8).forEach { permission ->
                            val isDangerous = permission in dangerousPermissions

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF061017))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = permission.substringAfterLast("."),
                                        color = Color(0xFFFFFFFF),
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = if (isDangerous) "DANGEROUS" else "NORMAL",
                                        color = if (isDangerous) Color(0xFFFF4D6D) else Color(0xFF35E08A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        if (result.permissions.size > 8) {
                            Text(
                                text = "+ ${result.permissions.size - 8} more permissions (view in Full Report)",
                                color = Color(0xFF80A0B0),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ThreatExplanationCard(
                            explanation = result.explanation,
                            indicators = result.indicators
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
fun SummaryCard(title: String, count: String, color: Color, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = count,
                    color = color,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color(0xFF80A0B0),
                fontSize = 11.sp
            )
        }
    }
}
