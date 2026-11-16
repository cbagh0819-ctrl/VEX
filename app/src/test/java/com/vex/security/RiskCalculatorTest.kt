package com.vex.security

import com.vex.security.security.RiskCalculator
import com.vex.security.security.ThreatIndicators
import org.junit.Assert.*
import org.junit.Test

class RiskCalculatorTest {

    @Test
    fun calculate_cleanAuthenticIndicators_returnsSafeScore() {
        val indicators = listOf(ThreatIndicators.CAMERA_AUTHENTIC, ThreatIndicators.CLEAN_FILE)
        val score = RiskCalculator.calculate(indicators)
        assertEquals("Expected baseline safe score of 10", 10, score)
        assertEquals("Expected SAFE level", "SAFE", RiskCalculator.getRiskLevel(score))
    }

    @Test
    fun calculate_aiSyntheticGenerator_forcesQuarantineScore() {
        val indicators = listOf(ThreatIndicators.AI_SYNTHETIC_GENERATOR)
        val score = RiskCalculator.calculate(indicators)
        assertTrue("Expected score >= 85 for generative AI synthetic image", score >= 85)
        assertEquals("Expected QUARANTINE level", "QUARANTINE", RiskCalculator.getRiskLevel(score))
    }

    @Test
    fun calculate_deepfakeVideo_forcesQuarantineScore() {
        val indicators = listOf(ThreatIndicators.DEEPFAKE_FACE_SYNTHESIS)
        val score = RiskCalculator.calculate(indicators)
        assertTrue("Expected score >= 80 for deepfake face synthesis", score >= 80)
        assertEquals("Expected QUARANTINE level", "QUARANTINE", RiskCalculator.getRiskLevel(score))
    }

    @Test
    fun calculate_scoreBounds_coercedUpTo100() {
        val indicators = listOf(
            ThreatIndicators.EXECUTABLE_PAYLOAD,
            ThreatIndicators.PHISHING_URL,
            ThreatIndicators.MALICIOUS_DOWNLOAD,
            ThreatIndicators.TELECOM_DOS_ATTACK
        )
        val score = RiskCalculator.calculate(indicators)
        assertTrue("Score should not exceed 100", score <= 100)
        assertTrue("Score should be >= 80 for multiple high-severity threats", score >= 80)
    }
}
