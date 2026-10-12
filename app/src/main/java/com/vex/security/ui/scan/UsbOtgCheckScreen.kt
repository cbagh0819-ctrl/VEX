package com.vex.security.ui.scan

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
import com.vex.security.notification.AlarmPlayer
import com.vex.security.notification.SecurityNotification
import com.vex.security.repository.ThreatRepository
import com.vex.security.security.ThreatExplanation
import com.vex.security.ui.components.ThreatExplanationCard
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.launch

data class DemoFolderResult(
    val title: String,
    val description: String,
    val riskScore: Int,
    val riskLevel: String,
    val action: String,
    val explanation: ThreatExplanation,
    val indicators: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsbOtgCheckScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeResult by remember { mutableStateOf<DemoFolderResult?>(null) }
    var alarmActive by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }

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
                            text = "USB/OTG FILE INSPECTION",
                            color = Color(0xFFFFFFFF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Interactive USB & MTP Demo Test Lab",
                            color = Color(0xFF80A0B0),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

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
                            "Folder contains dangerous malware payload! Immediate action required.",
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

        // 3 Testing Buttons Card
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                imageVector = Icons.Default.Usb,
                                contentDescription = "USB",
                                tint = Color(0xFF00FFE0),
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "USB / MTP Test Simulation",
                                color = Color(0xFFFFFFFF),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Click a test button below to simulate folder insertion",
                                color = Color(0xFF80A0B0),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 1. Insert Safe Button
                    Button(
                        onClick = {
                            AlarmPlayer.stopAlarm(context)
                            alarmActive = false
                            activeResult = DemoFolderResult(
                                title = "Safe Folder (/USB/Photos_2026/)",
                                description = "Folder contains standard documents and vacation photos. It is a safe folder.",
                                riskScore = 10,
                                riskLevel = "SAFE",
                                action = "ALLOW",
                                indicators = listOf("FILE_SAFE_DEMO"),
                                explanation = ThreatExplanation(
                                    reasons = listOf("Original Safe Folder Detected", "No suspicious executable or script signatures found", "Standard media file structure verified"),
                                    recommendation = "The folder is safe and allowed."
                                )
                            )
                            scope.launch {
                                try {
                                    ThreatRepository.sendThreatEvent(
                                        deviceId = DeviceIdentity.getDeviceId(context),
                                        threatType = "FILE",
                                        riskScore = 10,
                                        riskLevel = "SAFE",
                                        action = "ALLOW",
                                        status = "RESOLVED",
                                        indicators = listOf("FILE_SAFE_DEMO"),
                                        source = "USB",
                                        explanation = "Original Safe Folder Detected | No suspicious executable signatures | Standard media file structure verified",
                                        recommendation = "The folder is safe and allowed."
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF35E08A))
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF000810))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("INSERT SAFE", color = Color(0xFF000810), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // 2. Insert Warn Button
                    Button(
                        onClick = {
                            AlarmPlayer.stopAlarm(context)
                            alarmActive = false
                            activeResult = DemoFolderResult(
                                title = "Warning Folder (/USB/Downloads/)",
                                description = "Folder contains unusual files or unrecognized structure. Warning detected.",
                                riskScore = 35,
                                riskLevel = "WARN",
                                action = "WARN",
                                indicators = listOf("FILE_WARN_DEMO", "SUSPICIOUS_FILENAME"),
                                explanation = ThreatExplanation(
                                    reasons = listOf("Unusual Folder Structure Detected", "Contains files with unrecognized or mixed extensions", "Requires user attention"),
                                    recommendation = "Review folder contents before opening any files."
                                )
                            )
                            SecurityNotification.showWarning(context, "Warning Folder detected unusual content (Score: 35)")
                            scope.launch {
                                try {
                                    ThreatRepository.sendThreatEvent(
                                        deviceId = DeviceIdentity.getDeviceId(context),
                                        threatType = "FILE",
                                        riskScore = 35,
                                        riskLevel = "WARN",
                                        action = "WARN",
                                        status = "OPEN",
                                        indicators = listOf("FILE_WARN_DEMO", "SUSPICIOUS_FILENAME"),
                                        source = "USB",
                                        explanation = "Unusual Folder Structure Detected | Contains files with unrecognized or mixed extensions",
                                        recommendation = "Review folder contents before opening any files."
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD166))
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF000810))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("INSERT WARN", color = Color(0xFF000810), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // 3. Insert Harmful Button
                    Button(
                        onClick = {
                            activeResult = DemoFolderResult(
                                title = "Harmful Folder (/USB/Payloads/)",
                                description = "Folder contains dangerous malware payload! Immediate action required.",
                                riskScore = 85,
                                riskLevel = "QUARANTINE",
                                action = "QUARANTINE",
                                indicators = listOf("FILE_HARMFUL_DEMO", "MALICIOUS_TEST_INDICATOR", "SUSPICIOUS_EXTENSION"),
                                explanation = ThreatExplanation(
                                    reasons = listOf("Dangerous Malware Payload Detected", "Matched known exploit signatures and hidden executable scripts", "High risk of device compromise"),
                                    recommendation = "The folder has been quarantined. Block or delete immediately."
                                )
                            )
                            alarmActive = true
                            SecurityNotification.showHarmfulAlert(context, "Harmful Folder (/USB/Payloads/)")
                            scope.launch {
                                try {
                                    ThreatRepository.sendThreatEvent(
                                        deviceId = DeviceIdentity.getDeviceId(context),
                                        threatType = "FILE",
                                        riskScore = 85,
                                        riskLevel = "QUARANTINE",
                                        action = "QUARANTINE",
                                        status = "OPEN",
                                        indicators = listOf("FILE_HARMFUL_DEMO", "MALICIOUS_TEST_INDICATOR", "SUSPICIOUS_EXTENSION"),
                                        source = "USB",
                                        explanation = "Dangerous Malware Payload Detected | Matched known exploit signatures and hidden executable scripts",
                                        recommendation = "The folder has been quarantined. Block or delete immediately."
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D6D))
                    ) {
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("INSERT HARMFUL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Result Display Card
        activeResult?.let { res ->
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = res.title,
                                color = Color(0xFFFFFFFF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        when (res.riskLevel) {
                                            "SAFE" -> Color(0xFF35E08A).copy(alpha = 0.2f)
                                            "WARN" -> Color(0xFFFFD166).copy(alpha = 0.2f)
                                            else -> Color(0xFFFF4D6D).copy(alpha = 0.2f)
                                        },
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = res.riskLevel,
                                    color = when (res.riskLevel) {
                                        "SAFE" -> Color(0xFF35E08A)
                                        "WARN" -> Color(0xFFFFD166)
                                        else -> Color(0xFFFF4D6D)
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(res.description, color = Color(0xFF80A0B0), fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("RISK SCORE", color = Color(0xFF80A0B0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("${res.riskScore} / 100", color = Color(0xFF00FFE0), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("ACTION", color = Color(0xFF80A0B0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(res.action, color = Color(0xFFFFFFFF), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        ThreatExplanationCard(
                            explanation = res.explanation,
                            indicators = res.indicators
                        )

                        if (res.riskLevel == "QUARANTINE") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { showBlockDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9F43))
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("1. BLOCK", color = Color.Black, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D6D))
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("2. DELETE", color = Color.White, fontWeight = FontWeight.Bold)
                                }
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

    // Block Dialog
    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = { Text("Folder Blocked") },
            text = { Text("The harmful folder has been successfully blocked from access and quarantined.") },
            confirmButton = {
                Button(onClick = {
                    AlarmPlayer.stopAlarm(context)
                    alarmActive = false
                    showBlockDialog = false
                }) {
                    Text("OK")
                }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Demo Deletion Confirmation") },
            text = { Text("Demo testing: Are you sure you want to delete this harmful folder payload?") },
            confirmButton = {
                Button(
                    onClick = {
                        AlarmPlayer.stopAlarm(context)
                        alarmActive = false
                        activeResult = null
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D6D))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
