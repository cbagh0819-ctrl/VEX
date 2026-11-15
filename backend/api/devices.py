from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from database import get_db
from models import Device
from schemas import DeviceCreate

router = APIRouter(prefix="/devices", tags=["Devices"])

@router.post("/")
def register_device(
    device: DeviceCreate,
    db: Session = Depends(get_db)
):
    existing = db.query(Device).filter(
        Device.device_id == device.device_id
    ).first()
    if existing:
        return {
            "status": "already_registered",
            "device_id": existing.device_id
        }
    new_device = Device(
        device_id=device.device_id,
        device_name=device.device_name,
        android_version=device.android_version,
        model=device.model,
        registered_at=device.registered_at
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
