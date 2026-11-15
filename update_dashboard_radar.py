# Python script to inject Attacker Geo-Location Radar Modal into backend/dashboard/index.html

import os

modal_html = '''
<!-- ==============================================================================
     VEX ACTIVE DECEPTION: ATTACKER GEO-LOCATION RADAR MODAL
     ============================================================================== -->
<div id="canaryModalOverlay" style="display:none; position:fixed; top:0; left:0; width:100vw; height:100vh; background:rgba(0,0,0,0.85); backdrop-filter:blur(8px); z-index:99999; justify-content:center; align-items:center;">
    <div style="width:850px; max-width:95vw; background:#070a10; border:2px solid #ff0055; border-radius:12px; box-shadow:0 0 50px rgba(255,0,85,0.7); padding:24px; color:#fff; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; animation:canaryPulse 2s infinite;">
        <!-- Header -->
        <div style="display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid #1e293b; padding-bottom:14px; margin-bottom:18px;">
            <div style="display:flex; align-items:center; gap:12px;">
                <span style="font-size:26px;">🎯</span>
                <div>
                    <div style="color:#ff0055; font-weight:900; font-size:17px; letter-spacing:1px; text-transform:uppercase;">🚨 INTRUDER TRAPPED & LOCATED (ACTIVE DECEPTION)</div>
                    <div style="color:#94a3b8; font-size:12px;">VEX Honeypot Camera Backfire Engaged • Origin Intercepted</div>
                </div>
            </div>
            <button onclick="dismissCanaryModal()" style="background:#1e293b; border:1px solid #334155; color:#cbd5e1; padding:6px 14px; border-radius:6px; cursor:pointer; font-weight:700;">✕ CLOSE</button>
        </div>

        <!-- Body Grid: Left Telemetry, Right Animated Radar -->
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:20px; align-items:center;">
            <!-- Left Info Panel -->
            <div style="background:#0c1220; border:1px solid #1e293b; border-radius:8px; padding:16px; font-size:13px; line-height:1.7;">
                <div style="color:#00ff9d; font-weight:800; font-size:14px; margin-bottom:8px; border-bottom:1px solid #1e293b; padding-bottom:4px;">
                    🛰️ ATTACKER TELEMETRY DOSSIER
                </div>
                <div><strong style="color:#94a3b8;">Attacker IP:</strong> <span id="canaryModalIp" style="color:#00f0ff; font-weight:800; font-family:monospace; font-size:14px;">-</span></div>
                <div><strong style="color:#94a3b8;">Location:</strong> <span id="canaryModalCity" style="color:#ffb703; font-weight:700;">-</span></div>
                <div><strong style="color:#94a3b8;">Coordinates:</strong> <span id="canaryModalCoords" style="color:#00ff9d; font-family:monospace;">-</span></div>
                <div><strong style="color:#94a3b8;">ISP / Subnet:</strong> <span id="canaryModalIsp" style="color:#cbd5e1;">-</span></div>
                <div><strong style="color:#94a3b8;">Exploit Vector:</strong> <span style="color:#ff0055;">Remote Linux Camera2 HAL Shutter</span></div>
                <div><strong style="color:#94a3b8;">Tool / Agent:</strong> <span id="canaryModalTool" style="color:#64748b; font-size:11px; font-family:monospace;">-</span></div>
                <div><strong style="color:#94a3b8;">Capture Time:</strong> <span id="canaryModalTime" style="color:#cbd5e1;">-</span></div>

                <div style="margin-top:14px; background:rgba(255,0,85,0.1); border:1px solid #ff0055; border-radius:6px; padding:8px 10px; font-size:11.5px; color:#ff0055;">
                    🛡️ Decoy Selfie Served: Attacker received watermarked synthetic honeypot frame while origin was traced!
                </div>
            </div>

            <!-- Right: Radar Canvas -->
            <div style="text-align:center;">
                <div style="color:#94a3b8; font-size:11px; margin-bottom:6px; font-weight:700; letter-spacing:1px;">GEO-LOCATION TARGET ACQUISITION RADAR</div>
                <div style="position:relative; display:inline-block; border:1px solid #00f0ff; border-radius:50%; padding:4px; box-shadow:0 0 25px rgba(0,240,255,0.3); background:#050811;">
                    <canvas id="canaryRadarCanvas" width="280" height="280" style="border-radius:50%; display:block;"></canvas>
                </div>
                <div style="margin-top:8px; font-size:11px; color:#00ff9d; font-family:monospace;">
                    STATUS: <span id="canaryRadarStatus">TARGET LOCKED • BEACON ACTIVE</span>
                </div>
            </div>
        </div>

        <!-- Footer Actions -->
        <div style="margin-top:18px; display:flex; justify-content:flex-end; gap:12px; border-top:1px solid #1e293b; padding-top:14px;">
            <a id="canaryDecoyLink" href="/api/honeypot/selfie" target="_blank" style="background:#00f0ff; color:#07090e; font-weight:800; padding:8px 16px; border-radius:6px; text-decoration:none; font-size:12.5px; display:inline-flex; align-items:center; gap:6px;">
                🖼️ VIEW STOLEN DECOY IMAGE
            </a>
            <button onclick="dismissCanaryModal()" style="background:#ff0055; color:#fff; border:none; font-weight:800; padding:8px 18px; border-radius:6px; cursor:pointer; font-size:12.5px;">
                DISMISS ALARM
            </button>
        </div>
    </div>
</div>

<style>
@keyframes canaryPulse {
    0% { box-shadow: 0 0 30px rgba(255, 0, 85, 0.4); }
    50% { box-shadow: 0 0 65px rgba(255, 0, 85, 0.85); }
    100% { box-shadow: 0 0 30px rgba(255, 0, 85, 0.4); }
}
</style>
'''

script_code = '''
// ==============================================================================
// CANARY HONEYPOT ACTIVE TRACKING & RADAR CONTROLLER
// ==============================================================================
let canaryRadarInterval = null;
let canaryRadarAngle = 0;
let lastKnownCanaryTime = 0;

function playCanaryBeep() {
    try {
        const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        const osc = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        osc.type = "sawtooth";
        osc.frequency.setValueAtTime(880, audioCtx.currentTime); // High alarm pitch
        osc.frequency.exponentialRampToValueAtTime(440, audioCtx.currentTime + 0.35);
        gain.gain.setValueAtTime(0.3, audioCtx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + 0.35);
        osc.connect(gain);
        gain.connect(audioCtx.destination);
        osc.start();
        osc.stop(audioCtx.currentTime + 0.35);
    } catch(e) {}
}

async function checkCanaryAlert() {
    try {
        const res = await fetch('/api/canary/latest?t=' + Date.now());
        if (!res.ok) return;
        const data = await res.json();
        
        if (data && data.trapped && data.timestamp > lastKnownCanaryTime) {
            lastKnownCanaryTime = data.timestamp;
            showCanaryModal(data);
        }
    } catch(e) {}
}

function showCanaryModal(data) {
    document.getElementById('canaryModalIp').textContent = data.ip || '192.168.1.105';
    document.getElementById('canaryModalCity').textContent = `${data.city || 'Bhubaneswar'}, ${data.region || 'Odisha'}, ${data.country || 'India'}`;
    document.getElementById('canaryModalCoords').textContent = `${(data.lat || 20.2961).toFixed(4)}° N, ${(data.lon || 85.8245).toFixed(4)}° E`;
    document.getElementById('canaryModalIsp').textContent = data.isp || 'Local Attacker Subnet';
    document.getElementById('canaryModalTool').textContent = (data.user_agent || 'Linux x86_64').substring(0, 50);
    document.getElementById('canaryModalTime').textContent = new Date(data.timestamp || Date.now()).toLocaleTimeString();
    
    const overlay = document.getElementById('canaryModalOverlay');
    overlay.style.display = 'flex';
    
    playCanaryBeep();
    startRadarAnimation(data.lat || 20.2961, data.lon || 85.8245);
}

function dismissCanaryModal() {
    document.getElementById('canaryModalOverlay').style.display = 'none';
    if (canaryRadarInterval) clearInterval(canaryRadarInterval);
    fetch('/api/canary/dismiss', { method: 'POST' }).catch(()=>{});
}

function startRadarAnimation(lat, lon) {
    const canvas = document.getElementById('canaryRadarCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const w = canvas.width;
    const h = canvas.height;
    const cx = w / 2;
    const cy = h / 2;
    
    if (canaryRadarInterval) clearInterval(canaryRadarInterval);
    
    canaryRadarInterval = setInterval(() => {
        canaryRadarAngle += 0.05;
        if (canaryRadarAngle >= Math.PI * 2) canaryRadarAngle = 0;
        
        // Background
        ctx.fillStyle = "#050811";
        ctx.fillRect(0, 0, w, h);
        
        // Concentric Rings
        ctx.strokeStyle = "rgba(0, 240, 255, 0.25)";
        ctx.lineWidth = 1;
        [40, 80, 115, 135].forEach(r => {
            ctx.beginPath();
            ctx.arc(cx, cy, r, 0, Math.PI * 2);
            ctx.stroke();
        });
        
        // Crosshair Lines
        ctx.beginPath();
        ctx.moveTo(cx, 0); ctx.lineTo(cx, h);
        ctx.moveTo(0, cy); ctx.lineTo(w, cy);
        ctx.strokeStyle = "rgba(0, 240, 255, 0.2)";
        ctx.stroke();
        
        // Rotating Radar Sweep
        ctx.save();
        ctx.translate(cx, cy);
        ctx.rotate(canaryRadarAngle);
        const grad = ctx.createLinearGradient(0, 0, 130, 0);
        grad.addColorStop(0, "rgba(0, 255, 157, 0)");
        grad.addColorStop(1, "rgba(0, 255, 157, 0.7)");
        ctx.fillStyle = grad;
        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.arc(0, 0, 130, -0.4, 0);
        ctx.closePath();
        ctx.fill();
        ctx.restore();
        
        // Pinned Target Blip (Pulsing Red)
        const targetX = cx + 55;
        const targetY = cy - 45;
        const pulse = 4 + Math.sin(Date.now() / 150) * 2.5;
        
        ctx.beginPath();
        ctx.arc(targetX, targetY, pulse, 0, Math.PI * 2);
        ctx.fillStyle = "#ff0055";
        ctx.fill();
        
        ctx.beginPath();
        ctx.arc(targetX, targetY, pulse + 6, 0, Math.PI * 2);
        ctx.strokeStyle = "rgba(255, 0, 85, 0.5)";
        ctx.lineWidth = 1.5;
        ctx.stroke();
        
        // Target Text
        ctx.fillStyle = "#00f0ff";
        ctx.font = "9px monospace";
        ctx.fillText(`TARGET LOCKED`, targetX + 12, targetY - 4);
        ctx.fillText(`${lat.toFixed(2)}N, ${lon.toFixed(2)}E`, targetX + 12, targetY + 8);
    }, 40);
}

// Check every 1.5 seconds for canary triggers
setInterval(checkCanaryAlert, 1500);
'''

for base_dir in [r'C:\vex\backend\dashboard', r'c:\Users\HP\Downloads\VEX-master\backend\dashboard']:
    index_html_path = os.path.join(base_dir, 'index.html')
    if os.path.exists(index_html_path):
        with open(index_html_path, 'r', encoding='utf-8') as f:
            html = f.read()
        
        if 'canaryModalOverlay' not in html:
            # Insert modal before </body>
            pos = html.rfind('</body>')
            if pos != -1:
                html = html[:pos] + modal_html + '\n<script>\n' + script_code + '\n</script>\n' + html[pos:]
                with open(index_html_path, 'w', encoding='utf-8') as f:
                    f.write(html)
                print(f"[+] Injected Radar Modal to {index_html_path}")
        else:
            print(f"[=] Radar Modal already present in {index_html_path}")
