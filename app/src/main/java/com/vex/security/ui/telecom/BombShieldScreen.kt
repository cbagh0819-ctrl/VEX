package com.vex.security.ui.telecom

import com.vex.security.ui.theme.VexPalette
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.vex.security.telecom.BombShieldManager
import com.vex.security.telecom.SensitivityMode
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BombShieldScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isShieldEnabled by BombShieldManager.isEnabledState
    val currentSensitivity by BombShieldManager.sensitivityState
    val ignoreContacts by BombShieldManager.ignoreContactsState
    val detectOtpFloods by BombShieldManager.detectOtpFloodsState
    val autoMuteRinger by BombShieldManager.autoMuteRingerState
    val autoRejectCalls by BombShieldManager.autoRejectCallsState

    val blockedSms by BombShieldManager.blockedSmsCount
    val blockedCalls by BombShieldManager.blockedCallsCount
    val attacksNeutralized by BombShieldManager.attacksNeutralizedCount
    val incidentLogs = BombShieldManager.incidentLogs

    var isSimulatingSms by remember { mutableStateOf(false) }
    var isSimulatingCall by remember { mutableStateOf(false) }
    var simulationStatus by remember { mutableStateOf<String?>(null) }

    // Permission states
    var hasSmsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasPhonePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasSmsPermission = results[Manifest.permission.RECEIVE_SMS] == true ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        hasPhonePermission = results[Manifest.permission.READ_PHONE_STATE] == true ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

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
                    IconButton(
                        onClick = onBackPressed,
                        modifier = Modifier
                            .size(38.dp)
                            .background(VexPalette.pillBackground, RoundedCornerShape(10.dp))
                            .border(1.dp, VexPalette.cardBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = VexPalette.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "BOMB SHIELD",
                            color = VexPalette.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "CALL & SMS FLOOD DEFENSE",
                            color = Color(0xFFA0B0C0),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Active / Standby Status Pill
                val statusColor by animateColorAsState(
                    targetValue = if (isShieldEnabled) Color(0xFF35E08A) else Color(0xFFFF5252),
                    label = "statusColor"
                )
                Box(
                    modifier = Modifier
                        .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .background(VexPalette.pillBackground, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    statusColor.copy(alpha = if (isShieldEnabled) pulseAlpha else 1f),
                                    CircleShape
                                )
                        )
                        Text(
                            text = if (isShieldEnabled) "SHIELD ACTIVE" else "STANDBY",
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Master ON / OFF Switch Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isShieldEnabled) VexPalette.primary.copy(alpha = 0.6f) else Color(0xFF405565),
                        RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = if (isShieldEnabled) Color(0xFF08222E) else Color(0xFF241239)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(
                                    if (isShieldEnabled) VexPalette.primary.copy(alpha = 0.15f) else Color(0xFF162530),
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    if (isShieldEnabled) VexPalette.primary else Color(0xFF3B4E5D),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isShieldEnabled) Icons.Default.Security else Icons.Default.Shield,
                                contentDescription = "Shield",
                                tint = if (isShieldEnabled) VexPalette.primary else Color(0xFF708595),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isShieldEnabled) "Auto Bomb Blocker ON" else "Auto Bomb Blocker OFF",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isShieldEnabled)
                                    "Rate limiter active. Suppressing sudden unknown SMS & call bursts."
                                else
                                    "Protection suspended. Device vulnerable to SMS/call bombing.",
                                color = if (isShieldEnabled) Color(0xFF7EE7D8) else Color(0xFF8A9BA8),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Switch(
                        checked = isShieldEnabled,
                        onCheckedChange = { enabled ->
                            BombShieldManager.setEnabled(context, enabled)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF001824),
                            checkedTrackColor = VexPalette.primary,
                            uncheckedThumbColor = Color(0xFF8A9BA8),
                            uncheckedTrackColor = Color(0xFF162530)
                        )
                    )
                }
            }
        }

        // Permission Banner (if needed)
        if (!hasSmsPermission || !hasPhonePermission) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFFFB300).copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF241C06))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Telecom Permissions Required",
                                color = Color(0xFFFFB300),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Grant SMS and Phone State access so VEX can intercept incoming flood vectors in real-time.",
                            color = Color(0xFFE0D0B0),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.RECEIVE_SMS,
                                        Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.READ_CONTACTS
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "GRANT TELECOM PERMISSIONS",
                                color = Color(0xFF1A1202),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Telecom Metrics Counter Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ShieldMetricTile(
                    modifier = Modifier.weight(1f),
                    title = "SMS BLOCKED",
                    count = blockedSms,
                    icon = Icons.Default.Sms,
                    accentColor = VexPalette.primary
                )
                ShieldMetricTile(
                    modifier = Modifier.weight(1f),
                    title = "CALLS HALTED",
                    count = blockedCalls,
                    icon = Icons.Default.CallEnd,
                    accentColor = Color(0xFFFF5252)
                )
                ShieldMetricTile(
                    modifier = Modifier.weight(1f),
                    title = "ATTACKS FOILED",
                    count = attacksNeutralized,
                    icon = Icons.Default.Shield,
                    accentColor = Color(0xFF35E08A)
                )
            }
        }

        // Sensitivity Mode Selector
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VexPalette.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = VexPalette.cardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BURST SENSITIVITY",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "60s Sliding Window",
                            color = Color(0xFF7090A0),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SensitivityMode.values().forEach { mode ->
                            val isSelected = currentSensitivity == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        if (isSelected) VexPalette.primary else Color(0xFF1E3545),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .background(
                                        if (isSelected) Color(0xFF0E3040) else Color(0xFF061018)
                                    )
                                    .clickable {
                                        BombShieldManager.setSensitivity(context, mode)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = mode.title,
                                        color = if (isSelected) VexPalette.primary else Color(0xFF8A9BA8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${mode.smsThreshold} SMS / ${mode.callThreshold} Calls",
                                        color = if (isSelected) Color(0xFF7EE7D8) else Color(0xFF5A7080),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentSensitivity.description,
                        color = Color(0xFFA0B0C0),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Intelligent Defense Rules & Toggles
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VexPalette.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = VexPalette.cardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INTELLIGENT DEFENSE HEURISTICS",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    DefenseToggleRow(
                        title = "Ignore Saved Contacts",
                        description = "Friends & family never trigger false-positive blocks",
                        checked = ignoreContacts,
                        onCheckedChange = { BombShieldManager.setIgnoreContacts(context, it) }
                    )

                    HorizontalDivider(color = Color(0xFF142430), modifier = Modifier.padding(vertical = 10.dp))

                    DefenseToggleRow(
                        title = "Detect OTP / 2FA Floods",
                        description = "Triggers aggressive threshold when verification codes flood in",
                        checked = detectOtpFloods,
                        onCheckedChange = { BombShieldManager.setDetectOtpFloods(context, it) }
                    )

                    HorizontalDivider(color = Color(0xFF142430), modifier = Modifier.padding(vertical = 10.dp))

                    DefenseToggleRow(
                        title = "Auto-Mute Ringer",
                        description = "Silences incoming robocalls during active bombing bursts",
                        checked = autoMuteRinger,
                        onCheckedChange = { BombShieldManager.setAutoMuteRinger(context, it) }
                    )

                    HorizontalDivider(color = Color(0xFF142430), modifier = Modifier.padding(vertical = 10.dp))

                    DefenseToggleRow(
                        title = "Auto-Reject Flood Calls",
                        description = "Directly disconnects repeat automated calling attacks",
                        checked = autoRejectCalls,
                        onCheckedChange = { BombShieldManager.setAutoRejectCalls(context, it) }
                    )
                }
            }
        }

        // Interactive Attack Simulator Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VexPalette.primary.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF081C26))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Test",
                            tint = VexPalette.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "LIVE ATTACK SIMULATOR",
                            color = VexPalette.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Verify real-time defense mechanics without waiting for real attacker bots. Triggers authentic bursts against the rate limiter.",
                        color = Color(0xFFA0B0C0),
                        fontSize = 11.sp
                    )

                    AnimatedVisibility(visible = simulationStatus != null) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF05121A), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF008080), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = VexPalette.primary,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = simulationStatus.orEmpty(),
                                        color = VexPalette.primary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!isSimulatingSms && !isSimulatingCall) {
                                    isSimulatingSms = true
                                    simulationStatus = "Launching SMS Bomber simulation..."
                                    scope.launch {
                                        BombShieldManager.simulateSmsBombAttack(context) { current, total, sender ->
                                            simulationStatus = "Burst [$current/$total]: Intercepted OTP from $sender"
                                        }
                                        simulationStatus = "SMS Bombing attack repelled! 10 flood items neutralized."
                                        isSimulatingSms = false
                                    }
                                }
                            },
                            enabled = !isSimulatingSms && !isSimulatingCall,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0A4050),
                                disabledContainerColor = Color(0xFF08202A)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .border(1.dp, VexPalette.primary.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sms,
                                        contentDescription = null,
                                        tint = VexPalette.primary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "SMS BOMB",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "10 OTP Burst",
                                    color = Color(0xFF7EE7D8),
                                    fontSize = 9.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (!isSimulatingSms && !isSimulatingCall) {
                                    isSimulatingCall = true
                                    simulationStatus = "Launching Call Bomber simulation..."
                                    scope.launch {
                                        BombShieldManager.simulateCallBombAttack(context) { current, total, number ->
                                            simulationStatus = "Burst [$current/$total]: Intercepted call from $number"
                                        }
                                        simulationStatus = "Call Bombing neutralized! Ringers muted & calls terminated."
                                        isSimulatingCall = false
                                    }
                                }
                            },
                            enabled = !isSimulatingSms && !isSimulatingCall,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4A1020),
                                disabledContainerColor = Color(0xFF260810)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneCallback,
                                        contentDescription = null,
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "CALL FLOOD",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "5 Spoofed Calls",
                                    color = Color(0xFFFF8A80),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Intercepted Incident Audit Log Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INTERCEPTED INCIDENTS AUDIT",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                if (incidentLogs.isNotEmpty()) {
                    Text(
                        text = "CLEAR LOGS",
                        color = VexPalette.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                BombShieldManager.resetStats()
                                simulationStatus = null
                            }
                            .padding(4.dp)
                    )
                }
            }
        }

        // Incident List Items
        if (incidentLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF162530), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF061018))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Secure",
                            tint = Color(0xFF35E08A),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Telecom Channel Secure",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "No sudden SMS bursts or robocall floods detected. Shield is actively guarding.",
                            color = Color(0xFF708595),
                            fontSize = 11.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(incidentLogs) { incident ->
                IncidentItem(incident)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ShieldMetricTile(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = modifier
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF08141C))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "$count",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = title,
                color = Color(0xFF8A9BA8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DefenseToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = Color(0xFF708595),
                fontSize = 11.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF001824),
                checkedTrackColor = VexPalette.primary,
                uncheckedThumbColor = Color(0xFF8A9BA8),
                uncheckedTrackColor = Color(0xFF162530)
            )
        )
    }
}

@Composable
fun IncidentItem(incident: com.vex.security.telecom.BombIncident) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val timeStr = remember(incident.timestamp) { dateFormat.format(Date(incident.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E3545), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF07121A))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (incident.type == "SMS_BOMB") Color(0xFF0A3040) else Color(0xFF40121C),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (incident.type == "SMS_BOMB") "SMS BOMB" else "CALL FLOOD",
                            color = if (incident.type == "SMS_BOMB") VexPalette.primary else Color(0xFFFF5252),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Text(
                        text = incident.sender,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = timeStr,
                    color = Color(0xFF708595),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = incident.sampleContent,
                color = Color(0xFFA0B0C0),
                fontSize = 11.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Burst Count: ${incident.burstCount} events / 60s",
                    color = Color(0xFF6B8090),
                    fontSize = 10.sp
                )

                Box(
                    modifier = Modifier
                        .background(Color(0xFF35E08A).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFF35E08A).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = incident.actionTaken,
                        color = Color(0xFF35E08A),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
