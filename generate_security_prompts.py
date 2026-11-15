# Python script to generate VEX_APPLICATION_SECURITY_AND_ANTI_HACKING_PROMPTS.txt
import os

output_path = r'C:\vex\VEX_APPLICATION_SECURITY_AND_ANTI_HACKING_PROMPTS.txt'

content = '''================================================================================
       VEX CYBER GUARD -- RUNTIME APPLICATION SELF-PROTECTION (RASP)
         MASTER PROMPTS TO SECURE AND HARDEN APPLICATIONS FROM HACKERS
================================================================================
Target Platform: Android 14+ (API 34-37), Kotlin 2.0, Native C++ NDK
Backend Stack: Python 3.12, FastAPI, WebSockets, Redis, SQLite/PostgreSQL
Standard Compliance: OWASP Mobile Top 10 (2024), MITRE ATT&CK for Mobile, NIST SP 800-163
Compatibility: Universal (Can be used with Antigravity, Cursor, Claude, or ChatGPT)
Author: VEX Security Core Research Labs (BPUT Hackathon Grand Finale)
================================================================================

EXECUTIVE OVERVIEW: WHY THIS DOCUMENT EXISTS
--------------------------------------------------------------------------------
A cybersecurity defense tool (like VEX) is the NUMBER ONE target for black-hat
attackers, reverse engineers, and malware authors. If hackers can hook, decompile,
tamper with, or debug VEX, they can neutralize its alarms and operate invisibly.

This document contains 10 production-grade MASTER IMPLEMENTATION PROMPTS designed
to build a military-grade Runtime Application Self-Protection (RASP) shield around
the application.

By feeding these prompts into an AI coding assistant (or implementing them
manually), your application gains:
1. Anti-Dynamic Hooking (Frida, Xposed, Substrate detection and neutralization)
2. Advanced Root & Cloaking Detection (Magisk, KernelSU, APatch)
3. Native Anti-Debugging & Ptrace Sealing (blocking jdb, GDB, lldb, and IDA Pro)
4. Cryptographic APK Signature Verification & Anti-Repackaging
5. Emulator, QEMU & Dual-Space Sandbox Isolation
6. Hardware-Backed Keystore Encryption (StrongBox / TEE)
7. TLS 1.3 Certificate Pinning & MitM Interception Defense
8. Strict Android IPC, Intent Spoofing & Deep-Link Hardening
9. Full-Mode R8 Bytecode Obfuscation & Sensitive String Encryption
10. Zero-Trust Backend API Rate-Limiting & Injection Defense
================================================================================


================================================================================
TABLE OF CONTENTS
================================================================================
PART 1: THE 10 MASTER SECURITY HARDENING PROMPTS
  - [PROMPT 1] Dynamic Hooking Defense: Frida, Xposed & Substrate Neutralizer
  - [PROMPT 2] Multi-Tier Root & Magisk/KernelSU Cloaking Detection
  - [PROMPT 3] Native Linux Anti-Debugging & Ptrace Sealing (C++ NDK)
  - [PROMPT 4] APK Integrity, Anti-Repackaging & Keystore Hash Verification
  - [PROMPT 5] Hardware Emulator, QEMU & Dual-Space Sandbox Detector
  - [PROMPT 6] TLS 1.3 Certificate Pinning & MitM Interception Sentry
  - [PROMPT 7] In-Memory Cryptographic Protection, FLAG_SECURE & String Purge
  - [PROMPT 8] Android IPC, BroadcastReceiver & Deep-Link Hardening
  - [PROMPT 9] ProGuard / R8 Full-Mode Obfuscation & Bytecode Scrambler
  - [PROMPT 10] Zero-Trust Backend API Armor (FastAPI, Rate Limiting, CSP)
PART 2: ACTIVE DEFENSE PROTOCOLS (WHAT TO DO WHEN ATTACK IS DETECTED)
PART 3: OWASP MOBILE TOP 10 (2024) VERIFICATION MATRIX
PART 4: JURY & AUDITOR DEFENSE BATTLE-CARD
================================================================================


================================================================================
PART 1: THE 10 MASTER SECURITY HARDENING PROMPTS
================================================================================

--------------------------------------------------------------------------------
[MASTER PROMPT 1: DYNAMIC HOOKING DEFENSE (FRIDA, XPOSED & SUBSTRATE)]
--------------------------------------------------------------------------------
"Act as a Principal Mobile Application Security Engineer specializing in RASP
(Runtime Application Self-Protection).

Implement a standalone, production-ready anti-hooking engine in Kotlin:
'./app/src/main/java/com/vex/security/hardening/AntiHookingEngine.kt'

The engine must detect and neutralize Frida, Xposed, LSPosed, Cydia Substrate,
and dynamic instrumentation frameworks using a 5-vector inspection protocol:

1. Memory Map Inspection (/proc/self/maps):
   - Parse '/proc/self/maps' line-by-line reading all loaded shared libraries.
   - Flag any memory region containing strings:
     ['frida-agent', 'frida-gadget', 'gadget.so', 'libfrida', 'xposed',
      'edxposed', 'sandhook', 'substrate', 'com.saurik.substrate'].
   - If found, raise 'TAMPER_EVENT_HOOKING_LIBRARY_LOADED'.

2. Default Frida Port & Socket Scanning:
   - Attempt non-blocking TCP socket connections to standard local Frida server
     ports: [27042, 27043, 27044, 27045].
   - If a connection succeeds on localhost (127.0.0.1) or returns a D-Bus reply,
     flag 'TAMPER_EVENT_FRIDA_SERVER_LISTENING'.

3. Named Pipes & Runtime Artifact Scan:
   - Check filesystem existence of named pipes and temporary inject files:
     ['/data/local/tmp/frida-*', '/data/local/tmp/re.frida.server',
      '/data/local/tmp/gadget*', '/system/lib/libxposed_art.so',
      '/system/lib64/libxposed_art.so', '/system/framework/XposedBridge.jar'].

4. Thread & Stack Trace Anomaly Detection:
   - Inspect active thread names via Thread.getAllStackTraces().keys.
   - Flag any thread containing: ['gmain', 'gdbus', 'gum-js-loop', 'frida'].
   - Inspect the current execution stack trace (Throwable().stackTrace) for:
     'de.robv.android.xposed.XposedBridge', 'me.weishu.epic', 'top.canyie.pine'.

5. Memory Scan for Frida Native Trampolines:
   - In C++ (or via JNI hook), inspect the prologue of critical libc functions
     (open, read, connect, ptrace, exit) for hook trampolines (e.g. ARM LDR PC,
     ARM64 MOV X16 / BR X16 instructions).

When any check triggers:
- Immediately invoke 'AntiTamperCoordinator.onTamperDetected(TamperType.DYNAMIC_HOOKING)'
- Cryptographically poison memory and cleanly terminate the application."
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
[MASTER PROMPT 2: ADVANCED MULTI-TIER ROOT & MAGISK/KERNELSU CLOAKING DETECTION]
--------------------------------------------------------------------------------
"Act as an Android Kernel & Root Exploit Security Specialist.

Implement a comprehensive, evasion-resistant root detection module:
'./app/src/main/java/com/vex/security/hardening/RootSentryManager.kt'

The detector must expose not just legacy root, but modern root solutions that
use kernel-level cloaking (Magisk Zygisk, Shamiko, KernelSU, APatch):

1. Binary & Path Traversal:
   - Check standard and hidden binary locations for 'su', 'busybox', 'magisk',
     'ksud', 'daemonsu':
     ['/system/bin/su', '/system/xbin/su', '/sbin/su', '/system/sd/xbin/su',
      '/system/bin/failsafe/su', '/data/local/xbin/su', '/data/local/bin/su',
      '/data/local/su', '/system/usr/we-need-root/su-backup', '/sbin/.magisk/'].
   - Do NOT just use 'File.exists()' (which Magisk hooks). Execute a raw shell
     process probe: 'which su', 'which magisk', and check exit codes.

2. Read-Write Mount Verification:
   - Read '/proc/mounts'.
   - Verify if any system partitions ('/system', '/system_root', '/vendor') are
     mounted as Read-Write ('rw'). Genuine production Android devices mount these
     strictly as Read-Only ('ro').

3. Package Manager & Cloaked App Verification:
   - Check for installed root managers even if their package names are randomized:
     Look for intent filters handling 'com.topjohnwu.magisk', 'com.noshufou.android.su',
     'eu.chainfire.supersu', 'me.weishu.kernelsu', 'org.lsposed.manager'.

4. Build Tags & Test-Keys:
   - Inspect 'android.os.Build.TAGS'. If it contains 'test-keys' instead of
     'release-keys', flag as a rooted or uncertified custom ROM.
   - Inspect 'android.os.Build.FINGERPRINT' for 'userdebug' or 'eng' builds.

5. Direct Execution of 'su' via ProcessBuilder:
   - Attempt spawning a child process running 'su -c id'.
   - If standard output contains 'uid=0(root)', root is unequivocally confirmed.

Provide a public property 'val isDeviceCompromised: StateFlow<Boolean>' and
emit security events to Room local forensic database."
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
[MASTER PROMPT 3: NATIVE ANTI-DEBUGGING & PTRACE SEALING (C++ NDK & KERNEL)]
--------------------------------------------------------------------------------
"Act as an Android NDK C++ Systems Security Engineer.

Implement a native Linux anti-debugging and ptrace protection layer:
File 1: './app/src/main/cpp/vex_security_core.cpp'
File 2: './app/src/main/java/com/vex/security/hardening/NativeAntiDebug.kt'

Hackers use GDB, lldb, IDA Pro, and Ghidra to attach to the Android zygote process,
pause execution, and manipulate CPU registers. Implement these defenses:

1. Self-Trace Lockdown (ptrace PTRACE_TRACEME):
   - In native C++, call 'ptrace(PTRACE_TRACEME, 0, 1, 0)'.
   - On Linux, a process can only be traced by ONE parent debugger. If VEX traces
     itself at process launch, any external debugger (lldb / gdb / frida) trying
     to call 'PTRACE_ATTACH' will fail with EPERM (Operation not permitted).
   - If 'ptrace(PTRACE_TRACEME, ...)' returns -1 on startup, an attacker is
     ALREADY attached! Trigger instant process suicide.

2. Kernel Status Inspection (/proc/self/status):
   - In a background native daemon thread, poll '/proc/self/status' every 500ms.
   - Parse the line: 'TracerPid:\t<PID>'.
   - In a normal unattached process, TracerPid is ALWAYS 0.
   - If TracerPid > 0, an unauthorized debugger is attached. Immediately exit.

3. Debugger Flag Checks in Java/Kotlin:
   - Call 'android.os.Debug.isDebuggerConnected()'.
   - Call 'android.os.Debug.waitingForDebugger()'.
   - Check 'context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0'.
     If true, the APK was recompiled with debugging enabled!

4. Breakpoint Detection (Software Interrupt Scan):
   - In critical native functions, calculate a checksum of the function opcode
     bytes in memory.
   - If a hacker sets a software breakpoint, the opcode at the target address
     is overwritten with '0xBKPT' (ARM: 0xE1200070, ARM64: 0xD4200000).
   - If checksum diverges or contains breakpoint opcodes, abort execution.

5. Timing Anomaly Checks:
   - Measure execution time of critical validation routines using 'clock_gettime(CLOCK_MONOTONIC)'.
   - If a 2ms cryptographic routine takes > 150ms, a human reverse engineer is
     stepping through the code in a debugger! Flag timing attack."
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
[MASTER PROMPT 4: APK INTEGRITY, ANTI-REPACKAGING & SIGNATURE VERIFICATION]
--------------------------------------------------------------------------------
"Act as an Application Integrity & Anti-Piracy Specialist.

Implement a runtime signature and repackaging verification system:
'./app/src/main/java/com/vex/security/hardening/ApkIntegrityVerifier.kt'

Hackers use APKTool to decompile apps into Smali, remove license/threat checks,
and re-sign with a generic debug keystore. Prevent this completely:

1. Cryptographic Certificate SHA-256 Fingerprint Check:
   - At runtime, query Android's PackageManager:
     * For API 28+: 'packageManager.getPackageInfo(packageName, GET_SIGNING_CERTIFICATES).signingInfo'
     * For API < 28: 'packageManager.getPackageInfo(packageName, GET_SIGNATURES).signatures'
   - Extract the public X.509 signing certificate byte array.
   - Compute its SHA-256 hash using MessageDigest.getInstance('SHA-256').
   - Compare against your known production release keystore fingerprint (hardcoded as
     an obfuscated byte array, NOT a plain string).
   - If the hash mismatches (e.g. Android Debug Key or hacker's certificate), the app
     is repackaged!

2. APK Installer Source Verification:
   - Check 'packageManager.getInstallerPackageName(packageName)'.
   - Ensure the installer is an authorized app marketplace ('com.android.vending',
     authorized enterprise MDM) or flag for user confirmation if sideloaded.

3. Classes.dex CRC / Checksum Audit:
   - Retrieve the path to 'context.packageCodePath' (the base.apk file).
   - Open base.apk as a ZipFile and extract the CRC32 checksum of 'classes.dex'.
   - Compare against the release build checksum. If Smali code was modified, the
     classes.dex checksum will completely mismatch!

4. Dynamic Google Play Integrity API Handshake:
   - Provide an interface to request an attestation token from the Play Integrity API.
   - Send token to the FastAPI backend to verify:
     * 'appLicensingVerdict': LICENSED
     * 'appRecognitionVerdict': PLAY_RECOGNIZED
     * 'deviceRecognitionVerdict': MEETS_STRONG_INTEGRITY."
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
[MASTER PROMPT 5: HARDWARE EMULATOR, QEMU & DUAL-SPACE SANDBOX DETECTOR]
--------------------------------------------------------------------------------
"Act as a Virtualization & Sandbox Security Researcher.

Implement an advanced emulator and sandbox detection engine:
'./app/src/main/java/com/vex/security/hardening/EmulatorDetector.kt'

Hackers and automated malware analysis sandboxes (like Cuckoo or VirusTotal) run
apps inside Android emulators (QEMU, Genymotion, BlueStacks, Nox) or container
apps (Parallel Space, VirtualXposed, Dual Space). Implement detection:

1. Build & Hardware Property Fingerprinting:
   - Check 'Build.FINGERPRINT': Contains 'generic', 'vbox', 'test-keys', 'sdk_gphone'.
   - Check 'Build.MODEL': Contains 'google_sdk', 'Emulator', 'Android SDK built for x86'.
   - Check 'Build.MANUFACTURER': Contains 'Genymotion', 'unknown', 'nox'.
   - Check 'Build.HARDWARE': Contains 'goldfish', 'ranchu', 'vbox86'.
   - Check 'Build.PRODUCT': Contains 'sdk', 'google_sdk', 'vbox86p', 'nox'.
   - Check 'Build.BOARD': Contains 'goldfish'.

2. QEMU Drivers & Virtual Devices:
   - Check filesystem for virtual drivers:
     ['/dev/socket/qemud', '/dev/qemu_pipe', '/system/lib/libc_malloc_debug_qemu.so',
      '/sys/qemu_trace', '/system/bin/nox-prop', '/dev/vboxguest', '/dev/vboxuser'].

3. Telephony & Sensor Absence Checks:
   - Genuine phones have cellular hardware and multiple IMU sensors.
   - Query TelephonyManager for Operator Name: Emulators report 'Android' or 'null'.
   - Query SensorManager: Emulators frequently have 0 step detectors, 0 light sensors,
     or return static, unchanging 0.0 values on accelerometer.
   - Check Battery State: Emulators often report 100% battery, temperature 0°C, and AC charging.

4. Dual-Space & Container Isolation Checks:
   - VirtualXposed and Parallel Space run apps within their own virtual container UID.
   - Check 'context.filesDir.absolutePath': If the path contains multiple package
     names (e.g. '/data/data/com.lbe.parallel/parallel/com.vex.security'), the app
     is trapped inside a container!
   - Compare 'Process.myUid()' against the expected single app package UID."
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
[MASTER PROMPT 6: TLS 1.3 CERTIFICATE PINNING & MITM INTERCEPTION SENTRY]
--------------------------------------------------------------------------------
"Act as a Network Security & Cryptographic Protocol Architect.

Implement a bulletproof TLS certificate pinning and proxy interception defense:
File 1: './app/src/main/java/com/vex/security/network/SecureNetworkClient.kt'
File 2: './app/src/main/res/xml/network_security_config.xml'

Hackers use Burp Suite, Charles Proxy, and mitmproxy with custom root CA certificates
to intercept, read, and manipulate API calls between the phone and backend:

1. OkHttp CertificatePinner Implementation:
   - Configure OkHttpClient with CertificatePinner using SHA-256 public key hashes:
     ```kotlin
     val certificatePinner = CertificatePinner.Builder()
         .add("api.vexguard.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=") // Primary Key
         .add("api.vexguard.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=") // Backup Pin
         .build()
     ```
   - Pin ONLY the Subject Public Key Info (SPKI), allowing server cert renewals
     without breaking existing app installs.

2. Android Network Security Config (Strict Enforcement):
   - In 'network_security_config.xml', enforce:
     * 'cleartextTrafficPermitted="false"' across all domains.
     * Trust ONLY system CAs: '<certificates src="system" />'.
     * Explicitly EXCLUDE '<certificates src="user" />' so user-installed Burp Suite
       CA certificates are completely ignored by the OS network stack.

3. System Proxy Detection:
   - Check system properties:
     * 'System.getProperty("http.proxyHost")'
     * 'System.getProperty("http.proxyPort")'
   - Check active network capabilities:
     * 'connectivityManager.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_VPN)'
   - If a proxy or unauthorized VPN is detected while attempting secure telemetry,
     flag 'NETWORK_INTERCEPTION_PROXY_DETECTED'.

4. Hostname Verification & TLS 1.3 Only:
   - Enforce 'ConnectionSpec.RESTRICTED_TLS' supporting only TLS 1.3 and modern
     AES-GCM/CHACHA20 cipher suites. Disallow legacy TLS 1.0, 1.1, and 1.2."
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
[MASTER PROMPT 7: IN-MEMORY CRYPTOGRAPHIC PROTECTION, FLAG_SECURE & STRING PURGE]
--------------------------------------------------------------------------------
"Act as a Cryptographic Data Protection & Memory Forensics Engineer.

Implement a zero-leak in-memory security manager:
'./app/src/main/java/com/vex/security/hardening/MemorySecurityManager.kt'

Hackers dump RAM via Frida or Android Debug Bridge (gcore / memdump) to extract
API keys, session tokens, and encryption secrets:

1. Hardware-Backed Keystore (StrongBox / TEE):
   - Generate Master Encryption Keys inside Android KeyStore:
     KeyGenParameterSpec with AES/GCM/NoPadding, key size 256 bits.
   - Enforce 'setIsStrongBoxBacked(true)' if available on device (dedicated hardware
     security silicon like Titan M or Knox Vault).
   - Enforce 'setUserAuthenticationRequired(false)' for autonomous services, but
     require Biometric Prompt for manual vault unlocking.

2. Mutable Char Arrays Over Immutable Strings:
   - In Java/Kotlin, 'String' is immutable and remains in the garbage collection
     heap for minutes, easily dumpable by hackers.
   - Store passwords, keys, and tokens as 'CharArray' or 'ByteArray'.
   - Implement an explicit wiping routine:
     ```kotlin
     fun wipe(data: CharArray) { Arrays.fill(data, '\\u0000') }
     fun wipe(data: ByteArray) { Arrays.fill(data, 0.toByte()) }
     ```
   - Wipe the array in a 'finally' block the instant the cryptographic operation finishes!

3. Screenshot & Task Switcher Protection (FLAG_SECURE):
   - In 'MainActivity.kt', configure:
     'window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)'
   - Prevents screen recorders, remote screenshot daemons, and recent app thumbnail
     caching from capturing sensitive security screens.

4. RAM-Only Ephemeral Cache (No Disk Spills):
   - Ensure decrypted alerts and honey-token seeds are marked with transient or
     stored purely in memory-bound StateFlow variables."
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
[MASTER PROMPT 8: ANDROID IPC, BROADCASTRECEIVER & DEEP-LINK HARDENING]
--------------------------------------------------------------------------------
"Act as an Android Inter-Process Communication (IPC) & Application Security Auditor.

Audit and harden all application components in AndroidManifest.xml and Kotlin:
'./app/src/main/AndroidManifest.xml'

Attackers exploit exported activities, intent spoofing, and insecure broadcast
receivers to inject malicious commands without user interaction:

1. Exported Component Audit:
   - Strictly set 'android:exported="false"' on ALL Activities, Services, and
     BroadcastReceivers unless explicitly designed for public system intents.
   - For components handling system intents (e.g. SMS_RECEIVED, USB_DEVICE_ATTACHED),
     enforce mandatory system permissions:
     * SMS Receiver: 'android:permission="android.permission.BROADCAST_SMS"'
     * Boot Receiver: 'android:permission="android.permission.RECEIVE_BOOT_COMPLETED"'.

2. Custom Signature-Level Permissions:
   - For internal communication between VEX services:
     Define a custom permission with 'android:protectionLevel="signature"':
     ```xml
     <permission
         android:name="com.vex.security.permission.INTERNAL_COMMUNICATION"
         android:protectionLevel="signature" />
     ```
   - Only apps signed with the EXACT SAME developer certificate can send or receive
     these intents. Rogue apps on the phone are blocked by the OS kernel.

3. Intent Payload Sanitization:
   - Validate and sanitize all extras:
     Never pass raw, unvalidated Intent extras to 'startActivity()' or 'startService()'
     (prevents Intent Redirection attacks).
   - Use 'intent.filterEquals()' and explicit class bindings:
     'Intent(context, TargetActivity::class.java)' instead of implicit action strings.

4. Deep Link & URL Scheme Defense:
   - If using custom schemes (e.g. 'vex://open'), validate host, path, and query params.
   - Reject arbitrary redirects, file URLs ('file://'), or JavaScript injection."
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
[MASTER PROMPT 9: PROGUARD / R8 FULL-MODE OBFUSCATION & STRING SCRAMBLING]
--------------------------------------------------------------------------------
"Act as a Code Obfuscation & Binary Reverse-Engineering Defense Engineer.

Create the complete, production-grade ProGuard / R8 configuration:
File 1: './app/proguard-rules.pro'
File 2: './app/src/main/java/com/vex/security/hardening/StringScrambler.kt'

When a hacker opens an un-obfuscated APK in JADX, they can read all variable names,
logic flow, and hardcoded API endpoints. Neutralize decompilers:

1. Comprehensive 'proguard-rules.pro':
   - Enable full-mode R8 shrinking, class repackaging, and name mangling:
     ```pro
     -repackageclasses 'com.vex.security.a'
     -allowaccessmodification
     -flattenpackagehierarchy
     -overloadaggressively
     -repackageclasses ''
     -optimizations !class/merging/*,!code/allocation/variable

     # Strip debug information completely
     -renamesourcefileattribute 'SourceFile'
     -keepattributes SourceFile,LineNumberTable
     -assumenosideeffects class android.util.Log {
         public static *** d(...);
         public static *** v(...);
         public static *** i(...);
     }
     ```

2. Compile-Time Sensitive String Scrambler:
   - Never store critical strings (API endpoints, public key pins, honey-token seeds)
     as plaintext string literals in bytecode.
   - Implement an automated XOR + bit-shift string obfuscator in 'StringScrambler.kt':
     Each sensitive string is stored as a randomized byte array with an encrypted key
     and dynamically reconstructed in memory only during execution, then immediately wiped."
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
[MASTER PROMPT 10: ZERO-TRUST BACKEND API ARMOR (FASTAPI, RATE-LIMITING & CSP)]
--------------------------------------------------------------------------------
"Act as a Backend Security Architect specializing in FastAPI and API Hardening.

Harden the FastAPI SOC command backend against automated attacks, SQL injection,
and WebSocket denial-of-service:
'./backend/security_hardening.py' and './backend/main.py'

Attackers will scan the backend with Nikto, Burp Suite, SQLMap, and WebSocket
bombing scripts. Implement production armor:

1. SlowAPI Dynamic Rate-Limiting:
   - Integrate SlowAPI with in-memory or Redis storage.
   - Enforce limits:
     * General REST API: 60 requests per minute per IP.
     * Event Ingestion endpoint: 120 requests per minute per device token.
     * Auth/Registration endpoint: 5 attempts per 5 minutes (blocking brute force).

2. Security Headers (OWASP Recommended):
   - Add middleware injecting strict HTTP headers:
     * 'Strict-Transport-Security: max-age=31536000; includeSubDomains; preload'
     * 'X-Content-Type-Options: nosniff'
     * 'X-Frame-Options: DENY'
     * 'Content-Security-Policy: default-src \\'self\\'; script-src \\'self\\''
     * 'Referrer-Policy: strict-origin-when-cross-origin'

3. Strict Pydantic Schema Validation:
   - Forbid arbitrary extra fields in JSON bodies: 'model_config = ConfigDict(extra=\"forbid\")'.
   - Validate data types, lengths, and regex patterns to prevent SQL Injection
     and Path Traversal attacks.

4. WebSocket Authentication & Heartbeat Flood Protection:
   - Enforce cryptographic device tokens in WebSocket query params.
   - Disconnect connections sending > 10 messages per second (preventing server memory leaks).
   - Use non-blocking async handlers with uvloop."
================================================================================


================================================================================
PART 2: ACTIVE DEFENSE PROTOCOLS (WHAT TO DO WHEN TAMPER IS DETECTED)
================================================================================

When any RASP check detects tampering (Frida, Root, Debugger, Repackaging):
DO NOT simply display a dialog saying "You are rooted" — hackers easily hook that.

Implement the VEX Silent Active Defense Strategy:
1. Silent Forensic Logging: Cryptographically sign a tamper event and queue it for
   immediate dispatch to the FastAPI Web SOC backend.
2. Honey-Token Poisoning: Swap legitimate cryptographic keys or API endpoints with
   bogus honey-tokens ('attacker-trapped-by-vex.internal'). The hacker wastes hours
   reverse-engineering decoy endpoints.
3. Cryptographic Memory Zeroing: Zero out all sensitive data structures, wipe
   WebStorage, and invalidate active session tokens.
4. Graceful Process Termination: Call native exit:
   'android.os.Process.killProcess(android.os.Process.myPid())' or 'exit(0)' via C++
   to prevent stack unwinding or exception catch interception.


================================================================================
PART 3: OWASP MOBILE TOP 10 (2024) VERIFICATION MATRIX
================================================================================

| OWASP Mobile Risk | Vulnerability Description | VEX Hardening Solution |
| :--- | :--- | :--- |
| **M1: Improper Credential Usage** | Hardcoded API keys or plaintext credentials. | Hardware StrongBox Keystore + XOR String Scrambler (Prompt 7, 9). |
| **M2: Inadequate Supply Chain Security** | Malicious third-party SDK dependencies. | Minimal dependencies + strict SHA-256 certificate verification (Prompt 4). |
| **M3: Insecure Authentication / Authorization** | Weak session management or offline bypass. | Hardware-backed Biometric prompt + JWT tokens (Prompt 7, 10). |
| **M4: Insufficient Input / Output Validation** | Intent injection, SQLi, and path traversal. | Signature permissions + strict Pydantic models (Prompt 8, 10). |
| **M5: Insecure Communication** | Cleartext HTTP or fake SSL certificates. | TLS 1.3 only + OkHttp Certificate Pinning (Prompt 6). |
| **M6: Inadequate Privacy Controls** | Leaking sensitive data to logs or clipboard. | FLAG_SECURE + in-memory CharArray wiping (Prompt 7). |
| **M7: Insufficient Binary Protections** | Reverse engineering via JADX, APKTool, IDA. | R8 Full-Mode Obfuscation + Native Anti-Debug (Prompt 3, 9). |
| **M8: Security Misconfiguration** | Debuggable flags left on, wide permissions. | ProGuard release strip + exported="false" audit (Prompt 8, 9). |
| **M9: Insecure Data Storage** | Plaintext SQLite or SharedPrefs storage. | SQLCipher encrypted Room database + zero-trace RAM purge (Prompt 7). |
| **M10: Insufficient Cryptography** | Weak algorithms like MD5, SHA-1, or DES. | AES-256-GCM, HMAC-SHA256, and ECC curve verification (Prompt 4, 7). |


================================================================================
PART 4: JURY & AUDITOR DEFENSE BATTLE-CARD
================================================================================

Judge Question:
"Since your app is a security scanner, how do you prevent hackers from reverse-
engineering VEX and modifying your code?"

Your 25-Second Bulletproof Response:
"Judges, VEX is protected by an active Runtime Application Self-Protection (RASP)
architecture that operates at the kernel, native C++, and bytecode layers:

1. Native Self-Trace: At process launch, our C++ core calls ptrace(PTRACE_TRACEME),
   locking the process so debuggers like GDB, lldb, or IDA Pro cannot attach.
2. Anti-Hooking Sentry: We continuously inspect /proc/self/maps and local socket ports,
   instantly neutralizing dynamic instrumentation frameworks like Frida and Xposed.
3. Cryptographic Signature Seal: We verify the SHA-256 fingerprint of our signing
   certificate at runtime. If an attacker decompiles and repackages VEX using APKTool,
   the app detects the signature mismatch, zeroes out in-memory secrets, and terminates!

We don't just protect the user's phone; we protect VEX itself to military specifications."

================================================================================
             END OF VEX APPLICATION SECURITY & ANTI-HACKING SUITE
================================================================================
'''

with open(output_path, 'w', encoding='utf-8') as f:
    f.write(content)

print(f"[+] Successfully generated {output_path} ({len(content)} bytes)")
