package com.vex.security.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vex.security.repository.SocRepository

@Composable
fun SocDashboardScreen() {
    var summary by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    LaunchedEffect(Unit) {
        summary = SocRepository.getSummary()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "SOC Operations Center",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Real-time threat monitoring & telemetry",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            MetricCard("Total Threats Detected", "${summary["total_threats"] ?: summary["total_events"] ?: 0}", MaterialTheme.colorScheme.primary)
        }
        item {
            MetricCard("Critical Threats", "${summary["critical"] ?: 0}", MaterialTheme.colorScheme.error)
        }
        item {
            MetricCard("High Risk Threats", "${summary["high"] ?: 0}", MaterialTheme.colorScheme.tertiary)
        }
        item {
            MetricCard("Blocked Threats", "${summary["blocked"] ?: 0}", MaterialTheme.colorScheme.secondary)
        }
        item {
            MetricCard("Files Scanned", "${summary["files_scanned"] ?: 0}", MaterialTheme.colorScheme.outline)
        }
        item {
            MetricCard("Registered Devices", "${summary["devices"] ?: 0}", MaterialTheme.colorScheme.primaryContainer)
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = color
            )
        }
    }
}
