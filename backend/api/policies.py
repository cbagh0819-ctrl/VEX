from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from database import get_db
import models

router = APIRouter(prefix="/policies", tags=["Policies"])

# Standard 5-Tier Automated Security Policies (Admin Configurable)
DEFAULT_POLICIES = {
    "SAFE": "ALLOW",
    "LOW": "TAG",
    "MEDIUM": "NOTIFY",
    "HIGH": "BLOCK",
    "CRITICAL": "QUARANTINE"
}

policy_store = dict(DEFAULT_POLICIES)

def init_db_policies(db: Session):
    global policy_store
    try:
        existing = db.query(models.SecurityPolicy).all()
        if not existing:
            for level, action in DEFAULT_POLICIES.items():
                p = models.SecurityPolicy(risk_level=level, action=action, enabled=1)
                db.add(p)
            db.commit()
        else:
            for p in existing:
                if p.risk_level and p.action:
                    policy_store[p.risk_level.upper()] = p.action.upper()
    except Exception:
        pass

def get_action_for_risk(risk_level: str) -> str:
    """
    Dynamically maps a standardized risk level to the Admin's configured Automated Action.
    """
    lvl = str(risk_level or "SAFE").upper()
    # Normalize legacy/alias tiers to 5-tier standard
    if lvl in ["WARN"]:
        lvl = "MEDIUM"
    elif lvl in ["BLOCK"]:
        lvl = "HIGH"

    return policy_store.get(lvl, policy_store.get("SAFE", "ALLOW"))

@router.get("/")
def get_policies(db: Session = Depends(get_db)):
    init_db_policies(db)
    return {
        "status": "success",
        "policies": policy_store,
        "available_actions": {
            "SAFE": ["ALLOW", "TAG"],
            "LOW": ["ALLOW", "TAG", "NOTIFY"],
            "MEDIUM": ["NOTIFY", "TAG", "WARN", "BLOCK"],
            "HIGH": ["BLOCK", "QUARANTINE", "NOTIFY"],
            "CRITICAL": ["QUARANTINE", "BLOCK", "NOTIFY"]
        }
    }

@router.post("/")
def update_policies(policies: dict, db: Session = Depends(get_db)):
    global policy_store
    init_db_policies(db)

    # Clean and update policy store
    for level, action in policies.items():
        lvl = str(level).upper()
        act = str(action).upper()
        if lvl in ["SAFE", "LOW", "MEDIUM", "HIGH", "CRITICAL"]:
            policy_store[lvl] = act

            # Persist to database
            existing = db.query(models.SecurityPolicy).filter(models.SecurityPolicy.risk_level == lvl).first()
            if existing:
                existing.action = act
            else:
                db.add(models.SecurityPolicy(risk_level=lvl, action=act, enabled=1))

    db.commit()
    return {
        "status": "success",
        "message": "Automated response policies successfully deployed across SOC",
        "policies": policy_store
    }
