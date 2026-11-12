from fastapi import FastAPI
from fastapi.responses import FileResponse, HTMLResponse, RedirectResponse
from fastapi.middleware.cors import CORSMiddleware
from pathlib import Path
import socket
import json
import re
from database import engine, Base, SessionLocal
import models
from api.devices import router as devices_router
from api.events import router as events_router
from api.incidents import router as incidents_router
from api.alerts import router as alerts_router
from api.reports import router as reports_router
from api.policies import router as policies_router
from api.testlab import router as testlab_router
from api.ai import router as ai_router
from api.canary import router as canary_router
from api.blocklist import router as blocklist_router
from api.auth import router as auth_router

Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="VEX Security SOC",
    version="1.0.0"
)

# Enable Universal Cross-Origin Access for all connected mobile devices & web clients
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
app.include_router(canary_router)
app.include_router(blocklist_router)
app.include_router(auth_router)

def get_host_lan_ip() -> str:
    """Detects active host LAN / Wi-Fi IPv4 address."""
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except Exception:
        return "192.168.64.235"

def get_active_tunnel_url() -> str:
    """Finds active Cloudflare tunnel URL if available."""
    # 1. Check .active_tunnel.json
    tunnel_json = Path(__file__).parent / ".active_tunnel.json"
    if tunnel_json.exists():
        try:
            data = json.loads(tunnel_json.read_text(encoding="utf-8"))
            if data.get("url"):
                return data["url"]
        except Exception:
            pass

    # 2. Check tunnel.log for trycloudflare url
    tunnel_log = Path(__file__).parent.parent / "tunnel.log"
    if tunnel_log.exists():
        try:
            txt = tunnel_log.read_text(encoding="utf-8", errors="ignore")
            matches = re.findall(r"https://[a-zA-Z0-9-]+\.trycloudflare\.com", txt)
            if matches:
                return matches[-1]
        except Exception:
            pass

    return "https://questionnaire-solution-tri-move.trycloudflare.com"

@app.get("/")
def root():
    return RedirectResponse(url="/connect")

@app.get("/api/system/network-info")
def get_network_info():
    lan_ip = get_host_lan_ip()
    tunnel_url = get_active_tunnel_url()
    
    dev_count = 0
    evt_count = 0
    try:
        db = SessionLocal()
        dev_count = db.query(models.Device).count()
        evt_count = db.query(models.ThreatEvent).count()
        db.close()
    except Exception:
        pass

    return {
        "status": "online",
        "lan_ip": lan_ip,
        "port": 8000,
        "local_connect_url": f"http://{lan_ip}:8000/connect",
        "local_dashboard_url": f"http://{lan_ip}:8000/dashboard",
        "tunnel_url": tunnel_url,
        "tunnel_connect_url": f"{tunnel_url}/connect",
        "tunnel_dashboard_url": f"{tunnel_url}/dashboard",
        "devices_count": dev_count,
        "events_count": evt_count
    }

@app.get("/health")
def health_check():
    return {
        "status": "online",
        "service": "VEX Security API"
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

@app.api_route("/app.apk", methods=["GET", "HEAD"])
@app.api_route("/download/app", methods=["GET", "HEAD"])
@app.api_route("/download/vex.apk", methods=["GET", "HEAD"])
def download_app_apk():
    release_apk = Path(__file__).parent.parent / "app" / "build" / "outputs" / "apk" / "release" / "app-release.apk"
    debug_apk = Path(__file__).parent.parent / "app" / "build" / "outputs" / "apk" / "debug" / "app-debug.apk"
    apk_path = release_apk if release_apk.exists() else debug_apk
    if apk_path.exists():
        return FileResponse(
            apk_path,
            media_type="application/vnd.android.package-archive",
            filename="vex-cyber-guard.apk",
            headers={
                "Accept-Ranges": "bytes",
                "Cache-Control": "no-store, no-cache, must-revalidate",
                "Content-Disposition": 'attachment; filename="vex-cyber-guard.apk"'
            }
        )
    return {"error": "APK not compiled yet"}

@app.get("/connect", response_class=HTMLResponse)
def get_connect_portal():
    portal_path = Path(__file__).parent / "dashboard" / "connect.html"
    if portal_path.exists():
        return HTMLResponse(
            content=portal_path.read_text(encoding="utf-8"),
            headers={"Cache-Control": "no-store, no-cache, must-revalidate"}
        )
    return HTMLResponse("<h2>Connect portal loading...</h2>")
