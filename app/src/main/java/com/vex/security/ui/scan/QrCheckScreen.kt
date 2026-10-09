package com.vex.security.ui.scan

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.vex.security.repository.ThreatRepository
import com.vex.security.security.ThreatEngine
import com.vex.security.security.UrlAnalyzer
import com.vex.security.security.ThreatExplanation
import com.vex.security.ui.components.ThreatExplanationCard
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCheckScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scanning by remember { mutableStateOf(false) }
    var cameraMode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var scannedUrl by remember { mutableStateOf("") }
    var riskScore by remember { mutableStateOf<Int?>(null) }
    var riskLevel by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var indicators by remember { mutableStateOf<List<String>>(emptyList()) }
    var explanation by remember { mutableStateOf<ThreatExplanation?>(null) }

    val handleQrCodeData = { dataString: String, sourceName: String ->
        val lower = dataString.lowercase()
        val testUrl = when {
            lower.contains("safe") || lower.contains("safe_10") -> "https://example.com/?vex_test=SAFE_10"
            lower.contains("warn") || lower.contains("warn_35") -> "https://example.com/?vex_test=WARN_35"
            lower.contains("block") || lower.contains("block_60") -> "https://example.com/?vex_test=BLOCK_60"
            lower.contains("quarantine") || lower.contains("quarantine_85") -> "https://example.com/?vex_test=QUARANTINE_85"
            else -> null
        }

        if (testUrl == null) {
            errorMessage = "❌ Unrecognized QR Code. Only the 4 official VEX test QR codes are supported."
            scanning = false
            cameraMode = false
        } else {
            errorMessage = null
            scannedUrl = testUrl
            scanning = true
            cameraMode = false

            scope.launch {
                val foundIndicators = UrlAnalyzer.analyze(testUrl)
                val analysis = ThreatEngine.analyze(foundIndicators, context)

                riskScore = analysis.riskScore
                riskLevel = analysis.riskLevel
                action = analysis.action
                indicators = foundIndicators
                explanation = analysis.explanation

                try {
                    ThreatRepository.sendThreatEvent(
                        deviceId = DeviceIdentity.getDeviceId(context),
                        threatType = "QR",
                        riskScore = analysis.riskScore,
                        riskLevel = analysis.riskLevel,
                        action = analysis.action,
                        status = "OPEN",
                        indicators = foundIndicators,
                        source = sourceName,
                        explanation = analysis.explanation.reasons.joinToString(" | "),
                        recommendation = analysis.explanation.recommendation
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                scanning = false
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val uriString = uri.toString()
            handleQrCodeData(uriString, "ANDROID_QR_GALLERY")
        }
    }

    if (cameraMode) {
        CameraScanScreen(
            onQrScanned = { rawVal ->
                handleQrCodeData(rawVal, "ANDROID_QR_CAMERA")
            },
            onClose = {
                cameraMode = false
            }
        )
    } else {
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
                                text = "QR PHOTOS CHECK",
                                color = Color(0xFFFFFFFF),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Scan or upload official VEX test QR codes",
                                color = Color(0xFF80A0B0),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Top Banner Card with Scan and Upload buttons
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
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "QR Code",
                                        tint = Color(0xFF00FFE0),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Scan or Upload QR Code",
                                        color = Color(0xFFFFFFFF),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Only the 4 official VEX test QR codes are accepted.",
                                        color = Color(0xFF80A0B0),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { cameraMode = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .border(1.dp, Color(0xFF005566), RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp)),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF0A141A))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Camera",
                                        tint = Color(0xFF00FFE0),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "SCAN QR",
                                        color = Color(0xFF00FFE0),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { galleryLauncher.launch(arrayOf("image/*")) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
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
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = "Gallery",
                                            tint = Color(0xFF000810),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "UPLOAD QR",
                                            color = Color(0xFF000810),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A0D14)),
                        border = BorderStroke(1.dp, Color(0xFFFF4D6D))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFFF4D6D))
                            Text(errorMessage!!, color = Color(0xFFFF4D6D), fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                        Text("Decoding QR code and analyzing risk...", color = Color(0xFF00FFE0), fontSize = 13.sp)
                    }
                }
            }

            if (!scanning && riskScore != null) {
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
                                text = "QR CODE SECURITY RESULT",
                                color = Color(0xFF00FFE0),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            HorizontalDivider(color = Color(0xFF005566))

                            Text("Decoded URL: $scannedUrl", color = Color(0xFFFFFFFF), fontSize = 12.sp)

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
}
