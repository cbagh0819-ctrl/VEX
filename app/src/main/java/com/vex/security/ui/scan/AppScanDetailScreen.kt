package com.vex.security.ui.scan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScanDetailScreen(
    onBackPressed: () -> Unit = {},
    onSelectApkClicked: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("OVERVIEW") }
    val tabs = listOf("OVERVIEW", "PERMISSIONS", "ACTIVITIES", "NETWORK", "CERTIFICATE")

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
                            text = "APP SCAN (APK ANALYSIS)",
                            color = Color(0xFFFFFFFF),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Analyze APK files for hidden threats",
                            color = Color(0xFF80A0B0),
                            fontSize = 11.sp
                        )
                    }
                }

                // Select APK Pill Button
                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(20.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(20.dp))
                        .clickable { onSelectApkClicked() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Select APK",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Select APK",
                            color = Color(0xFF00FFE0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Selected APK Top Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFFF4D6D).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
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
                                    text = "Selected APK",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "GameHack_v2.1.apk",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Version: 2.1.0  ·  Size: 12 MB",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "Package: com.gamehack.v2",
                                    color = Color(0xFF608090),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Malicious Risk Score Badge
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color(0xFFFF4D6D), RoundedCornerShape(20.dp))
                                    .background(Color(0xFFFF4D6D).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "MALICIOUS",
                                    color = Color(0xFFFF4D6D),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Risk Score",
                                color = Color(0xFF80A0B0),
                                fontSize = 9.sp
                            )
                            Text(
                                text = "92 / 100",
                                color = Color(0xFFFF4D6D),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Scan Again & Open Report Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .border(1.dp, Color(0xFF005566), RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF0A141A))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00FFE0), modifier = Modifier.size(16.dp))
                                Text("SCAN AGAIN", color = Color(0xFF00FFE0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
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
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF000810), modifier = Modifier.size(16.dp))
                                    Text("OPEN REPORT", color = Color(0xFF000810), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tab Bar (OVERVIEW, PERMISSIONS, ACTIVITIES, NETWORK, CERTIFICATE)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF00FFE0) else Color(0xFF005566),
                                RoundedCornerShape(10.dp)
                            )
                            .background(
                                if (isSelected) Color(0xFF0A2530) else Color(0xFF0A141A),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color(0xFF00FFE0) else Color(0xFF80A0B0),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Threat Summary Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Threat Summary",
                    color = Color(0xFFFFFFFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Malicious Indicators", count = "4", color = Color(0xFFFF4D6D), icon = Icons.Default.BugReport)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Suspicious Behaviors", count = "12", color = Color(0xFFFF9F43), icon = Icons.Default.Warning)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Requested Permissions", count = "8", color = Color(0xFF38BDF8), icon = Icons.Default.Info)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Suspicious Connections", count = "3", color = Color(0xFFC084FC), icon = Icons.Default.Storage)
                    }
                }
            }
        }

        // Risk Analysis Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF005566), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Risk Analysis",
                        color = Color(0xFFFFFFFF),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(110.dp)) {
                                drawArc(
                                    color = Color(0xFF0A2530),
                                    startAngle = 135f,
                                    sweepAngle = 270f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = Color(0xFF35E08A),
                                    startAngle = 135f,
                                    sweepAngle = 60f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = Color(0xFFFFD166),
                                    startAngle = 195f,
                                    sweepAngle = 60f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = Color(0xFFFF9F43),
                                    startAngle = 255f,
                                    sweepAngle = 60f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = Color(0xFFFF4D6D),
                                    startAngle = 315f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("92", color = Color(0xFFFF4D6D), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Text("HIGH RISK", color = Color(0xFFFF4D6D), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1.3f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RiskFactorRow(label = "Known Malware Signature", points = "+40")
                            RiskFactorRow(label = "Suspicious Permissions", points = "+20")
                            RiskFactorRow(label = "Suspicious Network Activity", points = "+15")
                            RiskFactorRow(label = "Code Obfuscation Detected", points = "+10")
                            RiskFactorRow(label = "Data Exfiltration Indicators", points = "+7")
                        }
                    }
                }
            }
        }

        // Key Findings Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF005566), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Key Findings",
                            color = Color(0xFFFFFFFF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .border(1.dp, Color(0xFF005566), RoundedCornerShape(20.dp))
                                .background(Color(0xFF0A141A), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("VIEW ALL", color = Color(0xFF00FFE0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF00FFE0), modifier = Modifier.size(12.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FindingItem(icon = Icons.Default.BugReport, title = "Malware Signature Detected", subtitle = "Matches known malicious patterns (Trojan/Spyware)", color = Color(0xFFFF4D6D))
                        FindingItem(icon = Icons.Default.Lock, title = "Dangerous Permissions", subtitle = "Requests Camera, Microphone, Location, Contacts", color = Color(0xFFFF9F43))
                        FindingItem(icon = Icons.Default.Wifi, title = "Suspicious Network Domains", subtitle = "Connects to unknown / suspicious servers", color = Color(0xFFFF9F43))
                        FindingItem(icon = Icons.Default.Code, title = "Code Obfuscation", subtitle = "APK is heavily obfuscated to hide its behavior", color = Color(0xFF38BDF8))
                    }
                }
            }
        }

        // Bottom Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .border(1.dp, Color(0xFFFF4D6D), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A0A10))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF4D6D), modifier = Modifier.size(15.dp))
                            Text("BLOCK INSTALLATION", color = Color(0xFFFF4D6D), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .border(1.dp, Color(0xFFFFD166), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF251A0A))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFFD166), modifier = Modifier.size(15.dp))
                            Text("QUARANTINE APK", color = Color(0xFFFFD166), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Button(
                    onClick = { },
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF000810), modifier = Modifier.size(16.dp))
                            Text("VIEW FULL REPORT", color = Color(0xFF000810), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun RiskFactorRow(label: String, points: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFFFF4D6D), shape = RoundedCornerShape(50))
            )
            Text(label, color = Color(0xFFC0D0E0), fontSize = 10.sp)
        }
        Text(points, color = Color(0xFFFF4D6D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FindingItem(icon: ImageVector, title: String, subtitle: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF003344), RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp)),
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
                        .size(38.dp)
                        .background(Color(0xFF0A1F28), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
                }

                Column {
                    Text(text = title, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = subtitle, color = Color(0xFF608090), fontSize = 10.sp)
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Details",
                tint = Color(0xFF608090),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
