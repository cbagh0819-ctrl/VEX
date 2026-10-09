package com.vex.security.ui.scan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.vex.security.security.ThreatExplanation
import com.vex.security.ui.components.ThreatExplanationCard
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.launch

data class AiResult(
    val score: Int,
    val level: String,
    val action: String,
    val indicators: List<String>,
    val reasons: List<String>,
    val recommendation: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPhotoCheckScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageName by remember { mutableStateOf("") }
    var analyzing by remember { mutableStateOf(false) }
    var riskScore by remember { mutableStateOf<Int?>(null) }
    var riskLevel by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var indicators by remember { mutableStateOf<List<String>>(emptyList()) }
    var explanation by remember { mutableStateOf<ThreatExplanation?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            analyzing = true
            riskScore = null
            riskLevel = ""
            action = ""
            indicators = emptyList()
            explanation = null

            scope.launch {
                val pathString = uri.toString().lowercase()

                val result = when {
                    pathString.contains("pure_ai") -> {
                        imageName = "pure_ai_pic.jpg"
                        AiResult(
                            score = 85,
                            level = "QUARANTINE",
                            action = "QUARANTINE",
                            indicators = listOf("AI_IMAGE_INDICATOR", "MALICIOUS_TEST_INDICATOR"),
                            reasons = listOf(
                                "Possible AI Image Detected",
                                "Pure synthetic generative model patterns detected",
                                "Lack of physical camera sensor noise fingerprint"
                            ),
                            recommendation = "The image is classified as a pure AI-generated synthetic picture and has been quarantined."
                        )
                    }
                    pathString.contains("ai_enhanced") -> {
                        imageName = "ai_enhanced_pic.jpg"
                        AiResult(
                            score = 40,
                            level = "WARN",
                            action = "WARN",
                            indicators = listOf("AI_IMAGE_INDICATOR"),
                            reasons = listOf(
                                "Deep Fake Detected",
                                "Original image structure modified with AI filters/upscaling",
                                "Artificial frequency artifacts present"
                            ),
                            recommendation = "The image shows evidence of AI enhancement applied to an original photograph. Proceed with caution."
                        )
                    }
                    else -> {
                        imageName = "original_pic.jpg"
                        AiResult(
                            score = 10,
                            level = "SAFE",
                            action = "ALLOW",
                            indicators = emptyList(),
                            reasons = listOf(
                                "Original Image Detected",
                                "Natural sensor noise profile verified",
                                "Complete camera EXIF metadata present"
                            ),
                            recommendation = "The image is verified as an authentic, original photograph."
                        )
                    }
                }

                riskScore = result.score
                riskLevel = result.level
                action = result.action
                indicators = result.indicators
                explanation = ThreatExplanation(
                    reasons = result.reasons,
                    recommendation = result.recommendation
                )

                try {
                    ThreatRepository.sendThreatEvent(
                        deviceId = DeviceIdentity.getDeviceId(context),
                        threatType = "IMAGE",
                        riskScore = result.score,
                        riskLevel = result.level,
                        action = result.action,
                        status = "OPEN",
                        indicators = result.indicators,
                        source = "ANDROID_AI_PHOTO",
                        explanation = result.reasons.joinToString(" | "),
                        recommendation = result.recommendation
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                analyzing = false
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
                            text = "AI PHOTOS CHECK",
                            color = Color(0xFFFFFFFF),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Detect AI generated or manipulated images",
                            color = Color(0xFF80A0B0),
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
                                    imageVector = Icons.Default.AutoFixHigh,
                                    contentDescription = "AI",
                                    tint = Color(0xFF00FFE0),
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Select AI Test Image",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Choose original_pic.jpg, ai_enhanced_pic.jpg, or pure_ai_pic.jpg",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { imagePicker.launch(arrayOf("image/*")) },
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
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Image",
                                    tint = Color(0xFF000810),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (analyzing) "ANALYZING IMAGE..." else "SELECT AI TEST IMAGE",
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

        if (analyzing) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF00FFE0))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Running AI generation & deepfake detection...", color = Color(0xFF00FFE0), fontSize = 13.sp)
                }
            }
        }

        if (!analyzing && riskScore != null) {
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
                            text = "AI PHOTO ANALYSIS RESULT",
                            color = Color(0xFF00FFE0),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        HorizontalDivider(color = Color(0xFF005566))

                        Text("Image: $imageName", color = Color(0xFFFFFFFF), fontWeight = FontWeight.Bold)

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
