package com.vex.security.ui.telecom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BombShieldScreen(onBackPressed: () -> Unit = {}) {
    var smsProtectionEnabled by remember { mutableStateOf(true) }
    var callProtectionEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BOMB SHIELD DEFENSE",
                            color = VexCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Automated anti-flood telecom protection",
                            color = VexTextMuted,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = VexCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VexNavContainer)
            )
        },
        containerColor = VexVoid
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            VexBackgroundTop,
                            VexBackgroundMid,
                            VexVoid
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Status Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VexCardBorder, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = VexCardDark)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(VexNavIndicator, CircleShape)
                                    .border(1.dp, VexCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = VexCyan,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (smsProtectionEnabled || callProtectionEnabled) "TELECOM SHIELD ACTIVE" else "SHIELD PAUSED",
                                    color = if (smsProtectionEnabled || callProtectionEnabled) VexEmerald else VexAmber,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Burst rate-limiting & silent call drop enabled",
                                    color = VexTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = VexCardBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("FLOOD RATE LIMIT", color = VexTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("4 calls / 10s window", color = VexTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("OTP BURST DEFENSE", color = VexTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Automatic Suppression", color = VexCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Controls Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VexCardBorderSubtle, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = VexCardDark)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // SMS Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sms,
                                    contentDescription = null,
                                    tint = VexCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text("SMS Bomb Defense", color = VexTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Detect & throttle high-frequency OTP flooding", color = VexTextMuted, fontSize = 11.sp)
                                }
                            }
                            Switch(
                                checked = smsProtectionEnabled,
                                onCheckedChange = { smsProtectionEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = VexVoid,
                                    checkedTrackColor = VexCyan,
                                    uncheckedThumbColor = VexTextMuted,
                                    uncheckedTrackColor = VexCardElevated
                                )
                            )
                        }

                        HorizontalDivider(color = VexCardBorder.copy(alpha = 0.3f))

                        // Call Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = VexCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text("Call Bomb Defense", color = VexTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Mute ringers & mitigate automated spam rings", color = VexTextMuted, fontSize = 11.sp)
                                }
                            }
                            Switch(
                                checked = callProtectionEnabled,
                                onCheckedChange = { callProtectionEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = VexVoid,
                                    checkedTrackColor = VexCyan,
                                    uncheckedThumbColor = VexTextMuted,
                                    uncheckedTrackColor = VexCardElevated
                                )
                            )
                        }
                    }
                }
            }

            // Informational Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VexCardBorderSubtle, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = VexCardElevated)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = VexCyan, modifier = Modifier.size(18.dp))
                            Text("How VEX Bomb Shield Protects You", color = VexCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Attackers often use SMS and call flooders to overwhelm your handset with verification codes while trying to compromise banking accounts. VEX intercepts high-frequency bursts in real time and silences the ringer without crashing device services.",
                            color = VexTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
