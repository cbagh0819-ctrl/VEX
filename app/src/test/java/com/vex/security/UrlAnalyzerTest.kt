package com.vex.security

import com.vex.security.security.ThreatIndicators
import com.vex.security.security.UrlAnalyzer
import org.junit.Assert.*
import org.junit.Test

class UrlAnalyzerTest {

    @Test
    fun analyze_cleanUrl_returnsNormalUrl() {
        val indicators = UrlAnalyzer.analyze("https://google.com/search")
        assertTrue("Expected NORMAL_URL for standard https domain", indicators.contains(ThreatIndicators.NORMAL_URL))
    }

    @Test
    fun analyze_insecureHttp_detectsInsecureProtocol() {
        val indicators = UrlAnalyzer.analyze("http://example.com/test")
        assertTrue("Expected INSECURE_HTTP for http link", indicators.contains(ThreatIndicators.INSECURE_HTTP))
    }

    @Test
    fun analyze_ipAddressUrl_detectsIpBasedUrl() {
        val indicators = UrlAnalyzer.analyze("http://192.168.1.100/admin")
        assertTrue("Expected IP_BASED_URL for IP hostname", indicators.contains(ThreatIndicators.IP_BASED_URL))
    }

    @Test
    fun analyze_punycodeDomain_detectsPunycode() {
        val indicators = UrlAnalyzer.analyze("https://xn--pple-43d.com/login")
        assertTrue("Expected PUNYCODE_DOMAIN for homograph attack", indicators.contains(ThreatIndicators.PUNYCODE_DOMAIN))
    }

    @Test
    fun analyze_urlShortener_detectsShortener() {
        val indicators = UrlAnalyzer.analyze("https://bit.ly/3xY7z9")
        assertTrue("Expected URL_SHORTENER for bit.ly", indicators.contains(ThreatIndicators.URL_SHORTENER))
    }

    @Test
    fun analyze_dangerousFileDownload_detectsMaliciousDownload() {
        val indicators = UrlAnalyzer.analyze("https://example.com/downloads/invoice.exe")
        assertTrue("Expected MALICIOUS_DOWNLOAD for executable download", indicators.contains(ThreatIndicators.MALICIOUS_DOWNLOAD))
    }

    @Test
    fun analyze_brandTyposquatting_detectsPhishing() {
        val indicators = UrlAnalyzer.analyze("https://paypal-verify-account-security.com/signin")
        assertTrue("Expected PHISHING_URL for brand impersonation", indicators.contains(ThreatIndicators.PHISHING_URL))
    }
}
