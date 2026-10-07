package com.vex.security.ui.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vex.security.repository.ThreatRepository
import com.vex.security.security.ThreatEngine
import com.vex.security.security.UrlAnalyzer
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.launch

@Composable
fun UrlScanScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var url by remember { mutableStateOf("") }
    var riskScore by remember { mutableStateOf<Int?>(null) }
    var riskLevel by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var indicators by remember { mutableStateOf<List<String>>(emptyList()) }
    var scanned by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "VEX URL Security & Phishing Detector",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Check links, IP URLs, and shorteners for potential risks",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Paste URL (e.g. http://...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (url.isNotBlank()) {
                    val foundIndicators = UrlAnalyzer.analyze(url)
                    val analysis = ThreatEngine.analyze(foundIndicators)

                    riskScore = analysis.riskScore
                    riskLevel = analysis.riskLevel
                    action = analysis.action
                    indicators = foundIndicators
                    scanned = true

                    scope.launch {
                        try {
                            ThreatRepository.sendUrlThreat(
                                deviceId = DeviceIdentity.getDeviceId(context),
                                url = url,
                                riskScore = analysis.riskScore,
                                riskLevel = analysis.riskLevel,
                                action = analysis.action,
                                indicators = foundIndicators
                            )
                        } catch (_: Exception) {
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ANALYZE URL THREAT")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (scanned) {
            val isDangerous = (riskScore ?: 0) >= 30
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDangerous) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = if (isDangerous) "⚠️ DANGEROUS LINK DETECTED" else "✅ LINK APPEARS SAFE",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDangerous) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Risk Score: $riskScore / 100")
                    Text("Risk Level: $riskLevel")
                    Text("Recommended Action: $action")

                    if (indicators.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Detected Indicators: ${indicators.joinToString(", ")}")
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Detected Indicators: None (Clean)")
                    }
                }
            }
        }
    }
}
