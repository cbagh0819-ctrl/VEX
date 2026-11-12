"""
VEX CyberGuard — Blast Radius Containment & Global Real-Time Blocklist Engine
Pillar 4: Instant sub-millisecond global threat containment with WebSocket broadcast.
Bypasses AI inference for known threats across all connected endpoints (Android, iOS, Chrome, SOC).
"""

from fastapi import APIRouter, WebSocket, WebSocketDisconnect, Query, HTTPException, Depends
from pydantic import BaseModel
from typing import List, Optional, Set, Dict, Any
from sqlalchemy.orm import Session
import time
import re
import hashlib
import asyncio
import json

from database import get_db, Base
from sqlalchemy import Column, Integer, String, BigInteger, Text

# Database model for persistent blocklist items
class BlockedThreat(Base):
    __tablename__ = "global_blocklist"
    id = Column(Integer, primary_key=True, index=True)
    target: str = Column(String, unique=True, index=True) # domain, URL, IP, or SHA256
    target_type: str = Column(String) # DOMAIN, URL, IP, HASH, SMS_SENDER
    threat_type: str = Column(String)
    risk_level: str = Column(String) # HIGH, CRITICAL
    source_device_id: str = Column(String)
    source_platform: str = Column(String) # ANDROID, IOS, CHROME_EXT, SOC
    reason: str = Column(Text)
    created_at: int = Column(BigInteger, index=True)
    hit_count: int = Column(Integer, default=1)

router = APIRouter(prefix="/api/blocklist", tags=["Blast Radius Containment"])

# In-Memory Sub-Millisecond Cache for O(1) Lookups
class BlastRadiusCache:
    def __init__(self):
        # Normalized targets (lowercase domains, stripped IPs, sha256 hashes)
        self._cache: Dict[str, Dict[str, Any]] = {}
        self._url_hashes: Set[str] = set()

    def _normalize(self, target: str) -> str:
        t = target.strip().lower()
        # Strip protocol if domain/url
        if "://" in t:
            try:
                # Extract host/domain
                host = t.split("://", 1)[1].split("/", 1)[0].split(":", 1)[0]
                return host
            except Exception:
                pass
        return t

    def add(self, target: str, metadata: Dict[str, Any]):
        norm = self._normalize(target)
        target_raw = target.strip()
        now = int(time.time() * 1000)
        
        record = {
            "target": target_raw,
            "normalized": norm,
            "target_type": metadata.get("target_type", "DOMAIN"),
            "threat_type": metadata.get("threat_type", "PHISHING"),
            "risk_level": metadata.get("risk_level", "CRITICAL"),
            "source_device_id": metadata.get("source_device_id", "UNKNOWN"),
            "source_platform": metadata.get("source_platform", "UNIVERSAL"),
            "reason": metadata.get("reason", "Automated Blast Radius Quarantine"),
            "timestamp": metadata.get("timestamp", now),
            "hit_count": 1
        }
        self._cache[norm] = record
        # Also index exact raw target if different
        if target_raw != norm:
            self._cache[target_raw] = record
            
        # If it's a URL, compute SHA-256 for fast payload hashing
        url_hash = hashlib.sha256(target_raw.encode("utf-8")).hexdigest()
        self._cache[url_hash] = record
        self._url_hashes.add(url_hash)

    def check(self, target: str) -> Optional[Dict[str, Any]]:
        if not target:
            return None
        t_raw = target.strip()
        norm = self._normalize(t_raw)
        
        # 1. Exact match on normalized host or IP
        if norm in self._cache:
            item = self._cache[norm]
            item["hit_count"] += 1
            return item
            
        # 2. Exact match on raw string
        if t_raw in self._cache:
            item = self._cache[t_raw]
            item["hit_count"] += 1
            return item
            
        # 3. SHA-256 match
        h = hashlib.sha256(t_raw.encode("utf-8")).hexdigest()
        if h in self._cache:
            item = self._cache[h]
            item["hit_count"] += 1
            return item
            
        # 4. Suffix match for subdomains (e.g., phish.evilbank.com matching evilbank.com)
        for cached_key in list(self._cache.keys()):
            if norm.endswith("." + cached_key):
                item = self._cache[cached_key]
                item["hit_count"] += 1
                return item

        return None

    def get_all(self) -> List[Dict[str, Any]]:
        # Return unique records
        seen = set()
        res = []
        for v in self._cache.values():
            t = v["target"]
            if t not in seen:
                seen.add(t)
                res.append(v)
        return sorted(res, key=lambda x: x.get("timestamp", 0), reverse=True)

    def count(self) -> int:
        return len(self.get_all())


# WebSocket Connection Manager for Real-Time Threat Broadcasting
class ConnectionManager:
    def __init__(self):
        self.active_connections: List[WebSocket] = []

    async def connect(self, websocket: WebSocket):
        await websocket.accept()
        self.active_connections.append(websocket)

    def disconnect(self, websocket: WebSocket):
        if websocket in self.active_connections:
            self.active_connections.remove(websocket)

    async def broadcast_containment(self, data: Dict[str, Any]):
        """Broadcasts threat containment payload to all active endpoints in parallel"""
        payload = {
            "event": "BLAST_RADIUS_CONTAINMENT_EVENT",
            "action": "INSTANT_BLOCK",
            "data": data,
            "dispatched_at": int(time.time() * 1000)
        }
        msg = json.dumps(payload)
        disconnected = []
        for connection in self.active_connections:
            try:
                await connection.send_text(msg)
            except Exception:
                disconnected.append(connection)
        for dead in disconnected:
            self.disconnect(dead)

# Global Singletons
blocklist_cache = BlastRadiusCache()
ws_manager = ConnectionManager()

# Seed known high-profile malicious test targets into cache on startup
SEED_TARGETS = [
    {"target": "phish-bank-login.com", "target_type": "DOMAIN", "threat_type": "PHISHING_URL", "risk_level": "CRITICAL", "reason": "Credential Harvester Seed"},
    {"target": "apple-id-verify.click", "target_type": "DOMAIN", "threat_type": "IMPERSONATION", "risk_level": "CRITICAL", "reason": "Digital Brand Spoofing"},
    {"target": "free-iphone-claim.xyz", "target_type": "DOMAIN", "threat_type": "CAMPHISH_LURE", "risk_level": "CRITICAL", "reason": "Biometric CamPhish Exploit"},
    {"target": "evil-payload.ru", "target_type": "DOMAIN", "threat_type": "MALWARE_DROPPER", "risk_level": "CRITICAL", "reason": "Ransomware Distribution Host"}
]
for seed in SEED_TARGETS:
    blocklist_cache.add(seed["target"], seed)


# Schemas
class BlocklistReportRequest(BaseModel):
    target: str
    target_type: Optional[str] = "DOMAIN"  # DOMAIN, URL, IP, HASH, SMS_SENDER
    threat_type: Optional[str] = "PHISHING_URL"
    risk_level: Optional[str] = "CRITICAL"
    source_device_id: Optional[str] = "MANUAL_REPORT"
    source_platform: Optional[str] = "UNIVERSAL"
    reason: Optional[str] = "Blast radius auto-containment triggered"

class BlocklistCheckResponse(BaseModel):
    target: str
    blocked: bool
    bypass_ai: bool
    risk_level: Optional[str] = None
    threat_type: Optional[str] = None
    reason: Optional[str] = None
    latency_ms: float
    source_platform: Optional[str] = None
    contained_at: Optional[int] = None


@router.get("/check", response_model=BlocklistCheckResponse)
def check_blocklist(target: str = Query(..., description="URL, domain, IP, or hash to evaluate")):
    """
    Sub-millisecond containment check.
    If target is found, bypasses heavy AI reasoning pipeline immediately.
    """
    start = time.perf_counter()
    hit = blocklist_cache.check(target)
    elapsed_ms = (time.perf_counter() - start) * 1000.0

    if hit:
        return BlocklistCheckResponse(
            target=target,
            blocked=True,
            bypass_ai=True,
            risk_level=hit.get("risk_level", "CRITICAL"),
            threat_type=hit.get("threat_type", "PHISHING_URL"),
            reason=f"[BLAST RADIUS CONTAINMENT] {hit.get('reason', 'Active Threat')}",
            latency_ms=round(elapsed_ms, 3),
            source_platform=hit.get("source_platform", "UNIVERSAL"),
            contained_at=hit.get("timestamp")
        )

    return BlocklistCheckResponse(
        target=target,
        blocked=False,
        bypass_ai=False,
        latency_ms=round(elapsed_ms, 3)
    )


@router.post("/report")
async def report_threat_and_contain(payload: BlocklistReportRequest, db: Session = Depends(get_db)):
    """
    Ingests threat from ANY endpoint, caches it in <1ms, persists to DB,
    and broadcasts instant containment to all active mobile/desktop/extension clients.
    """
    now = int(time.time() * 1000)
    meta = {
        "target_type": payload.target_type,
        "threat_type": payload.threat_type,
        "risk_level": payload.risk_level,
        "source_device_id": payload.source_device_id,
        "source_platform": payload.source_platform,
        "reason": payload.reason,
        "timestamp": now
    }

    # 1. Update in-memory sub-millisecond cache
    blocklist_cache.add(payload.target, meta)

    # 2. Persist to GlobalBlocklist Table (safely ignoring duplicates)
    try:
        existing = db.query(BlockedThreat).filter(BlockedThreat.target == payload.target.strip()).first()
        if existing:
            existing.hit_count += 1
            existing.risk_level = payload.risk_level
        else:
            db_entry = BlockedThreat(
                target=payload.target.strip(),
                target_type=payload.target_type,
                threat_type=payload.threat_type,
                risk_level=payload.risk_level,
                source_device_id=payload.source_device_id,
                source_platform=payload.source_platform,
                reason=payload.reason,
                created_at=now,
                hit_count=1
            )
            db.add(db_entry)
        db.commit()
    except Exception as e:
        db.rollback()

    # 3. Millisecond Real-Time Broadcast to all endpoints via WebSocket
    broadcast_data = {
        "target": payload.target,
        "target_type": payload.target_type,
        "threat_type": payload.threat_type,
        "risk_level": payload.risk_level,
        "contained_at": now,
        "source_platform": payload.source_platform,
        "reason": payload.reason
    }
    asyncio.create_task(ws_manager.broadcast_containment(broadcast_data))

    return {
        "status": "CONTAINED",
        "message": f"Target '{payload.target}' contained across global mesh in < 1ms",
        "active_blocklist_size": blocklist_cache.count(),
        "broadcast_subscribers": len(ws_manager.active_connections),
        "target": payload.target,
        "timestamp": now
    }


@router.get("/list")
def list_contained_threats():
    """Returns all currently active contained threats and SOC metrics"""
    items = blocklist_cache.get_all()
    return {
        "total_contained": len(items),
        "active_subscribers": len(ws_manager.active_connections),
        "engine": "Blast Radius Real-Time Memory Cache (Sub-1ms)",
        "threats": items
    }


@router.websocket("/ws")
async def blocklist_websocket(websocket: WebSocket):
    """
    Real-time push channel for Android, iOS, Chrome Extensions, and Next.js SOC.
    Pushes instantaneous containment directives the millisecond any endpoint detects a threat.
    """
    await ws_manager.connect(websocket)
    try:
        # Send initial sync payload
        sync_payload = {
            "event": "INITIAL_SYNC",
            "active_blocklist_size": blocklist_cache.count(),
            "items": blocklist_cache.get_all()[:20],
            "server_time": int(time.time() * 1000)
        }
        await websocket.send_text(json.dumps(sync_payload))

        while True:
            # Keep-alive heartbeat & ping-pong
            data = await websocket.receive_text()
            try:
                parsed = json.loads(data)
                if parsed.get("type") == "PING":
                    await websocket.send_text(json.dumps({"type": "PONG", "time": int(time.time() * 1000)}))
            except Exception:
                pass
    except WebSocketDisconnect:
        ws_manager.disconnect(websocket)
    except Exception:
        ws_manager.disconnect(websocket)
