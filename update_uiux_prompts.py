# Python script to update VEX_UIUX_CHATGPT_AND_COMPOSE_PROMPTS.txt

file_path = r'C:\vex\VEX_UIUX_CHATGPT_AND_COMPOSE_PROMPTS.txt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update Section 1 Screen 2 description
old_sec1_s2 = '''2. SCREEN 2: VEX BROWSER (VB) -- ZERO-TRACE NAVIGATOR
   - Top Address Bar with active TLS padlock and '🛡️ DNS:ADGUARD' security pill.
   - Central Banner: 'VEX SANDBOX BROWSER -- No Login • No Trace • AdGuard DoH'.
   - Privacy Ledger: Checkmarks for Auto-Wipe History, Zero Cookies, Media Sniffer.
   - Floating Action Buttons: [Wipe All Cache Now] and [Media Grabber].'''

new_sec1_s2 = '''2. SCREEN 2: VEX BROWSER (VB) -- ZERO-TRACE NAVIGATOR & MEDIA SNIFFER
   - Top Address Bar with active TLS padlock, '🛡️ DNS:ADGUARD' badge, and 'AUTO-VPN' pill.
   - 5 Pillars from Architecture Diagram:
     1. No login / signup / payment required (Purely anonymous in-memory sessions).
     2. Auto-clear data and history on exit (RAM zeroing & WebStorage purge).
     3. Private DNS (dns.adguard.com / DoH via RFC 8484 TLS 1.3).
     4. In-flight media sniffer (Automatically detects and grabs streaming video/audio).
     5. Auto VPN connected state / Zero-trace in-memory sessions.
   - Floating Media Sniffer Drawer: Real-time pop-up showing detected MP4/HLS streams with 'GRAB' button.
   - Top Quick Action: Red 'PANIC WIPE' button to instantly zero out memory.'''

if old_sec1_s2 in content:
    content = content.replace(old_sec1_s2, new_sec1_s2)
    print('[+] Updated Section 1 Screen 2')
else:
    print('[-] Section 1 Screen 2 not found')

# 2. Add PROMPT 2B for Individual Screen: VEX BROWSER (VB)
prompt_2b = '''
--------------------------------------------------------------------------------
PROMPT 2B: INDIVIDUAL SCREEN -- VEX BROWSER (VB) ZERO-TRACE & MEDIA SNIFFER (SCREEN 2)
--------------------------------------------------------------------------------
"UI/UX mobile screen design of 'VEX Browser (VB)', zero-trace sandboxed privacy web browser,
futuristic mobile app interface, cyberpunk dark mode.
- Top Header: Sleek dark slate URL address bar (#131B2E) showing '🔒 https://duckduckgo.com',
  a glowing electric green badge '🛡️ DNS:ADGUARD', a cyan pill 'AUTO-VPN: ACTIVE', and a
  crimson button 'PANIC WIPE'.
- Sub-banner: Minimalist tactical info bar displaying '0 TRACKERS • 0 COOKIES • RAM ONLY SESSION'.
- Center Screen: Isolated dark web view displaying a secure search interface with zero ads,
  clean typography, and neon accent lines.
- Hero Floating Drawer (Bottom 35% of Screen): Glassmorphic Cyberpunk Media Sniffer Drawer:
  * Title bar: '🎬 IN-FLIGHT MEDIA SNIFFER' with an animated pulsating green radar dot.
  * Card 1: Video icon, 'defense_briefing_1080p.mp4 [48.2 MB]', status 'STREAM READY',
    with a vibrant neon green 'GRAB STREAM ⬇' pill button.
  * Card 2: Audio icon, 'intercept_audio_ch1.mp3 [4.1 MB]', with a cyan 'DOWNLOAD' button.
- Bottom Navigation Dock: 5 icons with 'VB' (globe with cyber shield) brightly illuminated
  in neon green with a subtle halo reflection.
Color palette: Obsidian Black (#07090E), Neon Green (#00FF9D), Cyan (#00F0FF), Crimson (#FF0055).
Style: Ultra-modern cybersecurity HUD, Figma 8k, Dribbble trending, crisp vector UI."
--------------------------------------------------------------------------------
'''

target_prompt3 = '--------------------------------------------------------------------------------\nPROMPT 3: INDIVIDUAL SCREEN -- RADIOZERO™ RF RADAR HUD (SCREEN 3)'
if target_prompt3 in content:
    content = content.replace(target_prompt3, prompt_2b.strip() + '\n\n' + target_prompt3)
    print('[+] Inserted PROMPT 2B for VEX Browser')
else:
    print('[-] Target prompt 3 marker not found')

# 3. Update Part 3 Screen 2 Compose specifications
old_compose_s2 = '''4. SCREEN 2: 'VexBrowserScreen.kt':
   - Custom top URL bar with AdGuard DoH security badge ('🛡️ DNS:ADG').
   - AndroidView embedding WebView with custom WebViewClient:
     * Disables persistent cookies and disk cache.
     * Hooks into 'UrlXRayEngine' to pre-screen every URL before loading.
   - Bottom bar with 'Wipe Cache Now' button that executes in-memory zeroing.'''

new_compose_s2 = '''4. SCREEN 2: 'VexBrowserScreen.kt' (VEX Browser Zero-Trace & Media Sniffer):
   - Custom Top Privacy HUD Bar:
     * TextField URL bar with leading SSL lock icon and trailing refresh/go button.
     * Green '🛡️ DNS:ADGUARD' badge and 'AUTO-VPN' pill indicating encrypted DoH routing.
     * Red 'Wipe Data' icon button with haptic feedback triggering instant memory flush.
   - AndroidView Embedding Isolated WebView:
     * Custom WebViewClient implementing 'shouldInterceptRequest' and 'doUpdateVisitedHistory'.
     * Hooks into 'VexBrowserEngine.sniffMedia(url)' to intercept in-flight video/audio streams:
       Regex matches: .mp4, .m3u8 (HLS), .mpd (DASH), .webm, .mov, .mp3, .aac, .flac, .jpg, .png.
     * WebSettings hardened: domStorageEnabled = false, databaseEnabled = false,
       cacheMode = WebSettings.LOAD_NO_CACHE, allowFileAccess = false.
     * DownloadListener attached to intercept direct downloads into app-private cache.
   - In-Flight Media Sniffer Bottom Drawer / Sheet:
     * Animated presence when 'VexBrowserEngine.detectedMediaList' is non-empty.
     * Header with glowing green pulsing radar indicator: '🎬 DETECTED MEDIA STREAMS'.
     * LazyColumn / items rendering each detected file with mime badge, filename, and 'GRAB' button.
   - Strict Auto-Wipe Lifecycle:
     * 'DisposableEffect' calling 'VexBrowserEngine.clearAllBrowserData()' on composition disposal
       (clearing CookieManager, WebStorage, cache, and detected media list).'''

if old_compose_s2 in content:
    content = content.replace(old_compose_s2, new_compose_s2)
    print('[+] Updated Part 3 Compose Screen 2')
else:
    print('[-] Part 3 Compose Screen 2 not found')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('[+] UIUX prompts updated successfully!')
