# -*- coding: utf-8 -*-
"""
VEX CYBER GUARD — SCREEN-BY-SCREEN UI & COMPONENT GUIDE GENERATOR
Compiles a dedicated PDF where EVERY PAGE specifies one complete app screen.
"""
import os
import subprocess
import time

HTML_OUT = r"C:\vex\VEX_App_Pages_Sample_Guide.html"
PDF_OUT_VEX = r"C:\vex\VEX_App_Pages_Sample_Guide.pdf"
PDF_OUT_DL = r"C:\Users\HP\Downloads\VEX_App_Pages_Sample_Guide.pdf"

html_content = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>VEX Cyber Guard — Screen-by-Screen Page Specification</title>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600;700&family=Inter:wght@300;400;500;600;700;800;900&family=Orbitron:wght@600;700;800;900&display=swap');

    :root {
      --bg-dark: #07090e;
      --bg-card: #0d121d;
      --bg-card-border: #1a2333;
      --bg-surface: #121826;
      --neon-cyan: #00f0ff;
      --neon-green: #00ff9d;
      --neon-amber: #ffb703;
      --neon-red: #ff0055;
      --neon-purple: #c084fc;
      --text-primary: #f8fafc;
      --text-secondary: #94a3b8;
      --text-muted: #64748b;
    }

    * { box-sizing: border-box; margin: 0; padding: 0; }

    body {
      background-color: var(--bg-dark);
      color: var(--text-primary);
      font-family: 'Inter', -apple-system, sans-serif;
      line-height: 1.5;
      font-size: 12.5px;
      -webkit-print-color-adjust: exact !important;
      print-color-adjust: exact !important;
    }

    @page {
      size: A4 portrait;
      margin: 12mm 12mm 14mm 12mm;
    }

    .page {
      page-break-after: always;
      break-after: page;
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      padding-bottom: 8px;
    }

    .page:last-child {
      page-break-after: avoid;
      break-after: avoid;
    }

    /* Header Bar */
    .screen-header-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-bottom: 2px solid var(--neon-cyan);
      padding-bottom: 10px;
      margin-bottom: 14px;
    }

    .screen-page-tag {
      font-family: 'Orbitron', monospace;
      font-size: 11px;
      font-weight: 700;
      color: var(--neon-cyan);
      letter-spacing: 1px;
    }

    .screen-title {
      font-family: 'Orbitron', sans-serif;
      font-size: 20px;
      font-weight: 900;
      color: var(--text-primary);
      margin-top: 2px;
    }

    .screen-subtitle {
      font-size: 11.5px;
      color: var(--text-secondary);
    }

    .badge {
      display: inline-block;
      padding: 3px 8px;
      border-radius: 4px;
      font-size: 9.5px;
      font-weight: 700;
      font-family: 'Fira Code', monospace;
      text-transform: uppercase;
    }

    .badge-cyan { background: rgba(0, 240, 255, 0.15); color: var(--neon-cyan); border: 1px solid var(--neon-cyan); }
    .badge-green { background: rgba(0, 255, 157, 0.15); color: var(--neon-green); border: 1px solid var(--neon-green); }
    .badge-amber { background: rgba(255, 183, 3, 0.15); color: var(--neon-amber); border: 1px solid var(--neon-amber); }
    .badge-red { background: rgba(255, 0, 85, 0.15); color: var(--neon-red); border: 1px solid var(--neon-red); }
    .badge-purple { background: rgba(192, 132, 252, 0.15); color: var(--neon-purple); border: 1px solid var(--neon-purple); }

    /* Layout Columns */
    .screen-body-grid {
      display: grid;
      grid-template-columns: 280px 1fr;
      gap: 18px;
      flex: 1;
      margin-bottom: 10px;
    }

    /* Phone Wireframe Mockup */
    .phone-frame {
      background: #000;
      border: 2.5px solid #334155;
      border-radius: 28px;
      padding: 10px;
      box-shadow: 0 10px 25px rgba(0,0,0,0.8), 0 0 15px rgba(0,240,255,0.15);
      height: 520px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }

    .notch {
      width: 75px;
      height: 12px;
      background: #111;
      border-radius: 0 0 8px 8px;
      margin: -10px auto 6px auto;
    }

    .screen-inner {
      background: #080c14;
      border-radius: 18px;
      padding: 10px;
      border: 1px solid #1e293b;
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      overflow: hidden;
    }

    .phone-status {
      display: flex;
      justify-content: space-between;
      font-size: 8px;
      color: #64748b;
      margin-bottom: 6px;
      font-family: 'Fira Code', monospace;
    }

    .phone-nav-bar {
      display: flex;
      justify-content: space-around;
      background: #0c1220;
      border: 1px solid #1e293b;
      border-radius: 8px;
      padding: 6px 2px;
      font-size: 7.5px;
      color: #94a3b8;
      text-align: center;
      font-family: 'Fira Code', monospace;
      margin-top: 6px;
    }

    .phone-nav-bar .active {
      color: var(--neon-cyan);
      font-weight: 700;
    }

    /* Content Cards */
    .spec-card {
      background: var(--bg-card);
      border: 1px solid var(--bg-card-border);
      border-radius: 8px;
      padding: 12px 14px;
      margin-bottom: 10px;
    }

    .spec-card-title {
      font-family: 'Orbitron', sans-serif;
      font-size: 11.5px;
      font-weight: 700;
      color: var(--neon-cyan);
      margin-bottom: 6px;
      display: flex;
      align-items: center;
      gap: 6px;
      text-transform: uppercase;
    }

    .code-box {
      background: #04060a;
      border: 1px solid #1e293b;
      border-radius: 5px;
      padding: 6px 8px;
      font-family: 'Fira Code', monospace;
      font-size: 10px;
      color: #38bdf8;
      line-height: 1.4;
      margin: 4px 0;
    }

    .bullet-list {
      padding-left: 16px;
      font-size: 11.5px;
      color: #cbd5e1;
      line-height: 1.5;
    }

    .bullet-list li {
      margin-bottom: 4px;
    }

    .screen-footer {
      border-top: 1px solid #1e293b;
      padding-top: 6px;
      font-size: 10px;
      color: #64748b;
      display: flex;
      justify-content: space-between;
      font-family: 'Fira Code', monospace;
    }
  </style>
</head>
<body>

  <!-- ==================== PDF PAGE 1: COVER & SYSTEM INDEX ==================== -->
  <div class="page">
    <div style="background:linear-gradient(135deg, rgba(0,240,255,0.12), rgba(192,132,252,0.12)), #0d121d; border:1.5px solid var(--neon-cyan); border-radius:12px; padding:24px; margin-bottom:14px;">
      <div style="font-family:'Orbitron', monospace; font-size:24px; font-weight:900; color:var(--neon-cyan); text-shadow:0 0 15px rgba(0,240,255,0.4);">
        🛡️ VEX CYBER GUARD
      </div>
      <div style="font-size:14px; font-weight:700; color:#f8fafc; margin:4px 0 6px 0;">
        SCREEN-BY-SCREEN UI & COMPONENT BLUEPRINT SPECIFICATION
      </div>
      <div style="font-size:12px; color:#94a3b8; max-width:800px;">
        A page-by-page visual breakdown defining what <strong>every screen contains</strong>: visual mockups, layout hierarchies, Jetpack Compose UI trees, user interactions, connected security engines, and live stage testing protocols.
      </div>
      <div style="margin-top:12px; display:flex; gap:8px;">
        <span class="badge badge-cyan">BPUT HACKATHON GRAND FINALE</span>
        <span class="badge badge-green">7 CORE SCREENS</span>
        <span class="badge badge-purple">17 SECURITY ENGINES</span>
      </div>
    </div>

    <div style="flex:1;">
      <div style="font-family:'Orbitron', sans-serif; font-size:14px; font-weight:800; color:var(--neon-cyan); margin-bottom:10px;">
        📑 SCREEN INDEX & NAVIGATION TOPOLOGY
      </div>

      <div style="display:grid; grid-template-columns:repeat(2, 1fr); gap:10px;">
        <div class="spec-card">
          <div class="spec-card-title">📱 Page 2: Screen 1 — Home / Tactical Hub</div>
          <p style="font-size:11px; color:#cbd5e1;">Quick Scan trigger banner, 2x5 interactive feature grid, dynamic threat level dial, persistent 5-icon navigation bar.</p>
        </div>
        <div class="spec-card">
          <div class="spec-card-title">🌐 Page 3: Screen 2 — VEX Browser (VB)</div>
          <p style="font-size:11px; color:#cbd5e1;">Zero-trace sandboxed web navigator, AdGuard DoH security badge, media sniffer, instant data/history wipe on exit.</p>
        </div>
        <div class="spec-card">
          <div class="spec-card-title">📡 Page 4: Screen 3 — VEX RadioZero™</div>
          <p style="font-size:11px; color:#cbd5e1;">Offline RF Jammer Radar HUD, Path Loss radius estimation (60m), inverted compass azimuth (East), step countdown.</p>
        </div>
        <div class="spec-card">
          <div class="spec-card-title">🔍 Page 5: Screen 4 — Quick & Full Snap Scan</div>
          <p style="font-size:11px; color:#cbd5e1;">5-phase asynchronous multi-vector diagnostic audit, circular percentage ring, 5 subsystem progress bars.</p>
        </div>
        <div class="spec-card">
          <div class="spec-card-title">📊 Page 6: Screen 5 — Forensic Reports Vault</div>
          <p style="font-size:11px; color:#cbd5e1;">Tamper-proof incident ledger, color-coded severity cards (MitM, SMS Bomber, Cam Decoy), SHA-256 evidence seals.</p>
        </div>
        <div class="spec-card">
          <div class="spec-card-title">📈 Page 7: Screen 6 — SOC Live Telemetry Dashboard</div>
          <p style="font-size:11px; color:#cbd5e1;">WebSocket duplex oscilloscope stream, real-time RF power meter (dBm), network socket counters, hardware load.</p>
        </div>
        <div class="spec-card">
          <div class="spec-card-title">⚙️ Page 8: Screen 7 — Settings & Zero-Trust Vault</div>
          <p style="font-size:11px; color:#cbd5e1;">Zero-trust policy toggles, 15-second BIP-39 mnemonic auto-scrubber, honeypot decoy frame selector, panic wipe.</p>
        </div>
        <div class="spec-card">
          <div class="spec-card-title">🎭 Page 9: Testing Modals & Showstopper Protocols</div>
          <p style="font-size:11px; color:#cbd5e1;">The 3 live stage verification dialogs: BadUSB hardware siren, remote Linux cam honeypot backfire, Wi-Fi MitM sever.</p>
        </div>
      </div>
    </div>

    <div class="screen-footer">
      <span>VEX CYBER GUARD • BPUT GRAND FINALE</span>
      <span>PAGE 1 OF 9 (INDEX)</span>
    </div>
  </div>


  <!-- ==================== PDF PAGE 2: SCREEN 1 - HOME DASHBOARD ==================== -->
  <div class="page">
    <div class="screen-header-bar">
      <div>
        <div class="screen-page-tag">SCREEN 1 OF 7 • TAB 1</div>
        <div class="screen-title">Home / Tactical Defense Hub</div>
        <div class="screen-subtitle">Primary launcher, quick scan trigger, 10-feature defense grid, and device health dial.</div>
      </div>
      <div>
        <span class="badge badge-cyan">COMPOSE ROOT: HomeScreen.kt</span>
      </div>
    </div>

    <div class="screen-body-grid">
      <!-- PHONE WIREFRAME -->
      <div class="phone-frame">
        <div class="notch"></div>
        <div class="screen-inner">
          <div>
            <div class="phone-status"><span>09:41</span><span>5G ■■■ 98%</span></div>
            <!-- QUICK SCAN HERO BANNER -->
            <div style="background:linear-gradient(135deg, rgba(0,240,255,0.2), rgba(0,255,157,0.2)); border:1px solid var(--neon-cyan); border-radius:8px; padding:10px; text-align:center; margin-bottom:10px;">
              <div style="font-size:11px; font-weight:800; color:var(--neon-cyan); font-family:'Orbitron', sans-serif;">🛡️ CHECK YOUR PHONE</div>
              <div style="font-size:8px; color:#cbd5e1; margin-top:2px;">Tap for instant multi-vector audit</div>
              <div style="display:inline-block; background:var(--neon-cyan); color:#000; font-size:8px; font-weight:800; padding:3px 10px; border-radius:4px; margin-top:6px;">RUN QUICK SCAN ▶</div>
            </div>

            <!-- 10-FEATURE 2x5 GRID -->
            <div style="display:grid; grid-template-columns:repeat(2, 1fr); gap:6px; margin-bottom:8px;">
              <div class="phone-app-btn"><span class="icon">📦</span>APK CHECK</div>
              <div class="phone-app-btn"><span class="icon">🏁</span>QR CHECK</div>
              <div class="phone-app-btn"><span class="icon">🖼️</span>AI PHOTOS</div>
              <div class="phone-app-btn"><span class="icon">🎥</span>AI VIDEO</div>
              <div class="phone-app-btn"><span class="icon">🔍</span>METADATA</div>
              <div class="phone-app-btn"><span class="icon">🔌</span>USB / OTG</div>
              <div class="phone-app-btn"><span class="icon">🌐</span>URL CHECK</div>
              <div class="phone-app-btn" style="border-color:var(--neon-red); color:#fda4af;"><span class="icon">📡</span>RADIOZERO</div>
              <div class="phone-app-btn"><span class="icon">💣</span>BOMB SHIELD</div>
              <div class="phone-app-btn"><span class="icon">🎙️</span>AI SONG</div>
            </div>

            <!-- THREAT DIAL -->
            <div style="background:#111827; border:1px solid #1e293b; border-radius:6px; padding:6px; display:flex; align-items:center; justify-content:space-between;">
              <span style="font-size:8px; color:#94a3b8;">THREAT LEVEL:</span>
              <span style="font-size:8.5px; color:var(--neon-green); font-weight:700;">● ZERO THREATS DETECTED</span>
            </div>
          </div>

          <div class="phone-nav-bar">
            <span class="active">🏠 Home</span><span>🌐 VB</span><span>📊 Reports</span><span>📈 SOC</span><span>⚙️ Settings</span>
          </div>
        </div>
      </div>

      <!-- PAGE SPECIFICATION -->
      <div>
        <div class="spec-card">
          <div class="spec-card-title">📦 What This Page Contains</div>
          <ul class="bullet-list">
            <li><strong>Quick Scan Pulse Banner:</strong> Gradient pulsating card executing an immediate 10-second surface audit across active network connections and app permissions.</li>
            <li><strong>2x5 Interactive Security Grid:</strong> 10 one-tap access cards routing to dedicated modules (APK, QR, AI Photos, AI Video, Metadata, USB/OTG, URL, RadioZero, BombShield, AI Song).</li>
            <li><strong>Dynamic Threat Level Dial:</strong> Visual green/amber/red status badge that reacts instantly to background telemetry events.</li>
            <li><strong>Persistent Navigation Bar:</strong> 5-icon bottom bar providing seamless switching between Home, VB, Reports, SOC, and Settings.</li>
          </ul>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">🧩 Jetpack Compose Component Tree</div>
          <div class="code-box">
            HomeScreen(navController, viewModel)<br>
            ├── Scaffold(bottomBar = { VexBottomNavigation() })<br>
            │   └── LazyColumn()<br>
            │       ├── item { QuickScanHeroCard(onScanClick) }<br>
            │       ├── item { ThreatStatusIndicator(threatState) }<br>
            │       └── item { FeatureGrid2x5(features, onModuleClick) }
          </div>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">⚡ User Interactions & Events</div>
          <ul class="bullet-list">
            <li><strong>Tap "RUN QUICK SCAN":</strong> Triggers <code>DiagnosticScanScreen</code> modal.</li>
            <li><strong>Tap Feature Card:</strong> Navigates to corresponding screen with slide-in animation.</li>
            <li><strong>StateFlow Subscription:</strong> Listens to <code>HomeUiState.threatLevel</code>; turns UI red on attack.</li>
          </ul>
        </div>
      </div>
    </div>

    <div class="screen-footer">
      <span>VEX CYBER GUARD • BPUT GRAND FINALE</span>
      <span>PAGE 2 OF 9 (SCREEN 1: HOME)</span>
    </div>
  </div>


  <!-- ==================== PDF PAGE 3: SCREEN 2 - VEX BROWSER ==================== -->
  <div class="page">
    <div class="screen-header-bar">
      <div>
        <div class="screen-page-tag">SCREEN 2 OF 7 • TAB 2</div>
        <div class="screen-title">VEX Browser (VB) — Zero-Trace Navigator</div>
        <div class="screen-subtitle">Sandboxed private web viewer with AdGuard DNS, auto-wipe on exit, and media sniffer.</div>
      </div>
      <div>
        <span class="badge badge-green">COMPOSE ROOT: VexBrowserScreen.kt</span>
      </div>
    </div>

    <div class="screen-body-grid">
      <div class="phone-frame">
        <div class="notch"></div>
        <div class="screen-inner">
          <div>
            <div class="phone-status"><span>09:41</span><span>TOR-VPN ACTIVE 🔒</span></div>
            <!-- URL BAR -->
            <div style="background:#131b2e; border:1px solid #334155; border-radius:6px; padding:6px 8px; font-size:8.5px; color:#38bdf8; display:flex; justify-content:space-between; margin-bottom:8px;">
              <span>🔒 https://duckduckgo.com</span>
              <span style="color:var(--neon-green); font-weight:700;">🛡️ DNS:ADG</span>
            </div>

            <!-- BROWSER HEADER -->
            <div style="background:#0f172a; border:1px solid #1e293b; border-radius:6px; padding:10px; text-align:center; margin-bottom:10px;">
              <div style="font-size:13px; font-weight:800; color:var(--neon-green); font-family:'Orbitron', sans-serif;">VEX SANDBOX BROWSER</div>
              <div style="font-size:7.5px; color:#94a3b8; margin-top:2px;">Zero-Trace • No Login • AdGuard Private DNS</div>
            </div>

            <!-- PRIVACY CHECKLIST -->
            <div style="font-size:8px; color:#cbd5e1; line-height:1.8; background:#111827; padding:8px; border-radius:6px; margin-bottom:8px;">
              ✔ No Login / Sign-up / No Payment<br>
              ✔ Auto-Wipe History & Cache on Exit<br>
              ✔ Private DNS: <code>dns.adguard.com</code><br>
              ✔ Media Sniffer: Auto-detects Video/Audio<br>
              ✔ Canvas Fingerprint Randomizer
            </div>

            <!-- STATUS BADGE -->
            <div style="background:rgba(0,255,157,0.1); border:1px solid var(--neon-green); border-radius:6px; padding:6px; text-align:center; font-size:8px; color:var(--neon-green); font-weight:700;">
              🛡️ ZERO COOKIES • IN-MEMORY SESSION LOCKED
            </div>
          </div>

          <div class="phone-nav-bar">
            <span>🏠 Home</span><span class="active">🌐 VB</span><span>📊 Reports</span><span>📈 SOC</span><span>⚙️ Settings</span>
          </div>
        </div>
      </div>

      <div>
        <div class="spec-card">
          <div class="spec-card-title">📦 What This Page Contains</div>
          <ul class="bullet-list">
            <li><strong>Zero-Trace Security Header:</strong> Address bar with active TLS cipher display and AdGuard DNS verification pill.</li>
            <li><strong>Isolated Memory WebView:</strong> Embedded Android WebView configured with RAM-only cookie jars and zero disk caching.</li>
            <li><strong>Media Sniffer Engine:</strong> Intercepts streaming video (MP4/HLS) and audio links for secure direct offline capture.</li>
            <li><strong>Auto-Destruct Privacy Protocol:</strong> Clears DOM storage, WebSQL, indexedDB, and cache partitions when the app is minimized.</li>
          </ul>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">🧩 Technical Stack & Native Implementation</div>
          <div class="code-box">
            // VexBrowserEngine.kt<br>
            webView.settings.apply {<br>
            &nbsp;&nbsp;javaScriptEnabled = true<br>
            &nbsp;&nbsp;domStorageEnabled = false<br>
            &nbsp;&nbsp;databaseEnabled = false<br>
            &nbsp;&nbsp;cacheMode = WebSettings.LOAD_NO_CACHE<br>
            }<br>
            CookieManager.getInstance().removeAllCookies(null)
          </div>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">⚡ User Interactions & Events</div>
          <ul class="bullet-list">
            <li><strong>Enter URL:</strong> Validates domain via <code>UrlXRayEngine</code> before loading.</li>
            <li><strong>Tap "Wipe Data":</strong> Instantly zeroes out memory and resets to blank search.</li>
            <li><strong>Back Navigation:</strong> Prevents browser history tracking.</li>
          </ul>
        </div>
      </div>
    </div>

    <div class="screen-footer">
      <span>VEX CYBER GUARD • BPUT GRAND FINALE</span>
      <span>PAGE 3 OF 9 (SCREEN 2: VEX BROWSER)</span>
    </div>
  </div>


  <!-- ==================== PDF PAGE 4: SCREEN 3 - VEX RADIOZERO ==================== -->
  <div class="page">
    <div class="screen-header-bar">
      <div>
        <div class="screen-page-tag">SCREEN 3 OF 7 • SPECIALIZED MODAL</div>
        <div class="screen-title">VEX RadioZero™ — RF Jammer Radar HUD</div>
        <div class="screen-subtitle">Offline RF signal jammer detection, bubble radius estimation, and dead-reckoning escape guidance.</div>
      </div>
      <div>
        <span class="badge badge-red">COMPOSE ROOT: SignalJammerScreen.kt</span>
      </div>
    </div>

    <div class="screen-body-grid">
      <div class="phone-frame">
        <div class="notch"></div>
        <div class="screen-inner">
          <div>
            <div class="phone-status"><span style="color:var(--neon-red);">NO CARRIER ⚠️</span><span>JAMMER ACTIVE</span></div>
            <!-- RADAR BANNER -->
            <div style="background:rgba(255,0,85,0.15); border:1px solid var(--neon-red); border-radius:6px; padding:6px; text-align:center; margin-bottom:8px;">
              <div style="font-size:9.5px; font-weight:900; color:var(--neon-red); font-family:'Orbitron', sans-serif;">RF JAMMER INFLUENCE BUBBLE</div>
              <div style="font-size:7.5px; color:#fecdd3;">CLASS B EMITTER (~60M RADIUS)</div>
            </div>

            <!-- CIRCULAR RADAR HUD -->
            <div style="width:130px; height:130px; border-radius:50%; border:2px dashed var(--neon-red); margin:10px auto; display:flex; flex-direction:column; align-items:center; justify-content:center; position:relative; background:radial-gradient(circle, rgba(255,0,85,0.12) 0%, transparent 70%);">
              <div style="font-size:24px; color:var(--neon-cyan);">⬆ 90°</div>
              <div style="font-size:8px; color:#fda4af; font-weight:700;">ESCAPE: EAST</div>
            </div>

            <!-- COUNTDOWN CARD -->
            <div style="background:#1e1b4b; border:1px solid #6366f1; border-radius:6px; padding:8px; text-align:center; margin-bottom:6px;">
              <div style="font-size:13px; font-weight:900; color:#a5b4fc; font-family:'Orbitron', sans-serif;">38 METERS REMAINING</div>
              <div style="font-size:7.5px; color:#cbd5e1;">PDR Dead-Reckoning: 29 Steps taken</div>
            </div>

            <div style="font-size:7.5px; color:#94a3b8; text-align:center;">
              Walk straight along reverse azimuth to exit radio dead zone.
            </div>
          </div>

          <div class="phone-nav-bar">
            <span>🏠 Home</span><span>🌐 VB</span><span>📊 Reports</span><span>📈 SOC</span><span>⚙️ Settings</span>
          </div>
        </div>
      </div>

      <div>
        <div class="spec-card">
          <div class="spec-card-title">📦 What This Page Contains</div>
          <ul class="bullet-list">
            <li><strong>Jammer Classification HUD:</strong> Identifies emitter power and radius (e.g. Class B 12V vehicle jammer, 60m radius).</li>
            <li><strong>Tactical Radar Compass Needle:</strong> Animated directional arrow dynamically stabilized by hardware magnetometer and gyroscope.</li>
            <li><strong>Inverted Velocity Vector Indicator:</strong> Calculates theta_escape = -theta_approach to guide user out through the known safe path.</li>
            <li><strong>Real-Time Meter Countdown:</strong> Decrements remaining distance: <code>60m - (Steps * 0.75m)</code> in real time.</li>
          </ul>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">📐 Mathematical Formulation</div>
          <div class="code-box">
            // Log-Distance Path Loss Model<br>
            R_jam = d0 * 10^((P_tx - P_sens - Margin) / (10 * gamma))<br>
            // Pedestrian Dead Reckoning Step Filter<br>
            Remaining_Distance = R_jam - (StepCounter * 0.75m)<br>
            V_escape = - (V_approach / ||V_approach||)
          </div>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">⚡ Stage Testing Action</div>
          <ul class="bullet-list">
            <li><strong>Trigger:</strong> Simulate RF blackout by putting device in Faraday bag or clicking TestLab button.</li>
            <li><strong>Observation:</strong> HUD redlines, compass points East, distance counts down as presenter walks across stage.</li>
          </ul>
        </div>
      </div>
    </div>

    <div class="screen-footer">
      <span>VEX CYBER GUARD • BPUT GRAND FINALE</span>
      <span>PAGE 4 OF 9 (SCREEN 3: RADIOZERO)</span>
    </div>
  </div>


  <!-- ==================== PDF PAGE 5: SCREEN 4 - QUICK & FULL SNAP SCAN ==================== -->
  <div class="page">
    <div class="screen-header-bar">
      <div>
        <div class="screen-page-tag">SCREEN 4 OF 7 • MODAL</div>
        <div class="screen-title">Quick & Full Snap Scan Screen</div>
        <div class="screen-subtitle">5-phase multi-vector diagnostic audit across binaries, hardware buses, and kernel buffers.</div>
      </div>
      <div>
        <span class="badge badge-cyan">COMPOSE ROOT: DiagnosticScanScreen.kt</span>
      </div>
    </div>

    <div class="screen-body-grid">
      <div class="phone-frame">
        <div class="notch"></div>
        <div class="screen-inner">
          <div>
            <div class="phone-status"><span>09:41</span><span>DIAGNOSTICS</span></div>
            <!-- SCORE RING -->
            <div style="text-align:center; margin:14px 0 10px 0;">
              <div style="font-size:32px; font-weight:900; color:var(--neon-cyan); font-family:'Orbitron', sans-serif;">94%</div>
              <div style="font-size:8px; color:#94a3b8; letter-spacing:1px;">SYSTEM INTEGRITY SCORE</div>
            </div>

            <!-- 5 SUBSYSTEM BARS -->
            <div style="font-size:8px; color:#cbd5e1; line-height:2.0; background:#111827; padding:8px; border-radius:6px; margin-bottom:10px;">
              <div style="display:flex; justify-content:space-between;"><span>DEX Bytecode Taint:</span><span style="color:var(--neon-green);">CLEAN</span></div>
              <div style="display:flex; justify-content:space-between;"><span>Hardware Bus (USB):</span><span style="color:var(--neon-green);">SECURE</span></div>
              <div style="display:flex; justify-content:space-between;"><span>RF Spectrum Floor:</span><span style="color:var(--neon-green);">-92 dBm (NORMAL)</span></div>
              <div style="display:flex; justify-content:space-between;"><span>Cam/Mic Subsystem:</span><span style="color:var(--neon-green);">0 GHOST HOOKS</span></div>
              <div style="display:flex; justify-content:space-between;"><span>Clipboard Shadow Vault:</span><span style="color:var(--neon-green);">HMAC VALID</span></div>
              <div style="display:flex; justify-content:space-between;"><span>BLE Proximity Field:</span><span style="color:var(--neon-amber);">1 UNKNOWN TAG</span></div>
            </div>

            <div style="background:rgba(0,240,255,0.1); border:1px solid var(--neon-cyan); padding:6px; border-radius:6px; text-align:center; font-size:8px; color:var(--neon-cyan); font-weight:700;">
              AUDIT STATUS: DEEP PASS COMPLETE (14.2s)
            </div>
          </div>

          <div class="phone-nav-bar">
            <span class="active">🏠 Home</span><span>🌐 VB</span><span>📊 Reports</span><span>📈 SOC</span><span>⚙️ Settings</span>
          </div>
        </div>
      </div>

      <div>
        <div class="spec-card">
          <div class="spec-card-title">📦 What This Page Contains</div>
          <ul class="bullet-list">
            <li><strong>Central Health Ring:</strong> Animated circular progress bar representing aggregate device trust score (0-100%).</li>
            <li><strong>Phase 1: Binary & DEX Parsing:</strong> Shannon entropy checks across installed APKs to spot packed trojans.</li>
            <li><strong>Phase 2: Hardware Bus Inspection:</strong> Dissects USB descriptors in <code>/sys/bus/usb/</code> for HID keystroke injectors.</li>
            <li><strong>Phase 3: RF Spectrum Audit:</strong> Evaluates baseband RSSI delta and GNSS C/N0 carrier-to-noise ratio.</li>
            <li><strong>Phase 4: Cam/Mic Kernel Check:</strong> Probes TinyALSA and Video4Linux nodes for ghost background captures.</li>
          </ul>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">🧩 Kotlin Coroutine Engine</div>
          <div class="code-box">
            // DiagnosticScanViewModel.kt<br>
            viewModelScope.launch(Dispatchers.IO) {<br>
            &nbsp;&nbsp;val dexResult = async { scanDexEntropy() }<br>
            &nbsp;&nbsp;val usbResult = async { inspectUsbDescriptors() }<br>
            &nbsp;&nbsp;val rfResult  = async { checkRfSpectrumFloor() }<br>
            &nbsp;&nbsp;val camResult = async { probeV4L2Nodes() }<br>
            &nbsp;&nbsp;_scanState.value = aggregateResults(...)<br>
            }
          </div>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">⚡ User Action Flow</div>
          <ul class="bullet-list">
            <li><strong>Tap "Scan Now":</strong> Executes asynchronous audit without blocking main thread.</li>
            <li><strong>Tap Any Subsystem:</strong> Drills down into raw technical telemetry logs.</li>
          </ul>
        </div>
      </div>
    </div>

    <div class="screen-footer">
      <span>VEX CYBER GUARD • BPUT GRAND FINALE</span>
      <span>PAGE 5 OF 9 (SCREEN 4: QUICK SCAN)</span>
    </div>
  </div>


  <!-- ==================== PDF PAGE 6: SCREEN 5 - FORENSIC REPORTS ==================== -->
  <div class="page">
    <div class="screen-header-bar">
      <div>
        <div class="screen-page-tag">SCREEN 5 OF 7 • TAB 3</div>
        <div class="screen-title">Forensic Reports Vault</div>
        <div class="screen-subtitle">Cryptographic incident ledger, MITRE ATT&CK categorization, and evidence export.</div>
      </div>
      <div>
        <span class="badge badge-amber">COMPOSE ROOT: ReportsScreen.kt</span>
      </div>
    </div>

    <div class="screen-body-grid">
      <div class="phone-frame">
        <div class="notch"></div>
        <div class="screen-inner">
          <div>
            <div class="phone-status"><span>09:41</span><span>INCIDENT LEDGER</span></div>
            <!-- FILTER PILL -->
            <div style="background:#1e293b; padding:6px; border-radius:6px; margin-bottom:8px; font-size:8px; display:flex; justify-content:space-between;">
              <span style="color:var(--neon-amber); font-weight:700;">3 INCIDENTS RESOLVED (24H)</span>
              <span style="color:#94a3b8;">FILTER: ALL ▼</span>
            </div>

            <!-- INCIDENT CARD 1 -->
            <div style="background:#111827; border-left:3px solid var(--neon-red); padding:6px; border-radius:0 4px 4px 0; margin-bottom:6px; font-size:7.5px;">
              <div style="color:var(--neon-red); font-weight:700;">[MITM] ARP POISONING INTERCEPT</div>
              <div style="color:#94a3b8;">192.168.1.1 Gateway MAC Churn • 08:24 AM</div>
              <div style="color:#cbd5e1; margin-top:2px;">Auto-severed Wi-Fi connection in 1.2s.</div>
            </div>

            <!-- INCIDENT CARD 2 -->
            <div style="background:#111827; border-left:3px solid var(--neon-amber); padding:6px; border-radius:0 4px 4px 0; margin-bottom:6px; font-size:7.5px;">
              <div style="color:var(--neon-amber); font-weight:700;">[TDoS] SMS BOMBER CLAMP</div>
              <div style="color:#94a3b8;">142 OTPs Quarantined • 1 Bank SMS Saved</div>
              <div style="color:#cbd5e1; margin-top:2px;">Rs. 65,000 debit alert pierced through.</div>
            </div>

            <!-- INCIDENT CARD 3 -->
            <div style="background:#111827; border-left:3px solid var(--neon-cyan); padding:6px; border-radius:0 4px 4px 0; font-size:7.5px;">
              <div style="color:var(--neon-cyan); font-weight:700;">[CAM SNOOP] DECOY FRAME INJECTED</div>
              <div style="color:#94a3b8;">Linux Backfire Triggered • 07:15 AM</div>
              <div style="color:#cbd5e1; margin-top:2px;">Attacker IP 192.168.1.105 reverse-logged.</div>
            </div>
          </div>

          <div class="phone-nav-bar">
            <span>🏠 Home</span><span>🌐 VB</span><span class="active">📊 Reports</span><span>📈 SOC</span><span>⚙️ Settings</span>
          </div>
        </div>
      </div>

      <div>
        <div class="spec-card">
          <div class="spec-card-title">📦 What This Page Contains</div>
          <ul class="bullet-list">
            <li><strong>Tamper-Proof Incident Cards:</strong> Timestamped threat records color-coded by severity (Red: Critical, Amber: Suspicious, Cyan: Deception).</li>
            <li><strong>MITRE ATT&CK Mobile Classification:</strong> Auto-tags events with official tactical categories (e.g., T1407 Credential Access, T1412 SMS Interception).</li>
            <li><strong>Cryptographic SHA-256 Hash Seal:</strong> Generates tamper-evident hash tokens for court-admissible forensic reporting.</li>
            <li><strong>One-Tap Whitepaper Exporter:</strong> Compiles detailed incident logs into an exportable PDF directly on the phone.</li>
          </ul>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">🧩 Forensic Data Schema</div>
          <div class="code-box">
            data class IncidentRecord(<br>
            &nbsp;&nbsp;val id: String = UUID.randomUUID().toString(),<br>
            &nbsp;&nbsp;val timestamp: Long = System.currentTimeMillis(),<br>
            &nbsp;&nbsp;val attackType: AttackType, // MITM, BADUSB, CAM_SNOOP<br>
            &nbsp;&nbsp;val severity: Severity,     // CRITICAL, HIGH, INFO<br>
            &nbsp;&nbsp;val mitreTactic: String,    // "T1407"<br>
            &nbsp;&nbsp;val evidenceHash: String    // SHA-256(metadata)<br>
            )
          </div>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">⚡ User Action Flow</div>
          <ul class="bullet-list">
            <li><strong>Tap Any Incident:</strong> Opens deep technical evidence view.</li>
            <li><strong>Export Log:</strong> Generates signed PDF for IT administrators.</li>
          </ul>
        </div>
      </div>
    </div>

    <div class="screen-footer">
      <span>VEX CYBER GUARD • BPUT GRAND FINALE</span>
      <span>PAGE 6 OF 9 (SCREEN 5: REPORTS)</span>
    </div>
  </div>


  <!-- ==================== PDF PAGE 7: SCREEN 6 - SOC LIVE DASHBOARD ==================== -->
  <div class="page">
    <div class="screen-header-bar">
      <div>
        <div class="screen-page-tag">SCREEN 6 OF 7 • TAB 4</div>
        <div class="screen-title">SOC Live Telemetry Dashboard</div>
        <div class="screen-subtitle">Real-time WebSocket oscilloscope, baseband power meter, and C2 fleet command stream.</div>
      </div>
      <div>
        <span class="badge badge-cyan">COMPOSE ROOT: SocDashboardScreen.kt</span>
      </div>
    </div>

    <div class="screen-body-grid">
      <div class="phone-frame">
        <div class="notch"></div>
        <div class="screen-inner">
          <div>
            <div class="phone-status"><span>09:41</span><span>WEBSOCKET LIVE ⚡</span></div>
            <!-- STATUS CARD -->
            <div style="background:rgba(0,240,255,0.1); border:1px solid var(--neon-cyan); padding:6px; border-radius:6px; margin-bottom:8px; text-align:center;">
              <div style="font-size:8px; color:var(--neon-cyan); font-weight:700;">VEX ENTERPRISE TELEMETRY STREAM</div>
              <div style="font-size:7px; color:#cbd5e1;">Endpoint: 127.0.0.1:8000/ws/telemetry</div>
            </div>

            <!-- MINI OSCILLOSCOPE GRAPH -->
            <div style="background:#070a10; border:1px solid #1e293b; border-radius:6px; padding:6px; margin-bottom:8px; height:70px; display:flex; align-items:flex-end; gap:3px;">
              <div style="flex:1; height:30%; background:var(--neon-cyan);"></div>
              <div style="flex:1; height:45%; background:var(--neon-cyan);"></div>
              <div style="flex:1; height:20%; background:var(--neon-cyan);"></div>
              <div style="flex:1; height:70%; background:var(--neon-green);"></div>
              <div style="flex:1; height:90%; background:var(--neon-amber);"></div>
              <div style="flex:1; height:40%; background:var(--neon-cyan);"></div>
              <div style="flex:1; height:60%; background:var(--neon-cyan);"></div>
              <div style="flex:1; height:80%; background:var(--neon-green);"></div>
            </div>

            <!-- METRIC TILES -->
            <div style="font-size:7.5px; color:#cbd5e1; line-height:1.8; background:#111827; padding:6px; border-radius:6px;">
              <div>CPU Load: <strong>14.2%</strong> | RAM: <strong>88 MB</strong></div>
              <div>Active Sockets: <strong>4 TLS Encrypted</strong></div>
              <div>Baseband RF: <strong>LTE Band 3 (-84 dBm)</strong></div>
              <div>Threat Matrix: <strong>98% SECURE</strong></div>
            </div>
          </div>

          <div class="phone-nav-bar">
            <span>🏠 Home</span><span>🌐 VB</span><span>📊 Reports</span><span class="active">📈 SOC</span><span>⚙️ Settings</span>
          </div>
        </div>
      </div>

      <div>
        <div class="spec-card">
          <div class="spec-card-title">📦 What This Page Contains</div>
          <ul class="bullet-list">
            <li><strong>WebSocket Telemetry Stream:</strong> Continuous bi-directional socket connected to FastAPI server on port 8000.</li>
            <li><strong>Live Oscilloscope Waveform:</strong> Real-time hardware bus activity graph plotting packet velocity and sensor jitter.</li>
            <li><strong>Baseband RF Power Meter:</strong> Displays cellular RSSI, Wi-Fi channel width, and GNSS satellite signal-to-noise ratio.</li>
            <li><strong>Hardware Resource Monitor:</strong> CPU temperature, RAM consumption, and battery discharge current.</li>
          </ul>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">🧩 Duplex WebSocket Architecture</div>
          <div class="code-box">
            // FastAPI Telemetry WebSocket (main.py)<br>
            @app.websocket("/ws/telemetry")<br>
            async def telemetry_endpoint(websocket: WebSocket):<br>
            &nbsp;&nbsp;await websocket.accept()<br>
            &nbsp;&nbsp;while True:<br>
            &nbsp;&nbsp;&nbsp;&nbsp;data = await get_device_telemetry()<br>
            &nbsp;&nbsp;&nbsp;&nbsp;await websocket.send_json(data)<br>
            &nbsp;&nbsp;&nbsp;&nbsp;await asyncio.sleep(2.0)
          </div>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">⚡ Stage Presentation Mirroring</div>
          <ul class="bullet-list">
            <li>Phone stream mirrors simultaneously to the big-screen projector Web SOC dashboard at <code>http://127.0.0.1:8000/dashboard</code>.</li>
            <li>When an attack happens on the phone, the projector screen flashes in real time!</li>
          </ul>
        </div>
      </div>
    </div>

    <div class="screen-footer">
      <span>VEX CYBER GUARD • BPUT GRAND FINALE</span>
      <span>PAGE 7 OF 9 (SCREEN 6: SOC DASHBOARD)</span>
    </div>
  </div>


  <!-- ==================== PDF PAGE 8: SCREEN 7 - SETTINGS & VAULT ==================== -->
  <div class="page">
    <div class="screen-header-bar">
      <div>
        <div class="screen-page-tag">SCREEN 7 OF 7 • TAB 5</div>
        <div class="screen-title">Settings & Zero-Trust Vault Page</div>
        <div class="screen-subtitle">Hardware policy switches, BIP-39 mnemonic scrubber, honeypot decoy manager, and emergency panic wipe.</div>
      </div>
      <div>
        <span class="badge badge-purple">COMPOSE ROOT: PolicyManagerScreen.kt</span>
      </div>
    </div>

    <div class="screen-body-grid">
      <div class="phone-frame">
        <div class="notch"></div>
        <div class="screen-inner">
          <div>
            <div class="phone-status"><span>09:41</span><span>VAULT POLICIES</span></div>
            <!-- POLICY SWITCHES -->
            <div style="font-size:8.5px; font-weight:700; color:#e2e8f0; margin-bottom:6px;">ZERO-TRUST GUARDS</div>
            <div style="font-size:7.5px; color:#cbd5e1; line-height:2.0; background:#111827; padding:6px; border-radius:6px; margin-bottom:8px;">
              <div style="display:flex; justify-content:space-between;"><span>Hardware Bus Lock</span><span style="color:var(--neon-green);">[ACTIVE]</span></div>
              <div style="display:flex; justify-content:space-between;"><span>RF Bubble Radar</span><span style="color:var(--neon-green);">[ACTIVE]</span></div>
              <div style="display:flex; justify-content:space-between;"><span>Cam Mirror Deception</span><span style="color:var(--neon-green);">[ACTIVE]</span></div>
              <div style="display:flex; justify-content:space-between;"><span>BIP-39 Seed Scrubber</span><span style="color:var(--neon-green);">[15 SEC]</span></div>
              <div style="display:flex; justify-content:space-between;"><span>DoH Canary Sentry</span><span style="color:var(--neon-green);">[ACTIVE]</span></div>
            </div>

            <!-- HONEY-TOKEN REPOSITORY -->
            <div style="background:rgba(181,23,158,0.15); border:1px solid var(--neon-purple); border-radius:6px; padding:6px; font-size:7.5px; text-align:center; color:#e879f9; margin-bottom:8px;">
              🛡️ HONEY-TOKEN VAULT: 14 SEED PHRASES LOADED
            </div>

            <!-- PANIC BUTTON -->
            <div style="background:rgba(255,0,85,0.15); border:1px solid var(--neon-red); border-radius:6px; padding:6px; font-size:7.5px; text-align:center; color:var(--neon-red); font-weight:700;">
              ⚠️ EMERGENCY PANIC WIPE (ZERO-FILL MEMORY)
            </div>
          </div>

          <div class="phone-nav-bar">
            <span>🏠 Home</span><span>🌐 VB</span><span>📊 Reports</span><span>📈 SOC</span><span class="active">⚙️ Settings</span>
          </div>
        </div>
      </div>

      <div>
        <div class="spec-card">
          <div class="spec-card-title">📦 What This Page Contains</div>
          <ul class="bullet-list">
            <li><strong>Hardware Policy Switches:</strong> Instant toggle controls for USB Bus Lock, RadioZero Radar, Camera Mirroring, and DoH Canary.</li>
            <li><strong>BIP-39 Memory Scrubber Slider:</strong> Adjusts auto-purge countdown (10s–60s) for copied crypto mnemonic seed phrases.</li>
            <li><strong>Honeypot Decoy Frame Selector:</strong> Allows operator to select the decoy image served to remote hackers ("Protected by VEX").</li>
            <li><strong>Emergency Panic Wipe Trigger:</strong> Instantly zeroes out in-memory cryptographic keys and resets security state.</li>
          </ul>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">🧩 Security State Persistence</div>
          <div class="code-box">
            // PolicyManager.kt<br>
            val prefs = EncryptedSharedPreferences.create(<br>
            &nbsp;&nbsp;"vex_vault_prefs",<br>
            &nbsp;&nbsp;MasterKey.DEFAULT_MASTER_KEY_ALIAS,<br>
            &nbsp;&nbsp;context,<br>
            &nbsp;&nbsp;EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,<br>
            &nbsp;&nbsp;EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM<br>
            )
          </div>
        </div>

        <div class="spec-card">
          <div class="spec-card-title">⚡ User Action Flow</div>
          <ul class="bullet-list">
            <li><strong>Toggle Switch:</strong> Persists state to encrypted hardware Keystore instantly.</li>
            <li><strong>Tap Panic Wipe:</strong> Overwrites clipboard, clears browser cache, and disconnects sockets.</li>
          </ul>
        </div>
      </div>
    </div>

    <div class="screen-footer">
      <span>VEX CYBER GUARD • BPUT GRAND FINALE</span>
      <span>PAGE 8 OF 9 (SCREEN 7: SETTINGS)</span>
    </div>
  </div>


  <!-- ==================== PDF PAGE 9: TESTING MODALS & SHOWSTOPPERS ==================== -->
  <div class="page">
    <div class="screen-header-bar">
      <div>
        <div class="screen-page-tag">TESTING PROTOCOLS • STAGE DEMO MODALS</div>
        <div class="screen-title">Stage Testing Modals & Showstopper Overlays</div>
        <div class="screen-subtitle">Exact visual dialogues and hardware triggers for the 3 live hackathon stage demonstrations.</div>
      </div>
      <div>
        <span class="badge badge-red">LIVE STAGE GOLD</span>
      </div>
    </div>

    <div style="display:grid; grid-template-columns:repeat(3, 1fr); gap:12px; flex:1; margin-bottom:10px;">

      <!-- MODAL 1: BADUSB -->
      <div class="spec-card" style="border-color:var(--neon-purple); display:flex; flex-direction:column; justify-content:space-between;">
        <div>
          <div class="spec-card-title" style="color:var(--neon-purple);">🔌 Showstopper 1: BadUSB Siren Modal</div>
          <div style="background:#111827; border:1px solid #1e293b; border-radius:6px; padding:10px; text-align:center; margin:10px 0;">
            <div style="font-size:28px;">🚨</div>
            <div style="font-size:11px; font-weight:800; color:var(--neon-purple); margin-top:4px;">HARDWARE BUS INJECTION CLAMPED</div>
            <div style="font-size:8px; color:#cbd5e1; margin-top:4px;">Inhuman Velocity: 150 chars/sec<br>OTG Power Cut: 15ms<br>Hardware Siren: ACTIVE</div>
          </div>
          <p style="font-size:11px; color:#cbd5e1;">
            <strong>Trigger:</strong> Plug BadUSB via OTG cable.<br>
            <strong>Hardware Response:</strong> Cuts power to USB port, triggers continuous 2.4kHz square-wave siren, pops up non-dismissible alert.
          </p>
        </div>
        <div class="code-box">AlarmPlayer.playSiren(5000)<br>disableUsbPortSysfs()</div>
      </div>

      <!-- MODAL 2: CAM SNOOP -->
      <div class="spec-card" style="border-color:var(--neon-red); display:flex; flex-direction:column; justify-content:space-between;">
        <div>
          <div class="spec-card-title" style="color:var(--neon-red);">📸 Showstopper 2: Cam Honeypot Toast</div>
          <div style="background:#111827; border:1px solid #1e293b; border-radius:6px; padding:10px; text-align:center; margin:10px 0;">
            <div style="font-size:28px;">🎭</div>
            <div style="font-size:11px; font-weight:800; color:var(--neon-red); margin-top:4px;">REMOTE SNOOP HONEYPOTTED</div>
            <div style="font-size:8px; color:#cbd5e1; margin-top:4px;">Context: Screen Locked (0 Lux)<br>Decoy Frame Dispatched<br>Attacker IP: 192.168.1.105</div>
          </div>
          <p style="font-size:11px; color:#cbd5e1;">
            <strong>Trigger:</strong> Kali Linux runs <code>webcam_snap</code>.<br>
            <strong>Honeypot Response:</strong> Disconnects CMOS sensor, writes synthetic "PROTECTED BY VEX" decoy frame into capture buffer.
          </p>
        </div>
        <div class="code-box">injectDecoyFrame(vex_honeypot.jpg)<br>logAttackerTelemetry()</div>
      </div>

      <!-- MODAL 3: WI-FI MITM -->
      <div class="spec-card" style="border-color:var(--neon-cyan); display:flex; flex-direction:column; justify-content:space-between;">
        <div>
          <div class="spec-card-title" style="color:var(--neon-cyan);">🌐 Showstopper 3: MitM Sever Banner</div>
          <div style="background:#111827; border:1px solid #1e293b; border-radius:6px; padding:10px; text-align:center; margin:10px 0;">
            <div style="font-size:28px;">⚡</div>
            <div style="font-size:11px; font-weight:800; color:var(--neon-cyan); margin-top:4px;">ROGUE WI-FI AUTO-DISCONNECTED</div>
            <div style="font-size:8px; color:#cbd5e1; margin-top:4px;">Gateway MAC Churn Detected<br>DoH Canary Mismatch (1.1.1.1)<br>Action: Wi-Fi Socket Severed</div>
          </div>
          <p style="font-size:11px; color:#cbd5e1;">
            <strong>Trigger:</strong> Linux broadcasts fake ARP replies.<br>
            <strong>Zero-Trust Response:</strong> Drops Wi-Fi association in 1.2s and shifts device to encrypted cellular fallback tunnel.
          </p>
        </div>
        <div class="code-box">wifiManager.disconnect()<br>enableCellularFallback()</div>
      </div>

    </div>

    <div class="screen-footer">
      <span>VEX CYBER GUARD • BPUT GRAND FINALE</span>
      <span>PAGE 9 OF 9 (STAGE TESTING PROTOCOLS)</span>
    </div>
  </div>

</body>
</html>
"""

with open(HTML_OUT, "w", encoding="utf-8") as f:
    f.write(html_content)

print(f"HTML written to {HTML_OUT} ({len(html_content)} bytes)")

edge_path = r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
if os.path.exists(edge_path):
    print("Compiling Dedicated Pages Sample PDF...")
    cmd = [
        edge_path,
        "--headless=new",
        "--disable-gpu",
        "--run-all-compositor-stages-before-draw",
        f"--print-to-pdf={PDF_OUT_VEX}",
        "--no-pdf-header-footer",
        f"file:///{HTML_OUT.replace(chr(92), '/')}"
    ]
    res = subprocess.run(cmd, capture_output=True, text=True)
    time.sleep(2)
    if os.path.exists(PDF_OUT_VEX):
        pdf_size = os.path.getsize(PDF_OUT_VEX)
        print(f"PDF generated: {pdf_size} bytes")
        import shutil
        shutil.copy2(PDF_OUT_VEX, PDF_OUT_DL)
        print(f"PDF copied to {PDF_OUT_DL}")
    else:
        print("PDF was not created.")
else:
    print("Edge path missing.")
