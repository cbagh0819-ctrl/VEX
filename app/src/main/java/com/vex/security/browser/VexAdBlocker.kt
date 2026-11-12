package com.vex.security.browser

import java.net.URI
import java.util.Locale

/**
 * VEX Ad & Malware Blocker Engine
 * Intercepts network requests and sub-resources to block:
 * - Malicious ads and popup networks
 * - Crypto miners
 * - Invasive telemetry trackers
 * - Ad servers and script hijackers
 */
object VexAdBlocker {

    private val blockedDomains = setOf(
        // Google Ad Networks
        "doubleclick.net",
        "googleadservices.com",
        "googlesyndication.com",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "adservice.google.ca",
        "adservice.google.co.uk",
        "adservice.google.de",

        // Web Ad Networks & Programmatic Exchanges
        "adnxs.com",
        "adsystem.com",
        "adroll.com",
        "adform.net",
        "rubiconproject.com",
        "pubmatic.com",
        "openx.net",
        "smartadserver.com",
        "bidswitch.net",
        "casalemedia.com",
        "yieldmo.com",
        "amazon-adsystem.com",
        "adlightning.com",
        "aditude.com",
        "serving-sys.com",
        "adtechus.com",
        "medianet.com",
        "media.net",

        // Content Recommendation / Native Ads
        "criteo.com",
        "criteo.net",
        "taboola.com",
        "outbrain.com",
        "mgid.com",
        "revcontent.com",
        "zergnet.com",
        "content.ad",

        // Popups, Redirects & Aggressive Ad Networks
        "popads.net",
        "popcash.net",
        "propellerads.com",
        "adsterra.com",
        "exoclick.com",
        "trafficjunky.com",
        "trafficstars.com",
        "ero-advertising.com",
        "clksite.com",
        "onclickads.net",
        "exosrv.com",
        "revenuehits.com",
        "yllix.com",
        "ad-maven.com",
        "clickadu.com",
        "richpush.co",
        "pushame.com",
        "adcash.com",
        "adblade.com",

        // Mobile Ad Networks
        "applovin.com",
        "unityads.unity3d.com",
        "vungle.com",
        "ironsrc.com",
        "chartboost.com",
        "inmobi.com",
        "adcolony.com",
        "admob.com",
        "mopub.com",

        // Trackers & Telemetry Spyware
        "google-analytics.com",
        "googletagmanager.com",
        "analytics.google.com",
        "hotjar.com",
        "clarity.ms",
        "mouseflow.com",
        "crazyegg.com",
        "connect.facebook.net",
        "pixel.facebook.com",
        "branch.io",
        "adjust.com",
        "appsflyer.com",
        "kochava.com",
        "scorecardresearch.com",
        "quantserve.com",
        "moatads.com",

        // Crypto Miners & Coin Hijackers
        "coinhive.com",
        "crypto-loot.com",
        "coin-have.com",
        "jsecoin.com",
        "authedmine.com",
        "webmine.cz",
        "coinnebula.com",
        "miner.pr0gramm.com",
        "minr.pw",
        "crypto-webminer.com"
    )

    private val suspiciousPathKeywords = listOf(
        "/ads.js",
        "/advert.js",
        "/adsense.js",
        "/banner_ads/",
        "/ad_server/",
        "/popunder",
        "/pop-up-ads",
        "/ad_tracker",
        "pagead2.googlesyndication.com",
        "fbevents.js",
        "/adsystem/",
        "/adbanner/",
        "/adservice/",
        "/pixel.gif",
        "ad.doubleclick.net"
    )

    /**
     * Determines whether a resource URL should be intercepted and blocked.
     */
    fun isBlocked(url: String): Boolean {
        if (url.isBlank()) return false
        val lowerUrl = url.lowercase(Locale.ROOT)

        // Fast keyword check
        for (keyword in suspiciousPathKeywords) {
            if (lowerUrl.contains(keyword)) {
                return true
            }
        }

        // Host extraction
        val host = try {
            val uri = URI(if (!lowerUrl.contains("://")) "http://$lowerUrl" else lowerUrl)
            uri.host?.lowercase(Locale.ROOT) ?: ""
        } catch (_: Exception) {
            val withoutScheme = lowerUrl.substringAfter("://")
            withoutScheme.substringBefore("/").substringBefore("?").substringBefore(":")
        }

        if (host.isBlank()) return false

        // Check if host matches or ends with any blocked domain
        for (domain in blockedDomains) {
            if (host == domain || host.endsWith(".$domain")) {
                return true
            }
        }

        return false
    }
}
