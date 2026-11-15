# -*- coding: utf-8 -*-
"""
Helper script to assemble C:\vex\VEX_Master_Hackathon_Blueprint.html
"""
import sys

def get_css():
    return """
<style>
  @import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;700&family=Inter:wght@300;400;600;700;800;900&family=Orbitron:wght@600;800;900&display=swap');

  :root {
    --bg-dark: #0a0d14;
    --bg-card: #111726;
    --bg-card-border: #1e293b;
    --bg-surface: #161f33;
    --neon-cyan: #00f0ff;
    --neon-green: #00ff9d;
    --neon-amber: #ffb703;
    --neon-red: #ff0055;
    --neon-purple: #b5179e;
    --text-primary: #f8fafc;
    --text-secondary: #94a3b8;
    --text-muted: #64748b;
  }

  * { box-sizing: border-box; margin: 0; padding: 0; }

  body {
    background-color: var(--bg-dark);
    color: var(--text-primary);
    font-family: 'Inter', -apple-system, sans-serif;
    line-height: 1.6;
    font-size: 13.5px;
    padding: 0;
    margin: 0;
    -webkit-print-color-adjust: exact !important;
    print-color-adjust: exact !important;
  }

  @page {
    size: A4;
    margin: 14mm 12mm 14mm 12mm;
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
    max-width: 1080px;
    margin: 0 auto;
    padding: 24px;
  }

  /* Header & Banner */
  .hero-header {
    background: linear-gradient(135deg, rgba(0, 240, 255, 0.12) 0%, rgba(181, 23, 158, 0.12) 100%), #111726;
    border: 1px solid var(--neon-cyan);
    border-radius: 12px;
    padding: 28px;
    margin-bottom: 24px;
    position: relative;
    overflow: hidden;
  }

  .hero-title {
    font-family: 'Orbitron', monospace;
    font-size: 26px;
    font-weight: 900;
    letter-spacing: 1.5px;
    color: var(--neon-cyan);
    text-shadow: 0 0 15px rgba(0, 240, 255, 0.4);
    margin-bottom: 6px;
  }

  .hero-subtitle {
    font-size: 14px;
    color: var(--text-secondary);
    font-weight: 500;
    max-width: 850px;
  }

  .tag-pill {
    display: inline-block;
    padding: 3px 10px;
    border-radius: 999px;
    font-size: 10.5px;
    font-weight: 700;
    letter-spacing: 0.8px;
    text-transform: uppercase;
    font-family: 'Fira Code', monospace;
    margin-right: 6px;
    margin-top: 8px;
  }

  .tag-cyan { background: rgba(0, 240, 255, 0.15); color: var(--neon-cyan); border: 1px solid var(--neon-cyan); }
  .tag-green { background: rgba(0, 255, 157, 0.15); color: var(--neon-green); border: 1px solid var(--neon-green); }
  .tag-amber { background: rgba(255, 183, 3, 0.15); color: var(--neon-amber); border: 1px solid var(--neon-amber); }
  .tag-red { background: rgba(255, 0, 85, 0.15); color: var(--neon-red); border: 1px solid var(--neon-red); }
  .tag-purple { background: rgba(181, 23, 158, 0.15); color: var(--neon-purple); border: 1px solid var(--neon-purple); }

  /* Section Titles */
  .section-title {
    font-family: 'Orbitron', sans-serif;
    font-size: 18px;
    font-weight: 800;
    letter-spacing: 1px;
    color: var(--neon-cyan);
    border-left: 4px solid var(--neon-cyan);
    padding-left: 12px;
    margin: 28px 0 16px 0;
    text-transform: uppercase;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .section-sub {
    font-size: 12px;
    color: var(--text-muted);
    font-family: 'Fira Code', monospace;
    font-weight: 400;
  }

  /* Grid layouts */
  .grid-2 {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    margin-bottom: 20px;
  }

  .grid-3 {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 14px;
    margin-bottom: 20px;
  }

  /* Cards */
  .card {
    background: var(--bg-card);
    border: 1px solid var(--bg-card-border);
    border-radius: 10px;
    padding: 16px;
    position: relative;
  }

  .card-highlight {
    border-color: rgba(0, 240, 255, 0.4);
    box-shadow: 0 4px 20px rgba(0, 240, 255, 0.08);
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

  /* Phone Mockup Frame */
  .phone-mockup {
    background: #000;
    border: 3px solid #334155;
    border-radius: 28px;
    padding: 12px;
    max-width: 270px;
    margin: 0 auto 12px auto;
    box-shadow: 0 10px 30px rgba(0,0,0,0.8), 0 0 15px rgba(0,240,255,0.2);
    position: relative;
    font-family: 'Inter', sans-serif;
  }

  .phone-notch {
    width: 80px;
    height: 14px;
    background: #111;
    border-radius: 0 0 10px 10px;
    margin: -12px auto 8px auto;
  }

  .phone-screen {
    background: #080c14;
    border-radius: 16px;
    padding: 10px;
    min-height: 440px;
    border: 1px solid #1e293b;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .phone-top-status {
    display: flex;
    justify-content: space-between;
    font-size: 9px;
    color: #64748b;
    margin-bottom: 8px;
    font-family: 'Fira Code', monospace;
  }

  .phone-header-banner {
    background: linear-gradient(135deg, rgba(0,240,255,0.2), rgba(0,255,157,0.2));
    border: 1px solid var(--neon-cyan);
    border-radius: 8px;
    padding: 8px;
    text-align: center;
    margin-bottom: 10px;
  }

  .phone-header-banner h4 {
    font-size: 10.5px;
    color: var(--neon-cyan);
    font-family: 'Orbitron', sans-serif;
  }

  .phone-grid-icons {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 6px;
    margin-bottom: 10px;
  }

  .phone-app-btn {
    background: #131b2e;
    border: 1px solid #1e293b;
    border-radius: 6px;
    padding: 6px 4px;
    text-align: center;
    font-size: 8.5px;
    font-weight: 600;
    color: #e2e8f0;
  }

  .phone-app-btn .icon {
    font-size: 13px;
    display: block;
    margin-bottom: 2px;
  }

  .phone-bottom-nav {
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
  }

  .phone-bottom-nav .active {
    color: var(--neon-cyan);
    font-weight: 700;
  }

  /* Table styling */
  table {
    width: 100%;
    border-collapse: collapse;
    margin: 12px 0 18px 0;
    font-size: 12px;
  }

  th, td {
    padding: 8px 10px;
    text-align: left;
    border-bottom: 1px solid var(--bg-card-border);
  }

  th {
    background: #161f33;
    color: var(--neon-cyan);
    font-family: 'Fira Code', monospace;
    font-weight: 600;
    font-size: 11px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  tr:nth-child(even) td {
    background: rgba(22, 31, 51, 0.4);
  }

  /* Code & Math Box */
  .code-box {
    background: #070a10;
    border: 1px solid #1e293b;
    border-radius: 6px;
    padding: 10px 12px;
    font-family: 'Fira Code', monospace;
    font-size: 11px;
    color: #38bdf8;
    overflow-x: auto;
    margin: 8px 0;
    line-height: 1.5;
  }

  .math-box {
    background: rgba(0, 240, 255, 0.05);
    border-left: 3px solid var(--neon-cyan);
    border-radius: 0 6px 6px 0;
    padding: 10px 14px;
    font-family: 'Fira Code', monospace;
    font-size: 11.5px;
    color: #e0f2fe;
    margin: 8px 0;
  }

  /* Funny Example Banner */
  .funny-box {
    background: linear-gradient(135deg, rgba(255, 183, 3, 0.08), rgba(255, 0, 85, 0.08));
    border: 1px dashed var(--neon-amber);
    border-radius: 8px;
    padding: 10px 14px;
    margin: 8px 0;
    font-size: 12px;
    color: #fde68a;
  }

  .funny-title {
    font-weight: 700;
    color: var(--neon-amber);
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 4px;
    text-transform: uppercase;
    font-size: 11px;
    font-family: 'Fira Code', monospace;
  }

  /* Image Embeds */
  .img-container {
    text-align: center;
    margin: 14px 0;
    border: 1px solid #1e293b;
    border-radius: 8px;
    overflow: hidden;
    background: #070a10;
    padding: 8px;
  }

  .img-container img {
    max-width: 100%;
    max-height: 380px;
    border-radius: 6px;
    object-fit: contain;
  }

  .img-caption {
    font-size: 11px;
    color: var(--text-muted);
    font-family: 'Fira Code', monospace;
    margin-top: 6px;
  }
</style>
"""

print("get_css defined successfully")
