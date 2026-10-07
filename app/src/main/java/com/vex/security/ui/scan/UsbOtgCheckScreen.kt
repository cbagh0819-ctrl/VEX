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
fun UsbOtgCheckScreen(
    onBackPressed: () -> Unit = {},
    onScanDeviceClicked: () -> Unit = {}
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
                            text = "USB/OTG CHECK",
                            color = Color(0xFFFFFFFF),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Scan USB devices and external storage",
                            color = Color(0xFF80A0B0),
                            fontSize = 11.sp
                        )
                    }
                }

                // Rescan Pill Button
                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(20.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(20.dp))
                        .clickable { onScanDeviceClicked() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rescan",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Rescan",
                            color = Color(0xFF00FFE0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Top Banner Card ("Connect USB/OTG Device")
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
                                    imageVector = Icons.Default.Usb,
                                    contentDescription = "USB",
                                    tint = Color(0xFF00FFE0),
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Connect USB/OTG Device",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Scan external storage for malware, suspicious\nfiles and risky content.",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // USB Device Detected Sub-card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF060F15))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFF35E08A), shape = RoundedCornerShape(50))
                            )
                            Column {
                                Text(
                                    text = "USB Device Detected",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "SanDisk Cruzer Blade (32 GB)",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Scan Device Button
                    Button(
                        onClick = { onScanDeviceClicked() },
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
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Scan",
                                    tint = Color(0xFF000810),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "SCAN DEVICE",
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

        // Scan Summary Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Scan Summary",
                    color = Color(0xFFFFFFFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Total Files", count = "128", color = Color(0xFF00FFE0), icon = Icons.Default.Description)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Safe", count = "102", color = Color(0xFF35E08A), icon = Icons.Default.Security)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Suspicious", count = "18", color = Color(0xFFFFD166), icon = Icons.Default.Warning)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Malicious", count = "8", color = Color(0xFFFF4D6D), icon = Icons.Default.BugReport)
                    }
                }
            }
        }

        // Risk Overview Card
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
                        text = "Risk Overview",
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
                                .height(140.dp),
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
                                    sweepAngle = 80f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = Color(0xFFFFD166),
                                    startAngle = 215f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = Color(0xFFFF4D6D),
                                    startAngle = 305f,
                                    sweepAngle = 100f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("HIGH", color = Color(0xFFFF4D6D), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text("RISK LEVEL", color = Color(0xFF80A0B0), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1.3f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CategoryStatRow(icon = Icons.Default.Description, label = "Documents", files = "40 files", threats = "2 threats", threatColor = Color(0xFFFF9F43))
                            CategoryStatRow(icon = Icons.Default.Image, label = "Images", files = "56 files", threats = "3 threats", threatColor = Color(0xFFFF9F43))
                            CategoryStatRow(icon = Icons.Default.VideoLibrary, label = "Videos", files = "18 files", threats = "1 threat", threatColor = Color(0xFFFF9F43))
                            CategoryStatRow(icon = Icons.Default.Android, label = "APK Files", files = "6 files", threats = "4 threats", threatColor = Color(0xFFFF4D6D))
                            CategoryStatRow(icon = Icons.Default.Settings, label = "Others", files = "8 files", threats = "0 threats", threatColor = Color(0xFF35E08A))
                        }
                    }
                }
            }
        }

        // Scan Results Section
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
                            text = "Scan Results",
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
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color(0xFF00FFE0), modifier = Modifier.size(14.dp))
                                Text("Filter", color = Color(0xFF00FFE0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF00FFE0), modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ScanResultItem(icon = Icons.Default.Android, name = "GameHack_v2.1.apk", path = "/USB/Android/", size = "12 MB", date = "24 Sep 2026", status = "MALICIOUS", color = Color(0xFFFF4D6D))
                        ScanResultItem(icon = Icons.Default.Description, name = "invoice.pdf", path = "/USB/Docs/", size = "2.4 MB", date = "23 Sep 2026", status = "SUSPICIOUS", color = Color(0xFFFF9F43))
                        ScanResultItem(icon = Icons.Default.Image, name = "holiday_photo.jpg", path = "/USB/Photos/", size = "4.1 MB", date = "23 Sep 2026", status = "SAFE", color = Color(0xFF35E08A))
                        ScanResultItem(icon = Icons.Default.Settings, name = "setup.exe", path = "/USB/Software/", size = "8.7 MB", date = "22 Sep 2026", status = "MALICIOUS", color = Color(0xFFFF4D6D))
                        ScanResultItem(icon = Icons.Default.Description, name = "notes.txt", path = "/USB/", size = "12 KB", date = "22 Sep 2026", status = "SAFE", color = Color(0xFF35E08A))
                    }
                }
            }
        }

        // Bottom Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF0A141A))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF00FFE0), modifier = Modifier.size(15.dp))
                            Text("VIEW REPORT", color = Color(0xFF00FFE0), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .weight(1.1f)
                            .height(46.dp)
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF0A141A))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFF00FFE0), modifier = Modifier.size(15.dp))
                            Text("QUARANTINE", color = Color(0xFF00FFE0), fontSize = 9.sp, fontWeight = FontWeight.Bold)
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
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF000810), modifier = Modifier.size(16.dp))
                            Text("REMOVE THREATS", color = Color(0xFF000810), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
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
fun CategoryStatRow(
    icon: ImageVector,
    label: String,
    files: String,
    threats: String,
    threatColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF005566), RoundedCornerShape(10.dp))
            .background(Color(0xFF060F15), RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, Color(0xFF005566), RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A1F28), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF00FFE0),
                    modifier = Modifier.size(16.dp)
                )
            }
            Column {
                Text(
                    text = label,
                    color = Color(0xFFFFFFFF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = files,
                    color = Color(0xFF80A0B0),
                    fontSize = 10.sp
                )
            }
        }

        Text(
            text = threats,
            color = threatColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ScanResultItem(
    icon: ImageVector,
    name: String,
    path: String,
    size: String,
    date: String,
    status: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF005566), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF060F15))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, Color(0xFF005566), RoundedCornerShape(10.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = Color(0xFF00FFE0),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = name,
                        color = Color(0xFFFFFFFF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$path · $size · $date",
                        color = Color(0xFF80A0B0),
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = status,
                    color = color,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
