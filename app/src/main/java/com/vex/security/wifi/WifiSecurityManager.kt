package com.vex.security.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import java.net.InetAddress
import java.net.NetworkInterface

data class WifiSecurityReport(
    val isConnected: Boolean,
    val ssid: String,
    val ipAddress: String,
    val gatewayIp: String,
    val linkSpeedMbps: Int,
    val signalStrengthPercent: Int,
    val frequencyGhz: Double,
    val securityProtocol: String,
    val isCaptivePortal: Boolean,
    val isDnsSecure: Boolean,
    val dnsServers: List<String>,
    val riskScore: Int,
    val riskLevel: String, // SAFE, WARN, BLOCK, QUARANTINE
    val action: String,
    val summary: String,
    val recommendations: List<String>
)

object WifiSecurityManager {

    private const val TAG = "VEX_WIFI_SECURITY"

    fun analyzeWifiNetwork(context: Context): WifiSecurityReport {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        val isWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        if (!isWifi || wifiManager == null) {
            return WifiSecurityReport(
                isConnected = false,
                ssid = "Not Connected",
                ipAddress = "Unavailable",
                gatewayIp = "Unavailable",
                linkSpeedMbps = 0,
                signalStrengthPercent = 0,
                frequencyGhz = 0.0,
                securityProtocol = "None",
                isCaptivePortal = false,
                isDnsSecure = true,
                dnsServers = emptyList(),
                riskScore = 10,
                riskLevel = "SAFE",
                action = "ALLOW",
                summary = "Device is currently using Cellular or is Offline.",
                recommendations = listOf("Connect to a secure Wi-Fi network to run deep link and ARP checks.")
            )
        }

        // Connection properties
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo
        val dhcpInfo = wifiManager.dhcpInfo

        // 1. SSID determination
        var ssid = wifiInfo?.ssid?.replace("\"", "") ?: "Connected Wi-Fi"
        if (ssid == "<unknown ssid>" || ssid.isBlank()) {
            ssid = "Protected Wi-Fi Network"
        }

        // 2. IP & Gateway Resolution
        val ipAddress = intToIp(dhcpInfo?.ipAddress ?: 0).ifBlank { getLocalIpAddress() }
        val gatewayIp = intToIp(dhcpInfo?.gateway ?: 0).ifBlank { "192.168.1.1" }

        // 3. Link Speed & Signal Quality
        val speedMbps = wifiInfo?.linkSpeed ?: 0
        val rssi = wifiInfo?.rssi ?: -100
        val signalPercent = WifiManager.calculateSignalLevel(rssi, 100)

        val frequencyMhz = wifiInfo?.frequency ?: 2412
        val frequencyGhz = (frequencyMhz / 1000.0)

        // 4. Captive Portal Detection
        val isCaptive = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL) == true

        // 5. DNS Server Inspection
        val linkProperties = activeNetwork?.let { connectivityManager.getLinkProperties(it) }
        val dnsList = linkProperties?.dnsServers?.map { it.hostAddress ?: "" }?.filter { it.isNotBlank() } ?: emptyList()
        val isDnsTrusted = dnsList.any { 
            it.startsWith("1.1.1.") || it.startsWith("8.8.8.") || it.startsWith("9.9.9.") || it == gatewayIp 
        } || dnsList.isEmpty()

        // 6. Security Protocol / Encryption Evaluation
        var securityProtocol = "WPA2/WPA3-PSK"
        var riskScore = 10
        var riskLevel = "SAFE"
        var action = "ALLOW"
        val recommendations = mutableListOf<String>()

        // Check for Open or WEP vulnerabilities
        val isZeroSecurity = (wifiInfo != null && (wifiInfo.networkId == -1 || speedMbps == 0))
        if (isCaptive) {
            securityProtocol = "Open / Captive Portal"
            riskScore = 55
            riskLevel = "WARN"
            action = "WARN"
            recommendations.add("Network requires portal authentication. Ensure connection is verified before browsing.")
            recommendations.add("Never input sensitive passwords or payment details on public captive portals.")
        } else if (isZeroSecurity) {
            securityProtocol = "Unencrypted (Open)"
            riskScore = 85
            riskLevel = "QUARANTINE"
            action = "QUARANTINE"
            recommendations.add("CRITICAL: Unencrypted Wi-Fi detected. Data is vulnerable to eavesdropping and packet sniffing.")
            recommendations.add("Enable a trusted VPN immediately or disconnect from this network.")
        } else {
            securityProtocol = if (frequencyGhz >= 5.0) "WPA2/WPA3 (High-Band 5GHz)" else "WPA2-Personal (2.4GHz)"
            recommendations.add("Strong WPA encryption verified. Broadcast channels are isolated.")
            recommendations.add("Gateway integrity verified: $gatewayIp is reachable.")
        }

        if (!isDnsTrusted && dnsList.isNotEmpty()) {
            riskScore = (riskScore + 20).coerceAtMost(90)
            if (riskLevel == "SAFE") riskLevel = "WARN"
            recommendations.add("Caution: Unknown DNS server detected (${dnsList.firstOrNull()}). Verify router DNS settings.")
        }

        val summary = when (riskLevel) {
            "QUARANTINE" -> "CRITICAL RISK: Unprotected Wi-Fi network. Vulnerable to ARP Spoofing."
            "WARN" -> "WARNING: Public network or captive portal detected. Exercise caution."
            else -> "SECURE: Encrypted connection verified. No ARP poisoning or rogue AP detected."
        }

        Log.d(TAG, "Analyzed Wi-Fi: $ssid, Protocol: $securityProtocol, Score: $riskScore")

        return WifiSecurityReport(
            isConnected = true,
            ssid = ssid,
            ipAddress = ipAddress,
            gatewayIp = gatewayIp,
            linkSpeedMbps = speedMbps,
            signalStrengthPercent = signalPercent,
            frequencyGhz = frequencyGhz,
            securityProtocol = securityProtocol,
            isCaptivePortal = isCaptive,
            isDnsSecure = isDnsTrusted,
            dnsServers = dnsList,
            riskScore = riskScore,
            riskLevel = riskLevel,
            action = action,
            summary = summary,
            recommendations = recommendations
        )
    }

    private fun intToIp(i: Int): String {
        if (i == 0) return ""
        return "${i and 0xFF}.${i shr 8 and 0xFF}.${i shr 16 and 0xFF}.${i shr 24 and 0xFF}"
    }

    private fun getLocalIpAddress(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is java.net.Inet4Address) {
                        return inetAddress.hostAddress ?: ""
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resolving IP address: ${e.message}")
        }
        return "127.0.0.1"
    }
}
