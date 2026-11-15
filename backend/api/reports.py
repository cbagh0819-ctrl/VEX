from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from database import get_db
import models

router = APIRouter(prefix="/reports", tags=["Reports"])

@router.get("/summary")
def dashboard_summary(db: Session = Depends(get_db)):
    events = db.query(models.ThreatEvent).all()
    total = len(events)
    safe = 0
    warn = 0
    block = 0
    quarantine = 0
    blocked = 0
    files = 0
    urls = 0
    permissions = 0
    usb = 0

    for event in events:
        level = str(event.risk_level or "").upper()
        action = str(event.action or "").upper()
        threat_type = str(event.threat_type or "").upper()
        source = str(event.source or "").upper()

        if level in ["SAFE", "LOW"]:
            safe += 1
        elif level in ["WARN", "MEDIUM"]:
            warn += 1
        elif level in ["BLOCK", "HIGH"]:
            block += 1
        elif level in ["QUARANTINE", "CRITICAL"]:
            quarantine += 1

        if action in ["BLOCK", "QUARANTINE", "CRITICAL", "WARN"]:
            blocked += 1

        if threat_type in ["FILE", "APK"]:
            files += 1
        elif threat_type == "URL":
            urls += 1
        elif threat_type == "PERMISSION":
            permissions += 1

        if source == "USB":
            usb += 1

    devices = db.query(models.Device).count()

    return {
        "total_threats": total,
        "critical": quarantine,
        "high": block,
        "medium": warn,
        "low": safe,
        "safe": safe,
        "warn": warn,
        "block": block,
        "quarantine": quarantine,
        "blocked": blocked,
        "files_scanned": files,
        "url_threats": urls,
        "permission_threats": permissions,
        "usb_events": usb,
        "devices": devices
    }
