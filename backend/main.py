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

Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="VEX Security SOC",
    version="1.0.0"
)

app.include_router(devices_router)
app.include_router(events_router)
app.include_router(incidents_router)
app.include_router(alerts_router)
app.include_router(reports_router)
app.include_router(policies_router)
app.include_router(testlab_router)
app.include_router(ai_router)

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
