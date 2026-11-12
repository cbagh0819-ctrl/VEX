package com.vex.security.scanner

import java.util.Locale

data class DangerousPermissionPair(
    val id: String,
    val title: String,
    val ruleSeverity: String, // "CRITICAL", "HIGH", "MEDIUM"
    val riskWeight: Int,
    val requiredPermissions: List<String>,
    val explanation: String,
    val mitreTechnique: String
)

object ApkDangerousPairsDetector {

    private val RULES = listOf(
        DangerousPermissionPair(
            id = "ACCESSIBILITY_OVERLAY",
            title = "Accessibility Service + Overlay Window (Banking Trojan)",
            ruleSeverity = "CRITICAL",
            riskWeight = 85,
            requiredPermissions = listOf(
                "android.permission.BIND_ACCESSIBILITY_SERVICE",
                "android.permission.SYSTEM_ALERT_WINDOW"
            ),
            explanation = "Allows drawing invisible deceptive overlays over banking/payment screens while using accessibility hooks to log credentials and steal PINs.",
            mitreTechnique = "MITRE ATT&CK T1056.001 (Input Capture: Keylogging) & T1411 (Input Prompting)"
        ),
        DangerousPermissionPair(
            id = "ACCESSIBILITY_SMS",
            title = "Accessibility Automation + SMS Reading (2FA Bypass)",
            ruleSeverity = "CRITICAL",
            riskWeight = 80,
            requiredPermissions = listOf(
                "android.permission.BIND_ACCESSIBILITY_SERVICE",
                "android.permission.READ_SMS"
            ),
            explanation = "Allows automated scraping of incoming SMS one-time passwords (OTP) and interacting with user interfaces without user intervention.",
            mitreTechnique = "MITRE ATT&CK T1418 (Software Discovery) & T1516 (2FA Bypass)"
        ),
        DangerousPermissionPair(
            id = "OVERLAY_NOTIFICATION",
            title = "System Overlay + Notification Listener (OTP Sniffing)",
            ruleSeverity = "HIGH",
            riskWeight = 65,
            requiredPermissions = listOf(
                "android.permission.SYSTEM_ALERT_WINDOW",
                "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
            ),
            explanation = "Application can intercept push notifications from WhatsApp/Banking while blocking or altering visual display.",
            mitreTechnique = "MITRE ATT&CK T1411 & T1516"
        ),
        DangerousPermissionPair(
            id = "INSTALLER_INTERNET",
            title = "Request Install Packages + Internet (Stealth Dropper)",
            ruleSeverity = "HIGH",
            riskWeight = 60,
            requiredPermissions = listOf(
                "android.permission.REQUEST_INSTALL_PACKAGES",
                "android.permission.INTERNET"
            ),
            explanation = "Enables dynamic retrieval and prompting for secondary payload installation, a common signature of staged dropper trojans.",
            mitreTechnique = "MITRE ATT&CK T1544 (Command & Control) & T1105 (Ingress Tool Transfer)"
        ),
        DangerousPermissionPair(
            id = "DEVICE_ADMIN_BOOT",
            title = "Device Admin + Boot Completed (Persistence & Ransomware)",
            ruleSeverity = "CRITICAL",
            riskWeight = 75,
            requiredPermissions = listOf(
                "android.permission.BIND_DEVICE_ADMIN",
                "android.permission.RECEIVE_BOOT_COMPLETED"
            ),
            explanation = "Grants administrative authority that resists uninstallation, surviving reboots to enforce screen locks or extortion.",
            mitreTechnique = "MITRE ATT&CK T1098 (Persistence) & T1486 (Data Encrypted for Impact)"
        ),
        DangerousPermissionPair(
            id = "SPYWARE_TRINITY",
            title = "Audio Recording + Camera + Fine Location (Surveillance)",
            ruleSeverity = "HIGH",
            riskWeight = 70,
            requiredPermissions = listOf(
                "android.permission.RECORD_AUDIO",
                "android.permission.CAMERA",
                "android.permission.ACCESS_FINE_LOCATION"
            ),
            explanation = "Full environmental surveillance package: simultaneous unconstrained audio recording, video capture, and high-precision GPS tracking.",
            mitreTechnique = "MITRE ATT&CK T1430 (Location Tracking) & T1429 (Audio Capture)"
        ),
        DangerousPermissionPair(
            id = "SMS_CONTACTS_PHONE",
            title = "SMS Send + Contacts + Phone State (SMS Worm Spreader)",
            ruleSeverity = "HIGH",
            riskWeight = 65,
            requiredPermissions = listOf(
                "android.permission.SEND_SMS",
                "android.permission.READ_CONTACTS",
                "android.permission.READ_PHONE_STATE"
            ),
            explanation = "Application can harvest device contacts and autonomously propagate phishing messages via SMS to contacts in address book.",
            mitreTechnique = "MITRE ATT&CK T1583 (Acquire Infrastructure) & T1418"
        ),
        DangerousPermissionPair(
            id = "ACCESSIBILITY_KEYGUARD",
            title = "Accessibility Service + Keyguard Bypass",
            ruleSeverity = "HIGH",
            riskWeight = 70,
            requiredPermissions = listOf(
                "android.permission.BIND_ACCESSIBILITY_SERVICE",
                "android.permission.DISABLE_KEYGUARD"
            ),
            explanation = "Can unlock screen lock guards and perform automated interactions without human presence.",
            mitreTechnique = "MITRE ATT&CK T1056 & T1548"
        )
    )

    fun evaluate(requestedPermissions: List<String>): List<DangerousPermissionPair> {
        val normalized = requestedPermissions.map { it.uppercase(Locale.ROOT).trim() }.toSet()
        val detected = mutableListOf<DangerousPermissionPair>()

        for (rule in RULES) {
            val allPresent = rule.requiredPermissions.all { req ->
                val upperReq = req.uppercase(Locale.ROOT)
                val simple = upperReq.substringAfterLast(".")
                normalized.any { it == upperReq || it.endsWith(".$simple") || it == simple }
            }
            if (allPresent) {
                detected.add(rule)
            }
        }
        return detected
    }
}
