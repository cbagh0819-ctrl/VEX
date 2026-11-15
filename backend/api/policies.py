from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from database import get_db
import models

router = APIRouter(prefix="/policies", tags=["Policies"])

@router.get("/")
def get_policies(db: Session = Depends(get_db)):
    policies = db.query(models.SecurityPolicy).all()
    default_policies = {
        "SAFE": "ALLOW",
        "WARN": "WARN",
        "BLOCK": "BLOCK",
        "QUARANTINE": "QUARANTINE"
    }
    result = default_policies.copy()
    for p in policies:
        if p.enabled:
            result[p.risk_level.upper()] = p.action.upper()
    return result

@router.post("/")
def update_policy(data: dict, db: Session = Depends(get_db)):
    level = data.get("risk_level", "").upper()
    action = data.get("action", "").upper()
    if level:
        p = db.query(models.SecurityPolicy).filter(models.SecurityPolicy.risk_level == level).first()
        if p:
            p.action = action
        else:
            p = models.SecurityPolicy(risk_level=level, action=action, enabled=1)
            db.add(p)
        db.commit()
        return {"status": "success", "policy": {level: action}}
    return {"status": "error"}
