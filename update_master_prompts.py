# Python script to update VEX_COMPLETE_APP_MASTER_PROMPTS.txt
import re

prompts_path = r'C:\vex\VEX_COMPLETE_APP_MASTER_PROMPTS.txt'
with open(prompts_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update Phase 3 Screen 2
old_screen2 = '''2. Screen 2: 'VexBrowserScreen.kt' (Tab 2)
   - Sandboxed WebView with custom WebViewClient disabling storage cookies.
   - Top URL bar with AdGuard DoH security badge ('🛡️ DNS:ADG').
   - Action buttons: 'Auto-Wipe Data' and 'Media Sniffer' (detects MP4/MP3).'''

new_screen2 = '''2. Screen 2: 'VexBrowserScreen.kt' (Tab 2: VEX Browser - VB)
   - Zero-trace isolated WebView with strict in-memory lifecycle.
   - 5 Architectural Diagram Pillars:
     * 1. No Login / Sign-up / No Payment: Zero account tracking, purely anonymous.
     * 2. Auto-Wipe Data & History: On exit/dispose, wipes RAM, WebStorage, CookieManager, IndexedDB.
     * 3. Private DNS: Integrated AdGuard DoH (dns.adguard.com) bypassing local ISP tracking.
     * 4. In-Flight Media Sniffer: Automatically intercepts video (MP4/HLS .m3u8/DASH .mpd) and audio streams.
     * 5. Auto VPN Connected State / Zero-Trace in-memory sessions.
   - Cyberpunk Top Bar: SSL Padlock URL bar with TLS status, '🛡️ DNS:ADGUARD' badge, and 'AUTO-VPN' pill.
   - Quick Action: 'Wipe Data Now' button executing instant zero-trace RAM flush.
   - Dynamic Floating Drawer: Surfaces detected media items with title, size, and 1-tap 'GRAB' download button.'''

if old_screen2 in content:
    content = content.replace(old_screen2, new_screen2)
    print('[+] Updated Phase 3 Screen 2')
else:
    print('[-] Phase 3 Screen 2 not found')

# 2. Add MASTER PROMPT 4D before Phase 4 checklist
prompt_4d = '''
[MASTER PROMPT 4D: ZERO-TRACE SANDBOX BROWSER & IN-FLIGHT MEDIA SNIFFER (VEX BROWSER - VB)]
--------------------------------------------------------------------------------
"Implement the privacy browser engine and media sniffer in './app/src/main/java/com/vex/security/browser/':

1. 'VexBrowserEngine.kt':
   - Data class 'DetectedMedia(id, url, title, mimeType, detectedAt, estimatedSize)'.
   - Expose 'StateFlow<List<DetectedMedia>>' tracking real-time intercepted media streams.
   - Sniffer Engine 'sniffMedia(url: String)':
     * Regex matching: '(\\.mp4|\\.m3u8|\\.mpd|\\.webm|\\.mov|\\.m4v|\\.mp3|\\.aac|\\.flac|\\.ogg|\\.wav|\\.jpg|\\.jpeg|\\.png|\\.webp)'.
     * Parses filename from URL path, generates human-readable title and estimated size.
     * Deduplicates URLs to prevent UI flood.
   - AdGuard Private DNS Wire-Format:
     * Configures DoH upstream 'https://dns.adguard.com/dns-query' over TLS 1.3 (RFC 8484).
     * Zero plaintext UDP 53 DNS leakage to local ISP.
   - WebView Configuration:
     * Disable DOM storage: 'domStorageEnabled = false'.
     * Disable database storage: 'databaseEnabled = false'.
     * Force zero cache: 'cacheMode = WebSettings.LOAD_NO_CACHE'.
     * Disable geolocation and file access: 'setGeolocationEnabled(false)', 'allowFileAccess = false'.
   - Media Download Interceptor:
     * Attach 'DownloadListener' to capture direct file downloads into app-private isolated vault.
   - Panic Memory Purge 'clearAllBrowserData()':
     * CookieManager.getInstance().removeAllCookies(null).
     * WebStorage.getInstance().deleteAllData().
     * WebViewDatabase clearHttpAuthUsernamePassword, clearFormData.
     * Clear all detected media state: '_detectedMediaList.value = emptyList()'.

2. 'VexBrowserScreen.kt' (in './app/src/main/java/com/vex/security/ui/browser/'):
   - Top Bar: Cyberpunk address bar with SSL lock indicator, '🛡️ DNS:ADGUARD' badge, and 'AUTO-VPN' pill.
   - 'Wipe Data' action button triggering 'clearAllBrowserData()' with haptic feedback.
   - AndroidView embedding WebView with full lifecycle handling.
   - In-Flight Media Sniffer Sheet:
     * When detectedMediaList is non-empty, render a floating cyberpunk drawer at bottom.
     * List each captured video/image with media badge, duration/size estimate, and 'GRAB' button.
   - Auto-Purge on Exit:
     * 'DisposableEffect' that invokes 'clearAllBrowserData()' on disposal to guarantee zero forensic traces left on disk."
--------------------------------------------------------------------------------
'''

target_checklist = '[PHASE 4 VERIFICATION CHECKLIST]:'
if target_checklist in content:
    content = content.replace(target_checklist, prompt_4d + '\n' + target_checklist)
    print('[+] Inserted MASTER PROMPT 4D')
else:
    print('[-] Phase 4 checklist marker not found')

# 3. Update checklist and Phase 5 title
content = content.replace(
    '[ ] PortShieldManager sounds siren and clamps keystrokes > 80 chars/sec',
    '[ ] PortShieldManager sounds siren and clamps keystrokes > 80 chars/sec\n[ ] VexBrowserEngine blocks ad trackers via DoH, sniffs streaming video, and purges RAM on exit'
)

content = content.replace(
    'PHASE 5: IMPLEMENTING FEATURES 10 TO 17 (ADVANCED MODULES)',
    'PHASE 5: IMPLEMENTING FEATURES 10 TO 18 (ADVANCED MODULES & BROWSER PILLAR)'
)

with open(prompts_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('[+] Master Prompts file updated successfully!')
