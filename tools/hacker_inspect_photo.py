#!/usr/bin/env python3
"""
VEX Red-Team Adversary Simulation Script
Simulates an attacker attempting illicit webcam exfiltration (CamPhish vector)
and inspecting the stolen photo loot.
"""

import sys
import time
import requests

def main():
    host = sys.argv[1] if len(sys.argv) > 1 else "127.0.0.1"
    port = sys.argv[2] if len(sys.argv) > 2 else "8000"
    base_url = f"http://{host}:{port}"

    print("=" * 60)
    print("     KALI LINUX EXPLOITATION SUITE: CAMPHISH VIEWER")
    print("=" * 60)
    print(f"[*] Target Endpoint           : {base_url}")
    print(f"[*] Simulating Victim Interaction: /phish/iphone")
    print(f"[*] Retrieving captured victim selfie from exfiltration bucket...")

    try:
        resp = requests.get(
            f"{base_url}/api/honeypot/selfie?session=victim_claim_88",
            headers={"User-Agent": "Kali-Linux/2024.3 (Metasploit-Framework/v6.4.1 x86_64)"},
            timeout=5
        )
        if resp.status_code == 200:
            with open("stolen_victim_selfie.jpg", "wb") as f:
                f.write(resp.content)
            print(f"[+] Download complete! Stolen photo saved: ./stolen_victim_selfie.jpg ({len(resp.content):,} bytes)")
            print("=" * 60)
            print("         ALERT: THE IMAGE WAS A CANARY HONEYTRAP!      ")
            print("=" * 60)
            print("[!] 🚨 YOU HAVE BEEN TRAPPED BY VEX ACTIVE DECEPTION!")
            print("[!] The stolen photo contained an active Canary Token Web-Bug.")
            print("[!] Opening the photo just leaked your identity to the target:")

            # Query SOC Telemetry
            time.sleep(1)
            soc_res = requests.get(f"{base_url}/api/canary/latest", timeout=5).json()
            print(f"    • Logged Hacker IP   : {soc_res.get('ip')}")
            print(f"    • Resolved Location  : {soc_res.get('city')}, {soc_res.get('region')}, {soc_res.get('country')}")
            print(f"    • System Fingerprint : {soc_res.get('user_agent')}")
            print(f"    • GPS Coordinates    : {soc_res.get('lat')}° N, {soc_res.get('lon')}° E")
            print("[!] The VEX Admin Dashboard is now flashing a RED RADAR LOCK on your coordinates!")
            print("=" * 60)
        else:
            print(f"[-] HTTP Error: {resp.status_code}")
    except Exception as e:
        print(f"[-] Attack failed: {e}")

if __name__ == "__main__":
    main()
