# -*- coding: utf-8 -*-
"""
VEX Master Blueprint Builder - Modular Construction
"""
import os

HTML_FILE = r"C:\vex\VEX_Master_Hackathon_Blueprint.html"

def get_head():
    return """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>VEX Cyber Guard — Enterprise Zero-Trust Mobile Defense Blueprint</title>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600;700&family=Inter:wght@300;400;500;600;700;800;900&family=Orbitron:wght@600;700;800;900&display=swap');

    :root {
      --bg-dark: #07090e;
      --bg-card: #0d121d;
      --bg-card-border: #1a2333;
      --bg-surface: #121826;
      --bg-surface-elevated: #162032;
      --neon-cyan: #00f0ff;
      --neon-green: #00ff9d;
      --neon-amber: #ffb703;
      --neon-red: #ff0055;
      --neon-purple: #c084fc;
      --text-primary: #f8fafc;
      --text-secondary: #94a3b8;
      --text-muted: #64748b;
      --card-radius: 12px;
    }

    * { box-sizing: border-box; margin: 0; padding: 0; }

    body {
      background-color: var(--bg-dark);
      color: var(--text-primary);
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
      line-height: 1.6;
      font-size: 13px;
      padding: 0;
      margin: 0;
      -webkit-print-color-adjust: exact !important;
      print-color-adjust: exact !important;
    }

    @page {
      size: A4;
      margin: 12mm 12mm 14mm 12mm;
    }

    .page-break {
      page-break-after: always;
      break-after: page;
    }

    .avoid-break {
      page-break-inside: avoid;
      break-inside: avoid;
    }

    .container {
      max-width: 1040px;
      margin: 0 auto;
      padding: 16px;
    }

    /* Cover / Hero Header */
    .hero-banner {
      background: linear-gradient(135deg, rgba(0, 240, 255, 0.12) 0%, rgba(192, 132, 252, 0.12) 100%), #0d121d;
      border: 1.5px solid var(--neon-cyan);
      border-radius: var(--card-radius);
      padding: 24px 28px;
      margin-bottom: 20px;
      box-shadow: 0 0 30px rgba(0, 240, 255, 0.15);
      position: relative;
    }

    .hero-title {
      font-family: 'Orbitron', monospace;
      font-size: 26px;
      font-weight: 900;
      letter-spacing: 1.5px;
      color: var(--neon-cyan);
      text-shadow: 0 0 20px rgba(0, 240, 255, 0.5);
      margin-bottom: 6px;
    }

    .hero-subtitle {
      font-size: 13.5px;
      color: var(--text-secondary);
      font-weight: 500;
      max-width: 820px;
      line-height: 1.5;
    }

    .badge-bar {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 14px;
    }

    .tag-pill {
      display: inline-block;
      padding: 3px 10px;
      border-radius: 999px;
      font-size: 10px;
      font-weight: 700;
      letter-spacing: 0.8px;
      text-transform: uppercase;
      font-family: 'Fira Code', monospace;
    }

    .tag-cyan { background: rgba(0, 240, 255, 0.15); color: var(--neon-cyan); border: 1px solid var(--neon-cyan); }
    .tag-green { background: rgba(0, 255, 157, 0.15); color: var(--neon-green); border: 1px solid var(--neon-green); }
    .tag-amber { background: rgba(255, 183, 3, 0.15); color: var(--neon-amber); border: 1px solid var(--neon-amber); }
    .tag-red { background: rgba(255, 0, 85, 0.15); color: var(--neon-red); border: 1px solid var(--neon-red); }
    .tag-purple { background: rgba(192, 132, 252, 0.15); color: var(--neon-purple); border: 1px solid var(--neon-purple); }

    /* Section Headings */
    .section-title {
      font-family: 'Orbitron', sans-serif;
      font-size: 17px;
      font-weight: 800;
      letter-spacing: 1px;
      color: var(--neon-cyan);
      border-left: 4px solid var(--neon-cyan);
      padding-left: 12px;
      margin: 24px 0 14px 0;
      text-transform: uppercase;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .section-sub {
      font-size: 11px;
      color: var(--text-muted);
      font-family: 'Fira Code', monospace;
      font-weight: 400;
    }

    /* Cards & Layouts */
    .card {
      background: var(--bg-card);
      border: 1px solid var(--bg-card-border);
      border-radius: var(--card-radius);
      padding: 16px;
      margin-bottom: 14px;
      position: relative;
    }

    .card-highlight {
      border-color: rgba(0, 240, 255, 0.35);
      box-shadow: 0 4px 20px rgba(0, 240, 255, 0.06);
    }

    .card-title {
      font-family: 'Orbitron', sans-serif;
      font-size: 13.5px;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 8px;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .grid-2 {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 14px;
      margin-bottom: 14px;
    }

    .grid-3 {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 12px;
      margin-bottom: 14px;
    }

    .grid-4 {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 10px;
      margin-bottom: 14px;
    }

    /* Tables */
    table {
      width: 100%;
      border-collapse: collapse;
      margin: 10px 0 14px 0;
      font-size: 11.5px;
    }

    th, td {
      padding: 8px 10px;
      text-align: left;
      border-bottom: 1px solid var(--bg-card-border);
      vertical-align: middle;
    }

    th {
      background: var(--bg-surface);
      color: var(--neon-cyan);
      font-family: 'Fira Code', monospace;
      font-weight: 600;
      font-size: 10.5px;
      text-transform: uppercase;
      letter-spacing: 0.6px;
    }

    tr:nth-child(even) td {
      background: rgba(18, 24, 38, 0.45);
    }

    /* Math & Code Blocks */
    .math-callout {
      background: rgba(0, 240, 255, 0.05);
      border-left: 3px solid var(--neon-cyan);
      border-radius: 0 6px 6px 0;
      padding: 8px 12px;
      font-family: 'Fira Code', monospace;
      font-size: 11px;
      color: #e0f2fe;
      margin: 6px 0;
      line-height: 1.5;
    }

    .code-snippet {
      background: #04060a;
      border: 1px solid #1e293b;
      border-radius: 6px;
      padding: 8px 10px;
      font-family: 'Fira Code', monospace;
      font-size: 10.5px;
      color: #38bdf8;
      overflow-x: auto;
      margin: 6px 0;
      line-height: 1.45;
    }

    /* Funny Example Callout */
    .funny-box {
      background: linear-gradient(135deg, rgba(255, 183, 3, 0.08), rgba(255, 0, 85, 0.08));
      border: 1px dashed var(--neon-amber);
      border-radius: 8px;
      padding: 10px 12px;
      margin: 8px 0;
      font-size: 11.5px;
      color: #fef08a;
      line-height: 1.5;
    }

    .funny-title {
      font-weight: 700;
      color: var(--neon-amber);
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 4px;
      text-transform: uppercase;
      font-size: 10.5px;
      font-family: 'Fira Code', monospace;
    }

    /* Phone Mockups */
    .phone-mockup {
      background: #000;
      border: 2.5px solid #334155;
      border-radius: 24px;
      padding: 10px;
      max-width: 250px;
      margin: 0 auto 10px auto;
      box-shadow: 0 10px 25px rgba(0,0,0,0.8), 0 0 15px rgba(0,240,255,0.15);
      font-family: 'Inter', sans-serif;
    }

    .phone-notch {
      width: 70px;
      height: 12px;
      background: #111;
      border-radius: 0 0 8px 8px;
      margin: -10px auto 6px auto;
    }

    .phone-screen {
      background: #080c14;
      border-radius: 14px;
      padding: 8px;
      min-height: 380px;
      border: 1px solid #1e293b;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }

    .phone-top-status {
      display: flex;
      justify-content: space-between;
      font-size: 8px;
      color: #64748b;
      margin-bottom: 6px;
      font-family: 'Fira Code', monospace;
    }

    .phone-header-banner {
      background: linear-gradient(135deg, rgba(0,240,255,0.2), rgba(0,255,157,0.2));
      border: 1px solid var(--neon-cyan);
      border-radius: 6px;
      padding: 6px;
      text-align: center;
      margin-bottom: 8px;
    }

    .phone-header-banner h4 {
      font-size: 9.5px;
      color: var(--neon-cyan);
      font-family: 'Orbitron', sans-serif;
    }

    .phone-grid-icons {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 5px;
      margin-bottom: 8px;
    }

    .phone-app-btn {
      background: #131b2e;
      border: 1px solid #1e293b;
      border-radius: 5px;
      padding: 5px 3px;
      text-align: center;
      font-size: 8px;
      font-weight: 600;
      color: #e2e8f0;
    }

    .phone-app-btn .icon {
      font-size: 11px;
      display: block;
      margin-bottom: 2px;
    }

    .phone-bottom-nav {
      display: flex;
      justify-content: space-around;
      background: #0c1220;
      border: 1px solid #1e293b;
      border-radius: 6px;
      padding: 5px 2px;
      font-size: 7px;
      color: #94a3b8;
      text-align: center;
      font-family: 'Fira Code', monospace;
    }

    .phone-bottom-nav .active {
      color: var(--neon-cyan);
      font-weight: 700;
    }

    /* Images */
    .img-box {
      text-align: center;
      margin: 12px 0;
      border: 1px solid #1e293b;
      border-radius: 8px;
      overflow: hidden;
      background: #04060a;
      padding: 6px;
    }

    .img-box img {
      max-width: 100%;
      max-height: 340px;
      border-radius: 6px;
      object-fit: contain;
    }

    .img-caption {
      font-size: 10.5px;
      color: var(--text-muted);
      font-family: 'Fira Code', monospace;
      margin-top: 4px;
    }
  </style>
</head>
<body>
<div class="container">
"""
