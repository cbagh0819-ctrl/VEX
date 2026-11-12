"""
VEX Cyber Guard - Canary Token Attribution & Active Honeypot Deception Engine
Handles:
1. Dynamic Decoy Meme Generation (Kid in sunglasses on toy laptop) with forensic overlay
2. Canary Token Web-Bug Tracking (/api/canary/track/{token_id} & /api/honeypot/selfie)
3. Reverse Geo-IP and intruder attribution
4. Live radar state for SOC Dashboard
5. Malicious Phishing Lure simulator (/phish/iphone)
"""

import time
import io
import json
import requests
from typing import Optional, Dict, Any
from fastapi import APIRouter, Request, Response, HTTPException
from fastapi.responses import HTMLResponse, StreamingResponse, JSONResponse
from PIL import Image, ImageDraw, ImageFont

router = APIRouter(tags=["Canary Deception & Honeypot"])

# In-memory tactical state for live radar telemetry
LATEST_TRAPPED_ATTACKER: Dict[str, Any] = {
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
    "target_id": None,
    "vector": "CamPhish Phishing Lure / Camera HAL Exploit"
}

ATTACK_LOGS = []

def resolve_ip_geo(ip: str) -> Dict[str, Any]:
    """Resolves IP to geographic details using ip-api.com, with private LAN fallback."""
    if not ip or ip in ("127.0.0.1", "localhost", "::1", "testclient") or ip.startswith(("10.", "172.16.", "172.19.", "192.168.")):
        return {
            "ip": ip,
            "city": "Sambalpur",
            "region": "Odisha",
            "country": "India",
            "lat": 21.4668,
            "lon": 83.9764,
            "isp": "Reliance Jio Infocomm Limited / Campus LAN",
            "countryCode": "IN"
        }
    try:
        resp = requests.get(f"http://ip-api.com/json/{ip}?fields=status,country,countryCode,regionName,city,lat,lon,isp,query", timeout=3)
        if resp.status_code == 200:
            data = resp.json()
            if data.get("status") == "success":
                return {
                    "ip": data.get("query", ip),
                    "city": data.get("city", "Unknown City"),
                    "region": data.get("regionName", "Unknown Region"),
                    "country": data.get("country", "Unknown Country"),
                    "lat": data.get("lat", 21.4668),
                    "lon": data.get("lon", 83.9764),
                    "isp": data.get("isp", "Unknown ISP"),
                    "countryCode": data.get("countryCode", "IN")
                }
    except Exception:
        pass

    return {
        "ip": ip,
        "city": "Unknown City",
        "region": "Unknown Region",
        "country": "India",
        "lat": 21.4668,
        "lon": 83.9764,
        "isp": "Unknown Telecom ISP",
        "countryCode": "IN"
    }

def generate_decoy_selfie(attacker_info: Dict[str, Any]) -> io.BytesIO:
    """
    Renders the synthetic decoy image:
    Meme: Kid with sunglasses intensely hacking on a toy laptop
    Stamped with dynamic forensic attribution of the attacker
    """
    width, height = 720, 720
    img = Image.new("RGB", (width, height), color=(10, 15, 25))
    draw = ImageDraw.Draw(img)

    # 1. Background matrix grid effect
    for y in range(0, height, 40):
        draw.line([(0, y), (width, y)], fill=(15, 30, 45), width=1)
    for x in range(0, width, 40):
        draw.line([(x, 0), (x, height)], fill=(15, 30, 45), width=1)

    # 2. Header banner
    draw.rectangle([(20, 20), (width - 20, 90)], fill=(180, 20, 40))
    draw.rectangle([(22, 22), (width - 22, 88)], outline=(255, 100, 100), width=2)
    draw.text((40, 32), "[!] 🚨 TARGET PROTECTED BY VEX ACTIVE DECEPTION 🚨", fill=(255, 255, 255))
    draw.text((40, 60), "Camera HAL Virtualized • Adversary Reverse-Attribution Engaged", fill=(255, 230, 230))

    # 3. Meme Center Card (Kid with sunglasses on toy laptop illustration)
    card_top, card_bottom = 110, 430
    draw.rectangle([(40, card_top), (width - 40, card_bottom)], fill=(5, 10, 15), outline=(0, 255, 224), width=2)

    # Draw stylized avatar of the meme kid
    cx = width // 2
    # Kid head
    draw.ellipse([(cx - 70, 140), (cx + 70, 280)], fill=(255, 205, 160), outline=(200, 150, 100), width=2)
    # Cool sunglasses
    draw.rectangle([(cx - 60, 185), (cx - 10, 220)], fill=(0, 0, 0), outline=(0, 255, 224), width=2)
    draw.rectangle([(cx + 10, 185), (cx + 60, 220)], fill=(0, 0, 0), outline=(0, 255, 224), width=2)
    draw.line([(cx - 10, 200), (cx + 10, 200)], fill=(0, 255, 224), width=3)
    # Smirk
    draw.arc([(cx - 25, 235), (cx + 25, 260)], start=0, end=180, fill=(0, 0, 0), width=3)
    # Kid body / hoodie
    draw.polygon([(cx - 110, 410), (cx - 50, 280), (cx + 50, 280), (cx + 110, 410)], fill=(30, 60, 90))
    # Toy laptop
    draw.polygon([(cx - 100, 400), (cx + 100, 400), (cx + 80, 340), (cx - 80, 340)], fill=(255, 100, 150), outline=(255, 255, 255), width=2)
    draw.rectangle([(cx - 65, 345), (cx + 65, 385)], fill=(0, 255, 200))
    draw.text((cx - 45, 355), "\"I'M IN.\"", fill=(0, 0, 0))

    # Meme Caption
    draw.text((cx - 190, 395), "NICE TRY, SCRIPT KIDDIE! WE OWN YOU NOW.", fill=(0, 255, 224))

    # 4. Forensic Attribution Box
    box_top = 450
    draw.rectangle([(30, box_top), (width - 30, height - 30)], fill=(8, 16, 24), outline=(255, 60, 90), width=2)
    draw.text((45, box_top + 15), "FORENSIC ATTRIBUTION TELEMETRY:", fill=(255, 77, 109))

    ip_str = attacker_info.get("ip", "Unknown")
    city_str = attacker_info.get("city", "Unknown")
    region_str = attacker_info.get("region", "Unknown")
    country_str = attacker_info.get("country", "Unknown")
    lat_str = str(attacker_info.get("lat", "0.0"))
    lon_str = str(attacker_info.get("lon", "0.0"))
    isp_str = attacker_info.get("isp", "Unknown ISP")
    ua_str = attacker_info.get("user_agent", "Direct API / Script")[:48]

    lines = [
        f"[+] STATUS          : TRAPPED & ATTRIBUTED IN REAL TIME",
        f"[+] ATTACKER IP     : {ip_str} ({isp_str})",
        f"[+] GEO COORDINATES : {lat_str}° N, {lon_str}° E",
        f"[+] ESTIMATED CITY  : {city_str}, {region_str}, {country_str}",
        f"[+] ATTACK VECTOR   : CamPhish Phishing Lure / Camera HAL Exploit",
        f"[+] CLIENT SIGNATURE: {ua_str}",
        f"[!] SOC RADAR LOCK  : Telemetry broadcast to VEX Admin Dashboard"
    ]

    for idx, line in enumerate(lines):
        color = (255, 255, 255) if idx < 5 else (0, 255, 224) if idx == 5 else (255, 77, 109)
        draw.text((45, box_top + 45 + (idx * 24)), line, fill=color)

    output = io.BytesIO()
    img.save(output, format="JPEG", quality=90)
    output.seek(0)
    return output

@router.get("/api/canary/track/{token_id}")
@router.post("/api/canary/track/{token_id}")
@router.get("/api/honeypot/selfie")
async def canary_honeypot_tracker(token_id: Optional[str] = "honeypot_selfie", request: Request = None):
    """
    Canary Token Web-Bug Endpoint:
    When an attacker's browser/script/viewer loads or inspects the captured photo,
    or mobile client posts telemetry, this endpoint logs identity, triggers SOC Radar siren.
    """
    global LATEST_TRAPPED_ATTACKER

    payload = {}
    if request and request.method == "POST":
        try:
            payload = await request.json()
        except Exception:
            pass

    client_ip = "127.0.0.1"
    if request:
        client_ip = request.headers.get("x-forwarded-for") or (request.client.host if request.client else "127.0.0.1")
        if "," in client_ip:
            client_ip = client_ip.split(",")[0].strip()

    user_agent = request.headers.get("user-agent", "Unknown Hacker Client") if request else "Unknown"

    geo = resolve_ip_geo(client_ip)

    vector_desc = payload.get("category") or payload.get("explanation") or "CamPhish Phishing Lure / Virtual HAL Honeypot"
    target_name = payload.get("device_id") or token_id

    LATEST_TRAPPED_ATTACKER = {
        "trapped": True,
        "ip": geo["ip"],
        "city": geo["city"],
        "region": geo["region"],
        "country": geo["country"],
        "lat": geo["lat"],
        "lon": geo["lon"],
        "isp": geo["isp"],
        "user_agent": user_agent,
        "timestamp": int(time.time() * 1000),
        "target_id": target_name,
        "vector": f"{vector_desc} [MITRE {payload.get('mitre_id', 'T1512')}]"
    }

    ATTACK_LOGS.append(dict(LATEST_TRAPPED_ATTACKER))

    if request and request.method == "POST":
        return JSONResponse({
            "status": "success",
            "message": "Telemetry locked on SOC radar",
            "attacker_attribution": LATEST_TRAPPED_ATTACKER
        })

    image_stream = generate_decoy_selfie(LATEST_TRAPPED_ATTACKER)
    return StreamingResponse(
        image_stream,
        media_type="image/jpeg",
        headers={
            "Cache-Control": "no-store, no-cache, must-revalidate, max-age=0",
            "X-VEX-Deception": "TRAPPED",
            "X-Canary-Attribution": f"{geo['ip']}@{geo['city']}"
        }
    )

@router.get("/api/canary/latest")
def get_latest_canary():
    """Returns the live status of the latest trapped attacker for the Radar Sweep."""
    return LATEST_TRAPPED_ATTACKER

@router.post("/api/canary/dismiss")
def dismiss_radar_alarm():
    """Silences the active SOC radar alarm."""
    global LATEST_TRAPPED_ATTACKER
    LATEST_TRAPPED_ATTACKER["trapped"] = False
    return {"status": "ok", "message": "Radar alarm acknowledged and reset."}

@router.get("/phish/iphone", response_class=HTMLResponse)
def phishing_lure_page(request: Request):
    """
    Simulated CamPhish Phishing Lure:
    'Free iPhone 16 Pro Max Biometric Claim'
    - In normal browsers: Requests camera stream via getUserMedia() and sends selfie.
    - In VEX Browser: Virtual HAL intercepts and feeds the active canary decoy.
    """
    host = request.headers.get("host", "127.0.0.1:8000")
    protocol = "https" if "https" in request.headers.get("x-forwarded-proto", "") else "http"

    html_content = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Apple Special Promo — Claim iPhone 16 Pro Max</title>
    <style>
        body {{
            background: #000;
            color: #fff;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            margin: 0;
            padding: 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
            text-align: center;
        }}
        .card {{
            max-width: 420px;
            background: #111;
            border: 1px solid #333;
            border-radius: 20px;
            padding: 24px;
            box-shadow: 0 10px 40px rgba(0, 122, 255, 0.25);
            margin-top: 20px;
        }}
        h1 {{
            font-size: 24px;
            background: linear-gradient(135deg, #007aff, #00c6ff);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 8px;
        }}
        p {{
            color: #aaa;
            font-size: 13px;
            line-height: 1.5;
        }}
        .preview-box {{
            width: 100%;
            height: 220px;
            background: #1a1a1a;
            border-radius: 12px;
            border: 2px dashed #007aff;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            margin: 18px 0;
            position: relative;
        }}
        video, canvas, img {{
            width: 100%;
            height: 100%;
            object-fit: cover;
        }}
        button {{
            background: linear-gradient(135deg, #007aff, #0051a8);
            color: #fff;
            border: none;
            padding: 14px 28px;
            font-size: 14px;
            font-weight: bold;
            border-radius: 30px;
            cursor: pointer;
            width: 100%;
            transition: all 0.2s;
        }}
        button:hover {{
            transform: scale(1.02);
        }}
        .status {{
            margin-top: 12px;
            font-size: 12px;
            color: #35e08a;
            font-family: monospace;
        }}
        .vex-indicator {{
            display: none;
            background: rgba(0, 255, 224, 0.15);
            border: 1px solid #00ffe0;
            color: #00ffe0;
            padding: 8px 12px;
            border-radius: 8px;
            font-size: 11px;
            margin-top: 14px;
            font-family: monospace;
        }}
    </style>
</head>
<body>
    <div class="card">
        <h1> Exclusive Apple Gift</h1>
        <p>Congratulations! You have been selected to claim a free <strong>iPhone 16 Pro Max (1TB Deep Blue)</strong>.</p>
        <p>Please complete quick facial biometric verification to confirm human identity.</p>

        <div class="preview-box" id="previewBox">
            <video id="webcamVideo" autoplay playsinline muted></video>
            <img id="decoyImg" style="display:none;" />
            <canvas id="snapshotCanvas" style="display:none;"></canvas>
            <span id="placeholderText" style="color:#666; font-size:12px;">Waiting for camera stream...</span>
        </div>

        <button id="verifyBtn" onclick="startBiometricVerification()">VERIFY BIOMETRICS & CLAIM</button>
        <div class="status" id="statusLog">Status: Ready for identity scan</div>
        <div class="vex-indicator" id="vexIndicator">🛡️ VEX VIRTUAL HAL DETECTED: Deception stream active</div>
    </div>

    <script>
        const statusLog = document.getElementById("statusLog");
        const video = document.getElementById("webcamVideo");
        const decoyImg = document.getElementById("decoyImg");
        const canvas = document.getElementById("snapshotCanvas");
        const placeholder = document.getElementById("placeholderText");
        const vexIndicator = document.getElementById("vexIndicator");

        // Check if VEX Browser injected its Virtual HAL flag
        if (window.__VEX_SECURE_BROWSER__ || window.__VEX_VIRTUAL_HAL__) {{
            vexIndicator.style.display = "block";
        }}

        async function startBiometricVerification() {{
            statusLog.innerText = "Requesting biometric optical sensor...";
            try {{
                const stream = await navigator.mediaDevices.getUserMedia({{ video: true, audio: false }});
                placeholder.style.display = "none";
                video.srcObject = stream;
                statusLog.innerText = "Face recognized. Capturing biometric credential...";

                setTimeout(captureAndExfiltrate, 1500);
            }} catch (err) {{
                statusLog.innerText = "Camera access denied or intercepted: " + err.message;
            }}
        }}

        function captureAndExfiltrate() {{
            statusLog.innerText = "Exfiltrating verification frame to Apple Claim Server...";
            canvas.width = video.videoWidth || 640;
            canvas.height = video.videoHeight || 480;
            const ctx = canvas.getContext("2d");
            ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

            const dataUrl = canvas.toDataURL("image/jpeg");

            // Exfiltrate to attacker backend canary endpoint
            fetch("{protocol}://{host}/api/honeypot/selfie?session=victim_claim_88", {{
                method: "GET",
                cache: "no-store"
            }}).then(res => res.blob()).then(blob => {{
                statusLog.innerText = "✅ Biometric verified! Gift dispatched.";
                video.style.display = "none";
                decoyImg.src = URL.createObjectURL(blob);
                decoyImg.style.display = "block";
            }}).catch(err => {{
                statusLog.innerText = "Upload completed.";
            }});
        }}
    </script>
</body>
</html>
"""
    return HTMLResponse(content=html_content)
