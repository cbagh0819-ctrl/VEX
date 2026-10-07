package com.vex.security.ui.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
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

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.vex.security.scanner.ApkScanResult
import com.vex.security.scanner.ApkScanManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApkCheckScreen(
    onBackPressed: () -> Unit = {},
    onSelectApkClicked: () -> Unit = {},
    onAppItemClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedApk by remember { mutableStateOf<Uri?>(null) }
    var scanning by remember { mutableStateOf(false) }
    var apkResult by remember { mutableStateOf<ApkScanResult?>(null) }

    val apkPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {

            selectedApk = uri
            scanning = true
            apkResult = null

            scope.launch {

                try {

                    val result = ApkScanManager.scan(
                        context,
                        uri
                    )

                    apkResult = result

                } catch (e: Exception) {

                    e.printStackTrace()

                } finally {

                    scanning = false
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
                            text = "APK CHECK",
                            color = Color(0xFFFFFFFF),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Scan and analyze apps and APK files",
                            color = Color(0xFF80A0B0),
                            fontSize = 11.sp
                        )
                    }
                }

                // Scan APK Pill Button
                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(20.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(20.dp))
                        .clickable {
                            apkPicker.launch(
                                arrayOf("application/vnd.android.package-archive")
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Scan",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Scan APK",
                            color = Color(0xFF00FFE0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Top Banner Card ("Scan any APK file")
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF00FFE0).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
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
                                    text = "Scan any APK file",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Detect malware, risky permissions,\nsuspicious behavior and more.",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Go",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            apkPicker.launch(
                                arrayOf("application/vnd.android.package-archive")
                            )
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
                                    text = "SELECT APK FILE",
                                    color = Color(0xFF000810),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    if (scanning) {

                        Spacer(modifier = Modifier.height(12.dp))

                        CircularProgressIndicator(color = Color(0xFF00FFE0))

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Scanning APK...", color = Color(0xFF00FFE0))

                    }

                    apkResult?.let { result ->

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1F28))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = "Selected APK",
                                    color = Color(0xFF00FFE0),
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("File: ${result.metadata.fileName}", color = Color(0xFFFFFFFF))

                                Text("Size: ${result.metadata.sizeBytes / 1024 / 1024} MB", color = Color(0xFF80A0B0))

                                Text(
                                    "SHA-256: ${
                                        result.metadata.sha256 ?: "Unavailable"
                                    }",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Status: Scan Complete", color = Color(0xFF35E08A))

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Requested Permissions",
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
                                    "android.permission.CALL_PHONE"
                                )

                                result.permissions.forEach { permission ->

                                    val isDangerous =
                                        permission in dangerousPermissions

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF061017))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp)
                                        ) {

                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {

                                                Text(
                                                    text = permission.substringAfterLast("."),
                                                    color = Color(0xFFFFFFFF),
                                                    style = MaterialTheme.typography.bodyLarge
                                                )

                                                Text(
                                                    text = if (isDangerous)
                                                        "DANGEROUS"
                                                    else
                                                        "NORMAL",
                                                    color = if (isDangerous) Color(0xFFFF4D6D) else Color(0xFF35E08A),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        // Scan Summary Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Scan Summary",
                    color = Color(0xFFFFFFFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Total Scanned", count = "12", color = Color(0xFF00FFE0), icon = Icons.Default.Android)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Safe Apps", count = "8", color = Color(0xFF35E08A), icon = Icons.Default.Security)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Suspicious", count = "3", color = Color(0xFFFFD166), icon = Icons.Default.Warning)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Malicious", count = "1", color = Color(0xFFFF4D6D), icon = Icons.Default.BugReport)
                    }
                }
            }
        }

        // Risk Category Sections
        item {
            RiskCategoryCard(
                category = "LOW",
                count = "2 apps",
                subtitle = "No significant threats found",
                dotColor = Color(0xFF35E08A),
                borderColor = Color(0xFF005566),
                apps = listOf(
                    AppItem("WhatsApp", "com.whatsapp", "LOW RISK", Color(0xFF35E08A), Icons.AutoMirrored.Filled.Chat),
                    AppItem("Google Drive", "com.google.android.apps.drive", "LOW RISK", Color(0xFF35E08A), Icons.Default.Cloud)
                ),
                onAppItemClicked = onAppItemClicked
            )
        }

        item {
            RiskCategoryCard(
                category = "MEDIUM",
                count = "3 apps",
                subtitle = "Contains suspicious permissions",
                dotColor = Color(0xFFFFD166),
                borderColor = Color(0xFF554400),
                apps = listOf(
                    AppItem("File Manager", "com.mi.android.filemanager", "MEDIUM RISK", Color(0xFFFFD166), Icons.Default.Folder),
                    AppItem("Video Player", "com.video.player", "MEDIUM RISK", Color(0xFFFFD166), Icons.Default.PlayArrow)
                ),
                onAppItemClicked = onAppItemClicked
            )
        }

        item {
            RiskCategoryCard(
                category = "HIGH",
                count = "1 app",
                subtitle = "Potentially harmful behavior detected",
                dotColor = Color(0xFFFF9F43),
                borderColor = Color(0xFF663300),
                apps = listOf(
                    AppItem("Unknown App", "com.unknown.app", "HIGH RISK", Color(0xFFFF9F43), Icons.Default.Android)
                ),
                onAppItemClicked = onAppItemClicked
            )
        }

        item {
            RiskCategoryCard(
                category = "CRITICAL",
                count = "1 app",
                subtitle = "Malware detected",
                dotColor = Color(0xFFFF4D6D),
                borderColor = Color(0xFF660011),
                apps = listOf(
                    AppItem("MaliciousApp.apk", "com.malware.sample", "CRITICAL", Color(0xFFFF4D6D), Icons.Default.Warning)
                ),
                onAppItemClicked = onAppItemClicked
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
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
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
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

data class AppItem(
    val name: String,
    val packageName: String,
    val riskLabel: String,
    val riskColor: Color,
    val icon: ImageVector
)

@Composable
fun RiskCategoryCard(
    category: String,
    count: String,
    subtitle: String,
    dotColor: Color,
    borderColor: Color,
    apps: List<AppItem>,
    onAppItemClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
                            .size(10.dp)
                            .background(dotColor, shape = RoundedCornerShape(50))
                    )
                    Text(
                        text = category,
                        color = dotColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(20.dp))
                            .background(Color(0xFF0A141A), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("VIEW ALL", color = Color(0xFF00FFE0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF00FFE0), modifier = Modifier.size(12.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFF0A1F28), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Expand",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$count · $subtitle",
                color = Color(0xFF80A0B0),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                apps.forEach { app ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF003344), RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onAppItemClicked() },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF060F15))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
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
                                        .background(Color(0xFF0A1F28), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = app.icon,
                                        contentDescription = app.name,
                                        tint = Color(0xFF00FFE0),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = app.name,
                                        color = Color(0xFFFFFFFF),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = app.packageName,
                                        color = Color(0xFF608090),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .border(1.dp, app.riskColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .background(app.riskColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = app.riskLabel,
                                    color = app.riskColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
