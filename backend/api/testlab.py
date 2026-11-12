from fastapi import APIRouter, HTTPException, Depends
from sqlalchemy.orm import Session
import time
import json
from database import get_db
import models
from security_test_engine import (
    TEST_SCENARIOS,
    run_test
)

router = APIRouter(
    prefix="/testlab",
    tags=["Detection Test Lab"]
)

def get_policy_action(
    db: Session,
    risk_level: str
):
    res = {
        "SAFE": "ALLOW",
        "WARN": "WARN",
        "BLOCK": "BLOCK",
        "QUARANTINE": "QUARANTINE"
    }.get(risk_level, "ALLOW")
    print(f"DEBUG get_policy_action({risk_level}) -> {res}")
    return res

@router.get("/scenarios")
def get_test_scenarios():
    return TEST_SCENARIOS

@router.get("/run-all")
def run_all_tests(
    db: Session = Depends(get_db)
):
    results = []
    for scenario in TEST_SCENARIOS:
        preview = run_test(scenario["id"])
        if preview is None:
            continue
        policy_action = get_policy_action(
            db,
            preview["actual_level"]
        )
        result = run_test(
            scenario["id"],
            action_override=policy_action
        )
        if result is None:
            continue

        test_run = models.DetectionTestRun(
            test_id=result["test_id"],
            test_name=result["name"],
            risk_score=result["risk_score"],
            expected_level=result["expected_level"],
            actual_level=result["actual_level"],
            result=result["result"],
            action=result["action"],
            explanation=" | ".join(result["explanation"]),
            recommendation=result["recommendation"],
            timestamp=int(time.time() * 1000)
        )
        db.add(test_run)

        threat_event = models.ThreatEvent(
            event_id=f"TEST-EVT-{int(time.time() * 1000)}-{result['test_id']}",
            device_id="TEST-DEVICE",
            threat_type=result.get("type", "TEST"),
            risk_score=result["risk_score"],
            risk_level=result["actual_level"],
            action=result["action"],
            status="OPEN",
            indicators=json.dumps(result["indicators"]),
            explanation=" | ".join(result["explanation"]),
            recommendation=result["recommendation"],
            source="TEST_LAB",
            timestamp=int(time.time() * 1000)
        )
        db.add(threat_event)

        results.append(result)
    db.commit()

    total = len(results)
    passed = sum(
        1 for r in results
        if r["result"] == "PASS"
    )
    failed = total - passed
    accuracy = (
        round((passed / total) * 100, 2)
        if total > 0
        else 0
    )
    return {
        "total_tests": total,
        "passed": passed,
        "failed": failed,
        "accuracy": accuracy,
        "results": results
    }

@router.get("/run/{test_id}")
def execute_test(
    test_id: str,
    db: Session = Depends(get_db)
):
    preview = run_test(test_id)
    if preview is None:
        raise HTTPException(
            status_code=404,
            detail="Test scenario not found"
        )
    policy_action = get_policy_action(
        db,
        preview["actual_level"]
    )
    result = run_test(
        test_id,
        action_override=policy_action
    )
    if result is None:
        raise HTTPException(
            status_code=404,
            detail="Test scenario not found"
        )

    test_run = models.DetectionTestRun(
        test_id=result["test_id"],
        test_name=result["name"],
        risk_score=result["risk_score"],
        expected_level=result["expected_level"],
        actual_level=result["actual_level"],
        result=result["result"],
        action=result["action"],
        explanation=" | ".join(result["explanation"]),
        recommendation=result["recommendation"],
        timestamp=int(time.time() * 1000)
    )
    db.add(test_run)

    threat_event = models.ThreatEvent(
        event_id=f"TEST-EVT-{int(time.time() * 1000)}",
        device_id="TEST-DEVICE",
        threat_type=result.get("type", "TEST"),
        risk_score=result["risk_score"],
        risk_level=result["actual_level"],
        action=result["action"],
        status="OPEN",
        indicators=json.dumps(result["indicators"]),
        explanation=" | ".join(result["explanation"]),
        recommendation=result["recommendation"],
        source="TEST_LAB",
        timestamp=int(time.time() * 1000)
    )
    db.add(threat_event)

    db.commit()
    db.refresh(test_run)

    return result

@router.get("/history")
def test_history(
    db: Session = Depends(get_db)
):
    runs = (
        db.query(models.DetectionTestRun)
        .order_by(
            models.DetectionTestRun.timestamp.desc()
        )
        .limit(100)
        .all()
    )
    return [
        {
            "test_id": r.test_id,
            "test_name": r.test_name,
            "risk_score": r.risk_score,
            "expected_level": r.expected_level,
            "actual_level": r.actual_level,
            "result": r.result,
            "action": r.action,
            "explanation": r.explanation,
            "recommendation": r.recommendation,
            "timestamp": r.timestamp
        }
        for r in runs
    ]

@router.get("/report")
def detection_validation_report(
    db: Session = Depends(get_db)
):
    runs = (
        db.query(models.DetectionTestRun)
        .order_by(
            models.DetectionTestRun.timestamp.desc()
        )
        .limit(100)
        .all()
    )
    total = len(runs)
    passed = sum(
        1 for r in runs
        if r.result == "PASS"
    )
    failed = total - passed
    accuracy = (
        round((passed / total) * 100, 2)
        if total > 0
        else 0
    )
    return {
        "report": "VEX Detection Validation Report",
        "total_tests": total,
        "passed": passed,
        "failed": failed,
        "controlled_test_accuracy": accuracy,
        "status": (
            "VALIDATED"
            if total > 0 and failed == 0
            else "NEEDS_REVIEW"
        ),
        "tests": [
            {
                "test_id": r.test_id,
                "test_name": r.test_name,
                "risk_score": r.risk_score,
                "expected_level": r.expected_level,
                "actual_level": r.actual_level,
                "result": r.result,
                "action": r.action,
                "explanation": r.explanation,
                "recommendation": r.recommendation,
                "timestamp": r.timestamp
            }
            for r in runs
        ]
    }
