package com.vex.security.ui.scan
import com.vex.security.ui.theme.VexPalette

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QrScanScreen() {

    var scannedUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "VEX QR Security",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // Camera/QR scanner will be connected in the next step
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SCAN QR CODE")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (scannedUrl.isNotEmpty()) {
            Text("Detected URL:")
            Text(scannedUrl)
        }
    }
}
