package com.vex.security.ui.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.vex.security.browser.AdGuardDnsEngine
import com.vex.security.browser.UrlSafetyCheck
import com.vex.security.browser.VexAdBlocker
import com.vex.security.browser.VexBrowserEngine
import com.vex.security.repository.ThreatRepository
import com.vex.security.utils.DeviceIdentity
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VexBrowserScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var inputUrl by remember { mutableStateOf("") }
    var currentUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loadProgress by remember { mutableIntStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var blockedAdsCount by remember { mutableIntStateOf(VexBrowserEngine.sessionBlockedAds) }
    var blockedThreatsCount by remember { mutableIntStateOf(VexBrowserEngine.sessionBlockedThreats) }
    var blockedThreat by remember { mutableStateOf<UrlSafetyCheck?>(null) }
    var showLaunchpad by remember { mutableStateOf(true) }
    var showDnsInspector by remember { mutableStateOf(false) }

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // Automatically activate AdGuard Custom DNS (dns.adguard.com) when visiting VEX Browser
    LaunchedEffect(Unit) {
        AdGuardDnsEngine.activate(context)
    }

    // Automatic zero-trace cleanup when leaving or closing this screen
    DisposableEffect(Unit) {
        onDispose {
            try {
                webViewInstance?.stopLoading()
                webViewInstance?.clearHistory()
                webViewInstance?.clearCache(true)
                webViewInstance?.destroy()
                VexBrowserEngine.clearAllBrowserData(context)
                AdGuardDnsEngine.deactivate()
            } catch (_: Exception) {}
        }
    }

    // Handle system back navigation inside browser
    BackHandler(enabled = !showLaunchpad || blockedThreat != null) {
        if (blockedThreat != null) {
            blockedThreat = null
            showLaunchpad = true
        } else if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            showLaunchpad = true
        }
    }

    fun navigateTo(destination: String) {
        val resolved = VexBrowserEngine.resolveUrlOrSearch(destination)
        inputUrl = resolved

        // Evaluate security of target URL before loading
        val safety = VexBrowserEngine.evaluateUrl(resolved, context)
        if (safety.isDangerous) {
            VexBrowserEngine.sessionBlockedThreats++
            blockedThreatsCount = VexBrowserEngine.sessionBlockedThreats
            blockedThreat = safety
            showLaunchpad = false

            // Report threat to SOC Dashboard & backend in real-time
            scope.launch {
                val deviceId = DeviceIdentity.getDeviceId(context)
                ThreatRepository.sendThreatEvent(
                    deviceId = deviceId,
                    threatType = "BROWSER_MALICIOUS_SITE",
                    riskScore = safety.riskScore,
                    riskLevel = safety.riskLevel,
                    action = "BLOCKED",
                    status = "OPEN",
                    indicators = safety.indicators,
                    source = "VEX_BROWSER",
                    explanation = safety.explanationReasons.joinToString("; "),
                    recommendation = safety.recommendation
                )
            }
        } else {
            blockedThreat = null
            showLaunchpad = false
            webViewInstance?.loadUrl(resolved)
        }
    }

    fun purgeBrowserSession() {
        webViewInstance?.stopLoading()
        webViewInstance?.clearHistory()
        webViewInstance?.clearCache(true)
        webViewInstance?.clearFormData()
        VexBrowserEngine.clearAllBrowserData(context)
        VexBrowserEngine.resetSessionCounters()
        AdGuardDnsEngine.resetStats()
        blockedAdsCount = 0
        blockedThreatsCount = 0
        blockedThreat = null
        showLaunchpad = true
        inputUrl = ""
        currentUrl = ""
        Toast.makeText(context, "⚡ Zero-Trace Wipe Complete! All data purged.", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            // Pinned Cyber Security & Header Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF071118))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // First Row: Branding & Shield Status & Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // VB Badge
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00FFE0).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFF00FFE0), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VB",
                                color = Color(0xFF00FFE0),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column {
                            Text(
                                text = "VEX BROWSER",
                                color = Color(0xFFFFFFFF),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00FF66))
                                )
                                Text(
                                    text = "SHIELD ACTIVE • INCOGNITO",
                                    color = Color(0xFF00FF66),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Top Right Action Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Purge / Wipe Button
                        IconButton(
                            onClick = { purgeBrowserSession() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFF3366).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFFF3366).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Zero-Trace Purge",
                                tint = Color(0xFFFF3366),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Refresh Button
                        IconButton(
                            onClick = {
                                if (!showLaunchpad && blockedThreat == null) {
                                    webViewInstance?.reload()
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0A2530))
                                .border(1.dp, Color(0xFF005566), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reload",
                                tint = Color(0xFF00FFE0),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // AdGuard Custom DNS Live Status Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF042018))
                        .border(1.dp, Color(0xFF00FF99).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { showDnsInspector = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00FF99))
                        )
                        Text(
                            text = "DNS: dns.adguard.com",
                            color = Color(0xFF00FF99),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${AdGuardDnsEngine.latencyMs.intValue}ms",
                            color = Color(0xFF00FFE0),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF004D36))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DNS BLOCKED: ${AdGuardDnsEngine.blockedByDns.intValue}",
                                color = Color(0xFF00FF99),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Second Row: Threat Protection Counter Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Blocked Ads Badge
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0A1F28))
                            .border(1.dp, Color(0xFF005566), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = Color(0xFF00FFE0),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "ADS: $blockedAdsCount",
                                color = Color(0xFF00FFE0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }

                    // Blocked Threats Badge
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (blockedThreatsCount > 0) Color(0xFF330A14) else Color(0xFF0A1F28))
                            .border(
                                1.dp,
                                if (blockedThreatsCount > 0) Color(0xFFFF3366) else Color(0xFF005566),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (blockedThreatsCount > 0) Color(0xFFFF3366) else Color(0xFF80A0B0),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "THREATS: $blockedThreatsCount",
                                color = if (blockedThreatsCount > 0) Color(0xFFFF3366) else Color(0xFF80A0B0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }

                    // Permission Leak Status Badge
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0A1F28))
                            .border(1.dp, Color(0xFF00FF66).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = null,
                                tint = Color(0xFF00FF66),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "PERMS: OFF",
                                color = Color(0xFF00FF66),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Third Row: Navigation controls & Address/Search Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Home Button
                    IconButton(
                        onClick = {
                            showLaunchpad = true
                            blockedThreat = null
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (showLaunchpad) Color(0xFF00FFE0).copy(alpha = 0.2f) else Color(0xFF0A2530))
                            .border(
                                1.dp,
                                if (showLaunchpad) Color(0xFF00FFE0) else Color(0xFF005566),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (showLaunchpad) Color(0xFF00FFE0) else Color(0xFF7090A0),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Back Button
                    IconButton(
                        onClick = {
                            if (blockedThreat != null) {
                                blockedThreat = null
                                showLaunchpad = true
                            } else if (webViewInstance?.canGoBack() == true) {
                                webViewInstance?.goBack()
                            } else {
                                showLaunchpad = true
                            }
                        },
                        enabled = canGoBack || blockedThreat != null || !showLaunchpad,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0A2530))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (canGoBack || blockedThreat != null || !showLaunchpad) Color(0xFF00FFE0) else Color(0xFF304550),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Forward Button
                    IconButton(
                        onClick = {
                            if (webViewInstance?.canGoForward() == true) {
                                webViewInstance?.goForward()
                            }
                        },
                        enabled = canGoForward,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0A2530))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Forward",
                            tint = if (canGoForward) Color(0xFF00FFE0) else Color(0xFF304550),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Address Input Field
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        placeholder = {
                            Text(
                                text = "Search or type URL...",
                                color = Color(0xFF7090A0),
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = if (blockedThreat != null) Icons.Default.Dangerous else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (blockedThreat != null) Color(0xFFFF3366) else Color(0xFF00FFE0),
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            if (inputUrl.isNotBlank()) {
                                IconButton(
                                    onClick = { navigateTo(inputUrl) }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Go",
                                        tint = Color(0xFF00FFE0),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = { navigateTo(inputUrl) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FFE0),
                            unfocusedBorderColor = Color(0xFF005566),
                            focusedTextColor = Color(0xFFFFFFFF),
                            unfocusedTextColor = Color(0xFFFFFFFF),
                            cursorColor = Color(0xFF00FFE0)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Cyber Progress Bar
                if (isLoading && !showLaunchpad && blockedThreat == null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { loadProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF00FFE0),
                        trackColor = Color(0xFF0A2530)
                    )
                }
            }
        }
    ) { innerPadding ->
        // Main Viewport Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF000305))
        ) {
            // Layer 1: Persistent Sandboxed WebView
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewInstance = this

                        // Sandboxed & Ephemeral Settings
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.setGeolocationEnabled(false)
                        settings.allowFileAccess = false
                        settings.allowContentAccess = false
                        settings.setSupportMultipleWindows(false)
                        settings.javaScriptCanOpenWindowsAutomatically = false
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE

                        // Disable third party cookies completely
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, false)

                        // Permission Interceptor: Bypass & Deny all intrusive web prompts
                        webChromeClient = object : WebChromeClient() {
                            override fun onGeolocationPermissionsShowPrompt(
                                origin: String?,
                                callback: android.webkit.GeolocationPermissions.Callback?
                            ) {
                                // Auto-bypass: Never prompt user, immediately deny location access
                                callback?.invoke(origin, false, false)
                            }

                            override fun onPermissionRequest(request: PermissionRequest?) {
                                // Auto-bypass: Deny camera, microphone, device sensors
                                request?.deny()
                            }

                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                loadProgress = newProgress
                            }
                        }

                        // Network & Ad/Malware Shield Interceptor
                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                val reqUrl = request?.url?.toString() ?: ""
                                val host = request?.url?.host
                                val isDnsBlocked = AdGuardDnsEngine.isBlockedByAdGuardDns(host)
                                val isAdBlocked = VexAdBlocker.isBlocked(reqUrl)

                                if (isDnsBlocked || isAdBlocked) {
                                    VexBrowserEngine.sessionBlockedAds++
                                    blockedAdsCount = VexBrowserEngine.sessionBlockedAds
                                    // Intercept & block: return empty stream
                                    return WebResourceResponse(
                                        "text/plain",
                                        "UTF-8",
                                        ByteArrayInputStream(ByteArray(0))
                                    )
                                }
                                return super.shouldInterceptRequest(view, request)
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val targetUrl = request?.url?.toString() ?: ""
                                val safety = VexBrowserEngine.evaluateUrl(targetUrl, context)

                                if (safety.isDangerous) {
                                    VexBrowserEngine.sessionBlockedThreats++
                                    blockedThreatsCount = VexBrowserEngine.sessionBlockedThreats
                                    blockedThreat = safety
                                    scope.launch {
                                        val deviceId = DeviceIdentity.getDeviceId(context)
                                        ThreatRepository.sendThreatEvent(
                                            deviceId = deviceId,
                                            threatType = "BROWSER_MALICIOUS_SITE",
                                            riskScore = safety.riskScore,
                                            riskLevel = safety.riskLevel,
                                            action = "BLOCKED",
                                            status = "OPEN",
                                            indicators = safety.indicators,
                                            source = "VEX_BROWSER",
                                            explanation = safety.explanationReasons.joinToString("; "),
                                            recommendation = safety.recommendation
                                        )
                                    }
                                    return true
                                }

                                // Disallow non-web protocol schemes safely
                                if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                                    return true
                                }

                                return false
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                if (url != null) {
                                    val safety = VexBrowserEngine.evaluateUrl(url, context)
                                    if (safety.isDangerous) {
                                        view?.stopLoading()
                                        VexBrowserEngine.sessionBlockedThreats++
                                        blockedThreatsCount = VexBrowserEngine.sessionBlockedThreats
                                        blockedThreat = safety
                                        return
                                    }
                                }
                                isLoading = true
                                currentUrl = url ?: ""
                                inputUrl = url ?: ""
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                canGoBack = view?.canGoBack() == true
                                canGoForward = view?.canGoForward() == true
                            }
                        }
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                }
            )

            // Layer 2: Launchpad Screen Overlay
            if (showLaunchpad && blockedThreat == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF03141C),
                                    Color(0xFF01080C),
                                    Color(0xFF000305)
                                )
                            )
                        )
                ) {
                    LaunchpadScreen(
                        onSelectBookmark = { destination ->
                            navigateTo(destination)
                        },
                        onOpenDnsInspector = { showDnsInspector = true }
                    )
                }
            }

            // Layer 3: Malicious Threat Block Overlay
            if (blockedThreat != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E050B),
                                    Color(0xFF0A0306),
                                    Color(0xFF000305)
                                )
                            )
                        )
                ) {
                    BlockedThreatScreen(
                        threat = blockedThreat!!,
                        onBackToSafety = {
                            blockedThreat = null
                            showLaunchpad = true
                        },
                        onPurgeSession = { purgeBrowserSession() }
                    )
                }
            }

            // Layer 4: AdGuard Custom DNS Inspector Modal
            if (showDnsInspector) {
                AdGuardDnsInspectorDialog(onDismiss = { showDnsInspector = false })
            }
        }
    }
}

/**
 * Cyber Shield Malicious Site Block Overlay
 */
@Composable
fun BlockedThreatScreen(
    threat: UrlSafetyCheck,
    onBackToSafety: () -> Unit,
    onPurgeSession: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // Glowing Red Danger Shield Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3366).copy(alpha = 0.15f))
                    .border(2.dp, Color(0xFFFF3366), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Threat Blocked",
                    tint = Color(0xFFFF3366),
                    modifier = Modifier.size(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "THREAT NEUTRALIZED",
                color = Color(0xFFFF3366),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Text(
                text = "MALICIOUS SITE BLOCKED BY VEX SHIELD",
                color = Color(0xFFFFFFFF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "VEX Browser detected a high-risk malicious resource and terminated navigation before any script, exploit payload, or phishing form could execute.",
                color = Color(0xFF80A0B0),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Blocked URL Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E050B)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFFF3366), Color(0xFF800020))))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "BLOCKED DESTINATION:",
                        color = Color(0xFFFF3366),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = threat.url,
                        color = Color(0xFFFFFFFF),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Threat Metrics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1F28)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF005566), Color(0xFF0A2530))))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRIMARY THREAT CLASSIFICATION",
                            color = Color(0xFF80A0B0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFF3366).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFFFF3366), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SCORE: ${threat.riskScore} (${threat.riskLevel})",
                                color = Color(0xFFFF3366),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Text(
                        text = threat.reasonTitle,
                        color = Color(0xFFFFFFFF),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(color = Color(0xFF16323F), thickness = 1.dp)

                    // Indicator Tags
                    Text(
                        text = "TRIGGERED INDICATORS:",
                        color = Color(0xFF80A0B0),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        threat.indicators.forEach { ind ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1A1218))
                                    .border(1.dp, Color(0xFFFF7799), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = ind,
                                    color = Color(0xFFFF7799),
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    if (threat.explanationReasons.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        threat.explanationReasons.forEach { reason ->
                            Text(
                                text = "• $reason",
                                color = Color(0xFFBACCD6),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onBackToSafety,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFE0)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color(0xFF000305),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RETURN TO SAFETY (LAUNCHPAD)",
                    color = Color(0xFF000305),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onPurgeSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFFF3366), Color(0xFFFF3366))))
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = Color(0xFFFF3366),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PURGE BROWSER DATA & FLUSH CACHE",
                    color = Color(0xFFFF3366),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Launchpad & Zero-Permission Quick Access Screen
 */
@Composable
fun LaunchpadScreen(
    onSelectBookmark: (String) -> Unit,
    onOpenDnsInspector: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Hero Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1F28)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF00FFE0), Color(0xFF005566))))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF00FFE0),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "VEX PRIVATE BROWSER (VB)",
                            color = Color(0xFF00FFE0),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "High-Security Ephemeral Browser • Zero Friction • Built-in Cyber Shield",
                        color = Color(0xFFFFFFFF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "VEX Browser enforces strict privacy: zero login required, automatic bypass/denial of location & device permissions, real-time ad/crypto-miner stripping, and complete cache annihilation upon exit.",
                        color = Color(0xFF80A0B0),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // AdGuard Custom DNS Active Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDnsInspector() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF042018)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(Color(0xFF00FF99), Color(0xFF005533)))
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00FF99))
                            )
                            Text(
                                text = "CUSTOM DNS: dns.adguard.com",
                                color = Color(0xFF00FF99),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF004D36))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = Color(0xFF00FF99),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Text(
                        text = "Primary Anycast IP: 94.140.14.14 • 94.140.14.15 (AdGuard Default)\nAll queries resolved via DoH (DNS-over-HTTPS). Ads, trackers, and malicious hosts resolve to 0.0.0.0 and are blocked before network dispatch.",
                        color = Color(0xFF90C0A8),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "PING: ${AdGuardDnsEngine.latencyMs.intValue}ms",
                                color = Color(0xFF00FFE0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "QUERIES: ${AdGuardDnsEngine.totalQueries.intValue}",
                                color = Color(0xFFE0E0E0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "BLOCKED: ${AdGuardDnsEngine.blockedByDns.intValue}",
                                color = Color(0xFFFF6688),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "INSPECT >",
                            color = Color(0xFF00FF99),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Privacy Guarantee Highlights
        item {
            Text(
                text = "ACTIVE SECURITY GUARANTEES",
                color = Color(0xFF80A0B0),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SecurityPillarItem(
                    icon = Icons.Default.Dns,
                    title = "AdGuard Custom DNS (94.140.14.14)",
                    description = "Filters all queries via dns.adguard.com over encrypted DoH, blocking ads and threats at the root DNS layer."
                )
                SecurityPillarItem(
                    icon = Icons.Default.LockOpen,
                    title = "Zero Login / Zero Setup",
                    description = "No credentials, email, or account needed. Starts instantly and demands nothing."
                )
                SecurityPillarItem(
                    icon = Icons.Default.LocationOff,
                    title = "Bypasses Location & Permissions",
                    description = "Websites cannot prompt for GPS location, camera, or mic. Prompts are silently rejected."
                )
                SecurityPillarItem(
                    icon = Icons.Default.Block,
                    title = "Malware & Ad Blocker",
                    description = "Intercepts ad networks, trackers, popups, and cryptojacking scripts before download."
                )
                SecurityPillarItem(
                    icon = Icons.Default.DeleteSweep,
                    title = "Auto-Purge on App Exit",
                    description = "Cookies, local storage, history, and cache are completely destroyed when the app closes."
                )
            }
        }

        // Quick Launch & Test Bookmarks
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "QUICK LAUNCH & SECURITY TEST SITES",
                color = Color(0xFF80A0B0),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BookmarkCard(
                    title = "🛡️ Test AdGuard DNS Intercept (doubleclick.net)",
                    subtitle = "Resolves to 0.0.0.0 via dns.adguard.com • Ad & Tracker Neutralized",
                    icon = Icons.Default.Shield,
                    color = Color(0xFF00FF99),
                    onClick = { onSelectBookmark("https://doubleclick.net") }
                )
                BookmarkCard(
                    title = "DuckDuckGo Private Search",
                    subtitle = "duckduckgo.com • Anonymous Tracker-free Web Search",
                    icon = Icons.Default.Search,
                    color = Color(0xFF00FFE0),
                    onClick = { onSelectBookmark("https://duckduckgo.com") }
                )
                BookmarkCard(
                    title = "Wikipedia (The Free Encyclopedia)",
                    subtitle = "en.wikipedia.org • Safe, Encrypted Knowledge Base",
                    icon = Icons.Default.Language,
                    color = Color(0xFF00FF66),
                    onClick = { onSelectBookmark("https://en.wikipedia.org") }
                )
                BookmarkCard(
                    title = "🚨 Test Malicious Site Block (Phishing Trigger)",
                    subtitle = "example.com/?vex_test=BLOCK_60 • Demonstrates VEX Shield Intercept",
                    icon = Icons.Default.Shield,
                    color = Color(0xFFFF3366),
                    onClick = { onSelectBookmark("https://example.com/?vex_test=BLOCK_60") }
                )
                BookmarkCard(
                    title = "☣️ Test Drive-by Malware Download Block",
                    subtitle = "malware.testing/sample.apk • Demonstrates Payload Protection",
                    icon = Icons.Default.Dangerous,
                    color = Color(0xFFFF7799),
                    onClick = { onSelectBookmark("https://malware.testing/sample.apk") }
                )
                BookmarkCard(
                    title = "🛡️ Test Ad & Tracker Blocker",
                    subtitle = "adblock-tester.com • Verifies Real-time Script Interception",
                    icon = Icons.Default.Block,
                    color = Color(0xFFFFB800),
                    onClick = { onSelectBookmark("https://adblock-tester.com") }
                )
            }
        }
    }
}

@Composable
fun SecurityPillarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF071118))
            .border(1.dp, Color(0xFF0A2530), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF0A2530)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF00FFE0),
                modifier = Modifier.size(16.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color(0xFFFFFFFF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = Color(0xFF7090A0),
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun BookmarkCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0A1F28))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.15f))
                .border(1.dp, color, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color(0xFFFFFFFF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = Color(0xFF80A0B0),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * AdGuard Custom DNS Interactive Inspector Modal
 */
@Composable
fun AdGuardDnsInspectorDialog(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var isPinging by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = Color(0xFF00FFE0), fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = null,
                    tint = Color(0xFF00FF99),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "ADGUARD DNS ENGINE",
                    color = Color(0xFF00FF99),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Endpoint details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF051812)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF00FF99), Color(0xFF004D36))))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "ACTIVE UPSTREAM HOSTNAME:",
                            color = Color(0xFF70B090),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "dns.adguard.com",
                            color = Color(0xFF00FF99),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Primary Anycast IP: 94.140.14.14\nSecondary Anycast IP: 94.140.14.15\nProtocol: DNS-over-HTTPS (DoH)\nResolve URL: https://dns.adguard.com/resolve",
                            color = Color(0xFFBACCD6),
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Live Performance & Counters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0A202A))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "LATENCY", color = Color(0xFF7090A0), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${AdGuardDnsEngine.latencyMs.intValue} ms", color = Color(0xFF00FFE0), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0A202A))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "QUERIES", color = Color(0xFF7090A0), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${AdGuardDnsEngine.totalQueries.intValue}", color = Color(0xFFFFFFFF), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF280E14))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "BLOCKED", color = Color(0xFFFF6688), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${AdGuardDnsEngine.blockedByDns.intValue}", color = Color(0xFFFF3366), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // Ping action button
                Button(
                    onClick = {
                        scope.launch {
                            isPinging = true
                            val ping = AdGuardDnsEngine.measureLatency()
                            AdGuardDnsEngine.latencyMs.intValue = ping
                            isPinging = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D36)),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isPinging
                ) {
                    Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = Color(0xFF00FF99), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPinging) "TESTING LATENCY..." else "PING dns.adguard.com",
                        color = Color(0xFF00FF99),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Query Log Section
                Text(
                    text = "LIVE DNS QUERY AUDIT LOG:",
                    color = Color(0xFF70B090),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                if (AdGuardDnsEngine.queryRecords.isEmpty()) {
                    Text(
                        text = "No DNS queries recorded yet. Browse any site to inspect real-time DNS resolution through dns.adguard.com.",
                        color = Color(0xFF507080),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                } else {
                    AdGuardDnsEngine.queryRecords.take(10).forEach { record ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (record.isBlocked) Color(0xFF200A10) else Color(0xFF07181F))
                                .border(0.5.dp, if (record.isBlocked) Color(0xFFFF3366).copy(alpha = 0.5f) else Color(0xFF005566), RoundedCornerShape(4.dp))
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = record.domain,
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                                Text(
                                    text = record.resolvedIp,
                                    color = if (record.isBlocked) Color(0xFFFF3366) else Color(0xFF00FFE0),
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (record.isBlocked) Color(0xFFFF3366).copy(alpha = 0.2f) else Color(0xFF00FF99).copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (record.isBlocked) "0.0.0.0 BLOCKED" else "RESOLVED (${record.latencyMs}ms)",
                                    color = if (record.isBlocked) Color(0xFFFF3366) else Color(0xFF00FF99),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF03141C),
        shape = RoundedCornerShape(14.dp)
    )
}
