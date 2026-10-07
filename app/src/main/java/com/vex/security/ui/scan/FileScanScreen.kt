package com.vex.security.ui.scan

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.vex.security.monitor.FileMonitor
import com.vex.security.repository.ThreatRepository
import com.vex.security.scanner.FileScanner
import com.vex.security.security.ThreatEngine
import com.vex.security.security.ThreatIndicators
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FileScanScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedFile by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    var fileHash by remember { mutableStateOf("") }
    var riskScore by remember { mutableStateOf<Int?>(null) }
    var riskLevel by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var scanning by remember { mutableStateOf(false) }

    val filePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                selectedFile = uri
                scanning = true
                fileName = ""
                fileHash = ""
                riskScore = null
                riskLevel = ""
                action = ""

                scope.launch {

                    val result = withContext(Dispatchers.IO) {
                        FileScanner.scanFile(
                            context = context,
                            uri = uri,
                            source = "UPLOAD"
                        )
                    }

                    val indicators = mutableListOf<String>()

                    if (result.extension == null) {
                        indicators.add(ThreatIndicators.UNKNOWN_FILE_TYPE)
                    }

                    val analysis = ThreatEngine.analyze(indicators)

                    riskScore = analysis.riskScore
                    riskLevel = analysis.riskLevel
                    action = analysis.action

                    val deviceId = DeviceIdentity.getDeviceId(context)

                    try {
                        ThreatRepository.sendThreatEvent(
                            deviceId = deviceId,
                            threatType = "FILE",
                            riskScore = analysis.riskScore,
                            riskLevel = analysis.riskLevel,
                            action = analysis.action,
                            status = "OPEN",
                            indicators = indicators,
                            source = "ANDROID_UPLOAD"
                        )
                        Log.d("VEX_UPLOAD", "Threat event sent successfully to backend.")
                    } catch (e: Exception) {
                        Log.e("VEX_UPLOAD", "Failed to send threat event: ${e.message}")
                    }

                    fileName = result.fileName
                    fileHash = result.sha256 ?: "Unavailable"

                    scanning = false
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "VEX File Security",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            enabled = !scanning,
            onClick = {
                filePicker.launch(arrayOf("*/*"))
            }
        ) {
            Text(
                if (scanning) "Scanning..."
                else "Upload & Scan File"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                FileMonitor.scanNow(context)
            }
        ) {
            Text("Scan Device Files Now")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (scanning) {
            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(12.dp))

            Text("Analyzing file...")
        }

        if (!scanning && selectedFile != null) {

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text("FILE SECURITY RESULT")

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("File: $fileName")

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("SHA-256:")
                    Text(fileHash)

                    Spacer(modifier = Modifier.height(10.dp))

                    riskScore?.let {
                        Text("Risk Score: $it / 100")
                    }

                    Text("Risk Level: $riskLevel")

                    Text("Action: $action")
                }
            }
        }
    }
}
