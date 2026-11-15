# Python script to update backend/main.py with Honeypot Canary & Attacker Geo-Tracing

import os

canary_code = '''
# ==============================================================================
# VEX ACTIVE CYBER DECEPTION: HONEYPOT CAMERA BACKFIRE & CANARY GEO-TRACER
# ==============================================================================
from fastapi import Request, Depends
from fastapi.responses import StreamingResponse
from sqlalchemy.orm import Session
from database import get_db
import io
import time
import json
import urllib.request
from PIL import Image, ImageDraw

latest_canary_alert = {
    "trapped": False,
    "ip": None,
    "city": None,
    "region": None,
    "country": None,
    "lat": None,
    "lon": None,
    "isp": None,
    "user_agent": None,
    "timestamp": None,
    "target_id": None
}

def resolve_ip_geo(client_ip: str):
    is_private = (
        client_ip.startswith("127.") or
        client_ip.startswith("192.168.") or
        client_ip.startswith("10.") or
        client_ip == "localhost" or
        client_ip.startswith("172.")
    )
    
    geo_info = {
        "ip": client_ip,
        "is_private": is_private,
        "city": "Bhubaneswar",
        "region": "Odisha",
        "country": "India",
        "lat": 20.2961,
        "lon": 85.8245,
        "isp": "Local Subnet / Wi-Fi Intruder" if is_private else "Public WAN Gateway",
        "subnet": f"{client_ip} (Target Subnet / Linux Exploit)" if is_private else client_ip
    }
    
    # Try public IP-API
    target_ip_to_query = client_ip if not is_private else ""
    try:
        url = f"http://ip-api.com/json/{target_ip_to_query}?fields=status,country,regionName,city,lat,lon,isp,query"
        req = urllib.request.Request(url, headers={"User-Agent": "VEX-Canary/1.0"})
        with urllib.request.urlopen(req, timeout=1.5) as resp:
            data = json.loads(resp.read().decode())
            if data.get("status") == "success":
                geo_info["city"] = data.get("city", geo_info["city"])
                geo_info["region"] = data.get("regionName", geo_info["region"])
                geo_info["country"] = data.get("country", geo_info["country"])
                geo_info["lat"] = data.get("lat", geo_info["lat"])
                geo_info["lon"] = data.get("lon", geo_info["lon"])
                geo_info["isp"] = data.get("isp", geo_info["isp"])
    except Exception:
        pass
    
    return geo_info

def generate_decoy_selfie(geo_info: dict, user_agent: str):
    img = Image.new("RGB", (800, 600), color=(7, 10, 16))
    draw = ImageDraw.Draw(img)
    
    # Neon crimson & cyan frame
    draw.rectangle([(10, 10), (790, 590)], outline=(255, 0, 85), width=3)
    draw.rectangle([(16, 16), (784, 584)], outline=(0, 240, 255), width=1)
    
    # Tactical Crosshair in Center
    cx, cy = 400, 240
    r = 85
    draw.ellipse([(cx-r, cy-r), (cx+r, cy+r)], outline=(0, 255, 157), width=2)
    draw.line([(cx-r-25, cy), (cx+r+25, cy)], fill=(0, 255, 157), width=1)
    draw.line([(cx, cy-r-25), (cx, cy+r+25)], fill=(0, 255, 157), width=1)
    
    # Header text
    draw.text((400, 55), "TARGET PROTECTED BY VEX CYBER GUARD", fill=(255, 0, 85), anchor="mm")
    draw.text((400, 85), "ACTIVE HONEYPOT CAMERA BACKFIRE ENGAGED", fill=(0, 240, 255), anchor="mm")
    
    # Decoy wireframe head
    draw.ellipse([(cx-32, cy-40), (cx+32, cy+25)], outline=(148, 163, 184), width=2)
    draw.arc([(cx-55, cy+10), (cx+55, cy+75)], start=0, end=180, fill=(148, 163, 184), width=2)
    draw.text((cx, cy-5), "DECOY", fill=(0, 255, 157), anchor="mm")
    
    # Forensic Telemetry Box
    draw.rectangle([(30, 360), (770, 565)], fill=(15, 23, 42), outline=(51, 65, 85), width=1)
    draw.text((50, 375), "CRITICAL ALERT: INTRUDER TELEMETRY TRACED & LOCKED", fill=(255, 183, 3))
    draw.text((50, 405), f"• ATTACKER IP      : {geo_info['ip']} ({geo_info['isp']})", fill=(0, 255, 157))
    draw.text((50, 435), f"• GEO COORDINATES  : {geo_info['lat']:.4f} N, {geo_info['lon']:.4f} E", fill=(0, 240, 255))
    draw.text((50, 465), f"• ESTIMATED CITY   : {geo_info['city']}, {geo_info['region']}, {geo_info['country']}", fill=(255, 255, 255))
    draw.text((50, 495), f"• ATTACK METHOD    : Remote Linux Camera2 HAL Shutter Exploit", fill=(148, 163, 184))
    draw.text((50, 525), f"• TOOL / USER-AGENT: {user_agent[:65]}", fill=(100, 116, 139))
    
    buf = io.BytesIO()
    img.save(buf, format="JPEG", quality=90)
    buf.seek(0)
    return buf

@app.get("/api/honeypot/selfie")
@app.get("/honeypot/stealth_snap.jpg")
@app.get("/api/canary/track/{token_id}")
def honeypot_camera_trap(request: Request, token_id: str = "webcam_snoop_01", db: Session = Depends(get_db)):
    client_ip = request.client.host
    user_agent = request.headers.get("user-agent", "curl/7.88.1 (x86_64-linux)")
    geo_info = resolve_ip_geo(client_ip)
    
    global latest_canary_alert
    latest_canary_alert = {
        "trapped": True,
        "ip": client_ip,
        "city": geo_info["city"],
        "region": geo_info["region"],
        "country": geo_info["country"],
        "lat": geo_info["lat"],
        "lon": geo_info["lon"],
        "isp": geo_info["isp"],
        "user_agent": user_agent,
        "timestamp": int(time.time() * 1000),
        "target_id": token_id
    }
    
    # Store in database
    event_id = f"CANARY-{int(time.time())}"
    expl = (f"Active Honeypot Camera Backfire triggered! Attacker IP: {client_ip} "
            f"({geo_info['city']}, {geo_info['country']}). "
            f"Coordinates: {geo_info['lat']}, {geo_info['lon']}. Tool: {user_agent}")
    
    threat_event = models.ThreatEvent(
        event_id=event_id,
        device_id="VEX-PHONE-PRIMARY",
        threat_type="HONEYPOT_ATTACKER_TRACED",
        risk_score=99,
        risk_level="QUARANTINE",
        action="TRACED",
        status="ACTIVE",
        indicators=json.dumps(["CANARY_TOKEN_TRIGGERED", "UNAUTHORIZED_CAMERA_HAL_ACCESS", "ATTACKER_GEO_RESOLVED"]),
        explanation=expl,
        recommendation="Attacker IP and geographical coordinates logged to evidence ledger. Honeypot decoy served.",
        source="HONEYPOT_CHAMBER",
        timestamp=int(time.time() * 1000)
    )
    db.add(threat_event)
    db.commit()
    
    image_buf = generate_decoy_selfie(geo_info, user_agent)
    return StreamingResponse(image_buf, media_type="image/jpeg", headers={
        "X-VEX-Honeypot": "TRIGGERED",
        "X-Attacker-IP": client_ip,
        "X-Attacker-City": geo_info["city"]
    })

@app.get("/api/canary/latest")
def get_latest_canary():
    return latest_canary_alert

@app.post("/api/canary/dismiss")
def dismiss_canary():
    global latest_canary_alert
    latest_canary_alert["trapped"] = False
    return {"status": "dismissed"}
'''

for base_dir in [r'C:\vex\backend', r'c:\Users\HP\Downloads\VEX-master\backend']:
    main_py = os.path.join(base_dir, 'main.py')
    if os.path.exists(main_py):
        with open(main_py, 'r', encoding='utf-8') as f:
            code = f.read()
        if 'HONEYPOT CAMERA BACKFIRE' not in code:
            with open(main_py, 'a', encoding='utf-8') as f:
                f.write('\n' + canary_code)
            print(f"[+] Appended Honeypot Canary to {main_py}")
        else:
            print(f"[=] Already updated in {main_py}")
