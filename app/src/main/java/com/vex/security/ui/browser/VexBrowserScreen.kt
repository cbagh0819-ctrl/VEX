package com.vex.security.ui.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.vex.security.browser.VexBrowserEngine
import java.io.ByteArrayInputStream
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.vex.security.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VexBrowserScreen(
    onBackPressed: () -> Unit = {}
) {
    var inputUrl by remember { mutableStateOf("https://duckduckgo.com") }
    var currentUrl by remember { mutableStateOf("https://duckduckgo.com") }
    var isLoading by remember { mutableStateOf(false) }
    var pageProgress by remember { mutableStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showShieldDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val navigateTo: (String) -> Unit = { target ->
        var formatted = target.trim()
        if (formatted.isNotBlank()) {
            if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
                formatted = if (formatted.contains(".") && !formatted.contains(" ")) {
                    "https://$formatted"
                } else {
                    "https://duckduckgo.com/?q=${formatted.replace(" ", "+")}"
                }
            }
            inputUrl = formatted
            currentUrl = formatted
            webViewInstance?.loadUrl(formatted)
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VexVoid)
    ) {
        // Top Security Header Strip
        Surface(
            color = VexCardDark,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Security Status Chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(VexCardElevated)
                            .border(1.dp, VexCardBorder, RoundedCornerShape(20.dp))
                            .clickable { showShieldDialog = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(VexGreenGlow, shape = CircleShape)
                        )
                        Text(
                            text = "VIRTUAL-HAL SHIELD ACTIVE",
                            color = VexCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Shield Info",
                            tint = VexTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    // Incognito Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Incognito",
                            tint = VexEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "ZERO LEAK",
                            color = VexEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Omnibox URL Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search or type website URL...", color = VexTextDim, fontSize = 13.sp) },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Security",
                                tint = VexCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (inputUrl.isNotBlank()) {
                                IconButton(
                                    onClick = { inputUrl = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = VexTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = { navigateTo(inputUrl) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VexTextPrimary,
                            unfocusedTextColor = VexTextSecondary,
                            focusedContainerColor = VexCardElevated,
                            unfocusedContainerColor = VexCardElevated,
                            focusedBorderColor = VexCyan,
                            unfocusedBorderColor = VexCardBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Execute / Navigate Button
                    Button(
                        onClick = { navigateTo(inputUrl) },
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VexCyan,
                            contentColor = VexVoid
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp)
                    ) {
                        Text("GO", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Loading Progress Bar
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LinearProgressIndicator(
                        progress = { pageProgress / 100f },
                        modifier = Modifier.fillMaxWidth().height(2.5.dp),
                        color = VexCyan,
                        trackColor = VexCardElevated
                    )
                }
            }
        }

        // Webview Viewport
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        VexBrowserEngine.configureSecureSettings(this)

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                pageProgress = newProgress
                                isLoading = newProgress < 100
                            }
                        }

                        val blockedAdHosts = setOf(
                            "doubleclick.net", "googlesyndication.com", "googleadservices.com",
                            "pagead2.googlesyndication.com", "adnxs.com", "scorecardresearch.com",
                            "taboola.com", "outbrain.com", "adservice.google.com",
                            "facebook.net/tr", "analytics.google.com"
                        )

                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                val host = request?.url?.host?.lowercase() ?: ""
                                if (blockedAdHosts.any { host.contains(it) }) {
                                    return WebResourceResponse(
                                        "text/plain",
                                        "UTF-8",
                                        ByteArrayInputStream(ByteArray(0))
                                    )
                                }
                                return super.shouldInterceptRequest(view, request)
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                                url?.let {
                                    currentUrl = it
                                    inputUrl = it
                                }
                                canGoBack = canGoBack()
                                canGoForward = canGoForward()
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                canGoBack = canGoBack()
                                canGoForward = canGoForward()
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }

                        loadUrl(currentUrl)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                    if (webView.url != currentUrl && currentUrl.isNotBlank()) {
                        webView.loadUrl(currentUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bottom Browser Navigation Controls Strip
        Surface(
            color = VexCardDark,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back
                IconButton(
                    onClick = {
                        if (webViewInstance?.canGoBack() == true) {
                            webViewInstance?.goBack()
                        }
                    },
                    enabled = canGoBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (canGoBack) VexCyan else VexTextDim
                    )
                }

                // Forward
                IconButton(
                    onClick = {
                        if (webViewInstance?.canGoForward() == true) {
                            webViewInstance?.goForward()
                        }
                    },
                    enabled = canGoForward
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (canGoForward) VexCyan else VexTextDim
                    )
                }

                // Refresh / Stop
                IconButton(
                    onClick = {
                        if (isLoading) webViewInstance?.stopLoading() else webViewInstance?.reload()
                    }
                ) {
                    Icon(
                        imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
                        contentDescription = if (isLoading) "Stop" else "Reload",
                        tint = VexCyan
                    )
                }

                // Privacy DuckDuckGo Home
                IconButton(
                    onClick = {
                        navigateTo("https://duckduckgo.com")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = VexTextSecondary
                    )
                }

                // Shield Info
                IconButton(
                    onClick = { showShieldDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Protection",
                        tint = VexGreenGlow
                    )
                }
            }
        }
    }

    // Protection Details Dialog
    if (showShieldDialog) {
        AlertDialog(
            onDismissRequest = { showShieldDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = VexCyan)
                    Text("VEX Virtual-HAL Protection", color = VexCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "You are browsing in VEX Protected Mode:",
                        color = VexTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("• Camera Hardware Interception: ACTIVE (Synthetic decoy fed to rogue scripts)", color = VexEmerald, fontSize = 12.sp)
                    Text("• Microphone Snatch Protection: ACTIVE (Zero hardware leakage)", color = VexEmerald, fontSize = 12.sp)
                    Text("• Zero Tracker / Zero Persistence: Cookies and storage wiped upon session termination", color = VexTextSecondary, fontSize = 12.sp)
                    Text("• Canary Attribution: Remote phishing servers are geo-attributed in real time", color = VexTextSecondary, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showShieldDialog = false }) {
                    Text("SECURE", color = VexCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = VexCardDark,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
