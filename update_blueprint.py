# Python script to update VEX_Master_Hackathon_Blueprint.html
import os

html_path = r'C:\vex\VEX_Master_Hackathon_Blueprint.html'
with open(html_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update Section 3 text
old_sec3 = 'integrating <strong>9 Current Live Modules</strong>, <strong>8 Advanced Future Engines</strong>, and <strong>3 Live Stage Demonstration Protocols</strong>.'
new_sec3 = 'integrating <strong>9 Current Live Modules</strong>, <strong>9 Advanced Security Engines</strong> (including VEX Browser Zero-Trace Navigator), and <strong>3 Live Stage Demonstration Protocols</strong> (Total 18 Specialized Defense Engines).'

if old_sec3 in content:
    content = content.replace(old_sec3, new_sec3)
    print('[+] Updated Section 3 feature count')
else:
    print('[-] Section 3 string not found')

# 2. Update Mockup 2 checklist to 5 diagram features
old_mockup_needle = '✔ Auto-Wipe History on Exit<br>'
if old_mockup_needle in content:
    old_start = content.rfind('<div style="font-size:7.5px;', 0, content.find(old_mockup_needle))
    old_end = content.find('🛡️ ZERO COOKIES • PRIVATE TAB LOCKED\n              </div>', old_start) + len('🛡️ ZERO COOKIES • PRIVATE TAB LOCKED\n              </div>')
    
    new_mockup_block = '''<div style="font-size:7px; color:#cbd5e1; line-height:1.5; background:#111827; padding:5px; border-radius:6px; margin-bottom:5px;">
                ✔ 1. No Login / Sign-up / No Payment<br>
                ✔ 2. Auto-Wipe History & Data on Exit<br>
                ✔ 3. Private DNS: dns.adguard.com<br>
                ✔ 4. In-Flight Media Sniffer (Video/Audio Grab)<br>
                ✔ 5. Auto VPN Connected State / Zero Trace
              </div>
              <div style="background:rgba(0,255,157,0.1); border:1px solid var(--neon-green); border-radius:6px; padding:4px; text-align:center; font-size:7px; color:var(--neon-green); font-weight:700;">
                🛡️ ZERO COOKIES • IN-MEMORY SESSION PURGE
              </div>'''
    content = content[:old_start] + new_mockup_block + content[old_end:]
    print('[+] Updated Mockup 2 checklist')
else:
    print('[-] Mockup 2 needle not found')

# 3. Update Section 4 Title
old_sec4_title = '<span>6. Technical Specifications: Features 10 to 17 (Advanced Future Modules)</span>'
new_sec4_title = '<span>6. Technical Specifications: Features 10 to 18 (Advanced Modules & Flagship Engines)</span>'

if old_sec4_title in content:
    content = content.replace(old_sec4_title, new_sec4_title)
    print('[+] Updated Section 4 title')
else:
    print('[-] Section 4 title not found')

# 4. Insert Feature 18 after Feature 17
feat18_html = '''
    <!-- FEATURE 18: VEX BROWSER (VB) - ZERO-TRACE SANDBOX NAVIGATOR & IN-FLIGHT MEDIA SNIFFER -->
    <div class="card card-highlight avoid-break" style="margin-bottom:16px;">
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <div class="card-title" style="color:var(--neon-green);">18. VEX Browser (VB) — Zero-Trace Sandbox Navigator & In-Flight Media Sniffer</div>
        <span class="tag-pill tag-green">ZERO LOGIN + AUTO-WIPE + ADGUARD DOH + IN-FLIGHT MEDIA SNIFFER</span>
      </div>
      <div class="grid-2">
        <div>
          <div style="font-size:11px; color:#94a3b8; font-weight:700; text-transform:uppercase;">Mathematics, Protocols & Network Privacy</div>
          <div class="math-box">
            RFC 8484 DNS-over-HTTPS (DoH) Wire-Format + AdGuard Upstream:<br>
            DNS Query Q &rarr; HTTPS POST https://dns.adguard.com/dns-query (Port 443 TLS 1.3)<br>
            Filters 250,000+ telemetry and ad tracker domains with 0ms leak to ISP.<br>
            Canvas Fingerprint Randomization: H(Canvas) perturbed with subtle Perlin noise to defeat cross-site tracking hashes!
          </div>
          <div style="font-size:11px; color:#94a3b8; font-weight:700; text-transform:uppercase; margin-top:8px;">In-Flight Media Sniffer & Zero-Trace RAM Purge</div>
          <p style="font-size:11.5px; color:#cbd5e1;">
            Intercepts HTTP headers via <code>WebViewClient.shouldInterceptRequest</code> and <code>DownloadListener</code>. Uses regex pattern matching against streaming video/audio (<code>.mp4</code>, <code>.m3u8</code> HLS manifests, <code>.mpd</code> DASH, <code>.webm</code>, <code>.mp3</code>) and surfaces an instant one-tap grab drawer. When closing or minimizing the app, RAM-only WebStorage, IndexedDB, DOM cookies, and cache partitions are cryptographically zeroed out.
          </p>
        </div>
        <div>
          <div class="funny-box">
            <div class="funny-title">😂 Funny Real-World Scenario: "The 47 Fake 'Download Now' Buttons"</div>
            <strong>The Threat:</strong> You try to download a college project PDF or watch a cricket highlight on a free streaming website. The page bombards you with 47 flashing "DOWNLOAD NOW" buttons, 3 pop-ups saying your phone has 14 viruses, and automatically starts an auto-subscribing $49.99/month lottery trap!<br><br>
            <strong>VEX in Action:</strong> VEX Browser blocks all 47 malicious ad network domains at the AdGuard DoH layer so zero pop-ups load. Then, the in-flight media sniffer detects the actual hidden video payload directly from the HTTP stream: <em>"MEDIA DETECTED: highlight_clip_1080p.mp4 [32.4 MB] — Tap 'GRAB' to save directly. Tap 'PANIC WIPE' to vaporize your history so your roommate never knows you watched it!"</em>
          </div>
        </div>
      </div>
    </div>
'''

target_marker = '<!-- FEATURE 17: AIRTAG & BLE STALKING -->'
pos = content.find(target_marker)
if pos != -1:
    end_card = content.find('    </div>\n\n  </div>', pos)
    if end_card != -1:
        insert_pos = end_card + len('    </div>\n')
        content = content[:insert_pos] + feat18_html + content[insert_pos:]
        print('[+] Successfully inserted Feature 18: VEX Browser')
    else:
        print('[-] End of card 17 not found')
else:
    print('[-] Target marker for Feature 17 not found')

with open(html_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('[+] Master Blueprint HTML updated successfully!')
