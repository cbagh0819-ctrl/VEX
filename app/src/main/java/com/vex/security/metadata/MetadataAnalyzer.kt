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

    fun analyze(context: Context, uri: Uri): MetadataRiskReport {
        val extracted = MetadataExtractor.extract(context, uri)
        val details = extracted.details

        val hasGps = details.containsKey("GPS_Latitude") || details.containsKey("GPS_Longitude")
        val model = details["Model"]
        val software = details["Software"]

        var risk = 10
        if (hasGps) risk += 60
        if (model != null) risk += 15
        if (software != null) risk += 10

        val rec = if (hasGps) {
            "CRITICAL PRIVACY RISK: Image contains embedded GPS coordinates. Strip EXIF tags before sharing."
        } else if (model != null) {
            "MODERATE RISK: Hardware device fingerprint detected in metadata."
        } else {
            "LOW RISK: File metadata is sanitized."
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
}
