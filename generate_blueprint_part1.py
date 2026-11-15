# -*- coding: utf-8 -*-
"""
VEX Master Blueprint Builder - Part 1
Creates the base HTML and writes Sections 1, 2, and 3.
"""
import os
import sys

HTML_FILE = r"C:\vex\VEX_Master_Hackathon_Blueprint.html"

from generate_sections import get_css

html_start = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>VEX Cyber Guard — Master Hackathon Blueprint & Technical Specification</title>
  {get_css()}
</head>
<body>
<div class="container">

  <!-- ==================== COVER / HEADER ==================== -->
  <div class="hero-header avoid-break">
    <div style="display:flex; justify-content:space-between; align-items:flex-start;">
      <div>
        <div class="hero-title">🛡️ VEX CYBER GUARD</div>
        <div class="hero-subtitle">
          <strong>BPUT HACKATHON GRAND FINALE (36-HR MASTER BLUEPRINT)</strong><br>
          Comprehensive Product Architecture, UI Wireframe Blueprints, Mathematical Formulations, On-Device AI Pipelines, and Stage Demonstration Protocols.
        </div>
      </div>
      <div style="text-align:right;">
        <span class="tag-pill tag-cyan">VERSION 2.4 PRO</span><br>
        <span class="tag-pill tag-green">BPUT GRAND FINALE READY</span><br>
        <span class="tag-pill tag-purple">ZERO-TRUST ARCHITECTURE</span>
      </div>
    </div>
    <div style="margin-top:14px; border-top:1px solid rgba(0,240,255,0.2); padding-top:10px; font-size:11.5px; color:#94a3b8; display:flex; justify-content:space-between;">
      <span><strong>Team:</strong> VEX Security Core</span>
      <span><strong>Target OS:</strong> Android 14+ (API 34-37) & Linux</span>
      <span><strong>Hardware Layer:</strong> RF, IMU, BLE, NFC, USB-C, CMOS</span>
      <span><strong>Status:</strong> Production & Hackathon Verified</span>
    </div>
  </div>

  <!-- ==================== SECTION 1: PLAN MAP ANALYSIS ==================== -->
  <div class="avoid-break">
    <div class="section-title">
      <span>1. Architectural Plan Map & Flowchart Analysis</span>
      <span class="section-sub">DECONSTRUCTION OF USER WORKFLOW & APP ECOSYSTEM</span>
    </div>
    <p style="color:#cbd5e1; margin-bottom:12px;">
      The user architectural diagram maps the entire <strong>VEX Cyber Guard Ecosystem</strong> into a unified 5-tab tactical defense suite, integrating <strong>9 Current Live Modules</strong>, <strong>8 Advanced Future Engines</strong>, and <strong>3 Live Stage Demonstration Protocols</strong>.
    </p>

    <div class="img-container">
      <img src="presentation_assets/vex_sketch_flowchart.png" alt="VEX Architecture Flowchart Sketch">
      <div class="img-caption">Figure 1.1: Core Architecture Map & Screen Relationship Topology (User System Specification)</div>
    </div>

    <div class="grid-2">
      <div class="card card-highlight">
        <div class="card-title" style="color:var(--neon-cyan);">📱 Navigation Hierarchy (5 Core Tabs)</div>
        <ul style="font-size:12px; color:#cbd5e1; padding-left:18px; line-height:1.7;">
          <li><strong>Tab 1: Home Dashboard</strong> — Quick Scan trigger, 10-feature defense grid, active threat level dial.</li>
          <li><strong>Tab 2: VEX Browser (VB)</strong> — Sandboxed private navigator, AdGuard DNS, auto-wipe cache, media sniffer.</li>
          <li><strong>Tab 3: Reports Vault</strong> — Forensic audit log, evidence timeline, MITRE ATT&CK threat telemetry.</li>
          <li><strong>Tab 4: SOC Live Dashboard</strong> — Real-time telemetry streaming, baseband radar, CPU/RAM/Battery metrics.</li>
          <li><strong>Tab 5: Settings & Vault</strong> — Zero-trust policy toggles, honey-token injector, decoy frame manager.</li>
        </ul>
      </div>

      <div class="card card-highlight">
        <div class="card-title" style="color:var(--neon-green);">⚡ Specialized Modal Experiences</div>
        <ul style="font-size:12px; color:#cbd5e1; padding-left:18px; line-height:1.7;">
          <li><strong>VEX RadioZero™ Modal:</strong> Autonomous RF Jammer Radar HUD with Pedestrian Dead Reckoning (PDR) escape vector.</li>
          <li><strong>VEX Full Snap Scan Modal:</strong> Multi-threaded recursive device audit (DEX, permissions, ELF binaries, network sockets).</li>
          <li><strong>Active Backfire Honey-Chamber:</strong> Intercepts unauthorized webcam snooping and injects decoy frame back to attacker.</li>
          <li><strong>Hardware Bus Clamping Modal:</strong> Suppresses high-speed BadUSB keystrokes and neutralizes juice-jacking.</li>
        </ul>
      </div>
    </div>
  </div>

  <div class="page-break"></div>
"""

with open(HTML_FILE, "w", encoding="utf-8") as f:
    f.write(html_start)

print("Part 1 written successfully.")
