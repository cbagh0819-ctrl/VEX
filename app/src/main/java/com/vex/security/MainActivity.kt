package com.vex.security

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.vex.security.monitor.FileMonitor
import com.vex.security.monitor.SystemMonitor
import com.vex.security.notification.SecurityNotificationManager
import com.vex.security.security.PolicyManager
import com.vex.security.telecom.BombShieldManager
import com.vex.security.ui.MainScreen
import com.vex.security.ui.splash.SplashScreen
import com.vex.security.ui.theme.VEXTheme
import com.vex.security.usb.UsbSecurityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val multiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { (permission, isGranted) ->
                Log.d("VEX_PERMISSIONS", "Permission: $permission -> granted: $isGranted")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Offline-first startup: No remote server calls, instant initialization
        SecurityNotificationManager.init(this)
        UsbSecurityManager.init(this)
        SystemMonitor.start(this)
        FileMonitor.start(this)
        BombShieldManager.init(this)

        requestRequiredPermissions()

        lifecycleScope.launch(Dispatchers.IO) {
            PolicyManager.refresh(this@MainActivity)
        }

        setContent {
            VEXTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else {
                    MainScreen()
                }
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissionsToRequest.add(Manifest.permission.CAMERA)
        permissionsToRequest.add(Manifest.permission.RECEIVE_SMS)
        permissionsToRequest.add(Manifest.permission.READ_SMS)
        permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE)
        permissionsToRequest.add(Manifest.permission.READ_CALL_LOG)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissionsToRequest.add(Manifest.permission.ANSWER_PHONE_CALLS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_VIDEO)
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val missingPermissions = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (missingPermissions.isNotEmpty()) {
            multiplePermissionsLauncher.launch(missingPermissions)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SystemMonitor.stop(this)
        com.vex.security.browser.VexBrowserEngine.clearAllBrowserData(this)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= TRIM_MEMORY_MODERATE) {
            com.vex.security.browser.VexBrowserEngine.clearAllBrowserData(this)
        }
    }
}
