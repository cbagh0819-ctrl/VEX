from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from database import get_db
from models import Incident
from schemas import IncidentCreate

router = APIRouter(prefix="/incidents", tags=["Incidents"])

@router.post("/")
def create_incident(
    incident: IncidentCreate,
    db: Session = Depends(get_db)
):
    new_incident = Incident(
        incident_id=incident.incident_id,
        device_id=incident.device_id,
        title=incident.title,
        description=incident.description,
        severity=incident.severity,
        status=incident.status,
        created_at=incident.created_at,
        updated_at=incident.updated_at
    )
    db.add(new_incident)
    db.commit()
    db.refresh(new_incident)
    return {
        "status": "created",
        "incident_id": new_incident.incident_id
    }

@router.get("/")
def get_incidents(db: Session = Depends(get_db)):
    return db.query(Incident).order_by(
        Incident.created_at.desc()
    ).all()
