package com.vex.security.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun HomeScreen(
    onNavigateToApkCheck: () -> Unit = {},
    onNavigateToAiCheck: () -> Unit = {},
    onNavigateToNoMetadataCheck: () -> Unit = {},
    onNavigateToUsbCheck: () -> Unit = {},
    onNavigateToFileScan: () -> Unit = {},
    onNavigateToUrlScan: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {}
) {
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
                        text = "VEX",
                        color = Color(0xFF00FFE0),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "CYBER GUARD",
                        color = Color(0xFFA0B0C0),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Device Protected Pill
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(20.dp))
                            .background(Color(0xFF0A1F28), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF35E08A), shape = RoundedCornerShape(50))
                            )
                            Text(
                                text = "Device Protected",
                                color = Color(0xFF00FFE0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Notification Bell
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(50))
                            .background(Color(0xFF0A1F28), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Risk Status Card
        item {
            CyberCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your Device Risk",
                            color = Color(0xFF80A0B0),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "LOW",
                            color = Color(0xFF35E08A),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your device is safe\nNo immediate threats",
                            color = Color(0xFFB0C4DE),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .border(1.dp, Color(0xFF005566), RoundedCornerShape(10.dp))
                                .background(Color(0xFF0A141A), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Shield",
                                    tint = Color(0xFF00FFE0),
                                    modifier = Modifier.size(16.dp)
                                )
                                Column {
                                    Text("Last Scan", color = Color(0xFF80A0B0), fontSize = 10.sp)
                                    Text("2 hours ago", color = Color(0xFFFFFFFF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    // Risk Gauge Visual
                    Box(
                        modifier = Modifier.size(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(110.dp)) {
                            drawArc(
                                color = Color(0xFF0A2530),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = Color(0xFF35E08A),
                                startAngle = 135f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = Color(0xFFFFD166),
                                startAngle = 225f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = Color(0xFFFF4D6D),
                                startAngle = 315f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Safe",
                                tint = Color(0xFF00FFE0),
                                modifier = Modifier.size(28.dp)
                            )
                            Text("RISK", color = Color(0xFF80A0B0), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Quick Scan Banner
        item {
            CyberCard(
                modifier = Modifier.clickable { onNavigateToApkCheck() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(12.dp))
                                .background(Color(0xFF0A1F28), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Quick Scan",
                                tint = Color(0xFF00FFE0),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column {
                            Text("QUICK SCAN", color = Color(0xFF80A0B0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("CHECK YOUR PHONE", color = Color(0xFFFFFFFF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Scan for threats, malware, risky files", color = Color(0xFFA0B0C0), fontSize = 11.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF00FFE0), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Go",
                            tint = Color(0xFF000810),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Feature Grid (2x3)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Android,
                            title = "APK CHECK",
                            subtitle = "Scan and analyze apps and APK files",
                            onClick = onNavigateToApkCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.QrCode,
                            title = "QR PHOTOS CHECK",
                            subtitle = "Scan QR codes from camera or gallery",
                            onClick = onNavigateToUrlScan
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Image,
                            title = "AI PHOTOS CHECK",
                            subtitle = "Detect AI generated or manipulated images",
                            onClick = onNavigateToAiCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Description,
                            title = "NO METADATA CHECK",
                            subtitle = "Find photos with missing metadata",
                            onClick = onNavigateToNoMetadataCheck
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Usb,
                            title = "USB/OTG CHECK",
                            subtitle = "Scan USB devices and external storage",
                            onClick = onNavigateToUsbCheck
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            icon = Icons.Default.Language,
                            title = "URL CHECK",
                            subtitle = "Check and detect malicious links",
                            onClick = onNavigateToUrlScan
                        )
                    }
                }
            }
        }

        // Recent Activity Card
        item {
            CyberCard(
                modifier = Modifier.clickable { onNavigateToDashboard() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Activity",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text("Recent Activity", color = Color(0xFFFFFFFF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("No threats detected today", color = Color(0xFF35E08A), fontSize = 11.sp)
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = Color(0xFF80A0B0),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF005566).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF005566).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF0A1F28), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color(0xFF00FFE0),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = Color(0xFF507080),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = Color(0xFFFFFFFF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Color(0xFF80A0B0),
                fontSize = 10.sp,
                lineHeight = 13.sp
            )
        }
    }
}
