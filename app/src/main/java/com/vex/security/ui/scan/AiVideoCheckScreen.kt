package com.vex.security.ui.scan

import com.vex.security.ui.theme.VexPalette
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.repository.ThreatRepository
import com.vex.security.scanner.AiVideoAnalysis
import com.vex.security.scanner.RealAiVideoAnalyzer
import com.vex.security.security.ThreatExplanation
import com.vex.security.ui.components.ThreatExplanationCard
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiVideoCheckScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedVideo by remember { mutableStateOf<Uri?>(null) }
    var scanning by remember { mutableStateOf(false) }
    var scanningStep by remember { mutableStateOf("Initializing video stream...") }
    var analysisResult by remember { mutableStateOf<AiVideoAnalysis?>(null) }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideo = uri
            scanning = true
            analysisResult = null

            scope.launch {
                scanningStep = "Reading video container & atom headers..."
                delay(400)
                scanningStep = "Sampling temporal frames & optical sensor profile..."
                delay(500)
                scanningStep = "Checking neural synthesis patterns (Sora, Runway, Pika)..."

                val result = withContext(Dispatchers.IO) {
                    RealAiVideoAnalyzer.analyze(context, uri)
                }

                analysisResult = result

                try {
                    ThreatRepository.sendThreatEvent(
                        deviceId = DeviceIdentity.getDeviceId(context),
                        threatType = "AI_VIDEO",
                        riskScore = result.score,
                        riskLevel = result.level,
                        action = result.action,
                        status = "OPEN",
                        indicators = result.indicators,
                        source = "ANDROID_AI_VIDEO_CHECK",
                        explanation = result.reasons.joinToString(" | "),
                        recommendation = result.recommendation
                    )
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
                            text = "AI VIDEO CHECK",
                            color = VexPalette.headerText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Detect AI generated videos, deepfakes & synthetic media",
                            color = Color(0xFFA998C4),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Top Banner Card
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
                                    .border(1.dp, VexPalette.primary, RoundedCornerShape(14.dp))
                                    .background(VexPalette.pillBackground, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "AI Video",
                                    tint = VexPalette.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Scan Any Video Clip",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Deep-inspect video containers, frame temporal\ncadence, Sora/Runway markers & deepfakes.",
                                    color = Color(0xFFA998C4),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Button(
                        enabled = !scanning,
                        onClick = { videoPicker.launch(arrayOf("video/*")) },
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
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (scanning) "ANALYZING VIDEO..." else "SELECT VIDEO TO SCAN",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = VexPalette.cardBorder)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.MovieFilter, contentDescription = null, tint = VexPalette.primary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "FRAME SAMPLING & SIGHTENGINE VIDEO PIPELINE ACTIVE",
                            color = VexPalette.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Samples keyframes at 10%, 25%, 50%, and 75% video duration using MediaMetadataRetriever, checks MP4 container atoms, and runs deepfake / generative AI classification via backend Sightengine video API.",
                        color = Color(0xFFA998C4),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Scanning Progress Card
        if (scanning) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VexPalette.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = VexPalette.cardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VexPalette.primary)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = scanningStep,
                            color = VexPalette.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Analysis Results
        analysisResult?.let { res ->
            val levelColor = when (res.level) {
                "SAFE" -> Color(0xFF35E08A)
                "WARN" -> Color(0xFFFFD166)
                "BLOCK" -> Color(0xFFFF9F43)
                else -> Color(0xFFFF4D6D)
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, levelColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = VexPalette.cardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "VIDEO FORENSIC RESULT",
                            color = VexPalette.primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        HorizontalDivider(color = VexPalette.cardBorder)

                        // Preview Frame (Thumbnail extracted from video)
                        res.previewFrame?.let { bitmap ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, VexPalette.cardBorder, RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Sampled Video Frame",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                        .background(Color(0xFF000000).copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Sampled Frame @ 25%",
                                        color = VexPalette.primary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text("File: ${res.fileName}", color = Color(0xFFFFFFFF), fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        // Video specs row
                        val durationSec = if (res.durationMs > 0) String.format("%.1fs", res.durationMs / 1000f) else "Unknown"
                        val sizeMb = if (res.fileSize > 0) "${res.fileSize / 1024 / 1024} MB" else "Unknown"
                        val resStr = if (res.width > 0 && res.height > 0) "${res.width}x${res.height}" else "Standard"

                        Text(
                            text = "Duration: $durationSec  ·  Resolution: $resStr  ·  Size: $sizeMb",
                            color = Color(0xFFA998C4),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Risk Score and Level
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("RISK SCORE", color = Color(0xFFA998C4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("${res.score} / 100", color = levelColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("RISK LEVEL", color = Color(0xFFA998C4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(res.level, color = levelColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text("ACTION: ${res.action}", color = Color(0xFFFFFFFF), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(6.dp))

                        // Forensic Breakdown Items
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF07141C))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Technical Markers", color = VexPalette.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                VideoFeatureRow(
                                    label = "AI Generative Model",
                                    value = res.detectedAiModel ?: "None Detected",
                                    isWarning = res.detectedAiModel != null
                                )
                                VideoFeatureRow(
                                    label = "Camera Hardware Profile",
                                    value = res.cameraDevice ?: "Missing / Synthesized",
                                    isWarning = res.cameraDevice == null
                                )
                                VideoFeatureRow(
                                    label = "Audio Stream Presence",
                                    value = if (res.hasAudio) "Natural Audio Present" else "Silent / Synthesized",
                                    isWarning = !res.hasAudio
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        ThreatExplanationCard(
                            explanation = ThreatExplanation(res.reasons, res.recommendation),
                            indicators = res.indicators
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
fun VideoFeatureRow(label: String, value: String, isWarning: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFFA998C4), fontSize = 11.sp)
        Text(
            text = value,
            color = if (isWarning) Color(0xFFFF9F43) else Color(0xFF35E08A),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
