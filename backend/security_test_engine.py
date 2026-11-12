INDICATOR_WEIGHTS = {
    "SUSPICIOUS_EXTENSION": 25,
    "DOUBLE_EXTENSION": 30,
    "UNKNOWN_FILE_TYPE": 20,
    "HIGH_RISK_PERMISSION": 25,
    "SUSPICIOUS_APK": 35,
    "MALICIOUS_TEST_INDICATOR": 60,
    "SUSPICIOUS_FILENAME": 25,
    "LARGE_EXECUTABLE": 20,
    "HIDDEN_EXTENSION": 20,
    "UNKNOWN_MIME_TYPE": 15,
    "EMPTY_FILE_NAME": 10,
    "LARGE_FILE": 10,
    "SUSPICIOUS_URL": 25,
    "PHISHING_URL": 40,
    "IP_BASED_URL": 20,
    "URL_SHORTENER": 15,
    "MISSING_METADATA": 10,
    "AI_IMAGE_INDICATOR": 35,
    "QR_SUSPICIOUS_TARGET": 40,
    "QR_PHISHING_TARGET": 60,
}

TEST_SCENARIOS = [
    # --- QR CATEGORY (4 levels) ---
    {
        "id": "TEST-QR-SAFE",
        "name": "Safe QR Code",
        "type": "QR",
        "description": "Simulates a clean, safe QR code link.",
        "indicators": ["EMPTY_FILE_NAME"],
        "expected_level": "SAFE"
    },
    {
        "id": "TEST-QR-WARN",
        "name": "Warning QR Code",
        "type": "QR",
        "description": "Simulates a QR code using a URL shortener.",
        "indicators": ["URL_SHORTENER", "EMPTY_FILE_NAME"],
        "expected_level": "WARN"
    },
    {
        "id": "TEST-QR-BLOCK",
        "name": "Blocked QR Code",
        "type": "QR",
        "description": "Simulates a QR code targeting a suspicious destination.",
        "indicators": ["QR_SUSPICIOUS_TARGET", "DOUBLE_EXTENSION"],
        "expected_level": "BLOCK"
    },
    {
        "id": "TEST-QR-CRITICAL",
        "name": "Critical QR Code",
        "type": "QR",
        "description": "Simulates a QR code with phishing target indicators.",
        "indicators": ["QR_PHISHING_TARGET", "PHISHING_URL"],
        "expected_level": "QUARANTINE"
    },

    # --- APK CATEGORY (4 levels) ---
    {
        "id": "TEST-APK-SAFE",
        "name": "Safe APK File",
        "type": "APK",
        "description": "Simulates a clean APK package.",
        "indicators": [],
        "expected_level": "SAFE"
    },
    {
        "id": "TEST-APK-WARN",
        "name": "Warning APK File",
        "type": "APK",
        "description": "Simulates an APK requesting high-risk permissions.",
        "indicators": ["HIGH_RISK_PERMISSION"],
        "expected_level": "WARN"
    },
    {
        "id": "TEST-APK-BLOCK",
        "name": "Blocked APK File",
        "type": "APK",
        "description": "Simulates a suspicious APK with high-risk permissions.",
        "indicators": ["SUSPICIOUS_APK", "HIGH_RISK_PERMISSION"],
        "expected_level": "BLOCK"
    },
    {
        "id": "TEST-APK-CRITICAL",
        "name": "Critical APK File",
        "type": "APK",
        "description": "Simulates a malicious APK payload test indicator.",
        "indicators": ["MALICIOUS_TEST_INDICATOR", "SUSPICIOUS_APK", "HIGH_RISK_PERMISSION"],
        "expected_level": "QUARANTINE"
    },

    # --- URL CATEGORY (4 levels) ---
    {
        "id": "TEST-URL-SAFE",
        "name": "Safe URL Link",
        "type": "URL",
        "description": "Simulates a secure domain link.",
        "indicators": [],
        "expected_level": "SAFE"
    },
    {
        "id": "TEST-URL-WARN",
        "name": "Warning URL Link",
        "type": "URL",
        "description": "Simulates a URL shortener service link.",
        "indicators": ["URL_SHORTENER", "LARGE_FILE"],
        "expected_level": "WARN"
    },
    {
        "id": "TEST-URL-BLOCK",
        "name": "Blocked URL Link",
        "type": "URL",
        "description": "Simulates a suspicious verification/login URL.",
        "indicators": ["SUSPICIOUS_URL", "IP_BASED_URL", "URL_SHORTENER"],
        "expected_level": "BLOCK"
    },
    {
        "id": "TEST-URL-CRITICAL",
        "name": "Critical URL Link",
        "type": "URL",
        "description": "Simulates a confirmed phishing URL threat.",
        "indicators": ["PHISHING_URL", "IP_BASED_URL", "SUSPICIOUS_URL"],
        "expected_level": "QUARANTINE"
    },

    # --- IMAGE CATEGORY (4 levels) ---
    {
        "id": "TEST-IMG-SAFE",
        "name": "Safe Image File",
        "type": "IMAGE",
        "description": "Simulates a clean digital photo.",
        "indicators": [],
        "expected_level": "SAFE"
    },
    {
        "id": "TEST-IMG-WARN",
        "name": "Warning Image File",
        "type": "IMAGE",
        "description": "Simulates an image with unknown MIME type.",
        "indicators": ["UNKNOWN_MIME_TYPE", "LARGE_FILE"],
        "expected_level": "WARN"
    },
    {
        "id": "TEST-IMG-BLOCK",
        "name": "Blocked Image File",
        "type": "IMAGE",
        "description": "Simulates an AI-generated or manipulated image.",
        "indicators": ["AI_IMAGE_INDICATOR", "UNKNOWN_FILE_TYPE"],
        "expected_level": "BLOCK"
    },
    {
        "id": "TEST-IMG-CRITICAL",
        "name": "Critical Image File",
        "type": "IMAGE",
        "description": "Simulates a highly manipulated deepfake image.",
        "indicators": ["AI_IMAGE_INDICATOR", "MALICIOUS_TEST_INDICATOR"],
        "expected_level": "QUARANTINE"
    },

    # --- IMAGE METADATA CATEGORY (4 levels) ---
    {
        "id": "TEST-METADATA-SAFE",
        "name": "Safe Metadata File",
        "type": "IMAGE_METADATA",
        "description": "Simulates a photo with complete metadata.",
        "indicators": [],
        "expected_level": "SAFE"
    },
    {
        "id": "TEST-METADATA-WARN",
        "name": "Warning Metadata File",
        "type": "IMAGE_METADATA",
        "description": "Simulates a photo missing metadata attributes.",
        "indicators": ["MISSING_METADATA", "UNKNOWN_FILE_TYPE"],
        "expected_level": "WARN"
    },
    {
        "id": "TEST-METADATA-BLOCK",
        "name": "Blocked Metadata File",
        "type": "IMAGE_METADATA",
        "description": "Simulates a file missing metadata and unknown type.",
        "indicators": ["MISSING_METADATA", "UNKNOWN_FILE_TYPE", "SUSPICIOUS_EXTENSION"],
        "expected_level": "BLOCK"
    },
    {
        "id": "TEST-METADATA-CRITICAL",
        "name": "Critical Metadata File",
        "type": "IMAGE_METADATA",
        "description": "Simulates stripped metadata with malicious indicator.",
        "indicators": ["MISSING_METADATA", "MALICIOUS_TEST_INDICATOR", "HIGH_RISK_PERMISSION"],
        "expected_level": "QUARANTINE"
    }
]

def calculate_score(indicators):
    score = sum(INDICATOR_WEIGHTS.get(ind, 5) for ind in indicators)
    return min(max(score, 0), 100)

calculate_risk = calculate_score

def get_risk_level(score):
    if score >= 75:
        return "QUARANTINE"
    elif score >= 50:
        return "BLOCK"
    elif score >= 25:
        return "WARN"
    else:
        return "SAFE"

def get_action(risk_level: str) -> str:
    return {
        "SAFE": "ALLOW",
        "WARN": "WARN",
        "BLOCK": "BLOCK",
        "QUARANTINE": "QUARANTINE"
    }.get(risk_level, "ALLOW")

get_default_action = get_action

def explain_test(indicators, action):
    reasons = []
    explanations = {
        "SUSPICIOUS_APK": "The APK contains characteristics that require additional security analysis.",
        "HIGH_RISK_PERMISSION": "The application requests multiple sensitive permissions.",
        "DOUBLE_EXTENSION": "The filename contains multiple extensions and may disguise its real file type.",
        "SUSPICIOUS_EXTENSION": "The file uses an extension commonly associated with executable or script content.",
        "PHISHING_URL": "The URL contains characteristics associated with phishing activity.",
        "SUSPICIOUS_URL": "The URL contains suspicious login, verification, or account-related patterns.",
        "AI_IMAGE_INDICATOR": "Detected patterns indicative of AI generation or image manipulation.",
        "MISSING_METADATA": "Critical metadata attributes are missing or stripped from the file.",
        "QR_SUSPICIOUS_TARGET": "The QR code points to a suspicious or untrusted target destination.",
        "QR_PHISHING_TARGET": "The QR code resolves to a confirmed phishing endpoint.",
        "MALICIOUS_TEST_INDICATOR": "Matched controlled test malware indicator signature.",
        "URL_SHORTENER": "The URL uses a shortening service that obscures the destination.",
        "IP_BASED_URL": "The URL directly utilizes an IP address instead of a standard domain.",
        "UNKNOWN_FILE_TYPE": "The system could not confidently determine the file type.",
        "UNKNOWN_MIME_TYPE": "No reliable MIME type was identified for this file."
    }

    for ind in indicators:
        if ind in explanations:
            reasons.append(explanations[ind])

    if not reasons:
        reasons.append("No known high-risk indicators were detected.")

    recommendation = (
        "The item has been quarantined and access is prevented pending further analysis."
        if action == "QUARANTINE"
        else "The item is blocked and access should be prevented."
        if action == "BLOCK"
        else "The item requires user attention before proceeding."
        if action == "WARN"
        else "The item is safe and allowed."
    )

    return {
        "reasons": reasons,
        "recommendation": recommendation
    }

def run_test(test_id, action_override=None):
    scenario = next((s for s in TEST_SCENARIOS if s["id"] == test_id), None)
    if not scenario:
        return None
    
    score = calculate_risk(scenario["indicators"])
    actual_level = get_risk_level(score)
    action = (
        action_override
        if action_override
        else get_action(actual_level)
    )
    explanation = explain_test(
        scenario["indicators"],
        action
    )
    
    return {
        "test_id": scenario["id"],
        "name": scenario["name"],
        "type": scenario["type"],
        "indicators": scenario["indicators"],
        "risk_score": score,
        "expected_level": scenario["expected_level"],
        "actual_level": actual_level,
        "result": "PASS" if actual_level == scenario["expected_level"] else "FAIL",
        "action": action,
        "explanation": explanation["reasons"],
        "recommendation": explanation["recommendation"]
    }
