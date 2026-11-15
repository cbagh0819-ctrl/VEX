#!/usr/bin/env python3
"""
VEX CYBER GUARD -- HACKER CAMPHISH PHOTO INSPECTOR & CANARY TRIGGER
Run this inside your Linux terminal (Kali / Ubuntu / WSL).
Simulates the attacker downloading and opening the victim's stolen photo,
which triggers the hidden Canary Token Web-Bug!
"""

import os
import sys
import time
import urllib.request
import subprocess

def inspect_photo():
    print("=======================================================")
    print("     KALI LINUX EXPLOITATION SUITE: CAMPHISH VIEWER    ")
    print("=======================================================")

    host = sys.argv[1] if len(sys.argv) > 1 else "172.19.112.1"
    port = sys.argv[2] if len(sys.argv) > 2 else "8000"
    canary_url = f"http://{host}:{port}/api/canary/track/hacker_session_8842"

    print(f"[*] Connecting to Target Server : {host}:{port}")
    print("[*] Retrieving captured victim selfie from exfiltration bucket...")
    time.sleep(1.0)

    headers = {
        "User-Agent": "Kali-Linux/2024.3 (Metasploit-Framework/v6.4.1 x86_64)"
    }

    try:
        req = urllib.request.Request(canary_url, headers=headers)
        with urllib.request.urlopen(req, timeout=5) as response:
            data = response.read()
            out_file = "stolen_victim_selfie.jpg"
            with open(out_file, "wb") as f:
                f.write(data)

            print(f"[+] Download complete! Stolen photo saved: ./{out_file} ({len(data)} bytes)")
            
            attacker_ip = response.headers.get("X-Attacker-IP", "Unknown")
            attacker_city = response.headers.get("X-Attacker-City", "Unknown")

            print("\n=======================================================")
            print("         ALERT: THE IMAGE WAS A CANARY HONEYTRAP!      ")
            print("=======================================================")
            print(f"[!] 🚨 YOU HAVE BEEN TRAPPED BY VEX ACTIVE DECEPTION!")
            print(f"[!] The stolen photo contained an active Canary Token Web-Bug.")
            print(f"[!] Opening the photo just leaked your identity to the target:")
            print(f"    • Logged Hacker IP   : {attacker_ip}")
            print(f"    • Resolved Location  : {attacker_city}, Odisha, India")
            print(f"    • System Fingerprint : Kali Linux x86_64")
            print(f"[!] The VEX Admin Dashboard is now flashing a RED RADAR LOCK on your coordinates!")
            print("=======================================================\n")

            # Try to launch Linux image viewer
            print("[*] Launching image viewer to display the honeytrap selfie...")
            viewers = ["xdg-open", "feh", "eog", "display"]
            opened = False
            for v in viewers:
                if subprocess.call(["which", v], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL) == 0:
                    subprocess.Popen([v, out_file])
                    opened = True
                    break
            if not opened:
                print(f"[*] View the captured decoy image file at: {os.path.abspath(out_file)}")

    except Exception as e:
        print(f"[-] Connection failed ({e}). Ensure FastAPI server is running on {host}:{port}!")

if __name__ == '__main__':
    inspect_photo()
