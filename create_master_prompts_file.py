# -*- coding: utf-8 -*-
"""
Generates C:\vex\VEX_COMPLETE_APP_MASTER_PROMPTS.txt
"""
import os

OUT_VEX = r"C:\vex\VEX_COMPLETE_APP_MASTER_PROMPTS.txt"
OUT_DL = r"C:\Users\HP\Downloads\VEX_COMPLETE_APP_MASTER_PROMPTS.txt"

content = """================================================================================
          VEX CYBER GUARD — COMPLETE APP IMPLEMENTATION MASTER PROMPTS
================================================================================
Target Platform: Android 14+ (API 34-37), Kotlin 2.0, Jetpack Compose, Material 3
Backend: Python 3.12, FastAPI, Uvicorn (ASGI), WebSockets
Project Root: C:\\vex (Mirrored: C:\\Users\\HP\\Downloads\\VEX-master)
Author: VEX Security Core Research Labs (BPUT Hackathon Grand Finale)
================================================================================

HOW TO USE THESE PROMPTS:
-------------------------
Feed these master prompts chronologically (Phase 0 through Phase 6) into
Antigravity or an AI coding agent. Each prompt is engineered to be 100%
self-contained, technically precise, and actionable without ambiguity.
Verify the checklist at the end of each phase before proceeding to the next.

================================================================================
PHASE 0: ENVIRONMENT PRE-FLIGHT & TOOLCHAIN VERIFICATION
================================================================================

[MASTER PROMPT 0: INITIALIZATION & ENVIRONMENT PROBING]
--------------------------------------------------------------------------------
"Act as the Lead Mobile Security Architect. Let's verify and initialize the
entire build environment for VEX Cyber Guard:

1. Check Android SDK location (C:\\Users\\HP\\AppData\\Local\\Android\\Sdk).
   Verify platform 'android-37.0' and build-tools '36.0.0'. Ensure 'local.properties'
   points to this exact sdk.dir path.
2. Check JDK 21 installation located at:
   'C:\\Program Files\\Android\\Android Studio\\jbr'. Set JAVA_HOME accordingly.
3. Check Python 3.12 at:
   'C:\\Users\\HP\\AppData\\Local\\Programs\\Python\\Python312\\python.exe'.
   Verify pip dependencies: fastapi, uvicorn, websockets, scapy, psutil, pillow.
4. Check running ADB devices via 'adb devices'. Confirm 'Medium_Phone_API_37'
   emulator is running on port 5554.
5. Establish reverse port tunnel: 'adb reverse tcp:8000 tcp:8000' so the Android
   app can talk to the local FastAPI backend at 127.0.0.1:8000.
Provide a clean status table showing all tools, paths, and versions."
--------------------------------------------------------------------------------

[PHASE 0 VERIFICATION CHECKLIST]:
[ ] local.properties exists with valid sdk.dir
[ ] JAVA_HOME points to Android Studio bundled JBR (JDK 21)
[ ] Python 3.12 verified with FastAPI & Uvicorn
[ ] adb reverse tcp:8000 tcp:8000 successfully executed


================================================================================
PHASE 1: PROJECT SCAFFOLDING & DEPENDENCIES (GRADLE 9.6.0 KTS)
================================================================================

[MASTER PROMPT 1: GRADLE ARCHITECTURE & PERMISSIONS MANIFEST]
--------------------------------------------------------------------------------
"Configure the complete project structure and build dependencies for VEX Cyber
Guard in Gradle 9.6.0 (Kotlin DSL):

1. File 'settings.gradle.kts':
   - rootProject.name = 'VEX'
   - include(':app')
   - Repositories: google(), mavenCentral()

2. File 'build.gradle.kts' (Project Root):
   - plugins {
       id('com.android.application') version '8.9.0' apply false
       id('org.jetbrains.kotlin.android') version '2.0.21' apply false
       id('org.jetbrains.kotlin.plugin.compose') version '2.0.21' apply false
     }

3. File 'app/build.gradle.kts':
   - compileSdk = 35 (or 37 preview)
   - minSdk = 26, targetSdk = 35
   - applicationId = 'com.vex.security'
   - buildFeatures { compose = true, buildConfig = true }
   - Dependencies:
     * Compose BOM: 'androidx.compose:compose-bom:2024.10.00'
     * Compose UI: ui, ui-graphics, ui-tooling-preview, material3, material-icons-extended
     * Navigation: 'androidx.navigation:navigation-compose:2.8.3'
     * Coroutines: 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0'
     * Lifecycle: lifecycle-runtime-ktx, lifecycle-viewmodel-compose:2.8.6
     * Networking: 'com.squareup.okhttp3:okhttp:4.12.0', 'com.squareup.retrofit2:retrofit:2.11.0'
     * Serialization: 'com.google.code.gson:gson:2.11.0'
     * CameraX: camera-core, camera-camera2, camera-lifecycle, camera-view:1.4.0
     * Biometrics: 'androidx.biometric:biometric:1.2.0-alpha05'
     * AndroidX Core & Activity: core-ktx:1.13.1, activity-compose:1.9.3

4. File 'app/src/main/AndroidManifest.xml':
   - Add essential permissions:
     * INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE, CHANGE_WIFI_STATE
     * ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
     * CAMERA, RECORD_AUDIO, VIBRATE, POST_NOTIFICATIONS
     * BLUETOOTH, BLUETOOTH_ADMIN, BLUETOOTH_SCAN, BLUETOOTH_CONNECT
     * NFC, READ_PHONE_STATE, SYSTEM_ALERT_WINDOW
   - Declare MainActivity with '@style/Theme.VEX' and hardware accelerated.
   - Register CallScreeningService, SmsReceiver, and UsbReceiver daemons."
--------------------------------------------------------------------------------

[PHASE 1 VERIFICATION CHECKLIST]:
[ ] Gradle sync succeeds without dependency resolution errors
[ ] Theme.VEX defined in res/values/themes.xml
[ ] AndroidManifest.xml contains all required hardware permissions


================================================================================
PHASE 2: CORE ARCHITECTURE & FASTAPI SOC TELEMETRY
================================================================================

[MASTER PROMPT 2: CORE CLIENT-SERVER TELEMETRY & HARDWARE DAEMONS]
--------------------------------------------------------------------------------
"Implement the core foundational infrastructure connecting the Android client
to the FastAPI SOC backend:

1. Kotlin Engine 'com.vex.security.network.ApiClient':
   - Prioritize candidate hosts: 'http://127.0.0.1:8000' and 'http://10.0.2.2:8000'.
   - Implement OkHttpClient with 3-second connect timeout and WebSocket listener.
   - Provide methods: pingServer(), sendTelemetry(metricJson), triggerTestlabAttack(type).

2. Kotlin Audio & Siren Daemon 'com.vex.security.notification.AlarmPlayer':
   - Implement hardware alert generator using Android AudioTrack or MediaPlayer.
   - Provide playSiren(durationMs): Generates an ear-piercing 2.4kHz square-wave
     warble at maximum STREAM_ALARM volume for BadUSB & Jammer breach alarms.
   - Provide stopSiren().

3. Kotlin State Engine 'com.vex.security.security.PolicyManager':
   - EncryptedSharedPreferences backed zero-trust state store.
   - Properties: isHardwareBusLocked, isRadioZeroActive, isCamMirrorDeceptionEnabled,
     seedPhraseScrubTimeoutSeconds (default: 15), isDoHCanaryEnabled.

4. Python FastAPI Backend at 'C:\\vex\\backend':
   - 'main.py': FastAPI app, CORS middleware for all origins, static files for dashboard.
   - 'api/policies.py': GET/POST endpoints for device security policy sync.
   - 'api/testlab.py': POST triggers for stage testing:
     * '/testlab/trigger-badusb'
     * '/testlab/trigger-cam-snoop'
     * '/testlab/trigger-arp-mitm'
     * '/testlab/trigger-sms-bomb'
     * '/testlab/trigger-jammer-bubble'
   - '/ws/telemetry': WebSocket endpoint broadcasting 2-second duplex device metrics
     (CPU, RAM, RF power, active socket count, incident counters).
   - '/dashboard': HTML5/Canvas live Web SOC operations console with neon dark UI.
Launch backend via uvicorn and verify with curl 'http://127.0.0.1:8000/api/policies'."
--------------------------------------------------------------------------------

[PHASE 2 VERIFICATION CHECKLIST]:
[ ] FastAPI server running on port 8000 without errors
[ ] Web SOC dashboard accessible at http://127.0.0.1:8000/dashboard
[ ] ApiClient in Android successfully connects and streams telemetry


================================================================================
PHASE 3: THE 7 CORE SCREENS (JETPACK COMPOSE & MVVM)
================================================================================

[MASTER PROMPT 3: SCREEN-BY-SCREEN JETPACK COMPOSE IMPLEMENTATION]
--------------------------------------------------------------------------------
"Build the 7 core screens mapped in the VEX architectural blueprint using Jetpack
Compose and Material 3 with a unified Cyberpunk / Dark Enterprise HUD theme:

Theme Palette:
- Background: #07090E, Surface: #0D121D, Border: #1A2333
- Neon Cyan: #00F0FF, Neon Green: #00FF9D, Neon Amber: #FFB703, Neon Red: #FF0055

1. Screen 1: 'HomeScreen.kt' (Tab 1)
   - Top banner 'CHECK YOUR PHONE' with animated gradient pulse.
   - 2x5 feature grid: APK Check, QR Check, AI Photos, AI Video, Metadata,
     USB/OTG, URL Check, RadioZero, BombShield, AI Song.
   - Circular animated Threat Level Dial (GREEN: 0 Threats / RED: Active Threat).
   - Bottom navigation bar: [Home] [VB] [Reports] [SOC] [Settings].

2. Screen 2: 'VexBrowserScreen.kt' (Tab 2)
   - Sandboxed WebView with custom WebViewClient disabling storage cookies.
   - Top URL bar with AdGuard DoH security badge ('🛡️ DNS:ADG').
   - Floating action buttons: 'Auto-Wipe Data' and 'Media Sniffer' (detects MP4/MP3).

3. Screen 3: 'SignalJammerScreen.kt' (VEX RadioZero™ Modal)
   - Circular radar HUD showing pulse waves and rotating scan line.
   - Active Threat Card: 'CLASS B RF JAMMER DETECTED (~60M RADIUS)'.
   - Inverted Compass Needle pointing toward 90° East (Escape Vector).
   - Dynamic Countdown: '38 METERS REMAINING TO SAFE ZONE' (steps * 0.75m).

4. Screen 4: 'DiagnosticScanScreen.kt' (Full Snap Scan)
   - Large central percentage gauge (0% to 100% animated scan).
   - 5-bar subsystem telemetry: DEX Bytecode Taint, Hardware USB Bus,
     RF Spectrum Floor, Cam/Mic Hooks, and Clipboard Shadow Vault.

5. Screen 5: 'ReportsScreen.kt' (Tab 3)
   - Reverse-chronological incident timeline.
   - Cards with color-coded severity: MitM ARP Intercept (Red), SMS Bomber Clamp (Amber),
     Cam Decoy Injected (Cyan).
   - Export forensic report button (generates signed SHA-256 evidence log).

6. Screen 6: 'SocDashboardScreen.kt' (Tab 4)
   - Real-time WebSocket oscilloscope plotting simulated network traffic and RF power.
   - Live hardware stats: CPU load, RAM usage, active sockets, baseband dBm.

7. Screen 7: 'PolicyManagerScreen.kt' (Tab 5)
   - Zero-trust toggles: Hardware Bus Lock, RadioZero Radar, Cam Mirror Deception,
     DoH Canary Sentry.
   - Seed phrase scrub slider (10s - 60s).
   - Honey-token repository selector with 14 pre-loaded dummy BIP-39 wallets."
--------------------------------------------------------------------------------

[PHASE 3 VERIFICATION CHECKLIST]:
[ ] Navigation graph connects all 5 tabs and modals cleanly
[ ] 60fps smooth Compose rendering on emulator without ANRs
[ ] Haptic feedback and alert colors trigger on simulated threats


================================================================================
PHASE 4: IMPLEMENTING FEATURES 1 TO 9 (CURRENT MODULES)
================================================================================

[MASTER PROMPT 4A: BINARY & METADATA FORENSICS (APK, IMAGE & METADATA)]
--------------------------------------------------------------------------------
"Implement the forensic analysis engines for APKs, AI Images, and Metadata:

1. 'com.vex.security.scan.ApkScannerEngine':
   - Extract ZIP entries of target APK.
   - Calculate Shannon Entropy H(X) = -sum(P(x) * log2(P(x))) on all files.
   - If H(X) >= 7.2 in non-media assets, flag as PACKED/ENCRYPTED DEX DROPPER.
   - Parse AndroidManifest.xml for dangerous permission combinations:
     RECEIVE_SMS + SEND_SMS + INTERNET + BIND_ACCESSIBILITY_SERVICE.

2. 'com.vex.security.scan.AiPhotoForensics':
   - Downscale bitmap to 256x256 grayscale.
   - Compute 2D Fast Fourier Transform (2D FFT) power spectrum.
   - Calculate high-frequency radial symmetry ratio.
   - Detect checkerboard upsampling spikes characteristic of Midjourney / DALL-E.

3. 'com.vex.security.scan.MetadataChronosEngine':
   - Parse JPEG byte stream for JUMBF C2PA provenance containers.
   - Extract Luminance DQT (Define Quantization Table) 64-element matrix.
   - Hash DQT against camera database (Apple iOS vs Photoshop Save-For-Web).
   - Compute astronomical solar elevation equation: theta = f(lat, lon, time).
   - Compare expected shadow angle against image contours to expose spoofed GPS."
--------------------------------------------------------------------------------

[MASTER PROMPT 4B: OPTICAL & NETWORK FORENSICS (QR, URL, BOMBER & USB)]
--------------------------------------------------------------------------------
"Implement the network, telecom, and hardware interface security engines:

1. 'com.vex.security.scan.OpticalQEngine' (QR & Quishing):
   - Sobel edge filter over camera preview frames detecting 0.15mm - 0.3mm
     step-height micro-shadows along the QR perimeter (sticker overlay detection).
   - Reed-Solomon parity consumption ratio: flag if parity usage > 25%.
   - Parse raw URI payloads for UPI mandates:
     Extract 'pa', 'pn', 'mode', 'am'. Flag 'upi://mandate' auto-debit scams.

2. 'com.vex.security.scan.UrlXRayEngine':
   - De-mask Punycode / IDN domains (e.g. Cyrillic 'a' \u0430 in 'paypal.com').
   - Calculate DGA Shannon character entropy on Second-Level Domain (H > 3.9).
   - Inspect X.509 TLS certificate SAN and ASN to catch Evilginx 2FA proxies.

3. 'com.vex.security.telecom.BombShieldManager' (SMS/Call Bomber):
   - Sliding-window Token Bucket rate limiter (max 3 burst, 0.2 refill/sec).
   - Temporal sender entropy: H_sender across 15-second arrival window.
   - On-device semantic sieve: silences e-commerce OTPs while triggering VIP
     emergency alarm for messages matching: 'debited', 'transferred', 'INR', 'Rs.'.

4. 'com.vex.security.usb.PortShieldManager' (BadUSB Hardware Lock):
   - Intercept Android InputManager KeyEvent dispatches.
   - Calculate Inter-Key Interval (IKI) velocity:
     If keystroke rate > 80 chars/sec with std_dev(IKI) < 3ms:
     1. Discard all incoming KeyEvents from that input device id.
     2. Call AlarmPlayer.playSiren(5000).
     3. Cut USB-C OTG power rail via Sysfs command."
--------------------------------------------------------------------------------

[MASTER PROMPT 4C: MEDIA ACOUSTICS (DEEPFAKE VIDEO & AI SONG)]
--------------------------------------------------------------------------------
"Implement the biometric and acoustic forensic detection modules:

1. 'com.vex.security.scan.BioSpectralDeepfakeEngine':
   - MediaPipe FaceMesh to track 468 facial landmarks across video frames.
   - Crop green-channel skin pixels on forehead and cheekbones across time.
   - Run Fast Fourier Transform on temporal Delta-G to extract rPPG pulse.
   - Output real-time BPM waveform: authentic (60-100 BPM) vs deepfake (0 BPM).
   - Ray-trace corneal specular light reflections across both eyes. Flag if
     vector divergence > 12 degrees.

2. 'com.vex.security.scan.SonicSentryEngine':
   - Compute Higher-Order Bispectrum B(w1, w2) on raw audio samples.
   - Detect phase decorrelation across harmonic multiples (neural vocoder flaw).
   - Respiratory mechanics analyzer: flag uninterrupted vocal phrases > 25 seconds
     with zero physiological inhalation pauses.
   - Inspect spectrum for hard 16.4 kHz Nyquist brick-wall cutoff."
--------------------------------------------------------------------------------

[PHASE 4 VERIFICATION CHECKLIST]:
[ ] ApkScannerEngine flags high-entropy test DEX
[ ] OpticalQEngine flags upi://mandate strings and displays sticker overlay warning
[ ] BombShieldManager silences simulated OTP bursts while piercing real bank SMS
[ ] PortShieldManager sounds siren and clamps keystrokes > 80 chars/sec


================================================================================
PHASE 5: IMPLEMENTING FEATURES 10 TO 17 (ADVANCED MODULES)
================================================================================

[MASTER PROMPT 5A: ADVANCED ESPIONAGE & CRYPTO DEFENSE (VOICE, CAM, CLIPBOARD)]
--------------------------------------------------------------------------------
"Implement the advanced anti-espionage and crypto-theft engines:

1. 'com.vex.security.scan.VoiceSentryEngine':
   - Calculate glottal pitch jitter J across speech frames:
     J = sum(|T_i - T_{i+1}|) / ((N-1) * T_avg) * 100%.
   - Flag AI cloned speech when jitter J < 0.1% or shows robotic step flatness.

2. 'com.vex.security.security.MirrorShieldManager' (Cam/Mic Snooper Backfire):
   - Monitor hardware camera state via CameraManager.AvailabilityCallback.
   - Sensor-fusion context check: if Camera/Mic opens while Display=OFF,
     Proximity=NEAR, and AmbientLight=0 lux, confirm ACTIVE HOSTILE SNOOPING.
   - Hook Camera2 HAL or inotify file observer:
     When an unauthorized process requests frame capture, swap the output with
     pre-rendered 'vex_honeypot_decoy.jpg' ('TARGET PROTECTED BY VEX').

3. 'com.vex.security.security.ClipSentryManager':
   - Listen to ClipboardManager.OnPrimaryClipChangedListener.
   - Cryptographically sign copied strings: HMAC-SHA256(K, text).
   - Vanity Clipper check: if clipboard mutates in < 400ms without touch,
     and new text matches first 4 and last 4 characters of original crypto address
     with Levenshtein distance > 15, REJECT MUTATION & RESTORE ORIGINAL.
   - BIP-39 mnemonic detector: bloom filter matching 12/24 words, auto-wipe in 15s."
--------------------------------------------------------------------------------

[MASTER PROMPT 5B: BANKING ATS, ROGUE WI-FI, NFC & AIRTAG DEFENSE]
--------------------------------------------------------------------------------
"Implement the banking fraud, wireless network, and physical tracking shields:

1. 'com.vex.security.security.BankVaultAtsShield':
   - AccessibilityService event monitor: track action dispatch velocity.
   - If velocity > 3.0 actions/sec inside registered banking packages,
     confirm AUTOMATED TRANSFER SYSTEM (ATS) ATTACK.
   - Freeze accessibility dispatch, tear down top-level transparent overlays
     (alpha <= 0.08), and revoke trojan privileges via DevicePolicyManager.

2. 'com.vex.security.network.AirSentryManager':
   - Differential DNS Canary: query local UDP 53 DNS and Cloudflare DoH 1.1.1.1.
     If IP_UDP53 != IP_DoH, flag active DNS poisoning.
   - Poll '/proc/net/arp': track Gateway MAC churn. If Gateway MAC changes
     without Wi-Fi reconnect (delta_MAC/delta_t > 0.2), trigger instant disconnect.

3. 'com.vex.security.nfc.NfcSentryManager':
   - High-priority foreground dispatch (NfcAdapter.enableForegroundDispatch).
   - Trap raw Tag before Android dispatches intents.
   - Verify NXP silicon 32-byte ECC signature via tag.transceive(0x3C, 0x00).
   - Sanitize 'tel:' URI records, blocking carrier USSD codes (*21*, *#06#).

4. 'com.vex.security.ble.ShadowSweepManager':
   - BluetoothLeScanner active scan filtering for Apple Find My (0x004C),
     Tile (0xFEED), SmartTag (0x0075).
   - Rolling MAC de-anonymizer: fingerprint 29-byte manufacturer payload.
   - Spatial Persistence Score: if same payload persists across >= 3 GPS waypoints
     over > 15 minutes, trigger STALKING ALERT with UWB directional compass."
--------------------------------------------------------------------------------

[PHASE 5 VERIFICATION CHECKLIST]:
[ ] MirrorShieldManager replaces stealth camera capture with honeypot frame
[ ] ClipSentryManager thwarts vanity lookalike address swaps
[ ] AirSentryManager detects ARP gateway MAC churn and disconnects Wi-Fi
[ ] BankVaultAtsShield clamps simulated high-speed accessibility taps


================================================================================
PHASE 6: LIVE STAGE SHOWSTOPPERS & FINAL APK COMPILATION
================================================================================

[MASTER PROMPT 6: SHOWSTOPPER DEMO WIREUP, TESTING LAB & RELEASE BUILD]
--------------------------------------------------------------------------------
"Wire up the 3 live stage showstoppers into the FastAPI TestLab and compile the
production-ready Android APK:

1. Wire up 'C:\\vex\\backend\\api\\testlab.py' triggers:
   - POST '/testlab/trigger-badusb': Sends ADB broadcast to simulate 150 char/sec
     keystroke burst & triggers AlarmPlayer siren.
   - POST '/testlab/trigger-cam-snoop': Simulates stealth webcam capture command,
     verifies honeypot frame is saved to attacker directory.
   - POST '/testlab/trigger-arp-mitm': Sends gratuitous ARP reply via Scapy to
     demonstrate live Wi-Fi auto-disconnect.

2. Build and assemble release APK:
   - Run: '.\\gradlew.bat assembleDebug' (or assembleRelease).
   - Output path: 'app/build/outputs/apk/debug/app-debug.apk'.
   - Verify installation via: 'adb install -r app/build/outputs/apk/debug/app-debug.apk'.
   - Launch app: 'adb shell am start -n com.vex.security/.MainActivity'.

3. Validate end-to-end integration:
   - Verify all 5 bottom navigation tabs switch smoothly.
   - Confirm WebSocket telemetry streams live to http://127.0.0.1:8000/dashboard.
   - Run simulated attack from TestLab and confirm screen turns crimson."
--------------------------------------------------------------------------------

================================================================================
                            END OF MASTER PROMPTS
================================================================================
"""

with open(OUT_VEX, "w", encoding="utf-8") as f:
    f.write(content)

with open(OUT_DL, "w", encoding="utf-8") as f:
    f.write(content)

print(f"Master prompts file written to {OUT_VEX} and {OUT_DL}")
