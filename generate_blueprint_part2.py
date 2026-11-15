# -*- coding: utf-8 -*-
"""
VEX Master Blueprint Builder - Part 2
Writes Section 2: The 7 Core Screens UI Wireframes, Layouts & Compose Architecture.
"""
import os

HTML_FILE = r"C:\vex\VEX_Master_Hackathon_Blueprint.html"

part2_content = """
  <!-- ==================== SECTION 2: THE 7 CORE SCREENS ==================== -->
  <div class="avoid-break">
    <div class="section-title">
      <span>2. The 7 Core Screens: Sample UIs, Layouts & Jetpack Compose Architecture</span>
      <span class="section-sub">SCREEN-BY-SCREEN UI/UX SPECIFICATIONS & WIREFRAME MOCKUPS</span>
    </div>
    <p style="color:#cbd5e1; margin-bottom:16px;">
      Every page is engineered with a high-contrast Cyberpunk / Enterprise HUD aesthetic, featuring 60fps animations, haptic feedback pulses, and real-time reactive StateFlow architecture. Below are the sample wireframes and layout designs for each page mapped in the user blueprint.
    </p>

    <!-- SCREEN 1, 2, 3 GRID -->
    <div class="grid-3">

      <!-- MOCKUP 1: HOME DASHBOARD -->
      <div class="card avoid-break">
        <div class="card-title" style="color:var(--neon-cyan);">1. Home Dashboard</div>
        <div class="phone-mockup">
          <div class="phone-notch"></div>
          <div class="phone-screen">
            <div>
              <div class="phone-top-status"><span>09:41</span><span>5G ■■■ 98%</span></div>
              <div class="phone-header-banner">
                <h4>🛡️ CHECK YOUR PHONE</h4>
                <div style="font-size:7.5px; color:#cbd5e1; margin-top:2px;">Tap for quick multi-vector audit</div>
                <div style="display:inline-block; background:var(--neon-cyan); color:#000; font-size:7.5px; font-weight:800; padding:2px 8px; border-radius:4px; margin-top:4px;">RUN SCAN ▶</div>
              </div>
              <div class="phone-grid-icons">
                <div class="phone-app-btn"><span class="icon">📦</span>APK CHECK</div>
                <div class="phone-app-btn"><span class="icon">🏁</span>QR CHECK</div>
                <div class="phone-app-btn"><span class="icon">🖼️</span>AI PHOTOS</div>
                <div class="phone-app-btn"><span class="icon">🎥</span>AI VIDEO</div>
                <div class="phone-app-btn"><span class="icon">🔍</span>METADATA</div>
                <div class="phone-app-btn"><span class="icon">🔌</span>USB / OTG</div>
                <div class="phone-app-btn"><span class="icon">🌐</span>URL CHECK</div>
                <div class="phone-app-btn"><span class="icon">📡</span>RADIOZERO</div>
                <div class="phone-app-btn"><span class="icon">💣</span>BOMB SHIELD</div>
                <div class="phone-app-btn"><span class="icon">🎙️</span>AI SONG</div>
              </div>
            </div>
            <div class="phone-bottom-nav">
              <span class="active">🏠 Home</span>
              <span>🌐 VB</span>
              <span>📊 Reports</span>
              <span>📈 SOC</span>
              <span>⚙️ Settings</span>
            </div>
          </div>
        </div>
        <div style="font-size:11px; color:#94a3b8; line-height:1.4;">
          <strong>Jetpack Compose Root:</strong> <code>HomeScreen.kt</code><br>
          <strong>State:</strong> <code>HomeUiState(threatLevel, isScanning, recentAlerts)</code><br>
          <strong>Components:</strong> <code>QuickScanPulseCard</code>, <code>FeatureGrid2x5</code>, <code>ThreatLevelDial</code>
        </div>
      </div>

      <!-- MOCKUP 2: VEX BROWSER -->
      <div class="card avoid-break">
        <div class="card-title" style="color:var(--neon-green);">2. VEX Browser (VB)</div>
        <div class="phone-mockup">
          <div class="phone-notch"></div>
          <div class="phone-screen">
            <div>
              <div class="phone-top-status"><span>09:41</span><span>TOR-VPN ACTIVE 🔒</span></div>
              <div style="background:#131b2e; border:1px solid #334155; border-radius:6px; padding:4px 6px; font-size:8px; color:#38bdf8; display:flex; justify-content:space-between; margin-bottom:6px;">
                <span>🔒 https://duckduckgo.com</span>
                <span>🛡️ DNS:ADG</span>
              </div>
              <div style="background:#0f172a; border:1px solid #1e293b; border-radius:6px; padding:8px; text-align:center; margin-bottom:8px;">
                <div style="font-size:12px; font-weight:800; color:var(--neon-green);">VEX SANDBOX BROWSER</div>
                <div style="font-size:7px; color:#94a3b8;">Zero-Trace • No Login • AdGuard Private DNS</div>
              </div>
              <div style="font-size:7.5px; color:#cbd5e1; line-height:1.6; background:#111827; padding:6px; border-radius:6px; margin-bottom:6px;">
                ✔ Auto-Wipe History on Exit<br>
                ✔ Media Sniffer: Auto Video/Audio Grab<br>
                ✔ Zero-Click Malicious Script Sandbox<br>
                ✔ Canvas Fingerprint Randomizer
              </div>
              <div style="background:rgba(0,255,157,0.1); border:1px solid var(--neon-green); border-radius:6px; padding:5px; text-align:center; font-size:7.5px; color:var(--neon-green);">
                🛡️ ZERO COOKIES • PRIVATE TAB LOCKED
              </div>
            </div>
            <div class="phone-bottom-nav">
              <span>🏠 Home</span>
              <span class="active">🌐 VB</span>
              <span>📊 Reports</span>
              <span>📈 SOC</span>
              <span>⚙️ Settings</span>
            </div>
          </div>
        </div>
        <div style="font-size:11px; color:#94a3b8; line-height:1.4;">
          <strong>Jetpack Compose Root:</strong> <code>VexBrowserScreen.kt</code><br>
          <strong>Engine:</strong> Sandboxed <code>WebView</code> with custom <code>WebViewClient</code><br>
          <strong>Privacy:</strong> Memory-only cookies, AdGuard DoH, clear-on-pause.
        </div>
      </div>

      <!-- MOCKUP 3: RADIOZERO -->
      <div class="card avoid-break">
        <div class="card-title" style="color:var(--neon-red);">3. VEX RadioZero™</div>
        <div class="phone-mockup">
          <div class="phone-notch"></div>
          <div class="phone-screen">
            <div>
              <div class="phone-top-status"><span style="color:var(--neon-red);">NO SIGNAL ⚠️</span><span>JAMMER DETECTED</span></div>
              <div style="background:rgba(255,0,85,0.15); border:1px solid var(--neon-red); border-radius:8px; padding:6px; text-align:center; margin-bottom:8px;">
                <div style="font-size:9px; font-weight:900; color:var(--neon-red);">RF JAMMER INFLUENCE BUBBLE</div>
                <div style="font-size:7.5px; color:#fecdd3;">CLASS B EMITTER (~60M RADIUS)</div>
              </div>
              <!-- RADAR CIRCLE -->
              <div style="width:110px; height:110px; border-radius:50%; border:2px dashed var(--neon-red); margin:8px auto; display:flex; align-items:center; justify-content:center; position:relative; background:radial-gradient(circle, rgba(255,0,85,0.1) 0%, transparent 70%);">
                <div style="font-size:24px; color:var(--neon-cyan);">⬆ 90°</div>
                <div style="position:absolute; top:4px; font-size:7px; color:#fda4af;">ESCAPE VECTOR: EAST</div>
              </div>
              <div style="background:#1e1b4b; border:1px solid #6366f1; border-radius:6px; padding:6px; text-align:center;">
                <div style="font-size:12px; font-weight:900; color:#a5b4fc;">38 METERS REMAINING</div>
                <div style="font-size:7.5px; color:#cbd5e1;">PDR Dead-Reckoning: 29 Steps taken</div>
              </div>
            </div>
            <div class="phone-bottom-nav">
              <span>🏠 Home</span>
              <span>🌐 VB</span>
              <span>📊 Reports</span>
              <span>📈 SOC</span>
              <span>⚙️ Settings</span>
            </div>
          </div>
        </div>
        <div style="font-size:11px; color:#94a3b8; line-height:1.4;">
          <strong>Jetpack Compose Root:</strong> <code>SignalJammerScreen.kt</code><br>
          <strong>Physics Engine:</strong> Log-Distance Path Loss + PDR IMU Filter<br>
          <strong>Sensors:</strong> Accelerometer, Gyroscope, Magnetometer.
        </div>
      </div>

    </div>

    <!-- SCREEN 4, 5, 6, 7 GRID -->
    <div class="grid-2">

      <!-- MOCKUP 4: QUICK SCAN & FULL SNAP -->
      <div class="card avoid-break">
        <div class="card-title" style="color:var(--neon-cyan);">4. VEX Quick & Full Snap Scan Screen</div>
        <div style="display:flex; gap:16px; align-items:center;">
          <div class="phone-mockup" style="margin:0;">
            <div class="phone-notch"></div>
            <div class="phone-screen" style="min-height:360px;">
              <div>
                <div class="phone-top-status"><span>09:41</span><span>DIAGNOSTICS</span></div>
                <div style="text-align:center; margin:10px 0;">
                  <div style="font-size:28px; font-weight:900; color:var(--neon-cyan);">94%</div>
                  <div style="font-size:8px; color:#94a3b8;">SYSTEM INTEGRITY SCORE</div>
                </div>
                <div style="font-size:8px; color:#cbd5e1; line-height:1.7;">
                  <div style="display:flex; justify-content:space-between;"><span>DEX Bytecode Taint:</span><span style="color:var(--neon-green);">CLEAN</span></div>
                  <div style="display:flex; justify-content:space-between;"><span>Hardware Bus (USB):</span><span style="color:var(--neon-green);">SECURE</span></div>
                  <div style="display:flex; justify-content:space-between;"><span>RF Spectrum Floor:</span><span style="color:var(--neon-green);">-92 dBm (NORMAL)</span></div>
                  <div style="display:flex; justify-content:space-between;"><span>Cam/Mic Subsystem:</span><span style="color:var(--neon-green);">0 GHOST HOOKS</span></div>
                  <div style="display:flex; justify-content:space-between;"><span>Clipboard Shadow Vault:</span><span style="color:var(--neon-green);">HMAC VALID</span></div>
                  <div style="display:flex; justify-content:space-between;"><span>BLE Proximity Field:</span><span style="color:var(--neon-amber);">1 UNKNOWN TAG</span></div>
                </div>
              </div>
              <div class="phone-bottom-nav">
                <span class="active">🏠 Home</span><span>🌐 VB</span><span>📊 Reports</span><span>📈 SOC</span><span>⚙️ Settings</span>
              </div>
            </div>
          </div>
          <div style="flex:1; font-size:12px; color:#cbd5e1;">
            <h4 style="color:var(--neon-cyan); margin-bottom:6px;">Full Snap Scan Diagnostic Flow</h4>
            <p style="margin-bottom:8px;">Executes a 5-phase asynchronous audit pipeline using Kotlin Coroutines on <code>Dispatchers.IO</code>:</p>
            <ol style="padding-left:18px; line-height:1.6; font-size:11.5px;">
              <li><strong>Phase 1: Binary & DEX Parsing</strong> — Traverses installed packages, calculating Shannon entropy on APK partitions and detecting unpacked native ELF strings.</li>
              <li><strong>Phase 2: Hardware Bus Inspection</strong> — Inspects <code>/dev/input/</code> and <code>/sys/bus/usb/</code> for composite HID injection descriptors.</li>
              <li><strong>Phase 3: Kernel Telephony & RF Health</strong> — Queries cell tower handoff delta and GNSS carrier-to-noise ratio ($C/N_0$).</li>
              <li><strong>Phase 4: Permission & Accessibility Audit</strong> — Scans active accessibility services for high-speed automated transfer triggers.</li>
            </ol>
          </div>
        </div>
      </div>

      <!-- MOCKUP 5: FORENSIC REPORTS PAGE -->
      <div class="card avoid-break">
        <div class="card-title" style="color:var(--neon-amber);">5. Forensic Reports Page</div>
        <div style="display:flex; gap:16px; align-items:center;">
          <div class="phone-mockup" style="margin:0;">
            <div class="phone-notch"></div>
            <div class="phone-screen" style="min-height:360px;">
              <div>
                <div class="phone-top-status"><span>09:41</span><span>INCIDENT LEDGER</span></div>
                <div style="background:#1e293b; padding:6px; border-radius:6px; margin-bottom:8px; font-size:8px;">
                  <span style="color:var(--neon-amber); font-weight:700;">3 INCIDENTS RESOLVED (24H)</span>
                </div>
                <!-- INCIDENT CARD 1 -->
                <div style="background:#111827; border-left:3px solid var(--neon-red); padding:5px 6px; border-radius:0 4px 4px 0; margin-bottom:6px; font-size:7.5px;">
                  <div style="color:var(--neon-red); font-weight:700;">[MITM] ARP POISONING INTERCEPT</div>
                  <div style="color:#94a3b8;">192.168.1.1 Gateway MAC Churn • 08:24 AM</div>
                </div>
                <!-- INCIDENT CARD 2 -->
                <div style="background:#111827; border-left:3px solid var(--neon-amber); padding:5px 6px; border-radius:0 4px 4px 0; margin-bottom:6px; font-size:7.5px;">
                  <div style="color:var(--neon-amber); font-weight:700;">[TDoS] SMS BOMBER CLAMP</div>
                  <div style="color:#94a3b8;">142 OTPs Quarantined • 1 Real Bank SMS Saved</div>
                </div>
                <!-- INCIDENT CARD 3 -->
                <div style="background:#111827; border-left:3px solid var(--neon-cyan); padding:5px 6px; border-radius:0 4px 4px 0; font-size:7.5px;">
                  <div style="color:var(--neon-cyan); font-weight:700;">[CAM SNOOP] DECOY FRAME INJECTED</div>
                  <div style="color:#94a3b8;">Linux Backfire Triggered • Attacker Attributed</div>
                </div>
              </div>
              <div class="phone-bottom-nav">
                <span>🏠 Home</span><span>🌐 VB</span><span class="active">📊 Reports</span><span>📈 SOC</span><span>⚙️ Settings</span>
              </div>
            </div>
          </div>
          <div style="flex:1; font-size:12px; color:#cbd5e1;">
            <h4 style="color:var(--neon-amber); margin-bottom:6px;">Forensic Audit & Evidence Chain</h4>
            <p style="margin-bottom:8px;">Designed for enterprise compliance and legal cyber-attribution:</p>
            <ul style="padding-left:18px; line-height:1.6; font-size:11.5px;">
              <li><strong>Cryptographic Hash Sealing:</strong> Each incident report is hashed using SHA-256 with timestamp metadata to ensure tamper-proof evidence logs.</li>
              <li><strong>MITRE ATT&CK Mapping:</strong> Auto-classifies events against standard MITRE Mobile Tactics (e.g., T1407 Credential Access, T1412 SMS Interception).</li>
              <li><strong>One-Tap PDF Export:</strong> Exports court-admissible forensic whitepapers directly from the device memory.</li>
            </ul>
          </div>
        </div>
      </div>

    </div>

    <!-- SCREEN 6 & 7 GRID -->
    <div class="grid-2">

      <!-- MOCKUP 6: LIVE SOC DASHBOARD -->
      <div class="card avoid-break">
        <div class="card-title" style="color:var(--neon-cyan);">6. SOC Live Operations Dashboard</div>
        <div style="display:flex; gap:16px; align-items:center;">
          <div class="phone-mockup" style="margin:0;">
            <div class="phone-notch"></div>
            <div class="phone-screen" style="min-height:360px;">
              <div>
                <div class="phone-top-status"><span>09:41</span><span>WEBSOCKET LIVE ⚡</span></div>
                <div style="background:rgba(0,240,255,0.1); border:1px solid var(--neon-cyan); padding:6px; border-radius:6px; margin-bottom:8px; text-align:center;">
                  <div style="font-size:8px; color:var(--neon-cyan); font-weight:700;">VEX ENTERPRISE TELEMETRY STREAM</div>
                  <div style="font-size:7px; color:#cbd5e1;">Endpoint: 127.0.0.1:8000/ws/telemetry</div>
                </div>
                <!-- MINI OSCILLOSCOPE GRAPH -->
                <div style="background:#070a10; border:1px solid #1e293b; border-radius:6px; padding:6px; margin-bottom:8px; height:60px; display:flex; align-items:flex-end; gap:3px;">
                  <div style="flex:1; height:30%; background:var(--neon-cyan);"></div>
                  <div style="flex:1; height:45%; background:var(--neon-cyan);"></div>
                  <div style="flex:1; height:20%; background:var(--neon-cyan);"></div>
                  <div style="flex:1; height:70%; background:var(--neon-green);"></div>
                  <div style="flex:1; height:90%; background:var(--neon-amber);"></div>
                  <div style="flex:1; height:40%; background:var(--neon-cyan);"></div>
                  <div style="flex:1; height:60%; background:var(--neon-cyan);"></div>
                </div>
                <div style="font-size:7.5px; color:#cbd5e1; line-height:1.7;">
                  <div>CPU Load: <strong>14.2%</strong> | RAM Usage: <strong>88 MB</strong></div>
                  <div>Network Sockets: <strong>4 Active (Encrypted)</strong></div>
                  <div>Baseband RF: <strong>LTE Band 3 (-84 dBm)</strong></div>
                </div>
              </div>
              <div class="phone-bottom-nav">
                <span>🏠 Home</span><span>🌐 VB</span><span>📊 Reports</span><span class="active">📈 SOC</span><span>⚙️ Settings</span>
              </div>
            </div>
          </div>
          <div style="flex:1; font-size:12px; color:#cbd5e1;">
            <h4 style="color:var(--neon-cyan); margin-bottom:6px;">Real-Time Command & Control (C2) Monitor</h4>
            <p style="margin-bottom:8px;">Powered by FastAPI WebSocket duplex streaming:</p>
            <ul style="padding-left:18px; line-height:1.6; font-size:11.5px;">
              <li><strong>Zero-Overhead Baseband Polling:</strong> Broadcasts RF power, GPS dilution of precision (DOP), and accelerometer jitter every 2 seconds.</li>
              <li><strong>Live Web SOC Mirroring:</strong> Any event occurring on the mobile device updates the big-screen SOC dashboard running on the presentation laptop simultaneously.</li>
              <li><strong>Tactical Defense Mode:</strong> Operators can trigger remote quarantine commands or deploy honeypot tokens from the console.</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- MOCKUP 7: SETTINGS & VAULT -->
      <div class="card avoid-break">
        <div class="card-title" style="color:var(--neon-purple);">7. Settings & Zero-Trust Vault Page</div>
        <div style="display:flex; gap:16px; align-items:center;">
          <div class="phone-mockup" style="margin:0;">
            <div class="phone-notch"></div>
            <div class="phone-screen" style="min-height:360px;">
              <div>
                <div class="phone-top-status"><span>09:41</span><span>VAULT POLICIES</span></div>
                <div style="font-size:8.5px; font-weight:700; color:#e2e8f0; margin-bottom:6px;">ZERO-TRUST GUARDS</div>
                <div style="font-size:7.5px; color:#cbd5e1; line-height:2.0; background:#111827; padding:6px; border-radius:6px; margin-bottom:6px;">
                  <div style="display:flex; justify-content:space-between;"><span>Hardware Bus Lock</span><span style="color:var(--neon-green);">[ACTIVE]</span></div>
                  <div style="display:flex; justify-content:space-between;"><span>RF Bubble Radar</span><span style="color:var(--neon-green);">[ACTIVE]</span></div>
                  <div style="display:flex; justify-content:space-between;"><span>Cam Mirror Deception</span><span style="color:var(--neon-green);">[ACTIVE]</span></div>
                  <div style="display:flex; justify-content:space-between;"><span>BIP-39 Seed Scrubber</span><span style="color:var(--neon-green);">[15 SEC]</span></div>
                  <div style="display:flex; justify-content:space-between;"><span>DoH Canary Sentry</span><span style="color:var(--neon-green);">[ACTIVE]</span></div>
                </div>
                <div style="background:rgba(181,23,158,0.15); border:1px solid var(--neon-purple); border-radius:6px; padding:6px; font-size:7.5px; text-align:center; color:#e879f9;">
                  🛡️ HONEY-TOKEN REPOSITORY: 14 SEED PHRASES LOADED
                </div>
              </div>
              <div class="phone-bottom-nav">
                <span>🏠 Home</span><span>🌐 VB</span><span>📊 Reports</span><span>📈 SOC</span><span class="active">⚙️ Settings</span>
              </div>
            </div>
          </div>
          <div style="flex:1; font-size:12px; color:#cbd5e1;">
            <h4 style="color:var(--neon-purple); margin-bottom:6px;">Decoy & Honey-Token Policy Manager</h4>
            <p style="margin-bottom:8px;">Customizes deceptive defense capabilities:</p>
            <ul style="padding-left:18px; line-height:1.6; font-size:11.5px;">
              <li><strong>Honeypot Frame Customizer:</strong> Choose the graphic served to unauthorized webcam spies ("Protected by VEX", "Access Denied", or custom reverse-attribution image).</li>
              <li><strong>Synthetic Seed Phrase Generator:</strong> Injects fake 12-word BIP-39 wallets to trace clipper malware back to hacker blockchain addresses.</li>
              <li><strong>Emergency Panic Wipe:</strong> Programmed button combination that zeroes out app memory and revokes all accessibility tokens.</li>
            </ul>
          </div>
        </div>
      </div>

    </div>

  </div>

  <div class="page-break"></div>
"""

with open(HTML_FILE, "a", encoding="utf-8") as f:
    f.write(part2_content)

print("Part 2 written successfully.")
