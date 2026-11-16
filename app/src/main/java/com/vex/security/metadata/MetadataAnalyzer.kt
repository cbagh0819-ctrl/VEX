package com.vex.security.metadata

import android.content.Context
import android.net.Uri
import android.util.Log

data class MetadataRiskReport(
    val hasExifLocation: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val deviceModel: String?,
    val software: String?,
    val riskScore: Int,
    val recommendation: String
)

object MetadataAnalyzer {

    private const val TAG = "VEX_METADATA_ANALYZER"

    fun analyze(extracted: ExtractedMetadata): MetadataRiskReport {
        val details = extracted.details

        val hasGps = details.containsKey("GPS_Latitude") || details.containsKey("GPS_Longitude")
        val model = details["Model"]
        val software = details["Software"]

        var risk = 10
        if (hasGps) risk += 60
        if (software != null) risk += 15

        val rec = when {
            risk >= 70 -> "High privacy risk: GPS location or metadata embedded. Strip before sharing."
            risk >= 25 -> "Medium risk: Camera model or software info embedded in image."
            else -> "Low risk: Clean metadata."
        }

        Log.d(TAG, "Analyzed metadata for: ${extracted.name}, Risk score: $risk")

        return MetadataRiskReport(
            hasExifLocation = hasGps,
            latitude = details["GPS_Latitude"]?.toDoubleOrNull(),
            longitude = details["GPS_Longitude"]?.toDoubleOrNull(),
            deviceModel = model,
            software = software,
            riskScore = risk,
            recommendation = rec
        )
    }

    fun analyze(context: Context, uri: Uri): MetadataRiskReport {
        val extracted = MetadataExtractor.extract(context, uri)
        return analyze(extracted)
    }
}
