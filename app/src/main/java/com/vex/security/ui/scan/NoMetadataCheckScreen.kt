package com.vex.security.ui.scan

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
import com.vex.security.metadata.MetadataExtractor
import com.vex.security.metadata.ExtractedMetadata
import com.vex.security.repository.ThreatRepository
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoMetadataCheckScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scanning by remember { mutableStateOf(false) }
    var extractedMetadata by remember { mutableStateOf<ExtractedMetadata?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scanning = true
            extractedMetadata = null
            scope.launch {
                val metadata = withContext(Dispatchers.IO) {
                    MetadataExtractor.extract(context, uri)
                }
                extractedMetadata = metadata

                val riskScore = if (metadata.hasMetadata) 10 else 50
                val riskLevel = if (metadata.hasMetadata) "SAFE" else "BLOCK"
                val action = if (metadata.hasMetadata) "ALLOW" else "BLOCK"
                val indicators = if (metadata.hasMetadata) emptyList() else listOf("MISSING_METADATA")

                try {
                    ThreatRepository.sendThreatEvent(
                        deviceId = DeviceIdentity.getDeviceId(context),
                        threatType = "METADATA",
                        riskScore = riskScore,
                        riskLevel = riskLevel,
                        action = action,
                        status = "OPEN",
                        indicators = indicators,
                        source = "ANDROID_METADATA_CHECK",
                        explanation = if (metadata.hasMetadata) "Metadata is present and valid." else "Critical EXIF metadata attributes are missing or stripped.",
                        recommendation = if (metadata.hasMetadata) "No action needed." else "Inspect file origin and handle with caution."
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

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
                            text = "NO METADATA PHOTO CHECK",
                            color = Color(0xFFFFFFFF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Find photos with missing or suspicious metadata",
                            color = Color(0xFF80A0B0),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Top Banner Card ("Check Photo Metadata")
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
                                    imageVector = Icons.Default.DocumentScanner,
                                    contentDescription = "Document",
                                    tint = Color(0xFF00FFE0),
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Check Photo Metadata",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Analyze EXIF metadata to find missing, modified or stripped information.",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Button(
                        enabled = !scanning,
                        onClick = { photoPicker.launch(arrayOf("image/*")) },
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
                                    contentDescription = "Photo",
                                    tint = Color(0xFF000810),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (scanning) "ANALYZING PHOTO..." else "SELECT PHOTO TO CHECK METADATA",
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

        if (scanning) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF00FFE0))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Extracting photo EXIF metadata...", color = Color(0xFF00FFE0), fontSize = 13.sp)
                }
            }
        }

        extractedMetadata?.let { meta ->
            item {
                val hasMeta = meta.hasMetadata
                val cardBorder = if (hasMeta) Color(0xFF35E08A) else Color(0xFFFF4D6D)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, cardBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (hasMeta) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = cardBorder,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (hasMeta) "METADATA PRESENT" else "METADATA MISSING / NO METADATA",
                                color = cardBorder,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(color = Color(0xFF005566))

                        Text("File Name: ${meta.name}", color = Color(0xFFFFFFFF), fontWeight = FontWeight.Bold)
                        Text("File Size: ${meta.size / 1024} KB", color = Color(0xFF80A0B0), fontSize = 12.sp)
                        Text("Mime Type: ${meta.mimeType ?: "Unknown"}", color = Color(0xFF80A0B0), fontSize = 12.sp)

                        if (meta.details.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("EXIF Details:", color = Color(0xFF00FFE0), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            meta.details.forEach { (k, v) ->
                                Text("• $k: $v", color = Color(0xFFFFFFFF), fontSize = 11.sp)
                            }
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
