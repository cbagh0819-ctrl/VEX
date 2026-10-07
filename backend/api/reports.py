from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from database import get_db
import models

router = APIRouter(prefix="/reports", tags=["Reports"])

@router.get("/summary")
def dashboard_summary(db: Session = Depends(get_db)):
    events = db.query(models.ThreatEvent).all()
    total = len(events)
    critical = 0
    high = 0
    medium = 0
    low = 0
    blocked = 0
    quarantined = 0
    files = 0
    urls = 0
    permissions = 0
    usb = 0

    for event in events:
        level = str(event.risk_level or "").upper()
        action = str(event.action or "").upper()
        threat_type = str(event.threat_type or "").upper()
        source = str(event.source or "").upper()

        if level == "CRITICAL":
            critical += 1
        elif level == "HIGH":
            high += 1
        elif level == "MEDIUM":
            medium += 1
        elif level == "LOW":
            low += 1

        if action == "BLOCK":
            blocked += 1
        elif action == "QUARANTINE":
            quarantined += 1

        if threat_type == "FILE":
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
        "critical": critical,
        "high": high,
        "medium": medium,
        "low": low,
        "blocked": blocked,
        "quarantined": quarantined,
        "files_scanned": files,
        "url_threats": urls,
        "permission_threats": permissions,
        "usb_events": usb,
        "devices": devices
    }
