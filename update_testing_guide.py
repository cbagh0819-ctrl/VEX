# Python script to update VEX_LINUX_TESTING_AND_STAGE_EXPLOITS.txt

file_path = r'C:\vex\VEX_LINUX_TESTING_AND_STAGE_EXPLOITS.txt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

exploit_8_block = '''
================================================================================
SECTION 10: EXPLOIT #8 -- VEX BROWSER ZERO-TRACE & MEDIA SNIFFER VERIFICATION
================================================================================

1. TEST A: VERIFY 0 PLAINTEXT DNS LEAKS (ADGUARD DOH VERIFICATION)
--------------------------------------------------------------------------------
# Run packet sniffer on Linux gateway / Wi-Fi AP watching for standard port 53 DNS:
sudo tcpdump -i any -n "udp port 53 and host $VEX_PHONE_IP" -c 10

# Inside VEX Browser on the phone, navigate to: https://duckduckgo.com
# EXPECTED RESULT:
# ZERO packets captured on UDP port 53!
# All DNS queries are routed via encrypted TLS 1.3 to https://dns.adguard.com/dns-query.
--------------------------------------------------------------------------------

2. TEST B: IN-FLIGHT MEDIA SNIFFER VERIFICATION
--------------------------------------------------------------------------------
# Host a lightweight local video streaming test server on Linux:
cat << 'EOF' > stream_server.py
#!/usr/bin/env python3
import http.server
import socketserver

PORT = 8888
print(f"[*] Serving mock streaming video at http://0.0.0.0:{PORT}/sample_defense_stream.mp4")

HTML = """
<!DOCTYPE html>
<html>
<head><title>VEX Stream Test</title></head>
<body style="background:#000; color:#fff; text-align:center; padding:40px;">
  <h2>Tactical Defense Stream Test</h2>
  <video width="320" height="240" controls autoplay>
    <source src="/sample_defense_stream.mp4" type="video/mp4">
  </video>
</body>
</html>
"""

class Handler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/" or self.path == "/index.html":
            self.send_response(200)
            self.send_header("Content-type", "text/html")
            self.end_headers()
            self.wfile.write(HTML.encode())
        else:
            self.send_response(200)
            self.send_header("Content-type", "video/mp4")
            self.send_header("Content-Length", "1048576")
            self.end_headers()
            self.wfile.write(b'\\x00' * 1048576)

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    httpd.serve_forever()
EOF
python3 stream_server.py &
--------------------------------------------------------------------------------
# Inside VEX Browser on phone, enter: http://<linux_ip>:8888
# EXPECTED RESULT:
# VEX Browser immediately detects the in-flight MP4 stream:
# - Pops open the floating "🎬 IN-FLIGHT MEDIA SNIFFER" drawer.
# - Displays: "sample_defense_stream.mp4 • 1.0 MB • [GRAB STREAM]".

3. TEST C: FORENSIC STORAGE ZERO-TRACE AUDIT
--------------------------------------------------------------------------------
# Tap the red "WIPE DATA" button in VEX Browser or close the app.
# Verify on host via ADB that zero cookies, WebSQL, or cached files persist:
adb shell run-as com.vex.security ls -la /data/data/com.vex.security/app_webview/

# EXPECTED RESULT:
# Zero persistent cookies or IndexedDB directories.
# Total forensic zero-trace guarantee verified.
--------------------------------------------------------------------------------
'''

target_sec10 = '================================================================================\nSECTION 10: STAGE PITCH CHEATSHEET & TIMING BREAKDOWN'
new_sec11 = '================================================================================\nSECTION 11: STAGE PITCH CHEATSHEET & TIMING BREAKDOWN'

if target_sec10 in content:
    content = content.replace(target_sec10, exploit_8_block.strip() + '\n\n\n' + new_sec11)
    print('[+] Inserted Section 10 for VEX Browser and bumped Stage Pitch to Section 11')
else:
    print('[-] Section 10 marker not found')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('[+] Testing guide updated successfully!')
