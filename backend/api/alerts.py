from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from database import get_db
from models import Alert
from schemas import AlertCreate

router = APIRouter(prefix="/alerts", tags=["Alerts"])

@router.post("/")
def create_alert(
    alert: AlertCreate,
    db: Session = Depends(get_db)
):
    new_alert = Alert(
        alert_id=alert.alert_id,
        device_id=alert.device_id,
        title=alert.title,
        message=alert.message,
        severity=alert.severity,
        threat_type=alert.threat_type,
        timestamp=alert.timestamp,
        acknowledged=1 if alert.acknowledged else 0
    )
    db.add(new_alert)
    db.commit()
    db.refresh(new_alert)
    return {
        "status": "created",
        "alert_id": new_alert.alert_id
    }

@router.get("/")
def get_alerts(db: Session = Depends(get_db)):
    return db.query(Alert).order_by(
        Alert.timestamp.desc()
    ).all()
