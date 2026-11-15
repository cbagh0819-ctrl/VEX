package com.vex.security.ui.settings

import android.content.Context
import android.widget.Toast
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("vex_settings", Context.MODE_PRIVATE)
    }

    var autoScan by remember { mutableStateOf(prefs.getBoolean("auto_scan", true)) }
    val scanInterval by remember { mutableStateOf(prefs.getString("scan_interval", "1 Hour") ?: "1 Hour") }
    var apkScanning by remember { mutableStateOf(prefs.getBoolean("apk_scanning", true)) }

    var usbAutomaticCheck by remember { mutableStateOf(prefs.getBoolean("usb_auto_check", true)) }
    var usbExternalAccess by remember { mutableStateOf(prefs.getBoolean("usb_external_access", true)) }

    var notificationsEnabled by remember { mutableStateOf(prefs.getBoolean("notifications_enabled", true)) }
    var highRiskAlarm by remember { mutableStateOf(prefs.getBoolean("high_risk_alarm", true)) }
    var vibrationEnabled by remember { mutableStateOf(prefs.getBoolean("vibration_enabled", true)) }

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
                            text = "VEX SECURITY SETTINGS",
                            color = Color(0xFFFFFFFF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Manage protection, alarms, and permissions",
                            color = Color(0xFF80A0B0),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Section 1: Protection
        item {
            SettingsSectionCard(title = "Protection") {
                SettingsSwitchRow(
                    title = "Automatic Scanning",
                    subtitle = "Continuously monitor and scan new files",
                    checked = autoScan,
                    onCheckedChange = {
                        autoScan = it
                        prefs.edit().putBoolean("auto_scan", it).apply()
                    }
                )
                HorizontalDivider(color = Color(0xFF005566), modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow(title = "Scan Interval", subtitle = scanInterval) {
                    Toast.makeText(context, "Scan Interval: $scanInterval", Toast.LENGTH_SHORT).show()
                }
                HorizontalDivider(color = Color(0xFF005566), modifier = Modifier.padding(vertical = 8.dp))
                SettingsSwitchRow(
                    title = "APK Deep Scanning",
                    subtitle = "Analyze app permissions and manifest on install",
                    checked = apkScanning,
                    onCheckedChange = {
                        apkScanning = it
                        prefs.edit().putBoolean("apk_scanning", it).apply()
                    }
                )
            }
        }

        // Section 2: USB / OTG Security
        item {
            SettingsSectionCard(title = "USB / OTG Security") {
                SettingsSwitchRow(
                    title = "Automatic File Checks",
                    subtitle = "Inspect files transferred over MTP / USB",
                    checked = usbAutomaticCheck,
                    onCheckedChange = {
                        usbAutomaticCheck = it
                        prefs.edit().putBoolean("usb_auto_check", it).apply()
                    }
                )
                HorizontalDivider(color = Color(0xFF005566), modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow(title = "Scan Status", subtitle = "Active & Monitoring", onClick = {
                    Toast.makeText(context, "USB Scanner is Active", Toast.LENGTH_SHORT).show()
                })
                HorizontalDivider(color = Color(0xFF005566), modifier = Modifier.padding(vertical = 8.dp))
                SettingsSwitchRow(
                    title = "External Storage Access",
                    subtitle = "Allow inspection of connected OTG drives",
                    checked = usbExternalAccess,
                    onCheckedChange = {
                        usbExternalAccess = it
                        prefs.edit().putBoolean("usb_external_access", it).apply()
                    }
                )
            }
        }

        // Section 3: Notifications & Alarm
        item {
            SettingsSectionCard(title = "Notifications & Alarm") {
                SettingsSwitchRow(
                    title = "Enable Notifications",
                    subtitle = "Receive security alerts and scan results",
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        notificationsEnabled = it
                        prefs.edit().putBoolean("notifications_enabled", it).apply()
                    }
                )
                HorizontalDivider(color = Color(0xFF005566), modifier = Modifier.padding(vertical = 8.dp))
                SettingsSwitchRow(
                    title = "High-Risk Alarm Sound",
                    subtitle = "Play emergency siren on quarantine threats",
                    checked = highRiskAlarm,
                    onCheckedChange = {
                        highRiskAlarm = it
                        prefs.edit().putBoolean("high_risk_alarm", it).apply()
                    }
                )
                HorizontalDivider(color = Color(0xFF005566), modifier = Modifier.padding(vertical = 8.dp))
                SettingsSwitchRow(
                    title = "Vibration Alert",
                    subtitle = "Vibrate device on high-risk detections",
                    checked = vibrationEnabled,
                    onCheckedChange = {
                        vibrationEnabled = it
                        prefs.edit().putBoolean("vibration_enabled", it).apply()
                    }
                )
                HorizontalDivider(color = Color(0xFF005566), modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow(title = "Test Emergency Alarm", subtitle = "Tap to test siren sound & stop", onClick = {
                    AlarmPlayer.playAlarm(context)
                    SecurityNotification.showWarning(context, "VEX Test Alarm ringing!")
                    Toast.makeText(context, "Playing test alarm. Tap Stop Alarm to cancel.", Toast.LENGTH_LONG).show()
                })
            }
        }

        // Section 4: Privacy & Permissions
        item {
            SettingsSectionCard(title = "Privacy & Permissions") {
                SettingsRow(title = "Storage Access Permission", subtitle = "Granted (All Files Access)", onClick = {
                    Toast.makeText(context, "Storage Access Granted", Toast.LENGTH_SHORT).show()
                })
                HorizontalDivider(color = Color(0xFF005566), modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow(title = "Notification Permission", subtitle = "Allowed", onClick = {
                    Toast.makeText(context, "Notifications Allowed", Toast.LENGTH_SHORT).show()
                })
                HorizontalDivider(color = Color(0xFF005566), modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow(title = "Privacy Policy", subtitle = "Zero-knowledge local telemetry", onClick = {
                    Toast.makeText(context, "VEX operates entirely locally with offline zero-knowledge security.", Toast.LENGTH_LONG).show()
                })
            }
        }

        // Section 5: Scan History
        item {
            SettingsSectionCard(title = "Scan History") {
                SettingsRow(title = "Clear Scan History & Cache", subtitle = "Remove temporary scan logs", onClick = {
                    context.getSharedPreferences("vex_scan_history", Context.MODE_PRIVATE).edit().clear().apply()
                    Toast.makeText(context, "Scan history cleared successfully.", Toast.LENGTH_SHORT).show()
                })
            }
        }

        // Section 6: About VEX
        item {
            SettingsSectionCard(title = "About VEX") {
                SettingsRow(title = "Version", subtitle = "VEX Cyber Guard v1.0.0 (Production)", onClick = {})
                HorizontalDivider(color = Color(0xFF005566), modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow(title = "Help & Support", subtitle = "Documentation & SOC FAQ", onClick = {
                    Toast.makeText(context, "Contact SOC Support at support@vexsecurity.local", Toast.LENGTH_LONG).show()
                })
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF005566), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B161F))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFF00FFE0),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            content()
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color(0xFFFFFFFF), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = Color(0xFF80A0B0), fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF00FFE0),
                checkedTrackColor = Color(0xFF005566),
                uncheckedThumbColor = Color(0xFF80A0B0),
                uncheckedTrackColor = Color(0xFF0A141A)
            )
        )
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color(0xFFFFFFFF), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = Color(0xFF80A0B0), fontSize = 11.sp)
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF00FFE0),
            modifier = Modifier.size(18.dp)
        )
    }
}
