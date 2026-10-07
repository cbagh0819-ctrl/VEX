from sqlalchemy import Column, Integer, String, Text, BigInteger
from database import Base

class Device(Base):
    __tablename__ = "devices"
    id = Column(Integer, primary_key=True, index=True)
    device_id = Column(String, unique=True, index=True)
    device_name = Column(String)
    android_version = Column(String)
    model = Column(String)
    registered_at = Column(BigInteger)

class ThreatEvent(Base):
    __tablename__ = "threat_events"
    id = Column(Integer, primary_key=True, index=True)
    event_id = Column(String, unique=True, index=True)
    device_id = Column(String, index=True)
    threat_type = Column(String)
    risk_score = Column(Integer)
    risk_level = Column(String)
    action = Column(String)
    status = Column(String)
    indicators = Column(Text)
    source = Column(String)
    timestamp = Column(Integer)

class Incident(Base):
    __tablename__ = "incidents"
    id = Column(Integer, primary_key=True, index=True)
    incident_id = Column(String, unique=True, index=True)
    device_id = Column(String, index=True)
    title = Column(String)
    description = Column(Text)
    severity = Column(String)
    status = Column(String)
    created_at = Column(Integer)
    updated_at = Column(Integer)

class Alert(Base):
    __tablename__ = "alerts"
    id = Column(Integer, primary_key=True, index=True)
    alert_id = Column(String, unique=True, index=True)
    device_id = Column(String, index=True)
    title = Column(String)
    message = Column(Text)
    severity = Column(String)
    threat_type = Column(String)
    timestamp = Column(Integer)
    acknowledged = Column(Integer)
