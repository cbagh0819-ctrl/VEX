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

    event = models.ThreatEvent(**event_data)
    db.add(event)
    db.commit()
    db.refresh(event)
    return event


@router.get("/")
def get_events(db: Session = Depends(get_db)):
    events = (
        db.query(models.ThreatEvent)
        .order_by(models.ThreatEvent.timestamp.desc())
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

        result.append({
            "event_id": e.event_id,
            "device_id": e.device_id,
            "threat_type": e.threat_type,
            "risk_score": e.risk_score,
            "risk_level": e.risk_level,
            "action": e.action,
            "status": e.status,
            "indicators": indicators,
            "source": e.source,
            "timestamp": e.timestamp
        })
    return result
