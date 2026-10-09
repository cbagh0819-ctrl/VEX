package com.vex.security.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.vex.security.ui.home.HomeScreen
import com.vex.security.ui.home.SocDashboardScreen
import com.vex.security.ui.scan.AiPhotoCheckScreen
import com.vex.security.ui.scan.AppScanDetailScreen
import com.vex.security.ui.scan.ApkCheckScreen
import com.vex.security.ui.scan.NoMetadataCheckScreen
import com.vex.security.ui.scan.QrCheckScreen
import com.vex.security.ui.scan.UrlScanScreen
import com.vex.security.ui.scan.UsbOtgCheckScreen
import com.vex.security.ui.settings.SettingsScreen
import com.vex.security.ui.threats.ThreatListScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Reports : Screen("reports", "Reports", Icons.Default.List)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.BarChart)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object ApkCheck : Screen("apk_check", "APK Check", Icons.Default.Security)
    object AppScanDetail : Screen("app_scan_detail", "App Scan Detail", Icons.Default.Security)
    object QrCheck : Screen("qr_check", "QR Check", Icons.Default.Security)
    object AiPhotoCheck : Screen("ai_photo_check", "AI Photo Check", Icons.Default.Security)
    object NoMetadataCheck : Screen("no_metadata_check", "No Metadata Check", Icons.Default.Security)
    object UsbCheck : Screen("usb_check", "USB Check", Icons.Default.Security)
    object UrlCheck : Screen("url_check", "URL Check", Icons.Default.Security)
}

@Composable
fun MainScreen() {
    val items = listOf(
        Screen.Home,
        Screen.Reports,
        Screen.Dashboard,
        Screen.Settings
    )
    var selectedRoute by remember { mutableStateOf(Screen.Home.route) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF071118),
                contentColor = Color(0xFF00FFE0)
            ) {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = selectedRoute == screen.route,
                        onClick = { selectedRoute = screen.route },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00FFE0),
                            unselectedIconColor = Color(0xFF7090A0),
                            selectedTextColor = Color(0xFF00FFE0),
                            unselectedTextColor = Color(0xFF7090A0),
                            indicatorColor = Color(0xFF0A2530)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFF000305)
        ) {
            when (selectedRoute) {
                Screen.Home.route -> HomeScreen(
                    onNavigateToApkCheck = { selectedRoute = Screen.ApkCheck.route },
                    onNavigateToAiCheck = { selectedRoute = Screen.AiPhotoCheck.route },
                    onNavigateToNoMetadataCheck = { selectedRoute = Screen.NoMetadataCheck.route },
                    onNavigateToUsbCheck = { selectedRoute = Screen.UsbCheck.route },
                    onNavigateToQrCheck = { selectedRoute = Screen.QrCheck.route },
                    onNavigateToUrlScan = { selectedRoute = Screen.UrlCheck.route },
                    onNavigateToFileScan = { selectedRoute = Screen.Reports.route },
                    onNavigateToDashboard = { selectedRoute = Screen.Dashboard.route }
                )
                Screen.ApkCheck.route -> ApkCheckScreen(
                    onBackPressed = { selectedRoute = Screen.Home.route },
                    onSelectApkClicked = { selectedRoute = Screen.Reports.route }
                )
                Screen.AppScanDetail.route -> AppScanDetailScreen(
                    onBackPressed = { selectedRoute = Screen.ApkCheck.route },
                    onSelectApkClicked = { selectedRoute = Screen.Reports.route }
                )
                Screen.QrCheck.route -> QrCheckScreen(
                    onBackPressed = { selectedRoute = Screen.Home.route }
                )
                Screen.AiPhotoCheck.route -> AiPhotoCheckScreen(
                    onBackPressed = { selectedRoute = Screen.Home.route }
                )
                Screen.NoMetadataCheck.route -> NoMetadataCheckScreen(
                    onBackPressed = { selectedRoute = Screen.Home.route }
                )
                Screen.UsbCheck.route -> UsbOtgCheckScreen(
                    onBackPressed = { selectedRoute = Screen.Home.route }
                )
                Screen.UrlCheck.route -> UrlScanScreen(
                    onBackPressed = { selectedRoute = Screen.Home.route }
                )
                Screen.Reports.route -> ThreatListScreen()
                Screen.Dashboard.route -> SocDashboardScreen()
                Screen.Settings.route -> SettingsScreen(
                    onBackPressed = { selectedRoute = Screen.Home.route }
                )
            }
        }
    }
}
