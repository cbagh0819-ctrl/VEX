package com.vex.security.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vex.security.security.ThreatExplanation
import com.vex.security.security.RiskCalculator

@Composable
fun ThreatExplanationCard(
    explanation: ThreatExplanation,
    indicators: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "WHY VEX FLAGGED THIS",
                style = MaterialTheme.typography.titleMedium
            )

            explanation.reasons.forEach { reason ->

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("• ")
                    Text(
                        text = reason,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "RISK SCORE BREAKDOWN",
                style = MaterialTheme.typography.titleMedium
            )

            indicators.forEach { indicator ->

                val weight = RiskCalculator.getIndicatorWeight(indicator)

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = indicator,
                        modifier = Modifier.weight(1f)
                    )

                    Text("+$weight")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "RECOMMENDATION",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = explanation.recommendation
            )
        }
    }
}
