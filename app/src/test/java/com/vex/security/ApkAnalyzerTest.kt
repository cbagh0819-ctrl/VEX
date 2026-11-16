package com.vex.security

import com.vex.security.scanner.ApkAnalyzer
import org.junit.Assert.*
import org.junit.Test

class ApkAnalyzerTest {

    @Test
    fun scorePermissions_emptyList_returnsSafe() {
        val (score, level, findings) = ApkAnalyzer.scorePermissions(emptyList())
        assertEquals("Expected score 0 for empty permissions", 0, score)
        assertEquals("Expected SAFE level for empty permissions", "SAFE", level)
        assertTrue("Expected no findings", findings.isEmpty())
    }

    @Test
    fun scorePermissions_criticalAccessibilityAndOverlay_flagsCombination() {
        val permissions = listOf(
            "android.permission.BIND_ACCESSIBILITY_SERVICE",
            "android.permission.SYSTEM_ALERT_WINDOW"
        )
        val (score, level, findings) = ApkAnalyzer.scorePermissions(permissions)
        assertTrue("Expected elevated risk score for accessibility+overlay combo", score >= 40)
        assertTrue("Expected finding mentioning combination", findings.any { it.contains("Dangerous permission combination") })
    }

    @Test
    fun scorePermissions_telecomSmsCombo_flagsDanger() {
        val permissions = listOf(
            "android.permission.READ_SMS",
            "android.permission.SEND_SMS",
            "android.permission.RECEIVE_SMS"
        )
        val (score, level, findings) = ApkAnalyzer.scorePermissions(permissions)
        assertTrue("Expected elevated score for SMS full control combo", score >= 40)
        assertTrue("Expected finding for SMS combo", findings.any { it.contains("READ_SMS") || it.contains("Dangerous") })
    }
}
