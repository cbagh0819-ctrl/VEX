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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCheckScreen(
    onBackPressed: () -> Unit = {},
    onScanCameraClicked: () -> Unit = {},
    onSelectGalleryClicked: () -> Unit = {}
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
                            text = "QR PHOTOS CHECK",
                            color = Color(0xFFFFFFFF),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Scan QR codes from camera or gallery",
                            color = Color(0xFF80A0B0),
                            fontSize = 11.sp
                        )
                    }
                }

                // Scan QR Pill Button
                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(20.dp))
                        .background(Color(0xFF0A1F28), RoundedCornerShape(20.dp))
                        .clickable { onScanCameraClicked() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Scan QR",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Scan QR",
                            color = Color(0xFF00FFE0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Top Banner Card ("Scan QR Code")
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF00FFE0).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
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
                                    text = "Scan QR Code",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Detect malicious links, phishing URLs, and\nunsafe content from QR codes.",
                                    color = Color(0xFF80A0B0),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Go",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onScanCameraClicked() },
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
                                    text = "SCAN FROM CAMERA",
                                    color = Color(0xFF00FFE0),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Button(
                            onClick = { onSelectGalleryClicked() },
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
                                        text = "SELECT FROM GALLERY",
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
                        SummaryCard(title = "Total Scanned", count = "25", color = Color(0xFF00FFE0), icon = Icons.Default.QrCode)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Safe", count = "18", color = Color(0xFF35E08A), icon = Icons.Default.Security)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Suspicious", count = "5", color = Color(0xFFFFD166), icon = Icons.Default.Warning)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SummaryCard(title = "Malicious", count = "2", color = Color(0xFFFF4D6D), icon = Icons.Default.BugReport)
                    }
                }
            }
        }

        // Risk Category Sections
        item {
            QrRiskCategoryCard(
                category = "LOW",
                count = "12 QR codes",
                subtitle = "Links are safe",
                dotColor = Color(0xFF35E08A),
                borderColor = Color(0xFF005566),
                items = listOf(
                    QrItem("https://www.google.com", "Scanned 2 hours ago", "LOW RISK", Color(0xFF35E08A))
                )
            )
        }

        item {
            QrRiskCategoryCard(
                category = "MEDIUM",
                count = "7 QR codes",
                subtitle = "Contains shortened URLs or unknown domains",
                dotColor = Color(0xFFFFD166),
                borderColor = Color(0xFF554400),
                items = listOf(
                    QrItem("https://tinyurl.com/3ab7x;w", "Scanned 5 hours ago", "MEDIUM RISK", Color(0xFFFFD166))
                )
            )
        }

        item {
            QrRiskCategoryCard(
                category = "HIGH",
                count = "4 QR codes",
                subtitle = "Potentially phishing or suspicious content",
                dotColor = Color(0xFFFF9F43),
                borderColor = Color(0xFF663300),
                items = listOf(
                    QrItem("http://login-update-secure.com", "Scanned 1 day ago", "HIGH RISK", Color(0xFFFF9F43))
                )
            )
        }

        item {
            QrRiskCategoryCard(
                category = "CRITICAL",
                count = "2 QR codes",
                subtitle = "Malicious links or dangerous content",
                dotColor = Color(0xFFFF4D6D),
                borderColor = Color(0xFF660011),
                items = listOf(
                    QrItem("http://malware-download.xyz", "Scanned 1 day ago", "CRITICAL", Color(0xFFFF4D6D))
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

data class QrItem(
    val url: String,
    val timeInfo: String,
    val riskLabel: String,
    val riskColor: Color
)

@Composable
fun QrRiskCategoryCard(
    category: String,
    count: String,
    subtitle: String,
    dotColor: Color,
    borderColor: Color,
    items: List<QrItem>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(dotColor, shape = RoundedCornerShape(50))
                    )
                    Text(
                        text = category,
                        color = dotColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(20.dp))
                            .background(Color(0xFF0A141A), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("VIEW ALL", color = Color(0xFF00FFE0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF00FFE0), modifier = Modifier.size(12.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFF0A1F28), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Expand",
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$count · $subtitle",
                color = Color(0xFF80A0B0),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEach { qr ->
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
                                        .size(36.dp)
                                        .background(Color(0xFF0A1F28), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = "QR",
                                        tint = Color(0xFF00FFE0),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = qr.url,
                                        color = Color(0xFFFFFFFF),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = qr.timeInfo,
                                        color = Color(0xFF608090),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .border(1.dp, qr.riskColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .background(qr.riskColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = qr.riskLabel,
                                    color = qr.riskColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
