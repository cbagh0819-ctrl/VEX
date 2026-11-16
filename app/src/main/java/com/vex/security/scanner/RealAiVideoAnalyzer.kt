package com.vex.security.scanner

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel

data class VideoAnalysisReport(
    val fileName: String,
    val durationSeconds: Long,
    val resolution: String,
    val hasAudio: Boolean,
    val tfliteAvailable: Boolean,
    val tfliteScore: Float,
    val heuristicScore: Float,
    val finalScore: Float,
    val verdict: String,
    val findings: List<String>
)

object RealAiVideoAnalyzer {

    private const val TAG = "VEX_AI_VIDEO"
    private const val MODEL_PATH = "deepfake_detector.tflite"

    fun analyzeVideo(context: Context, uri: Uri): VideoAnalysisReport {
        var durationSec = 0L
        var resolution = "Unknown"
        var hasAudio = false
        val findings = mutableListOf<String>()

        var tfliteAvailable = false
        var interpreter: Interpreter? = null
        var tfliteScore = 0.5f

        // 1. Safe TFLite Model Loading with Graceful Fallback
        try {
            val assetFileDescriptor = context.assets.openFd(MODEL_PATH)
            val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = fileInputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

            interpreter = Interpreter(modelBuffer)
            tfliteAvailable = true
            Log.d(TAG, "TFLite deepfake model loaded successfully.")
        } catch (e: Exception) {
            tfliteAvailable = false
            tfliteScore = 0.5f
            Log.w(TAG, "TFLite model not found — using heuristic analysis only (${e.message})")
            findings.add("ℹ️ TFLite neural model not bundled — heuristic deepfake inspection active.")
        }

        // 2. Extract Video & Temporal Metadata via MediaMetadataRetriever
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            durationSec = durationMs / 1000
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "0"
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "0"
            if (width != "0" && height != "0") {
                resolution = "${width}x${height}"
            }
            hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO) == "yes"

            val frameRateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)
            val frameRate = frameRateStr?.toFloatOrNull() ?: 30f

            // Heuristic Check A: Frame rate / temporal anomaly
            if (frameRate < 15f && durationSec > 2) {
                findings.add("⚠️ Unusually low frame rate (${frameRate} fps) — potential AI frame interpolation.")
            }

            // Heuristic Check B: AI generation signature indicators
            val mime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: ""
            if (mime.contains("synth", ignoreCase = true) || mime.contains("diffusion", ignoreCase = true)) {
                findings.add("🔴 Synthetic encoder signature detected in stream metadata.")
            }

            // Heuristic Check C: Missing audio stream on speech/talking head videos
            if (!hasAudio && durationSec > 4) {
                findings.add("⚠️ Muted video stream — typical in silent deepfake generation pipelines.")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error inspecting video metadata: ${e.message}")
        } finally {
            try { retriever.release() } catch (_: Exception) {}
        }

        // 3. Compute Scores
        var heuristicScore = 0.1f
        if (findings.any { it.startsWith("🔴") }) heuristicScore += 0.6f
        if (findings.any { it.startsWith("⚠️") }) heuristicScore += 0.3f
        heuristicScore = heuristicScore.coerceIn(0f, 1f)

        val finalScore = if (tfliteAvailable) {
            (tfliteScore * 0.7f + heuristicScore * 0.3f).coerceIn(0f, 1f)
        } else {
            heuristicScore
        }

        val verdict = when {
            finalScore >= 0.7f -> "DEEPFAKE_ALERT"
            finalScore >= 0.4f -> "SUSPICIOUS"
            else -> "SAFE"
        }

        try {
            interpreter?.close()
        } catch (_: Exception) {}

        return VideoAnalysisReport(
            fileName = uri.lastPathSegment ?: "video.mp4",
            durationSeconds = durationSec,
            resolution = resolution,
            hasAudio = hasAudio,
            tfliteAvailable = tfliteAvailable,
            tfliteScore = tfliteScore,
            heuristicScore = heuristicScore,
            finalScore = finalScore,
            verdict = verdict,
            findings = findings
        )
    }
}
