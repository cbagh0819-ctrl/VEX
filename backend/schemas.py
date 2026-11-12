from pydantic import BaseModel
from typing import List, Optional


class DeviceCreate(BaseModel):
    device_id: str
    device_name: str
    platform: Optional[str] = "ANDROID" # ANDROID, IOS, CHROME_EXTENSION, WINDOWS, MACOS, WEB
    os_version: Optional[str] = None
    android_version: Optional[str] = None
    model: Optional[str] = "Standard Endpoint"
    client_version: Optional[str] = "1.0.0"
    registered_at: Optional[int] = None


class ThreatEventCreate(BaseModel):
    event_id: str
    device_id: str
    platform: Optional[str] = "ANDROID"
    target_entity: Optional[str] = None
    threat_type: str
    risk_score: int
    risk_level: str
    action: str
    status: str
    indicators: List[str]
    explanation: str = ""
    recommendation: str = ""
    source: str
    timestamp: int


class IncidentCreate(BaseModel):
    incident_id: str
    device_id: str
    title: str
    description: str
    severity: str
    status: str
    created_at: int
    updated_at: int


class AlertCreate(BaseModel):
    alert_id: str
    device_id: str
    title: str
    message: str
    severity: str
    threat_type: str
    timestamp: int
    acknowledged: bool
