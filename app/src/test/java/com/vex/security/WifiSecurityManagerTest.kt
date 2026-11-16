package com.vex.security

import com.vex.security.wifi.WifiScanItem
import com.vex.security.wifi.WifiSecurityManager
import org.junit.Assert.*
import org.junit.Test

class WifiSecurityManagerTest {

    @Test
    fun detectEvilTwin_distinctBssids_returnsNoWarnings() {
        val networks = listOf(
            WifiScanItem("Home_WiFi", "00:11:22:33:44:55"),
            WifiScanItem("Office_WiFi", "AA:BB:CC:DD:EE:FF"),
            WifiScanItem("Cafe_Guest", "11:22:33:44:55:66")
        )
        val warnings = WifiSecurityManager.detectEvilTwin(networks)
        assertTrue("Expected zero warnings for unique BSSIDs", warnings.isEmpty())
    }

    @Test
    fun detectEvilTwin_duplicateBssidAcrossDifferentSsids_detectsEvilTwin() {
        val rogueBssid = "00:11:22:33:44:55"
        val networks = listOf(
            WifiScanItem("CoffeeShop_Free", rogueBssid),
            WifiScanItem("Starbucks_Official", rogueBssid)
        )
        val warnings = WifiSecurityManager.detectEvilTwin(networks)
        assertEquals("Expected 1 warning for duplicated BSSID", 1, warnings.size)
        assertTrue("Warning should mention rogue/evil twin", warnings[0].contains("Evil Twin") || warnings[0].contains("Rogue"))
    }

    @Test
    fun detectEvilTwin_sameSsidMultipleBssids_detectsEvilTwin() {
        val networks = listOf(
            WifiScanItem("Starbucks_Official", "00:11:22:33:44:55"),
            WifiScanItem("Starbucks_Official", "AA:BB:CC:DD:EE:FF")
        )
        val warnings = WifiSecurityManager.detectEvilTwin(networks)
        assertEquals("Expected 1 warning for cloned SSID across multiple BSSIDs", 1, warnings.size)
        assertTrue("Warning should mention Evil Twin clone", warnings[0].contains("Evil Twin"))
    }
}
