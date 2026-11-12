from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import func
import time
import json

from database import get_db
from models import Device, ThreatEvent, Incident, Alert
from schemas import DeviceCreate
from ai_engine import AIThreatReasoningEngine

router = APIRouter(prefix="/devices", tags=["Devices"])

@router.post("/")
def register_device(
    device: DeviceCreate,
    db: Session = Depends(get_db)
):
    clean_id = (device.device_id or "").strip()
    if not clean_id:
        return {"status": "error", "message": "Missing device_id"}

    existing = db.query(Device).filter(
        func.lower(Device.device_id) == clean_id.lower()
    ).first()

    if existing:
        existing.os_version = device.os_version or existing.os_version
        existing.model = device.model or existing.model
        existing.client_version = device.client_version or existing.client_version
        db.commit()
        return {
            "status": "updated",
            "device_id": existing.device_id
        }

    new_device = Device(
        device_id=clean_id,
        device_name=device.device_name or f"VEX Endpoint ({clean_id})",
        platform=device.platform or "ANDROID",
        os_version=device.os_version or device.android_version or "Android 14+",
        android_version=device.android_version or device.os_version or "Android 14+",
        model=device.model or "Physical Mobile Device",
        client_version=device.client_version or "2.4.0",
        registered_at=device.registered_at or int(time.time() * 1000)
    )
    db.add(new_device)
    db.commit()
    db.refresh(new_device)
    return {
        "status": "success",
        "device_id": new_device.device_id
    }

@router.get("/")
def get_devices(db: Session = Depends(get_db)):
    return db.query(Device).all()

@router.get("/{device_id}/threats")
@router.get("/{device_id}")
def get_device_threats(device_id: str, db: Session = Depends(get_db)):
    """
    Unique Device ID Threat Dossier:
    Allows SOC operators or mobile users to enter their unique ID into the dashboard
    and instantly retrieve real-time threat evaluations, risk score, MITRE ATT&CK tags,
    and canary decoy logs for that exact device.
    """
    clean_id = device_id.strip()

    # 1. Lookup Device Record
    dev = db.query(Device).filter(
        func.lower(Device.device_id) == clean_id.lower()
    ).first()

    # 2. Query all events for this device
    events = db.query(ThreatEvent).filter(
        func.lower(ThreatEvent.device_id) == clean_id.lower()
    ).order_by(ThreatEvent.timestamp.desc()).all()

    # If device not explicitly registered but events exist, auto-create virtual device profile
    if not dev and events:
        dev = Device(
            device_id=clean_id,
            device_name=f"VEX Mobile Client ({clean_id})",
            platform="ANDROID",
            os_version="Android 15+ (Mobile)",
            model="Connected Smartphone",
            client_version="2.4.0",
            registered_at=events[-1].timestamp if events[-1].timestamp else int(time.time() * 1000)
        )

    # 3. Aggregate Metrics & Threat Distribution
    total_events = len(events)
    critical_count = 0
    high_count = 0
    medium_count = 0
    low_count = 0
    safe_count = 0
    max_score = 0
    mitre_techniques = set()
    formatted_events = []

    for e in events:
        score = e.risk_score or 0
        if score > max_score:
            max_score = score

        indicators = []
        try:
            if e.indicators:
                indicators = json.loads(e.indicators) if isinstance(e.indicators, str) else e.indicators
        except Exception:
            indicators = [str(e.indicators)]

        level = (e.risk_level or "SAFE").upper()
        if score >= 80 or level in ["CRITICAL", "QUARANTINE"]:
            critical_count += 1
            level = "CRITICAL"
        elif score >= 60 or level in ["HIGH", "BLOCK"]:
            high_count += 1
            level = "HIGH"
        elif score >= 40 or level in ["MEDIUM", "WARN"]:
            medium_count += 1
            level = "MEDIUM"
        elif score >= 20 or level in ["LOW", "CAUTION"]:
            low_count += 1
            level = "LOW"
        else:
            safe_count += 1
            level = "SAFE"

        # AI Forensic Diagnostics
        ai_diag = AIThreatReasoningEngine.analyze(
            threat_type=e.threat_type or "GENERAL_SCAN",
            risk_score=score,
            risk_level=level,
            indicators=indicators,
            source=e.source or "MOBILE_DEVICE",
            details=e.explanation or ""
        )

        for m in ai_diag.get("mitre_attack", []):
            if isinstance(m, dict):
                label = f"{m.get('id', '')} - {m.get('technique', '')}".strip(" -")
                if label:
                    mitre_techniques.add(label)
            elif m:
                mitre_techniques.add(str(m))

        formatted_events.append({
            "event_id": e.event_id or f"EVT-{e.id}",
            "threat_type": e.threat_type or "FILE_SCAN",
            "risk_score": score,
            "risk_level": level,
            "action": e.action or ("QUARANTINE" if score >= 80 else "SAFE"),
            "status": e.status or "RESOLVED",
            "indicators": indicators,
            "explanation": e.explanation or ai_diag["reasoning"],
            "recommendation": e.recommendation or ai_diag["remediation"],
            "source": e.source or "ANDROID_CLIENT",
            "timestamp": e.timestamp or int(time.time() * 1000),
            "mitre_id": ai_diag.get("mitre_attack", ["N/A"])[0] if ai_diag.get("mitre_attack") else "N/A"
        })

    # Overall device posture
    overall_level = "SAFE"
    if max_score >= 80:
        overall_level = "CRITICAL"
    elif max_score >= 60:
        overall_level = "HIGH"
    elif max_score >= 40:
        overall_level = "MEDIUM"
    elif max_score >= 20:
        overall_level = "LOW"
    elif total_events == 0:
        max_score = 0
        overall_level = "STANDBY"

    # Check for Honeypot Canary hits matching device ID
    canary_hits = []
    try:
        from api.canary import ATTACK_LOGS, LATEST_TRAPPED_ATTACKER
        for attack in ATTACK_LOGS:
            if clean_id.lower() in str(attack.get("target_id", "")).lower():
                canary_hits.append(attack)
        if LATEST_TRAPPED_ATTACKER.get("trapped") and clean_id.lower() in str(LATEST_TRAPPED_ATTACKER.get("target_id", "")).lower():
            canary_hits.append(LATEST_TRAPPED_ATTACKER)
    except Exception:
        pass

    # Query associated incidents
    incidents = db.query(Incident).filter(
        func.lower(Incident.device_id) == clean_id.lower()
    ).order_by(Incident.created_at.desc()).limit(10).all()

    formatted_incidents = [
        {
            "incident_id": inc.incident_id,
            "title": inc.title,
            "description": inc.description,
            "severity": inc.severity,
            "status": inc.status,
            "created_at": inc.created_at
        } for inc in incidents
    ]

    return {
        "found": (dev is not None or len(events) > 0),
        "device_id": clean_id,
        "device": {
            "device_name": dev.device_name if dev else f"VEX Client {clean_id}",
            "platform": dev.platform if dev else "ANDROID",
            "os_version": dev.os_version if dev else "Android 15+",
            "model": dev.model if dev else "Connected Device",
            "client_version": dev.client_version if dev else "2.4.0",
            "registered_at": dev.registered_at if dev else int(time.time() * 1000),
            "status": "ONLINE" if (time.time() * 1000 - (events[0].timestamp if events and events[0].timestamp else 0) < 600000) else "SYNCHRONIZED"
        },
        "posture": {
            "overall_score": max_score,
            "risk_level": overall_level,
            "total_scanned_events": total_events,
            "critical_count": critical_count,
            "high_count": high_count,
            "medium_count": medium_count,
            "low_count": low_count,
            "safe_count": safe_count,
            "honeypot_triggers": len(canary_hits),
            "mitre_matrix": list(mitre_techniques)
        },
        "recent_threats": formatted_events[:25],
        "incidents": formatted_incidents,
        "canary_hits": canary_hits
    }
