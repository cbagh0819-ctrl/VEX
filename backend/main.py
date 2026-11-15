from fastapi import FastAPI
from fastapi.responses import FileResponse
from pathlib import Path
from database import engine, Base
import models
from api.devices import router as devices_router
from api.events import router as events_router
from api.incidents import router as incidents_router
from api.alerts import router as alerts_router
from api.reports import router as reports_router
from api.policies import router as policies_router
from api.testlab import router as testlab_router
from api.ai import router as ai_router
from api.blocklist import router as blocklist_router
from api.auth import router as auth_router

from fastapi.middleware.cors import CORSMiddleware

Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="VEX Security SOC",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(devices_router)
app.include_router(events_router)
app.include_router(incidents_router)
app.include_router(alerts_router)
app.include_router(reports_router)
app.include_router(policies_router)
app.include_router(testlab_router)
app.include_router(ai_router)
app.include_router(blocklist_router)
app.include_router(auth_router)

@app.get("/health")
def health_check():
    return {
        "status": "online",
        "service": "VEX Security API"
    }

@app.get("/api/system/network-info")
def get_network_info():
    dev_count = 0
    evt_count = 0
    try:
        from database import SessionLocal
        db = SessionLocal()
        dev_count = db.query(models.Device).count()
        evt_count = db.query(models.ThreatEvent).count()
        db.close()
    except Exception:
        pass

    return {
        "status": "online",
        "service": "VEX Security API",
        "devices_count": dev_count,
        "events_count": evt_count
    }

@app.get("/dashboard")
def dashboard():
    dashboard_file = Path(__file__).parent / "dashboard" / "index.html"
    return FileResponse(
        dashboard_file,
        headers={
            "Cache-Control": "no-store, no-cache, must-revalidate, max-age=0",
            "Pragma": "no-cache",
            "Expires": "0"
        }
    )


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
from PIL import Image, ImageDraw, ImageFont
import os

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

def get_client_ip(request: Request) -> str:
    cf_ip = request.headers.get("cf-connecting-ip")
    if cf_ip:
        return cf_ip.strip()
    x_forwarded = request.headers.get("x-forwarded-for")
    if x_forwarded:
        return x_forwarded.split(",")[0].strip()
    return request.client.host if request.client else "127.0.0.1"

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
    # Dimensions for crisp 840x780 forensic decoy frame
    w, h = 840, 780
    img = Image.new("RGB", (w, h), color=(10, 14, 23))
    draw = ImageDraw.Draw(img)
    
    # Load fonts with graceful fallback
    font_dir = os.path.join(os.environ.get("WINDIR", "C:\\Windows"), "Fonts")
    try:
        title_font = ImageFont.truetype(os.path.join(font_dir, "arialbd.ttf"), 22)
        sub_font = ImageFont.truetype(os.path.join(font_dir, "arial.ttf"), 13)
        mono_bold = ImageFont.truetype(os.path.join(font_dir, "consolab.ttf"), 15)
        mono_font = ImageFont.truetype(os.path.join(font_dir, "consola.ttf"), 14)
        caption_font = ImageFont.truetype(os.path.join(font_dir, "arialbd.ttf"), 15)
    except Exception:
        title_font = sub_font = mono_bold = mono_font = caption_font = ImageFont.load_default()

    # Outer double neon border (Crimson + Neon Cyan)
    draw.rectangle([(8, 8), (w - 9, h - 9)], outline=(255, 0, 85), width=3)
    draw.rectangle([(14, 14), (w - 15, h - 15)], outline=(0, 240, 255), width=1)
    
    # Header banner
    draw.text((w // 2, 34), "TARGET PROTECTED BY VEX ACTIVE DECEPTION", font=title_font, fill=(255, 42, 109), anchor="mm")
    draw.text((w // 2, 60), "CAMPHISH CAMERA2 HAL HARVEST INTERCEPTED AND BACKFIRED", font=sub_font, fill=(0, 240, 255), anchor="mm")
    
    # Meme placement (Matrix kid with toy laptop)
    meme_candidates = [
        Path(__file__).parent / "dashboard" / "decoy_meme.png",
        Path(r"c:\Users\HP\Downloads\VEX-master\backend\dashboard\decoy_meme.png"),
        Path(r"C:\vex\backend\dashboard\decoy_meme.png"),
        Path(r"C:\Users\HP\Downloads\decoy_meme.png"),
        Path(r"C:\Users\HP\.gemini\antigravity\brain\0f8331c2-646d-4be7-b98e-684723ad55ea\.user_uploaded\media_1792976050779_1b457aa4.png")
    ]
    meme_loaded = False
    for mp in meme_candidates:
        if mp.exists():
            try:
                meme = Image.open(mp).convert("RGBA")
                target_size = (360, 360)
                meme_resized = meme.resize(target_size, Image.Resampling.LANCZOS)
                
                mx = (w - target_size[0]) // 2
                my = 86
                
                # Glowing cyan/neon-green border around meme
                draw.rectangle([(mx - 4, my - 4), (mx + target_size[0] + 3, my + target_size[1] + 3)], outline=(0, 255, 157), width=2)
                img.paste(meme_resized, (mx, my), meme_resized)
                
                # Caption bar at bottom of meme
                draw.rectangle([(mx, my + target_size[1] - 32), (mx + target_size[0], my + target_size[1])], fill=(15, 23, 42))
                draw.line([(mx, my + target_size[1] - 32), (mx + target_size[0], my + target_size[1] - 32)], fill=(0, 255, 157), width=1)
                draw.text((w // 2, my + target_size[1] - 16), '"I\'M IN."  -  NICE TRY, SCRIPT KIDDIE!', font=caption_font, fill=(0, 255, 157), anchor="mm")
                meme_loaded = True
                break
            except Exception:
                pass
                
    if not meme_loaded:
        cx, cy = w // 2, 260
        r = 85
        draw.ellipse([(cx-r, cy-r), (cx+r, cy+r)], outline=(0, 255, 157), width=2)
        draw.line([(cx-r-25, cy), (cx+r+25, cy)], fill=(0, 255, 157), width=1)
        draw.line([(cx, cy-r-25), (cx, cy+r+25)], fill=(0, 255, 157), width=1)
        draw.text((cx, cy), "DECOY ACTIVE", fill=(0, 255, 157), anchor="mm")

    # Forensic Telemetry Box
    box_y = 475
    draw.rectangle([(25, box_y), (w - 25, h - 25)], fill=(15, 23, 42), outline=(0, 240, 255), width=1)
    
    # Header of box
    draw.rectangle([(25, box_y), (w - 25, box_y + 32)], fill=(30, 41, 59))
    draw.text((45, box_y + 16), "[!] INTRUDER FORENSIC ATTRIBUTION REPORT (ACTIVE CANARY)", font=mono_bold, fill=(255, 183, 3), anchor="lm")
    
    telemetry_rows = [
        ("[+] ATTRIBUTION     :", "TRAPPED & REVERSE-ATTRIBUTED IN REAL TIME", (52, 211, 153)),
        ("[+] ATTACKER IP     :", f"{geo_info['ip']} ({geo_info['isp']})", (0, 255, 157)),
        ("[+] GEO COORDINATES :", f"{geo_info['lat']:.4f} N, {geo_info['lon']:.4f} E", (0, 240, 255)),
        ("[+] ESTIMATED CITY  :", f"{geo_info['city']}, {geo_info['region']}, {geo_info['country']}", (255, 255, 255)),
        ("[+] ATTACK VECTOR   :", "CamPhish Phishing Lure / Camera2 HAL Exploit", (244, 63, 94)),
        ("[+] CLIENT SIGNATURE:", f"{user_agent[:55]}", (148, 163, 184)),
        ("[!] SOC RADAR LOCK  :", "Coordinates & Payload Broadcast to VEX Admin Dashboard", (251, 191, 36)),
    ]
    
    curr_y = box_y + 46
    for label, val, color in telemetry_rows:
        draw.text((45, curr_y), label, font=mono_bold, fill=(148, 163, 184))
        draw.text((250, curr_y), str(val), font=mono_font, fill=color)
        curr_y += 28
    
    buf = io.BytesIO()
    img.save(buf, format="JPEG", quality=92)
    buf.seek(0)
    return buf

@app.get("/api/honeypot/selfie")
@app.get("/honeypot/stealth_snap.jpg")
@app.get("/api/canary/track/{token_id}")
def honeypot_camera_trap(request: Request, token_id: str = "webcam_snoop_01", db: Session = Depends(get_db)):
    client_ip = get_client_ip(request)
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


# ==============================================================================
# CAMPHISH "FREE IPHONE" DEMO & ATTACKER COLLECTOR ENDPOINTS
# ==============================================================================
@app.get("/vex-browser")
def vex_browser_page():
    browser_file = Path(__file__).parent / "dashboard" / "vex_browser.html"
    return FileResponse(browser_file)


@app.get("/phish/iphone")
def phish_iphone_page():
    phish_file = Path(__file__).parent / "dashboard" / "free_iphone_phish.html"
    return FileResponse(phish_file)

@app.post("/api/phish/upload")
async def receive_phish_upload(request: Request):
    stolen_path = Path(__file__).parent / "dashboard" / "stolen_victim_selfie.jpg"
    try:
        form = await request.form()
        uploaded = form.get("file")
        if uploaded:
            content = await uploaded.read()
            with open(stolen_path, "wb") as f:
                f.write(content)
            return {"status": "SUCCESS", "saved_bytes": len(content)}
    except Exception as e:
        return {"status": "ERROR", "error": str(e)}
    return {"status": "EMPTY"}

@app.get("/api/hacker/latest_stolen_photo")
def get_hacker_stolen_photo(request: Request, db: Session = Depends(get_db)):
    client_ip = get_client_ip(request)
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
        "target_id": "camphish_stolen_loot"
    }
    
    image_buf = generate_decoy_selfie(geo_info, user_agent)
    return StreamingResponse(image_buf, media_type="image/jpeg", headers={
        "X-VEX-Honeypot": "TRIGGERED",
        "X-Attacker-IP": client_ip,
        "X-Attacker-City": geo_info["city"]
    })


if __name__ == "__main__":
    import uvicorn
    port = int(os.environ.get("PORT", 8000))
    uvicorn.run("main:app", host="0.0.0.0", port=port, reload=False)
