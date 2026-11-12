import urllib.request
import json

events = [
    {"event_id": "TEST-SAFE-01", "device_id": "DEV-1", "threat_type": "URL", "risk_score": 10, "risk_level": "SAFE", "action": "ALLOW", "status": "OPEN", "indicators": ["NORMAL"], "source": "TEST", "timestamp": 1791456000000},
    {"event_id": "TEST-WARN-02", "device_id": "DEV-1", "threat_type": "FILE", "risk_score": 35, "risk_level": "WARN", "action": "WARN", "status": "OPEN", "indicators": ["WARN"], "source": "TEST", "timestamp": 1791456001000},
    {"event_id": "TEST-BLOCK-03", "device_id": "DEV-1", "threat_type": "URL", "risk_score": 60, "risk_level": "BLOCK", "action": "BLOCK", "status": "OPEN", "indicators": ["BLOCK"], "source": "TEST", "timestamp": 1791456002000},
    {"event_id": "TEST-QUAR-04", "device_id": "DEV-1", "threat_type": "IMAGE", "risk_score": 85, "risk_level": "QUARANTINE", "action": "QUARANTINE", "status": "OPEN", "indicators": ["QUARANTINE"], "source": "TEST", "timestamp": 1791456003000}
]

for ev in events:
    req = urllib.request.Request('http://127.0.0.1:8000/events/', data=json.dumps(ev).encode('utf-8'), headers={'Content-Type': 'application/json'})
    resp = urllib.request.urlopen(req)
    print(ev['risk_level'], '->', resp.getcode())

print('Summary:', urllib.request.urlopen('http://127.0.0.1:8000/reports/summary').read().decode())
print('Events count:', len(json.loads(urllib.request.urlopen('http://127.0.0.1:8000/events/').read().decode())))
