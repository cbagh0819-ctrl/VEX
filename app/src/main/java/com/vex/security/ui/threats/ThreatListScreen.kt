package com.vex.security.ui.threats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vex.security.repository.ThreatEventsRepository

@Composable
fun ThreatListScreen() {

    var events by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(Unit) {
        events = ThreatEventsRepository.getEvents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "VEX Threat Events",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(events) { event ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Threat: ${event["threat_type"] ?: "UNKNOWN"}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Risk: ${event["risk_score"] ?: 0}")
                        Text("Level: ${event["risk_level"] ?: "UNKNOWN"}")
                        Text("Action: ${event["action"] ?: "UNKNOWN"}")
                        Text("Source: ${event["source"] ?: "UNKNOWN"}")
                    }
                }
            }
        }
    }
}
