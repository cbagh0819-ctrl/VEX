package com.vex.security

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.vex.security.monitor.FileMonitor
import com.vex.security.network.ApiClient
import com.vex.security.network.DeviceRequest
import com.vex.security.security.PolicyManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FileMonitor.start(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        lifecycleScope.launch {
            PolicyManager.refresh(this@MainActivity)
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.healthCheck()

                if (response.isSuccessful) {
                    Log.d("VEX_BACKEND", "Backend connected: ${response.body()}")
                } else {
                    Log.e("VEX_BACKEND", "Backend error: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("VEX_BACKEND", "Connection failed: ${e.message}")
            }
        }

        lifecycleScope.launch {
            try {
                val deviceId = DeviceIdentity.getDeviceId(this@MainActivity)

                val device = DeviceRequest(
                    device_id = deviceId,
                    device_name = "VEX Android",
                    android_version = Build.VERSION.RELEASE,
                    model = Build.MODEL,
                    registered_at = System.currentTimeMillis()
                )

                val response = ApiClient.apiService.registerDevice(device)

                if (response.isSuccessful) {
                    Log.d(
                        "VEX_DEVICE",
                        "Device registered: ${response.body()}"
                    )
                } else {
                    Log.e(
                        "VEX_DEVICE",
                        "Registration failed: ${response.code()}"
                    )
                }

            } catch (e: Exception) {
                Log.e(
                    "VEX_DEVICE",
                    "Registration error: ${e.message}"
                )
            }
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
}
