from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
import json

from database import get_db
import models

router = APIRouter(prefix="/events", tags=["Events"])


@router.post("/")
def create_event(event_data: dict, db: Session = Depends(get_db)):
    if "indicators" in event_data and isinstance(event_data["indicators"], list):
        event_data["indicators"] = json.dumps(event_data["indicators"])

    level = str(event_data.get("risk_level", "SAFE")).upper()
    action = str(event_data.get("action", "ALLOW")).upper()
    if level == "WARN" and action == "ALLOW":
        event_data["action"] = "WARN"
    elif level == "BLOCK" and action == "ALLOW":
        event_data["action"] = "BLOCK"
    elif level in ["QUARANTINE", "CRITICAL"] and action in ["ALLOW", "BLOCK"]:
        event_data["action"] = "QUARANTINE"
        event_data["risk_level"] = "QUARANTINE"

    event_id = event_data.get("event_id")
    existing = None
    if event_id:
        existing = db.query(models.ThreatEvent).filter(models.ThreatEvent.event_id == event_id).first()

    if existing:
        for k, v in event_data.items():
            setattr(existing, k, v)
        db.commit()
        db.refresh(existing)
        return existing
    else:
        event = models.ThreatEvent(**event_data)
        db.add(event)
        db.commit()
        db.refresh(event)
        return event


@router.get("/")
def get_events(db: Session = Depends(get_db)):
    events = (
        db.query(models.ThreatEvent)
        .order_by(models.ThreatEvent.timestamp.desc(), models.ThreatEvent.id.desc())
        .limit(100)
        .all()
    )
    result = []
    for e in events:
        indicators = []
        try:
            if e.indicators:
                indicators = json.loads(e.indicators)
        except Exception:
            indicators = []

        lvl = str(e.risk_level or "").upper()
        act = str(e.action or "").upper()
        if lvl == "WARN" and act == "ALLOW":
            act = "WARN"
        elif lvl == "BLOCK" and act == "ALLOW":
            act = "BLOCK"
        elif lvl in ["QUARANTINE", "CRITICAL"] and act in ["ALLOW", "BLOCK"]:
            act = "QUARANTINE"
            lvl = "QUARANTINE"

        result.append({
            "event_id": e.event_id,
            "device_id": e.device_id,
            "threat_type": e.threat_type,
            "risk_score": e.risk_score,
            "risk_level": lvl,
            "action": act,
            "status": e.status,
            "indicators": indicators,
            "explanation": e.explanation or "",
            "recommendation": e.recommendation or "",
            "source": e.source,
            "timestamp": e.timestamp
        })
    return result
