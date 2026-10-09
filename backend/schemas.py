from pydantic import BaseModel
from typing import List


class DeviceCreate(BaseModel):
    device_id: str
    device_name: str
    android_version: str
    model: str
    registered_at: int


class ThreatEventCreate(BaseModel):
    event_id: str
    device_id: str
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
