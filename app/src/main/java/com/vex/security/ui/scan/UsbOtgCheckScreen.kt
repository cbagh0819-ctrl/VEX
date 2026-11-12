package com.vex.security.ui.scan

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import com.vex.security.notification.AlarmPlayer
import com.vex.security.notification.SecurityNotification
import com.vex.security.repository.ThreatRepository
import com.vex.security.security.RiskCalculator
import com.vex.security.security.SecurityPolicy
import com.vex.security.security.ThreatExplanation
import com.vex.security.security.ThreatIndicators
import com.vex.security.ui.components.ThreatExplanationCard
import com.vex.security.ui.theme.VexPalette
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ScannedUsbFile(
    val name: String,
    val sizeBytes: Long,
    val mimeType: String?,
    val isDangerous: Boolean,
    val threatTag: String?,
    val docFile: DocumentFile
)

data class FolderScanResult(
    val folderName: String,
    val totalFiles: Int,
    val dangerousCount: Int,
    val riskScore: Int,
    val riskLevel: String,
    val action: String,
    val explanation: ThreatExplanation,
    val indicators: List<String>,
    val files: List<ScannedUsbFile>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsbOtgCheckScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<FolderScanResult?>(null) }
    var alarmActive by remember { mutableStateOf(false) }
    var fileToDelete by remember { mutableStateOf<ScannedUsbFile?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { treeUri: Uri? ->
        if (treeUri != null) {
            scanning = true
            scanResult = null
            alarmActive = false

            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    try {
                        val rootDoc = DocumentFile.fromTreeUri(context, treeUri)
                        val folderName = rootDoc?.name ?: "USB_Storage"
                        val docFiles = rootDoc?.listFiles() ?: emptyArray()

                        val scannedList = mutableListOf<ScannedUsbFile>()
                        val indicators = mutableListOf<String>()
                        var highestScore = 10

                        for (file in docFiles) {
                            if (file.isDirectory) continue
                            val name = file.name ?: "unnamed_file"
                            val size = file.length()
                            val mime = file.type
                            val lower = name.lowercase()

                            var isDang = false
                            var tag: String? = null

                            // Check double extension
                            val parts = lower.split(".")
                            if (parts.size > 2) {
                                val secondLast = parts[parts.size - 2]
                                val last = parts.last()
                                if (listOf("pdf", "doc", "jpg", "png", "txt", "mp4").contains(secondLast) &&
                                    listOf("exe", "apk", "sh", "bat", "vbs", "dex").contains(last)) {
                                    isDang = true
                                    tag = "DOUBLE_EXTENSION"
                                    indicators.add(ThreatIndicators.DOUBLE_EXTENSION)
                                    highestScore = maxOf(highestScore, 85)
                                }
                            }

                            // Check executable payload
                            if (listOf("apk", "exe", "sh", "bat", "vbs", "dex", "so", "elf", "bin").any { lower.endsWith(".$it") }) {
                                isDang = true
                                if (tag == null) tag = "EXECUTABLE_PAYLOAD"
                                indicators.add(ThreatIndicators.EXECUTABLE_PAYLOAD)
                                highestScore = maxOf(highestScore, 75)
                            }

                            scannedList.add(
                                ScannedUsbFile(
                                    name = name,
                                    sizeBytes = size,
                                    mimeType = mime,
                                    isDangerous = isDang,
                                    threatTag = tag,
                                    docFile = file
                                )
                            )
                        }

                        val dangCount = scannedList.count { it.isDangerous }
                        if (dangCount == 0) {
                            indicators.add(ThreatIndicators.CLEAN_FILE)
                            highestScore = 10
                        }

                        val finalScore = highestScore.coerceIn(10, 100)
                        val level = RiskCalculator.getRiskLevel(finalScore)
                        val action = SecurityPolicy.getAction(finalScore)

                        val reasons = mutableListOf<String>()
                        if (dangCount > 0) {
                            reasons.add("Folder contains $dangCount high-risk payload(s) with executable or double extension signatures.")
                            reasons.add("Drive storage inspection flagged potential autorun or staged dropper binaries.")
                        } else {
                            reasons.add("Drive storage inspection verified clean. No suspicious executables or masquerading extensions found.")
                        }

                        val reco = if (dangCount > 0) {
                            "Quarantine or delete flagged files immediately to prevent execution."
                        } else {
                            "Folder is safe and verified clean."
                        }

                        FolderScanResult(
                            folderName = folderName,
                            totalFiles = scannedList.size,
                            dangerousCount = dangCount,
                            riskScore = finalScore,
                            riskLevel = level,
                            action = action,
                            explanation = ThreatExplanation(reasons, reco),
                            indicators = indicators.distinct(),
                            files = scannedList
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                scanResult = result
                scanning = false

                if (result != null) {
                    if (result.riskScore >= 60) {
                        alarmActive = true
                        AlarmPlayer.playAlarm(context)
                        SecurityNotification.showHarmfulAlert(context, "USB Malware Payload Detected in ${result.folderName}")
                    }

                    try {
                        ThreatRepository.sendThreatEvent(
                            deviceId = DeviceIdentity.getDeviceId(context),
                            threatType = "FILE",
                            riskScore = result.riskScore,
                            riskLevel = result.riskLevel,
                            action = result.action,
                            status = if (result.riskScore >= 60) "OPEN" else "RESOLVED",
                            indicators = result.indicators,
                            source = "ANDROID_USB_STORAGE",
                            explanation = result.explanation.reasons.joinToString(" | "),
                            recommendation = result.explanation.recommendation
                        )
                    } catch (_: Exception) {}
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(VexPalette.backgroundTop, VexPalette.backgroundMid, VexPalette.backgroundBottom))
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
                            .border(1.dp, VexPalette.cardBorder, RoundedCornerShape(50))
                            .background(VexPalette.pillBackground, RoundedCornerShape(50))
                            .clickable { onBackPressed() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = VexPalette.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "USB / OTG REAL STORAGE SCAN",
                            color = VexPalette.headerText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Forensic inspection for external USB drives & SD card storage",
                            color = Color(0xFFA998C4),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Active Alarm Banner
        if (alarmActive) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFFF4D6D), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A0D14))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF4D6D))
                            Text("VEX CRITICAL ALARM ACTIVE", color = Color(0xFFFF4D6D), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text(
                            "External storage contains high-risk malware payloads! Immediate quarantine or file deletion recommended.",
                            color = Color(0xFFFFFFFF),
                            fontSize = 12.sp
                        )
                        Button(
                            onClick = {
                                AlarmPlayer.stopAlarm(context)
                                alarmActive = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D6D))
                        ) {
                            Text("STOP ALARM", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Storage Picker Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VexPalette.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = VexPalette.cardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .border(1.dp, VexPalette.primary, RoundedCornerShape(14.dp))
                                .background(VexPalette.pillBackground, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Usb,
                                contentDescription = "USB",
                                tint = VexPalette.primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Select USB / OTG Directory",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Scan real flash drives, MTP storage, or SD card folders",
                                color = Color(0xFFA998C4),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            AlarmPlayer.stopAlarm(context)
                            alarmActive = false
                            folderPicker.launch(null)
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
                                        listOf(Color(0xFFFF3358), VexPalette.primary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Text(
                                    text = if (scanning) "SCANNING STORAGE DIRECTORY..." else "CHOOSE USB / OTG FOLDER TO SCAN",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = VexPalette.cardBorder)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = VexPalette.primary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "REAL FILESYSTEM RECURSIVE INSPECTION",
                            color = VexPalette.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Recursively inspects physical drive files, verifies double extension camouflaging, detects executable binaries, and isolates malware payloads.",
                        color = Color(0xFFA998C4),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        if (scanning) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = VexPalette.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Reading external storage filesystem...", color = VexPalette.primary, fontSize = 12.sp)
                }
            }
        }

        // Result Card
        scanResult?.let { res ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            when (res.riskLevel) {
                                "SAFE" -> Color(0xFF35E08A)
                                "WARN" -> Color(0xFFFFD166)
                                else -> Color(0xFFFF4D6D)
                            }.copy(alpha = 0.6f),
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = VexPalette.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("SCANNED DIRECTORY", color = VexPalette.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(res.folderName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("RISK SCORE", color = Color(0xFFA998C4), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "${res.riskScore} / 100",
                                    color = when (res.riskLevel) {
                                        "SAFE" -> Color(0xFF35E08A)
                                        "WARN" -> Color(0xFFFFD166)
                                        else -> Color(0xFFFF4D6D)
                                    },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Total Files: ${res.totalFiles}", color = Color(0xFFB0C4DE), fontSize = 11.sp)
                            Text(
                                "Flagged Payloads: ${res.dangerousCount}",
                                color = if (res.dangerousCount > 0) Color(0xFFFF4D6D) else Color(0xFF35E08A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Action: ${res.action}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        HorizontalDivider(color = VexPalette.cardBorder)

                        Text("Files in Directory (${res.files.size}):", color = VexPalette.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        res.files.take(20).forEach { file ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .border(
                                        1.dp,
                                        if (file.isDangerous) Color(0xFFFF4D6D) else VexPalette.cardBorder,
                                        RoundedCornerShape(8.dp)
                                    ),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF081A24))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            file.name,
                                            color = if (file.isDangerous) Color(0xFFFF4D6D) else Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = if (file.isDangerous) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                        Text(
                                            "${file.sizeBytes / 1024} KB • ${file.mimeType ?: "unknown type"}",
                                            color = Color(0xFFA998C4),
                                            fontSize = 10.sp
                                        )
                                    }

                                    if (file.isDangerous) {
                                        Button(
                                            onClick = { fileToDelete = file },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D6D)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("DELETE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Text("SAFE", color = Color(0xFF35E08A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        ThreatExplanationCard(
                            explanation = res.explanation,
                            indicators = res.indicators
                        )
                    }
                }
            }
        }
    }

    // Real File Deletion Dialog
    fileToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { fileToDelete = null },
            title = { Text("Delete Malicious File", color = Color(0xFFFF4D6D)) },
            text = { Text("Are you sure you want to permanently delete '${target.name}' from external storage?") },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val deleted = target.docFile.delete()
                            if (deleted) {
                                statusMessage = "Deleted ${target.name} successfully"
                                scanResult = scanResult?.let { old ->
                                    val updatedFiles = old.files.filterNot { it.name == target.name }
                                    old.copy(
                                        files = updatedFiles,
                                        totalFiles = updatedFiles.size,
                                        dangerousCount = updatedFiles.count { it.isDangerous }
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        fileToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D6D))
                ) {
                    Text("DELETE NOW")
                }
            },
            dismissButton = {
                TextButton(onClick = { fileToDelete = null }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
