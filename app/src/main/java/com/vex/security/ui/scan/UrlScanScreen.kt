package com.vex.security.ui.scan

import com.vex.security.ui.theme.VexPalette
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
fun UrlScanScreen(
    onBackPressed: () -> Unit = {}
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var url by remember { mutableStateOf("") }
    var riskScore by remember { mutableStateOf<Int?>(null) }
    var riskLevel by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var indicators by remember { mutableStateOf<List<String>>(emptyList()) }
    var explanation by remember { mutableStateOf<ThreatExplanation?>(null) }
    var scanned by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(VexPalette.backgroundTop, VexPalette.backgroundMid, VexPalette.backgroundBottom))
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
                            text = "URL SECURITY & PHISHING",
                            color = VexPalette.primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Detect phishing links, shorteners & IP URLs",
                            color = Color(0xFFA998C4),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Input Card
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
                                imageVector = Icons.Default.Language,
                                contentDescription = "URL",
                                tint = VexPalette.primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Analyze Link / URL",
                                color = VexPalette.headerText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Paste any URL or test link (e.g. https://example.com/?vex_test=BLOCK_60)",
                                color = Color(0xFFA998C4),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("Paste URL here...", color = Color(0xFFA998C4)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VexPalette.primary,
                            unfocusedBorderColor = VexPalette.cardBorder,
                            focusedTextColor = Color(0xFFFFFFFF),
                            unfocusedTextColor = Color(0xFFFFFFFF),
                            focusedLabelColor = VexPalette.primary,
                            cursorColor = VexPalette.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (url.isNotBlank()) {
                                val foundIndicators = UrlAnalyzer.analyze(url)
                                val analysis = ThreatEngine.analyze(foundIndicators, context)

                                riskScore = analysis.riskScore
                                riskLevel = analysis.riskLevel
                                action = analysis.action
                                indicators = foundIndicators
                                explanation = analysis.explanation
                                scanned = true

                                scope.launch {
                                    try {
                                        ThreatRepository.sendThreatEvent(
                                            deviceId = DeviceIdentity.getDeviceId(context),
                                            threatType = "URL",
                                            riskScore = analysis.riskScore,
                                            riskLevel = analysis.riskLevel,
                                            action = analysis.action,
                                            status = "OPEN",
                                            indicators = foundIndicators,
                                            source = "ANDROID_URL",
                                            explanation = analysis.explanation.reasons.joinToString(" | "),
                                            recommendation = analysis.explanation.recommendation
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
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
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Scan",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "ANALYZE URL THREAT",
                                    color = Color.White,
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

        if (scanned && riskScore != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VexPalette.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = VexPalette.cardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "URL SECURITY RESULT",
                            color = VexPalette.primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        HorizontalDivider(color = VexPalette.cardBorder)

                        Text("URL: $url", color = Color(0xFFFFFFFF), fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("RISK SCORE", color = Color(0xFFA998C4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("$riskScore / 100", color = VexPalette.primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("RISK LEVEL", color = Color(0xFFA998C4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
