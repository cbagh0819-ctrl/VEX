package com.vex.security.security

data class ThreatExplanation(
    val reasons: List<String>,
    val recommendation: String
)

object ThreatExplanationEngine {

    fun explain(
        indicators: List<String>,
        riskScore: Int,
        riskLevel: String,
        action: String
    ): ThreatExplanation {

        val reasons = mutableListOf<String>()

        indicators.forEach { indicator ->
            when (indicator) {
                // Controlled test explanations (OTG fixture / backward compat)
                "CONTROLLED_SAFE_URL" ->
                    reasons.add("Controlled test safe URL (Score 10): Verified secure endpoint.")
                "CONTROLLED_WARN_URL" ->
                    reasons.add("Controlled test warning URL (Score 35): Requires user warning.")
                "CONTROLLED_BLOCK_URL" ->
                    reasons.add("Controlled test block URL (Score 60): Blocked due to suspicious parameters.")
                "CONTROLLED_QUARANTINE_URL" ->
                    reasons.add("Controlled test quarantine URL (Score 85): High risk detected, quarantine recommended.")
                "CONTROLLED_APK_SAFE" ->
                    reasons.add("Controlled test safe APK (Score 10): Verified secure application package.")
                "CONTROLLED_APK_WARN" ->
                    reasons.add("Controlled test warning APK (Score 35): Requires user confirmation.")
                "CONTROLLED_APK_BLOCK" ->
                    reasons.add("Controlled test block APK (Score 60): Blocked due to high-risk package characteristics.")
                "CONTROLLED_APK_QUARANTINE" ->
                    reasons.add("Controlled test quarantine APK (Score 85): Severe threat detected, quarantined.")
                "FILE_SAFE_DEMO" ->
                    reasons.add("Clean demo fixture: Verified safe folder structure with no security risks.")
                "FILE_WARN_DEMO" ->
                    reasons.add("Warning demo fixture: Unrecognized file patterns requiring attention.")
                "FILE_HARMFUL_DEMO" ->
                    reasons.add("Malware demo fixture: High risk payload detected, immediate isolation required.")

                // Clean indicators
                ThreatIndicators.NORMAL_URL ->
                    reasons.add("Legitimate website URL structure with standard domain and protocol.")
                ThreatIndicators.CAMERA_AUTHENTIC ->
                    reasons.add("Authentic hardware camera sensor metadata verified (Make, Model, Exposure settings).")
                ThreatIndicators.CLEAN_FILE ->
                    reasons.add("File structure and header match legitimate standard document/media formats.")
                ThreatIndicators.CLEAN_APP ->
                    reasons.add("Package uses standard Android permissions with no abusive capability combinations.")

                // File & Payload threats
                ThreatIndicators.EXECUTABLE_PAYLOAD ->
                    reasons.add("File contains executable code, compiled binary, or script payload (e.g. APK, DEX, EXE, SH).")
                ThreatIndicators.SUSPICIOUS_EXTENSION ->
                    reasons.add("The file uses an extension commonly associated with executable or script-based threats.")
                ThreatIndicators.DOUBLE_EXTENSION ->
                    reasons.add("Multiple file extensions detected (e.g. .pdf.exe), commonly used to camouflage malware.")
                ThreatIndicators.HIDDEN_EXTENSION ->
                    reasons.add("File extension does not match its actual underlying MIME format or header magic bytes.")
                ThreatIndicators.UNKNOWN_FILE_TYPE ->
                    reasons.add("System could not identify the MIME type or valid file signature.")
                ThreatIndicators.SUSPICIOUS_FILENAME ->
                    reasons.add("Filename contains keywords associated with malware, exploits, or payload delivery.")
                ThreatIndicators.LARGE_EXECUTABLE ->
                    reasons.add("Unusually large executable package that may embed packed or encrypted payloads.")
                ThreatIndicators.LARGE_FILE ->
                    reasons.add("File size exceeds 100MB; proceed with caution before transferring or opening.")

                // URL & Web threats
                ThreatIndicators.PHISHING_URL ->
                    reasons.add("Deceptive login, banking, or account verification link detected impersonating authentic services.")
                ThreatIndicators.MALICIOUS_DOWNLOAD ->
                    reasons.add("URL directly triggers download of an executable, installer, or script payload.")
                ThreatIndicators.EMBEDDED_CREDENTIALS ->
                    reasons.add("URL contains embedded username or password credentials in the address string.")
                ThreatIndicators.IP_BASED_URL ->
                    reasons.add("URL connects directly to a raw numerical IP address instead of a registered domain name.")
                ThreatIndicators.URL_SHORTENER ->
                    reasons.add("URL uses a shortening redirect service (e.g. bit.ly, tinyurl) masking the true destination.")
                ThreatIndicators.SUSPICIOUS_TLD ->
                    reasons.add("URL uses a top-level domain frequently associated with spam, phishing, and malware campaigns.")
                ThreatIndicators.EXCESSIVE_SUBDOMAINS ->
                    reasons.add("URL has excessive chained subdomains, a technique frequently used to hide malicious domains.")
                ThreatIndicators.PUNYCODE_DOMAIN ->
                    reasons.add("Punycode (xn--) internationalized domain detected, potentially mimicking popular brands.")
                ThreatIndicators.INSECURE_HTTP ->
                    reasons.add("Insecure unencrypted HTTP protocol used; credentials and data may be intercepted.")

                // Image & Metadata threats
                ThreatIndicators.AI_SYNTHETIC_GENERATOR ->
                    reasons.add("Generative AI synthetic image fingerprint or model metadata signature detected.")
                ThreatIndicators.AI_IMAGE_INDICATOR ->
                    reasons.add("Image exhibits characteristics of synthetic generation or missing optical sensor profile.")
                ThreatIndicators.AI_ENHANCED_IMAGE ->
                    reasons.add("Image was modified or altered using AI filtering, face swapping, or editor software.")
                ThreatIndicators.MISSING_METADATA ->
                    reasons.add("All camera EXIF metadata has been stripped or removed from the file.")

                // AI Video & Deepfake threats
                ThreatIndicators.AI_GENERATED_VIDEO ->
                    reasons.add("Synthetic generative video model signature or text-to-video encoder pattern detected (e.g. Sora, Runway, Pika, Kling).")
                ThreatIndicators.DEEPFAKE_FACE_SYNTHESIS ->
                    reasons.add("Deepfake facial synthesis or neural replacement patterns detected across sampled video frames.")
                ThreatIndicators.AI_LIPSYNC_MANIPULATION ->
                    reasons.add("Synthetic lip-sync voice dubbing or facial reenactment artifacts detected.")
                ThreatIndicators.SYNTHETIC_AUDIO_TRACK ->
                    reasons.add("Audio track exhibits synthetic frequency artifacts or unnatural absence of ambient environment audio.")
                ThreatIndicators.AI_VIDEO_INDICATOR ->
                    reasons.add("Video container lacks hardware camera atoms and exhibits artificial temporal cadence.")
                ThreatIndicators.CAMERA_RECORDED_VIDEO ->
                    reasons.add("Authentic hardware video capture verified with valid device container atoms and real optical timing.")

                // App & Permission threats
                ThreatIndicators.DANGEROUS_PERMISSIONS_COMBO ->
                    reasons.add("Critical permission combination detected: High privilege background automation.")
                ThreatIndicators.ACCESSIBILITY_OVERLAY_TROJAN ->
                    reasons.add("CRITICAL TROJAN SIGNATURE: Accessibility Service + System Alert Overlay window detected (Banking Trojan Cloak & Dagger).")
                ThreatIndicators.ACCESSIBILITY_SMS_BYPASS ->
                    reasons.add("CRITICAL 2FA BYPASS: Accessibility Service paired with SMS read access enables automated interception of one-time login passwords.")
                ThreatIndicators.DREBIN_ML_MALWARE_PREDICTION ->
                    reasons.add("Drebin ML permission vector classifier flagged this package as malicious based on behavioral tree splits.")
                ThreatIndicators.ACCESSIBILITY_ABUSE ->
                    reasons.add("Application requests Accessibility Service permission, enabling full screen & keystroke monitoring.")
                ThreatIndicators.OVERLAY_PERMISSION ->
                    reasons.add("Application requests System Overlay permission (Draw Over Other Apps), risking screen hijacking.")
                ThreatIndicators.HIGH_RISK_PERMISSION ->
                    reasons.add("Application requests high-privilege permissions (Camera, Mic, Contacts, Location).")
                ThreatIndicators.SUSPICIOUS_APK ->
                    reasons.add("Application requests critical capabilities requiring detailed scrutiny.")
                ThreatIndicators.OUTDATED_TARGET_SDK ->
                    reasons.add("Package targets an obsolete Android version, bypassing modern runtime permission security.")
                ThreatIndicators.DEBUG_SIGNATURE, ThreatIndicators.DEBUG_CERTIFICATE_DETECTED ->
                    reasons.add("Application is signed with a developer debug certificate (CN=Android Debug) rather than an authentic release keystore.")
                ThreatIndicators.EXPIRED_CERTIFICATE ->
                    reasons.add("Application code signing certificate has expired, compromising software integrity.")
                ThreatIndicators.WEAK_SIGNATURE_ALGORITHM ->
                    reasons.add("Certificate uses obsolete or weak hashing algorithm (MD5/SHA1), vulnerable to signature spoofing.")
                ThreatIndicators.SELF_SIGNED_CERTIFICATE ->
                    reasons.add("Package certificate is self-signed without a recognized developer identity.")

                // Call & SMS Bombing threats
                ThreatIndicators.SMS_BOMB_BURST ->
                    reasons.add("Sudden high-velocity SMS burst detected from unknown sender IDs (SMS Bomber attack signature).")
                ThreatIndicators.CALL_FLOOD_ATTACK ->
                    reasons.add("Repeated burst of incoming unknown calls detected in a short time window (Call Bomber attack signature).")
                ThreatIndicators.OTP_FLOOD_PATTERN ->
                    reasons.add("High volume of automated one-time-password and verification messages received rapidly.")
                ThreatIndicators.UNKNOWN_SENDER_FLOOD ->
                    reasons.add("Communications originating from unlisted/random numbers targeting user device availability.")
                ThreatIndicators.TELECOM_DOS_ATTACK ->
                    reasons.add("Denial-of-Service telecom flooding attempting to incapacitate mobile phone ringer and notifications.")
            }
        }

        if (reasons.isEmpty()) {
            reasons.add("No known high-risk indicators were detected by the current VEX detection rules.")
        }

        val recommendation = when (action) {
            "BLOCK" ->
                "Block this item immediately and prevent installation, opening, or browsing."
            "QUARANTINE" ->
                "Quarantine this item and isolate it from device storage and network access."
            "CRITICAL" ->
                "Critical threat: Immediately isolate and delete the item to protect device security."
            "WARN" ->
                "Exercise caution: Review permissions and origins before proceeding with this item."
            else ->
                "The item is clean and safe to open or access under standard protection."
        }

        return ThreatExplanation(
            reasons = reasons,
            recommendation = recommendation
        )
    }
}
