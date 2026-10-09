def get_action(risk_level: str) -> str:
    return {
        "SAFE": "ALLOW",
        "WARN": "WARN",
        "BLOCK": "BLOCK",
        "QUARANTINE": "QUARANTINE"
    }.get(risk_level, "ALLOW")

def get_risk_level_and_action(score: int):
    score = min(max(score, 0), 100)
    if score <= 24:
        level = "SAFE"
    elif score <= 49:
        level = "WARN"
    elif score <= 74:
        level = "BLOCK"
    else:
        level = "QUARANTINE"

    action = get_action(level)
    return level, action

def analyze_threat(indicators: list[str]) -> dict:
    score = 0
    for ind in indicators:
        if ind == "SUSPICIOUS_EXTENSION":
            score += 25
        elif ind == "DOUBLE_EXTENSION":
            score += 30
        elif ind == "UNKNOWN_FILE_TYPE":
            score += 20
        elif ind == "HIGH_RISK_PERMISSION":
            score += 25
        elif ind == "SUSPICIOUS_APK":
            score += 35
        elif ind == "MALICIOUS_TEST_INDICATOR":
            score += 60
        else:
            score += 5
    score = min(max(score, 0), 100)

    level, action = get_risk_level_and_action(score)
    return {"risk_score": score, "risk_level": level, "action": action}
