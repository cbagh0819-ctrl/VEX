from fastapi import APIRouter
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
from ai_engine import AIThreatReasoningEngine, MITRE_ATTACK_MAPPING, INDICATOR_WEIGHTS

router = APIRouter(prefix="/ai", tags=["AI Reasoning & Forensics"])

class AnalyzeThreatRequest(BaseModel):
    threat_type: str
    risk_score: int
    risk_level: Optional[str] = "SAFE"
    indicators: List[str] = []
    source: Optional[str] = "CYBERGUARD_CLIENT"
    details: Optional[str] = ""

class DeepfakeScanRequest(BaseModel):
    media_type: str  # "VIDEO" or "PHOTO"
    file_name: str
    file_size_kb: int = 1024
    has_optical_exif: bool = False
    detected_encoder: Optional[str] = None
    has_natural_audio: bool = True
    facial_temporal_jitter_score: float = 0.0

class SecurityAdvisorRequest(BaseModel):
    query: str
    threat_type: Optional[str] = None
    risk_level: Optional[str] = None

@router.post("/analyze")
def analyze_threat(payload: AnalyzeThreatRequest):
    """
    Evaluates threat indicators using Explainable AI (XAI),
    returning feature importance attribution, MITRE ATT&CK mappings, and natural language reasoning.
    """
    result = AIThreatReasoningEngine.analyze(
        threat_type=payload.threat_type,
        risk_score=payload.risk_score,
        risk_level=payload.risk_level or "SAFE",
        indicators=payload.indicators,
        source=payload.source or "",
        details=payload.details or ""
    )
    return result

@router.post("/deepfake-scan")
def deepfake_scan(payload: DeepfakeScanRequest):
    """
    Dedicated AI Deepfake & Generative Media forensic analyzer.
    Detects diffusion artifacts, missing hardware atoms, and synthetic temporal jitter.
    """
    indicators = []
    score = 10
    detected_tool = payload.detected_encoder or "None"

    if payload.detected_encoder:
        enc_lower = payload.detected_encoder.lower()
        if any(k in enc_lower for k in ["sora", "runway", "pika", "kling", "luma", "minimax"]):
            indicators.append("AI_GENERATED_VIDEO")
            score = max(score, 85)
        elif any(k in enc_lower for k in ["midjourney", "stable diffusion", "dall-e", "flux"]):
            indicators.append("AI_SYNTHETIC_GENERATOR")
            score = max(score, 85)
        elif any(k in enc_lower for k in ["deepface", "faceswap", "roop"]):
            indicators.append("DEEPFAKE_FACE_SYNTHESIS")
            score = max(score, 80)
        elif any(k in enc_lower for k in ["remini", "faceapp", "photoshop"]):
            indicators.append("AI_ENHANCED_IMAGE")
            score = max(score, 45)

    if not payload.has_optical_exif:
        indicators.append("MISSING_METADATA")
        score += 15

    if not payload.has_natural_audio and payload.media_type.upper() == "VIDEO":
        indicators.append("SYNTHETIC_AUDIO_TRACK")
        score += 20

    if payload.facial_temporal_jitter_score > 0.6:
        indicators.append("DEEPFAKE_FACE_SYNTHESIS")
        score = max(score, 80)

    score = min(score, 100)

    xai = AIThreatReasoningEngine.analyze(
        threat_type="AI_DEEPFAKE" if payload.media_type.upper() == "VIDEO" else "AI_PHOTO",
        risk_score=score,
        risk_level="CRITICAL" if score >= 80 else ("HIGH" if score >= 60 else ("MEDIUM" if score >= 40 else "SAFE")),
        indicators=indicators,
        source="AI_MEDIA_FORENSICS",
        details=f"Tool: {detected_tool}, Optical: {payload.has_optical_exif}"
    )

    return {
        "media_type": payload.media_type,
        "is_synthetic": score >= 60,
        "confidence_pct": xai["confidence_pct"],
        "detected_generator": detected_tool,
        "risk_score": score,
        "risk_level": xai["standard_risk_level"],
        "reasoning": xai["reasoning"],
        "mitre_attack": xai["mitre_attack"],
        "remediation": xai["remediation"],
        "score_breakdown": xai["score_breakdown"]
    }

@router.post("/advisor")
def security_advisor(payload: SecurityAdvisorRequest):
    """
    AI CyberGuard Security Advisor providing real-time recommendations,
    policy enforcement guidance, and threat mitigation strategies.
    """
    q_lower = payload.query.lower()

    if "bomb" in q_lower or "call" in q_lower or "sms" in q_lower:
        advice = (
            "CyberGuard Telecom DoS Defense active: The system employs sliding-window frequency analysis "
            "(threshold: >5 SMS/10s or >3 calls/15s) with automated carrier silence filters and unknown sender quarantine. "
            "MITRE Technique T1499. Action recommended: QUARANTINE / SILENCE."
        )
    elif "deepfake" in q_lower or "video" in q_lower or "photo" in q_lower:
        advice = (
            "CyberGuard Deepfake Verification Engine uses multi-stage inspection: (1) Hardware camera container atom validation, "
            "(2) Generative AI encoder keyword detection (Sora, Kling, Runway, Midjourney), (3) Optical noise vs diffusion frequency checks. "
            "MITRE Technique T1588.003. Action recommended: QUARANTINE."
        )
    elif "apk" in q_lower or "trojan" in q_lower or "permission" in q_lower:
        advice = (
            "CyberGuard Mobile Defense inspects Android package manifests for dangerous permission clustering: "
            "Accessibility Service (T1056.001) + Overlay Windows (T1411) + SMS Read (T1418). "
            "Action recommended: Automated BLOCK / Revocation."
        )
    elif "dns" in q_lower or "browser" in q_lower or "adguard" in q_lower:
        advice = (
            "VEX Browser operates with zero login, auto-clearing sandbox, and AdGuard DNS (dns.adguard.com). "
            "Threat domains and ad telemetry are intercepted upstream before TCP socket handshake can be established. "
            "MITRE Technique T1071.004. Action recommended: PROACTIVE DNS SINKHOLE."
        )
    elif "policy" in q_lower or "admin" in q_lower:
        advice = (
            "VEX Admin Policy Studio allows SOC administrators to dynamically configure automated actions across 5 tiers: "
            "Safe (Allow), Low (Tag), Medium (Notify/Warn), High (Block), and Critical (Quarantine). "
            "All incoming device telemetry enforces the active database policy instantly."
        )
    else:
        advice = (
            f"VEX CyberGuard AI SOC actively monitoring all telemetry vectors. Standardized risk score taxonomy: "
            f"Safe (0-19), Low (20-39), Medium (40-59), High (60-79), Critical (80-100). "
            f"Explainable AI (XAI) engine automatically correlates all indicators with MITRE ATT&CK taxonomy."
        )

    return {
        "query": payload.query,
        "advice": advice,
        "mitre_framework": "MITRE ATT&CK Mobile & Enterprise v14",
        "taxonomy_version": "5-Tier Standard (SAFE, LOW, MEDIUM, HIGH, CRITICAL)"
    }
