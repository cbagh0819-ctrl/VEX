package com.vex.security

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.vex.security.monitor.FileMonitor
import com.vex.security.network.ApiClient
import com.vex.security.network.DeviceRequest
import com.vex.security.security.PolicyManager
import com.vex.security.telecom.BombShieldManager
import com.vex.security.ui.MainScreen
import com.vex.security.ui.splash.SplashScreen
import com.vex.security.ui.theme.VEXTheme
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    private val mediaProjectionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val serviceIntent = Intent(this, com.vex.security.defense.VexLiveCallInterceptionService::class.java).apply {
                    putExtra("EXTRA_RESULT_CODE", result.resultCode)
                    putExtra("EXTRA_RESULT_DATA", result.data)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
                Toast.makeText(this, "🛡️ VEX Live Deepfake Call Shield Activated", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Live Screen Interception permission declined", Toast.LENGTH_SHORT).show()
            }
        }

    fun requestLiveCallShield() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
            Toast.makeText(this, "Please grant Overlay Permission for deepfake alerts", Toast.LENGTH_LONG).show()
            return
        }

        val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionLauncher.launch(projectionManager.createScreenCaptureIntent())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FileMonitor.start(this)
        BombShieldManager.init(this)
        com.vex.security.defense.VexAiDefenseEngine.initialize(this)
        com.vex.security.ai.VexAiDefenseEngine.initModel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        lifecycleScope.launch {
            PolicyManager.refresh(this@MainActivity)
        }

        lifecycleScope.launch {
            for (attempt in 1..10) {
                try {
                    val response = ApiClient.apiService.healthCheck()
                    if (response.isSuccessful) {
                        Log.d("VEX_BACKEND", "Backend connected successfully: ${response.body()}")
                        break
                    } else {
                        Log.e("VEX_BACKEND", "Backend returned error: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.w("VEX_BACKEND", "Backend connection attempt $attempt failed: ${e.message}")
                }
                kotlinx.coroutines.delay(3000)
            }
        }

        lifecycleScope.launch {
            val deviceId = DeviceIdentity.getDeviceId(this@MainActivity)
            val device = DeviceRequest(
                device_id = deviceId,
                device_name = "VEX Android",
                android_version = Build.VERSION.RELEASE,
                model = Build.MODEL,
                registered_at = System.currentTimeMillis()
            )

            for (attempt in 1..10) {
                try {
                    val response = ApiClient.apiService.registerDevice(device)
                    if (response.isSuccessful) {
                        Log.d("VEX_DEVICE", "Device registered successfully: ${response.body()}")
                        break
                    } else {
                        Log.e("VEX_DEVICE", "Registration returned error: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.w("VEX_DEVICE", "Device registration attempt $attempt failed: ${e.message}")
                }
                kotlinx.coroutines.delay(3000)
            }
        }

        com.vex.security.ui.theme.ThemeState.init(this)

        setContent {
            val currentTheme = com.vex.security.ui.theme.ThemeState.currentTheme
            key(currentTheme) {
                VEXTheme(themeMode = currentTheme) {
                    var showSplash by remember { mutableStateOf(true) }
                    if (showSplash) {
                        SplashScreen(onSplashFinished = { showSplash = false })
                    } else {
                        MainScreen()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Automatically annihilate all browser cache, cookies, DOM databases, and history
        com.vex.security.browser.VexBrowserEngine.clearAllBrowserData(this)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= TRIM_MEMORY_MODERATE) {
            com.vex.security.browser.VexBrowserEngine.clearAllBrowserData(this)
        }
    }
}
