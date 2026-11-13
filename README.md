# 🛡️ VEX CyberGuard — Autonomous Endpoint Security & Deepfake Defense System

[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20FastAPI%20%7C%20Web-blue.svg)](https://github.com/cbagh0819-ctrl/VEX)
[![AI Architecture](https://img.shields.io/badge/AI%20Engines-MesoNet%20%7C%20Gemma%202%202B-emerald.svg)](https://github.com/cbagh0819-ctrl/VEX)
[![Security Standard](https://img.shields.io/badge/Framework-MITRE%20ATT%26CK%20v14-red.svg)](https://attack.mitre.org/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

**VEX CyberGuard** is an enterprise-grade mobile security and synthetic media defense ecosystem. It provides real-time video call deepfake interception, on-device behavioral telemetry, explainable AI forensic diagnostics, blast-radius containment, and an interactive Security Operations Center (SOC) dashboard.

---

## 🏛️ The 4 Core Architectural Pillars

```
                     ┌──────────────────────────────────────────────┐
                     │           VEX ANDROID MOBILE AGENT           │
                     │  • MediaProjection Frame Capture             │
                     │  • Intent Sharing (.mp4 / .jpg)              │
                     │  • Foreground Interceptor & Overlay          │
                     │  • Drebin Random Forest APK Scanner          │
                     └──────────────────────┬───────────────────────┘
                                            │ Chunked Streaming (OkHttp)
                                            ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                          FASTAPI REAL-TIME AI REASONING MESH                           │
│                                                                                        │
│   ┌───────────────────────────────────┐    ┌───────────────────────────────────────┐   │
│   │        MesoNet Neural Core        │    │          Gemma 2 2B Engine            │   │
│   │  • Meso4 Compact ConvNet          │    │  • Camera HAL Predatory Reasoning     │   │
│   │  • Mesoscopic Blending Analysis   │    │  • Behavioral Origin Interception     │   │
│   │  • High-Pass Laplacian Variance   │    │  • MITRE ATT&CK Mobile Technique Map  │   │
│   └─────────────────┬─────────────────┘    └───────────────────┬───────────────────┘   │
└─────────────────────┼──────────────────────────────────────────┼───────────────────────┘
                      │                                          │
                      ▼                                          ▼
┌───────────────────────────────────────────┐    ┌───────────────────────────────────────┐
│        AUTOMATED RESPONSE ENGINE          │    │          ADMIN SOC DASHBOARD          │
│  • SYSTEM_ALERT_WINDOW Live Overlay       │    │  • Real-Time Threat Telemetry Radar   │
│  • Perceptual Media Hash Sync (pHash)     │    │  • Blast Radius Containment Logs      │
│  • Sub-millisecond Global Blocklist       │    │  • Explainable AI (XAI) Diagnostics   │
└───────────────────────────────────────────┘    └───────────────────────────────────────┘
```

### 1. Android Endpoint & Live Video Interception
- **Real-Time Video Call Interception**: Background service leveraging Android `MediaProjection` and `VirtualDisplay` to inspect live video call feeds (WhatsApp, Zoom, Google Meet).
- **Zero-OOM Streaming**: Custom `StreamingMediaUploader` utilizes OkHttp `BufferedSink` to upload 50MB+ media files via Android `ContentResolver` without loading full byte arrays into heap memory.
- **System-Wide Intent Sharing**: Native Android `ACTION_SEND` and `ACTION_SEND_MULTIPLE` intent filters to scan photos, videos, and downloads directly from the gallery or chat apps.
- **Malware & Telecom Protection**: Integrated Drebin static APK analysis, SMS Bomber mitigation, predatory loan caller ID tagging, and biometric deception traps.

### 2. MesoNet Neural AI Engine
- **Meso4 Convolutional Architecture**: Dedicated neural network detecting mesoscopic facial forgery, compression artifacts, and blending seams across microscopic pixels and macroscopic face boundaries.
- **Non-Blocking Video Processing**: Decoupled video processing using Python temporary disk buffers and asynchronous thread-pools (`run_in_threadpool`), preventing asyncio event loop stalling.
- **Multi-Factor Fusion**: Fuses MesoNet probability with 2D Laplacian high-frequency blur variance to detect synthetic face-swaps in under 80ms.

### 3. Automated Response & Containment
- **Instant System Overlay**: Deploys `SYSTEM_ALERT_WINDOW` (`TYPE_APPLICATION_OVERLAY`) directly over active video calls upon deepfake detection, giving the user a 1-tap call quarantine control.
- **Perceptual Hash Synchronization (pHash)**: Automatically computes a cryptographic fingerprint of detected fraudulent faces and syncs it across the network via WebSocket (`/api/blocklist`).
- **Active Honeypot & Deception**: Deploys dynamic forensic canary tokens and decoy camera HAL feeds to neutralize unauthorized webcam/camera access.

### 4. Admin SOC Telemetry Dashboard
- **Live Threat Telemetry**: Interactive radar monitoring connected endpoints, active incidents, and threat classifications.
- **Explainable AI (XAI)**: Translates raw risk scores into human-readable forensic justifications and actionable remediation playbooks mapped to MITRE ATT&CK.

---

## 🚀 Quick Start Guide

### Prerequisites
- **Python**: 3.10 or 3.11
- **Android Studio / JDK**: JDK 17 or JetBrains Runtime (JBR) 21
- **Android Device / Emulator**: Android 8.0+ (API 26+)

---

### Backend Setup (FastAPI & MesoNet)

1. Clone the repository:
   ```bash
   git clone https://github.com/cbagh0819-ctrl/VEX.git
   cd VEX/backend
   ```

2. Create and activate a Python virtual environment:
   ```bash
   python -m venv venv
   # On Windows:
   .\venv\Scripts\activate
   # On Linux/macOS:
   source venv/bin/activate
   ```

3. Install production dependencies:
   ```bash
   pip install -r requirements.txt
   ```

4. Start the backend server:
   ```bash
   uvicorn main:app --host 0.0.0.0 --port 8000
   ```

5. Access local endpoints:
   - **SOC Dashboard**: `http://127.0.0.1:8000/dashboard`
   - **Connect Portal**: `http://127.0.0.1:8000/connect`
   - **API Documentation**: `http://127.0.0.1:8000/docs`

---

### Android Client Build (Release APK)

1. Navigate to the repository root:
   ```bash
   cd c:\vex
   ```

2. Build the signed production APK:
   ```bash
   # Windows PowerShell
   $env:JAVA_HOME = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.3\jbr"
   .\gradlew.bat assembleRelease --no-daemon
   ```

3. The generated release APK is located at:
   ```
   app/build/outputs/apk/release/app-release.apk
   ```

4. Verify signature compliance (V2 & V3 required for Android 14 / MIUI package installers):
   ```bash
   apksigner verify --verbose --min-sdk-version 26 app/build/outputs/apk/release/app-release.apk
   ```

---

## 📡 API Reference Overview

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/ai/mesonet/live-frame` | Real-time MesoNet frame evaluation for active video call interception. |
| `POST` | `/ai/scan-video` | Non-blocking chunked streaming video deepfake detection. |
| `POST` | `/ai/gemma/evaluate` | Cloud-assisted Gemma 2 2B behavioral defense engine. |
| `POST` | `/events/` | High-throughput endpoint telemetry and incident ingestion. |
| `GET` | `/api/blocklist/check` | Sub-millisecond O(1) query for quarantined hashes, IPs, and domains. |
| `GET` | `/api/caller-id/lookup` | Fraudulent shortcode and predatory loan telephone registry lookup. |

---

## 🛡️ Cloud Deployment

The repository includes ready-to-use cloud infrastructure definitions:

- **Docker Container**: [`backend/Dockerfile`](backend/Dockerfile) builds a lightweight Debian image with all system graphics dependencies (`libgl1`, `libglib2.0-0`).
- **Render.com**: [`render.yaml`](render.yaml) enables zero-config deployment with managed healthchecks.
- **Hugging Face Spaces**: [`backend/README.md`](backend/README.md) allows 1-click Docker deployment on free GPU/CPU instances.

---

## 🔒 Security & MITRE ATT&CK Mapping

VEX systematically classifies all indicators according to the MITRE ATT&CK matrix:
- **T1512 (Camera Capture)**: Neutralized via Camera HAL decoy canary feeds.
- **T1204 (User Execution)**: Mitigated by deep link intent inspection and SMS phishing shields.
- **T1437 (Application Layer Protocol)**: Monitored through AdGuard encrypted DNS filtering.
- **T1636 (Protected User Data)**: Defended using the Drebin static permission classifier.

---

## 📄 License
This project is open-source under the MIT License.
