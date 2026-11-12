package com.vex.security.ui.splash
import com.vex.security.ui.theme.VexPalette

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
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
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (progress < 1f) {
            val elapsed = (System.currentTimeMillis() - startTime).toFloat()
            progress = (elapsed / 2000f).coerceIn(0f, 1f)
            delay(16)
        }
        delay(300)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        VexPalette.backgroundTop,
                        VexPalette.backgroundMid,
                        VexPalette.backgroundBottom
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Center Logo & Shield Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glowing Shield Container
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .border(
                                width = 1.5.dp,
                                brush = Brush.sweepGradient(
                                    listOf(
                                        VexPalette.primary,
                                        VexPalette.cardBorder,
                                        VexPalette.primary
                                    )
                                ),
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Shield Logo",
                        modifier = Modifier.size(90.dp),
                        tint = VexPalette.primary
                    )

                    Text(
                        text = "V",
                        color = Color(0xFFE0FFFF),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "VEX",
                    color = VexPalette.primary,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 6.sp
                )

                Text(
                    text = "CYBER GUARD",
                    color = Color(0xFFA0B0C0),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        color = VexPalette.cardBorder,
                        thickness = 1.dp,
                        modifier = Modifier.width(60.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(VexPalette.primary, shape = RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    HorizontalDivider(
                        color = VexPalette.cardBorder,
                        thickness = 1.dp,
                        modifier = Modifier.width(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "YOUR ANDROID SECURITY SHIELD",
                    color = Color(0xFF608090),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                )
            }

            // Bottom Progress Bar & Action Icons Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(VexPalette.pillBackground),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF008888), VexPalette.primary)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = VexPalette.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ActionBottomItem(icon = Icons.Default.Search, label = "SCAN")
                    ActionBottomItem(icon = Icons.Default.Security, label = "DETECT")
                    ActionBottomItem(icon = Icons.Default.Lock, label = "ANALYZE")
                    ActionBottomItem(icon = Icons.Default.Settings, label = "PROTECT")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ActionBottomItem(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .border(1.dp, VexPalette.cardBorder, RoundedCornerShape(12.dp))
                .background(Color(0xFF241239), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = VexPalette.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color(0xFFA998C4),
            fontSize = 10.sp,
            letterSpacing = 1.sp
        )
    }
}
