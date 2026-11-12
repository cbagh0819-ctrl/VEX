from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from database import get_db
import models

router = APIRouter(prefix="/reports", tags=["Reports"])

@router.get("/summary")
def dashboard_summary(db: Session = Depends(get_db)):
    events = db.query(models.ThreatEvent).all()
    total = len(events)
    
    # Standard 5-tier categorization
    critical = 0
    high = 0
    medium = 0
    low = 0
    safe = 0
    
    # Vector classification
    telecom_bomber = 0
    web_phishing = 0
    dns_intercept = 0
    apk_threats = 0
    ai_deepfakes = 0
    usb_hardware = 0
    file_threats = 0
    
    # Operational defenses
    proactive_blocks = 0
    reactive_audits = 0

    for event in events:
        score = event.risk_score or 0
        level = str(event.risk_level or "").upper()
        action = str(event.action or "").upper()
        threat_type = str(event.threat_type or "").upper()
        source = str(event.source or "").upper()

        # Strict 5-tier categorization
        if score >= 80 or level in ["CRITICAL", "QUARANTINE"]:
            critical += 1
        elif score >= 60 or level in ["HIGH", "BLOCK"]:
            high += 1
        elif score >= 40 or level in ["MEDIUM", "WARN"]:
            medium += 1
        elif score >= 20 or level in ["LOW"]:
            low += 1
        else:
            safe += 1

        # Threat Vector categorization
        if "BOMB" in threat_type or "TELECOM" in threat_type:
            telecom_bomber += 1
            proactive_blocks += 1
        elif "DNS" in threat_type or "ADGUARD" in source:
            dns_intercept += 1
            proactive_blocks += 1
        elif "URL" in threat_type or "BROWSER" in source or "PHISHING" in threat_type:
            web_phishing += 1
            if action in ["BLOCK", "QUARANTINE"]:
                proactive_blocks += 1
            else:
                reactive_audits += 1
        elif "APK" in threat_type or "PERMISSION" in threat_type:
            apk_threats += 1
            reactive_audits += 1
        elif "VIDEO" in threat_type or "PHOTO" in threat_type or "DEEPFAKE" in threat_type:
            ai_deepfakes += 1
            reactive_audits += 1
        elif source == "USB" or "USB" in threat_type:
            usb_hardware += 1
            reactive_audits += 1
        else:
            file_threats += 1
            reactive_audits += 1

    devices = db.query(models.Device).count()

    return {
        "total_threats": total,
        # Standardized 5-tier Risk Levels (KT Evaluator Requirement)
        "critical": critical,
        "high": high,
        "medium": medium,
        "low": low,
        "safe": safe,
        # Backward compatibility aliases
        "quarantine": critical,
        "block": high,
        "warn": medium,
        "blocked": critical + high,
        # Threat Vector Breakdown (Data-backed Insights)
        "vectors": {
            "telecom_bomber": telecom_bomber,
            "web_phishing": web_phishing,
            "dns_intercept": dns_intercept,
            "apk_threats": apk_threats,
            "ai_deepfakes": ai_deepfakes,
            "usb_hardware": usb_hardware,
            "file_threats": file_threats
        },
        # Proactive vs. Reactive Distribution
        "operations": {
            "proactive_blocks": proactive_blocks,
            "reactive_audits": reactive_audits
        },
        "devices": devices or 1
    }
