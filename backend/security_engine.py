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

    level = "LOW"
    if score >= 80:
        level = "CRITICAL"
    elif score >= 60:
        level = "HIGH"
    elif score >= 30:
        level = "MEDIUM"

    action = "ALLOW"
    if score >= 80:
        action = "BLOCK"
    elif score >= 60:
        action = "QUARANTINE"
    elif score >= 30:
        action = "WARN"

    return {"risk_score": score, "risk_level": level, "action": action}
