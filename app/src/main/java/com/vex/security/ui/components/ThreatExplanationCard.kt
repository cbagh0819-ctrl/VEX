package com.vex.security.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vex.security.security.RiskCalculator
import com.vex.security.security.ThreatExplanation
import com.vex.security.ui.theme.VexPalette

@Composable
fun ThreatExplanationCard(
    explanation: ThreatExplanation,
    indicators: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VexPalette.cardBorder, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = VexPalette.cardBackground),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "WHY VEX FLAGGED THIS",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            explanation.reasons.forEach { reason ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("• ", color = Color(0xFFFF3358), fontWeight = FontWeight.Bold)
                    Text(
                        text = reason,
                        color = Color.White,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "RISK SCORE BREAKDOWN",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            indicators.forEach { indicator ->
                val weight = RiskCalculator.getIndicatorWeight(indicator)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = indicator.replace("_", " "),
                        color = Color(0xFFA998C4),
                        fontSize = 11.5.sp
                    )
                    Text(
                        text = "+$weight pts",
                        color = if (weight >= 50) Color(0xFFFF3358) else if (weight >= 20) Color(0xFFFFB800) else Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "RECOMMENDED ACTION",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            Text(
                text = explanation.recommendation,
                color = Color(0xFFFF3358),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
