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
    explanation = Column(Text)
    recommendation = Column(Text)
    source = Column(String)
    timestamp = Column(BigInteger)

class Incident(Base):
    __tablename__ = "incidents"
    id = Column(Integer, primary_key=True, index=True)
    incident_id = Column(String, unique=True, index=True)
    device_id = Column(String, index=True)
    title = Column(String)
    description = Column(Text)
    severity = Column(String)
    status = Column(String)
    created_at = Column(BigInteger)
    updated_at = Column(BigInteger)

class Alert(Base):
    __tablename__ = "alerts"
    id = Column(Integer, primary_key=True, index=True)
    alert_id = Column(String, unique=True, index=True)
    device_id = Column(String, index=True)
    title = Column(String)
    message = Column(Text)
    severity = Column(String)
    threat_type = Column(String)
    timestamp = Column(BigInteger)
    acknowledged = Column(Integer)

class DetectionTestRun(Base):
    __tablename__ = "detection_test_runs"
    id = Column(Integer, primary_key=True, index=True)
    test_id = Column(String, index=True)
    test_name = Column(String)
    risk_score = Column(Integer)
    expected_level = Column(String)
    actual_level = Column(String)
    result = Column(String)
    action = Column(String)
    explanation = Column(Text)
    recommendation = Column(Text)
    timestamp = Column(BigInteger)

class SecurityPolicy(Base):
    __tablename__ = "security_policies"
    id = Column(Integer, primary_key=True, index=True)
    risk_level = Column(String, unique=True, index=True)
    action = Column(String)
    enabled = Column(Integer, default=1)
