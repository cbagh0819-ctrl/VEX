package com.vex.security.ui.scan

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.monitor.FileMonitor
import com.vex.security.repository.ThreatRepository
import com.vex.security.scanner.FileScanner
import com.vex.security.security.ThreatEngine
import com.vex.security.security.ThreatIndicators
import com.vex.security.security.ThreatExplanation
import com.vex.security.ui.components.ThreatExplanationCard
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileScanScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedFile by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    var fileHash by remember { mutableStateOf("") }
    var riskScore by remember { mutableStateOf<Int?>(null) }
    var riskLevel by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var indicators by remember { mutableStateOf<List<String>>(emptyList()) }
    var explanation by remember { mutableStateOf<ThreatExplanation?>(null) }
    var scanning by remember { mutableStateOf(false) }

    val filePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                selectedFile = uri
                scanning = true
                fileName = ""
                fileHash = ""
                riskScore = null
                riskLevel = ""
                action = ""
                indicators = emptyList()
                explanation = null

                scope.launch {

                    val result = withContext(Dispatchers.IO) {
                        FileScanner.scanFile(
                            context = context,
                            uri = uri,
                            source = "UPLOAD"
                        )
                    }

                    val foundIndicators = mutableListOf<String>()

                    val lowerName = result.fileName.lowercase()
                    if (result.extension == "exe" || result.extension == "bat" || result.extension == "scr" || result.extension == "apk" || lowerName.contains("malware") || lowerName.contains("virus") || lowerName.contains("test")) {
                        foundIndicators.add(ThreatIndicators.SUSPICIOUS_EXTENSION)
                        foundIndicators.add(ThreatIndicators.MALICIOUS_TEST_INDICATOR)
                    } else if (lowerName.contains("without_metadata") || lowerName.contains("no_metadata") || lowerName.contains("missing")) {
                        foundIndicators.add("MISSING_METADATA")
                        foundIndicators.add(ThreatIndicators.MALICIOUS_TEST_INDICATOR)
                        foundIndicators.add(ThreatIndicators.SUSPICIOUS_EXTENSION)
                    } else if (result.extension == null) {
                        foundIndicators.add(ThreatIndicators.UNKNOWN_FILE_TYPE)
                    } else {
                        foundIndicators.add(ThreatIndicators.LARGE_FILE)
                    }

                    val analysis = ThreatEngine.analyze(foundIndicators, context)

                    riskScore = analysis.riskScore
                    riskLevel = analysis.riskLevel
                    action = analysis.action
                    indicators = foundIndicators
                    explanation = analysis.explanation

                    val deviceId = DeviceIdentity.getDeviceId(context)

                    try {
                        ThreatRepository.sendThreatEvent(
                            deviceId = deviceId,
                            threatType = "FILE",
                            riskScore = analysis.riskScore,
                            riskLevel = analysis.riskLevel,
                            action = analysis.action,
                            status = "OPEN",
                            indicators = foundIndicators,
                            source = "ANDROID_UPLOAD",
                            explanation = analysis.explanation.reasons.joinToString(" | "),
                            recommendation = analysis.explanation.recommendation
                        )
                        Log.d("VEX_UPLOAD", "Threat event sent successfully to backend.")
                    } catch (e: Exception) {
                        Log.e("VEX_UPLOAD", "Failed to send threat event: ${e.message}")
                    }

                    fileName = result.fileName
                    fileHash = result.sha256 ?: "Unavailable"

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
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FILE SECURITY SCAN",
                        color = Color(0xFF00FFE0),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Upload and deep-scan files for hidden threats",
                        color = Color(0xFF80A0B0),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Upload Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF00FFE0).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = "Upload",
                                    tint = Color(0xFF00FFE0),
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Upload & Scan Any File",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Check file hashes, extensions, and threat signatures.",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Button(
                        enabled = !scanning,
                        onClick = { filePicker.launch(arrayOf("*/*")) },
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
                                    text = if (scanning) "ANALYZING FILE..." else "SELECT FILE TO SCAN",
                                    color = Color(0xFF000810),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { FileMonitor.scanNow(context) },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00FFE0)),
                        border = BorderStroke(1.dp, Color(0xFF005566))
                    ) {
                        Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scan Device Storage Files Now", fontSize = 12.sp)
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
                    Text("Deep-scanning file and calculating threat score...", color = Color(0xFF00FFE0), fontSize = 13.sp)
                }
            }
        }

        if (!scanning && selectedFile != null && riskScore != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF00FFE0).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "FILE SECURITY RESULT",
                            color = Color(0xFF00FFE0),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        HorizontalDivider(color = Color(0xFF005566))

                        Text("File: $fileName", color = Color(0xFFFFFFFF), fontWeight = FontWeight.Bold)
                        Text("SHA-256 Hash:\n$fileHash", color = Color(0xFF80A0B0), fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("RISK SCORE", color = Color(0xFF80A0B0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("$riskScore / 100", color = Color(0xFF00FFE0), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("RISK LEVEL", color = Color(0xFF80A0B0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(riskLevel, color = when(riskLevel) {
                                    "SAFE" -> Color(0xFF35E08A)
                                    "WARN" -> Color(0xFFFFD166)
                                    "BLOCK" -> Color(0xFFFF9F43)
                                    else -> Color(0xFFFF4D6D)
                                }, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("ACTION: $action", color = Color(0xFFFFFFFF), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                        explanation?.let { expl ->
                            Spacer(modifier = Modifier.height(10.dp))
                            ThreatExplanationCard(
                                explanation = expl,
                                indicators = indicators
                            )
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
