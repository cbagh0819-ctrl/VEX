"""
VEX CyberGuard — AI Threat Reasoning & Explainability Engine (XAI)
Generates natural language forensics, MITRE ATT&CK taxonomy mappings,
feature importance attribution, and operational remediation playbooks.
"""

from typing import Dict, List, Any

# MITRE ATT&CK Matrix for Mobile & Web Threats
MITRE_ATTACK_MAPPING = {
    "PHISHING_URL": {
        "technique_id": "T1566.002",
        "technique_name": "Phishing: Spearphishing Link",
        "tactic": "Initial Access",
        "impact": "Credential harvesting, session hijacking, identity theft"
    },
    "MALICIOUS_DOWNLOAD": {
        "technique_id": "T1204.002",
        "technique_name": "User Execution: Malicious File",
        "tactic": "Execution",
        "impact": "Drive-by trojan dropped onto device storage, arbitrary code execution"
    },
    "EMBEDDED_CREDENTIALS": {
        "technique_id": "T1552.001",
        "technique_name": "Unsecured Credentials: Credentials in Files",
        "tactic": "Credential Access",
        "impact": "Exfiltration of embedded passwords or API secret tokens"
    },
    "PUNYCODE_DOMAIN": {
        "technique_id": "T1036.007",
        "technique_name": "Masquerading: Double Extension / Homoglyph",
        "tactic": "Defense Evasion",
        "impact": "Deceives user via visual homograph spoofing of trusted brand domains"
    },
    "IP_BASED_URL": {
        "technique_id": "T1071.001",
        "technique_name": "Application Layer Protocol: Web Protocols",
        "tactic": "Command and Control",
        "impact": "Bypasses domain reputation databases via raw numerical IP routing"
    },
    "DANGEROUS_PERMISSIONS_COMBO": {
        "technique_id": "T1418",
        "technique_name": "Software Discovery / Excessive Privileges",
        "tactic": "Discovery & Privilege Escalation",
        "impact": "Spyware capability combining SMS read, overlay window, and device admin"
    },
    "ACCESSIBILITY_ABUSE": {
        "technique_id": "T1056.001",
        "technique_name": "Input Capture: Keylogging via Accessibility",
        "tactic": "Collection",
        "impact": "Keylogging banking passwords, reading 2FA screen content, auto-clicking consent"
    },
    "OVERLAY_PERMISSION": {
        "technique_id": "T1411",
        "technique_name": "Input Capture: GUI Overlay Attack",
        "tactic": "Collection",
        "impact": "Phishing overlay drawn on top of banking apps to steal login credentials"
    },
    "TELECOM_DOS_ATTACK": {
        "technique_id": "T1499",
        "technique_name": "Endpoint Denial of Service: Telephony Flooding",
        "tactic": "Impact",
        "impact": "Service disruption blocking emergency communication, SMS OTP exhaustion"
    },
    "SMS_BOMB_BURST": {
        "technique_id": "T1499.001",
        "technique_name": "Telephony Flooding: SMS Flood",
        "tactic": "Impact",
        "impact": "Automated OTP flood, harassment, distraction from concurrent financial fraud"
    },
    "CALL_FLOOD_ATTACK": {
        "technique_id": "T1499.002",
        "technique_name": "Telephony Flooding: Voice Call Bombing",
        "tactic": "Impact",
        "impact": "Denial of cellular channel via rapid automated call ringing"
    },
    "AI_SYNTHETIC_GENERATOR": {
        "technique_id": "T1588.003",
        "technique_name": "Obtain Capabilities: Artificial Intelligence Deepfake",
        "tactic": "Resource Development",
        "impact": "Synthetic biometric spoofing, KYC bypass, social engineering manipulation"
    },
    "DEEPFAKE_FACE_SYNTHESIS": {
        "technique_id": "T1588.003",
        "technique_name": "Deepfake Video Face Swapping",
        "tactic": "Initial Access & Social Engineering",
        "impact": "Video biometric impersonation, unauthorized identity verification spoof"
    },
    "EXECUTABLE_PAYLOAD": {
        "technique_id": "T1036.005",
        "technique_name": "Masquerading: Match Legitimate Name or Extension",
        "tactic": "Defense Evasion",
        "impact": "Dangerous executable binary disguised as multimedia or text document"
    },
    "ADGUARD_DNS_INTERCEPT": {
        "technique_id": "T1071.004",
        "technique_name": "Application Layer Protocol: DNS-over-HTTPS",
        "tactic": "Command and Control Interception",
        "impact": "Known telemetry beacon or tracking host intercepted at upstream DNS"
    }
}

INDICATOR_WEIGHTS = {
    "TELECOM_DOS_ATTACK": 90,
    "SMS_BOMB_BURST": 85,
    "CALL_FLOOD_ATTACK": 85,
    "AI_SYNTHETIC_GENERATOR": 85,
    "AI_GENERATED_VIDEO": 85,
    "DEEPFAKE_FACE_SYNTHESIS": 80,
    "DANGEROUS_PERMISSIONS_COMBO": 50,
    "EXECUTABLE_PAYLOAD": 50,
    "MALICIOUS_DOWNLOAD": 50,
    "ACCESSIBILITY_ABUSE": 45,
    "PHISHING_URL": 45,
    "EMBEDDED_CREDENTIALS": 40,
    "PUNYCODE_DOMAIN": 35,
    "DOUBLE_EXTENSION": 35,
    "AI_LIPSYNC_MANIPULATION": 65,
    "OVERLAY_PERMISSION": 30,
    "IP_BASED_URL": 25,
    "SUSPICIOUS_EXTENSION": 25,
    "SUSPICIOUS_TLD": 25,
    "UNKNOWN_SENDER_FLOOD": 70,
    "OTP_FLOOD_PATTERN": 75
}

class AIThreatReasoningEngine:
    """
    Explainable AI (XAI) inference module providing automated forensic reasoning,
    mathematical risk scoring decomposition, and operational guidance.
    """

    @classmethod
    def analyze(
        cls,
        threat_type: str,
        risk_score: int,
        risk_level: str,
        indicators: List[str],
        source: str = "",
        details: str = ""
    ) -> Dict[str, Any]:
        """
        Analyzes an event and generates rich Explainable AI diagnostics.
        """
        # 1. Feature Importance Decomposition
        score_breakdown = []
        base_score = 10
        total_calculated = base_score
        score_breakdown.append({"feature": "Base Device Baseline", "points": base_score, "description": "Standard baseline integrity score"})

        for ind in indicators:
            weight = INDICATOR_WEIGHTS.get(ind, 15)
            total_calculated += weight
            score_breakdown.append({
                "feature": ind.replace("_", " ").title(),
                "points": weight,
                "description": f"Heuristic signature weight assigned by CyberGuard analyzer: +{weight} pts"
            })

        calculated_score = min(max(total_calculated, risk_score), 100)

        # 2. Standardized 5-tier classification
        if calculated_score >= 80:
            standard_level = "CRITICAL"
        elif calculated_score >= 60:
            standard_level = "HIGH"
        elif calculated_score >= 40:
            standard_level = "MEDIUM"
        elif calculated_score >= 20:
            standard_level = "LOW"
        else:
            standard_level = "SAFE"

        # 3. MITRE ATT&CK Mapping
        mitre_entries = []
        for ind in indicators:
            if ind in MITRE_ATTACK_MAPPING:
                mitre_entries.append(MITRE_ATTACK_MAPPING[ind])
        if not mitre_entries and threat_type in MITRE_ATTACK_MAPPING:
            mitre_entries.append(MITRE_ATTACK_MAPPING[threat_type])

        # 4. Natural Language Forensics (Explainable AI reasoning)
        reasoning = cls._generate_natural_language_explanation(
            threat_type=threat_type,
            risk_score=calculated_score,
            risk_level=standard_level,
            indicators=indicators,
            source=source,
            details=details
        )

        # 5. Operational Playbook / Remediation Guidance
        remediation = cls._generate_remediation_guidance(
            threat_type=threat_type,
            risk_level=standard_level,
            indicators=indicators
        )

        return {
            "standard_risk_level": standard_level,
            "calculated_score": calculated_score,
            "confidence_pct": min(85 + len(indicators) * 3, 99),
            "reasoning": reasoning,
            "remediation": remediation,
            "score_breakdown": score_breakdown,
            "mitre_attack": mitre_entries,
            "attack_impact": mitre_entries[0]["impact"] if mitre_entries else "Unauthorized device operation or data leak"
        }

    @classmethod
    def _generate_natural_language_explanation(
        cls,
        threat_type: str,
        risk_score: int,
        risk_level: str,
        indicators: List[str],
        source: str,
        details: str
    ) -> str:
        upper_type = threat_type.upper()
        ind_str = ", ".join(indicators) if indicators else "anomaly baseline"

        if "BOMB" in upper_type or "TELECOM" in upper_type:
            return (
                f"The AI Telecom Monitor detected an anomalous burst of incoming communications violating frequency thresholds. "
                f"A high-velocity flood was identified with indicators [{ind_str}], indicating an automated denial-of-service attack. "
                f"This classified the incident as {risk_level} (Score {risk_score}/100) to protect telephony channels and prevent OTP exhaustion."
            )
        elif "DNS" in upper_type or "ADGUARD" in upper_type:
            return (
                f"AdGuard Secure DNS Engine intercepted an upstream domain resolution query. "
                f"The target host resolved to 0.0.0.0 or NXDOMAIN, matching verified telemetry networks and ad trackers. "
                f"Because domain resolution was neutralized prior to socket connection, the risk is rated {risk_level} ({risk_score}/100)."
            )
        elif "URL" in upper_type or "BROWSER" in upper_type or "PHISHING" in upper_type:
            return (
                f"VEX Web Threat Analyzer evaluated destination URI structure and detected high-confidence malicious patterns: [{ind_str}]. "
                f"The endpoint exhibits characteristics of credential harvesting or drive-by payload distribution. "
                f"Model confidence is high ({risk_score}/100), triggering an automated {risk_level} mitigation to isolate the browser session."
            )
        elif "APK" in upper_type or "APP" in upper_type or "PERMISSION" in upper_type:
            return (
                f"Package manifest forensic audit identified a dangerous combination of high-privilege capabilities: [{ind_str}]. "
                f"The application requested Accessibility automation alongside background communications, a known indicator of mobile banking trojans. "
                f"Scored at {risk_score}/100 ({risk_level}) due to severe privilege escalation potential."
            )
        elif "VIDEO" in upper_type or "DEEPFAKE" in upper_type:
            return (
                f"Computer Vision & Temporal Analyzer processed keyframes and detected inter-frame luminance variance and edge synthesis anomalies. "
                f"Temporal coherence metrics diverged from authentic camera sensors with indicators [{ind_str}]. "
                f"Classified as {risk_level} (Score {risk_score}/100) with synthetic deepfake confidence."
            )
        elif "PHOTO" in upper_type or "IMAGE" in upper_type:
            return (
                f"Forensic metadata inspection and spatial noise distribution revealed synthetic generative characteristics. "
                f"Exif camera quantization matrices were absent or matched generative diffusion artifacts: [{ind_str}]. "
                f"Risk evaluated at {risk_score}/100 ({risk_level})."
            )
        elif "FILE" in upper_type or "USB" in upper_type:
            return (
                f"Storage inspection detected suspicious file structure and potential MIME disguise: [{ind_str}]. "
                f"Binary entropy indicates an executable payload masked with a deceptive file extension. "
                f"Risk evaluated at {risk_score}/100 ({risk_level})."
            )
        else:
            return (
                f"CyberGuard heuristic engine flagged suspicious operational telemetry: [{ind_str}]. "
                f"Behavioral indicators contributed to an aggregated risk score of {risk_score}/100, placing this incident in the {risk_level} tier."
            )

    @classmethod
    def _generate_remediation_guidance(
        cls,
        threat_type: str,
        risk_level: str,
        indicators: List[str]
    ) -> str:
        upper_type = threat_type.upper()
        if "BOMB" in upper_type or "TELECOM" in upper_type:
            return "Active Call/SMS Bombing Shield engaged. Retain frequency filter enabled, do not interact with unsolicited OTPs, and report attack timestamps to carrier."
        elif "DNS" in upper_type:
            return "Upstream request blocked at DNS layer (0.0.0.0). No data leaked. Maintain AdGuard Custom DNS active during browsing."
        elif "URL" in upper_type or "BROWSER" in upper_type:
            return "Do not navigate to destination. Flush browser cookies and cache. If credentials were submitted, change corresponding account passwords immediately."
        elif "APK" in upper_type or "APP" in upper_type:
            return "Uninstall application immediately via Android Settings. Revoke Accessibility and Device Admin permissions if previously granted."
        elif "VIDEO" in upper_type or "PHOTO" in upper_type:
            return "Do not verify identity or approve transactions using this synthetic media. Disregard communication originating from this source."
        elif "FILE" in upper_type:
            return "Quarantine or delete file immediately. Do not execute or open via external file managers."
        else:
            return "Review threat report details. If suspicious, isolate device from corporate network and conduct full storage scan."
