package com.vex.security.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val title: String, val badgeColor: Long, val isDark: Boolean = true) {
    EXECUTIVE("Executive Obsidian & Coral", 0xFFFF3358, true),
    TACTICAL_CYAN("Tactical Cyber Cyan", 0xFF00FFE0, true),
    COBALT_DEFENSE("Cobalt Shield (Enterprise)", 0xFF38BDF8, true),
    CRIMSON_DECEPTION("Crimson Canary SOC", 0xFFFF4D6D, true),
    EXECUTIVE_LIGHT("Executive Light Mode", 0xFF0284C7, false),
    NORDIC_FROST("Nordic Frost (Clean Minimal)", 0xFF0D9488, false),
    WARM_EDITORIAL("Warm Editorial (Parchment & Amber)", 0xFFD97706, false),
    CERAMIC_SAGE("Ceramic Sage (Japanese Matte)", 0xFF059669, false)
}

object ThemeState {
    var currentTheme by mutableStateOf(AppThemeMode.EXECUTIVE)
    var isDarkTheme by mutableStateOf(true)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("vex_settings", Context.MODE_PRIVATE)
        val themeName = prefs.getString("app_theme_mode", AppThemeMode.EXECUTIVE.name)
        try {
            val mode = AppThemeMode.valueOf(themeName ?: AppThemeMode.EXECUTIVE.name)
            applyTheme(context, mode)
        } catch (_: Exception) {
            applyTheme(context, AppThemeMode.EXECUTIVE)
        }
    }

    fun applyTheme(context: Context?, mode: AppThemeMode) {
        currentTheme = mode
        isDarkTheme = mode.isDark
        context?.getSharedPreferences("vex_settings", Context.MODE_PRIVATE)?.edit()
            ?.putString("app_theme_mode", mode.name)
            ?.putBoolean("dark_theme", isDarkTheme)
            ?.apply()
    }

    fun cycleNextTheme(context: Context) {
        val all = AppThemeMode.values()
        val nextIdx = (currentTheme.ordinal + 1) % all.size
        applyTheme(context, all[nextIdx])
    }
}

object VexPalette {
    val isDark: Boolean
        get() = ThemeState.isDarkTheme

    val coralRed: Color = Color(0xFFFF3358)
    val coralRedLight: Color = Color(0xFFFF486C)
    val darkPlumBg: Color = Color(0xFF180D28)
    val darkPlumCard: Color = Color(0xFF241239)
    val darkPlumPill: Color = Color(0xFF1A0C2B)
    val lavenderText: Color = Color(0xFFA998C4)
    val goldCrown: Color = Color(0xFFFFB800)

    val primary: Color
        get() = when (ThemeState.currentTheme) {
            AppThemeMode.EXECUTIVE -> Color(0xFFFF3358)
            AppThemeMode.TACTICAL_CYAN -> Color(0xFF00FFE0)
            AppThemeMode.COBALT_DEFENSE -> Color(0xFF38BDF8)
            AppThemeMode.CRIMSON_DECEPTION -> Color(0xFFFF4D6D)
            AppThemeMode.EXECUTIVE_LIGHT -> Color(0xFF0284C7)
            AppThemeMode.NORDIC_FROST -> Color(0xFF0D9488)
            AppThemeMode.WARM_EDITORIAL -> Color(0xFFC2410C)
            AppThemeMode.CERAMIC_SAGE -> Color(0xFF059669)
        }

    val primaryVariant: Color
        get() = when (ThemeState.currentTheme) {
            AppThemeMode.EXECUTIVE -> Color(0xFFFF486C)
            AppThemeMode.TACTICAL_CYAN -> Color(0xFF0099A8)
            AppThemeMode.COBALT_DEFENSE -> Color(0xFF1D4ED8)
            AppThemeMode.CRIMSON_DECEPTION -> Color(0xFFCC1F43)
            AppThemeMode.EXECUTIVE_LIGHT -> Color(0xFF0369A1)
            AppThemeMode.NORDIC_FROST -> Color(0xFF0F766E)
            AppThemeMode.WARM_EDITORIAL -> Color(0xFF78350F)
            AppThemeMode.CERAMIC_SAGE -> Color(0xFF047857)
        }

    val backgroundTop: Color
        get() = when (ThemeState.currentTheme) {
            AppThemeMode.EXECUTIVE -> Color(0xFFF6F4F9)
            AppThemeMode.TACTICAL_CYAN -> Color(0xFF03141C)
            AppThemeMode.COBALT_DEFENSE -> Color(0xFF060F1E)
            AppThemeMode.CRIMSON_DECEPTION -> Color(0xFF180509)
            AppThemeMode.EXECUTIVE_LIGHT -> Color(0xFFF8FAFC)
            AppThemeMode.NORDIC_FROST -> Color(0xFFF8FAFC)
            AppThemeMode.WARM_EDITORIAL -> Color(0xFFFBF9F5)
            AppThemeMode.CERAMIC_SAGE -> Color(0xFFF6F8F6)
        }

    val backgroundMid: Color
        get() = when (ThemeState.currentTheme) {
            AppThemeMode.EXECUTIVE -> Color(0xFFF3EFF8)
            AppThemeMode.TACTICAL_CYAN -> Color(0xFF01080C)
            AppThemeMode.COBALT_DEFENSE -> Color(0xFF030712)
            AppThemeMode.CRIMSON_DECEPTION -> Color(0xFF0E0205)
            AppThemeMode.EXECUTIVE_LIGHT -> Color(0xFFF1F5F9)
            AppThemeMode.NORDIC_FROST -> Color(0xFFF1F5F9)
            AppThemeMode.WARM_EDITORIAL -> Color(0xFFF5EFE6)
            AppThemeMode.CERAMIC_SAGE -> Color(0xFFEEF2EE)
        }

    val backgroundBottom: Color
        get() = when (ThemeState.currentTheme) {
            AppThemeMode.EXECUTIVE -> Color(0xFFECE6F4)
            AppThemeMode.TACTICAL_CYAN -> Color(0xFF000305)
            AppThemeMode.COBALT_DEFENSE -> Color(0xFF010308)
            AppThemeMode.CRIMSON_DECEPTION -> Color(0xFF060002)
            AppThemeMode.EXECUTIVE_LIGHT -> Color(0xFFE2E8F0)
            AppThemeMode.NORDIC_FROST -> Color(0xFFE2E8F0)
            AppThemeMode.WARM_EDITORIAL -> Color(0xFFEBE3D5)
            AppThemeMode.CERAMIC_SAGE -> Color(0xFFE2E8E2)
        }

    val headerText: Color
        get() = if (ThemeState.currentTheme == AppThemeMode.EXECUTIVE) Color(0xFF1C1133) else textPrimary

    val headerSubtext: Color
        get() = if (ThemeState.currentTheme == AppThemeMode.EXECUTIVE) Color(0xFF6B5887) else textSecondary

    val cardBackground: Color
        get() = when (ThemeState.currentTheme) {
            AppThemeMode.EXECUTIVE -> Color(0xFF241239)
            AppThemeMode.TACTICAL_CYAN -> Color(0xFF0B161F)
            AppThemeMode.COBALT_DEFENSE -> Color(0xFF0E1A2E)
            AppThemeMode.CRIMSON_DECEPTION -> Color(0xFF1A0A0E)
            AppThemeMode.EXECUTIVE_LIGHT -> Color(0xFFFFFFFF)
            AppThemeMode.NORDIC_FROST -> Color(0xFFFFFFFF)
            AppThemeMode.WARM_EDITORIAL -> Color(0xFFFFFFFF)
            AppThemeMode.CERAMIC_SAGE -> Color(0xFFFFFFFF)
        }

    val cardBorder: Color
        get() = when (ThemeState.currentTheme) {
            AppThemeMode.EXECUTIVE -> Color(0xFF381A56)
            AppThemeMode.TACTICAL_CYAN -> Color(0xFF005566)
            AppThemeMode.COBALT_DEFENSE -> Color(0xFF1E3A5F)
            AppThemeMode.CRIMSON_DECEPTION -> Color(0xFF661525)
            AppThemeMode.EXECUTIVE_LIGHT -> Color(0xFFCBD5E1)
            AppThemeMode.NORDIC_FROST -> Color(0xFFCBD5E1)
            AppThemeMode.WARM_EDITORIAL -> Color(0xFFE7DEC8)
            AppThemeMode.CERAMIC_SAGE -> Color(0xFFD1DDD1)
        }

    val textPrimary: Color
        get() = when (ThemeState.currentTheme) {
            AppThemeMode.EXECUTIVE,
            AppThemeMode.TACTICAL_CYAN,
            AppThemeMode.COBALT_DEFENSE,
            AppThemeMode.CRIMSON_DECEPTION -> Color(0xFFFFFFFF)
            AppThemeMode.EXECUTIVE_LIGHT,
            AppThemeMode.NORDIC_FROST -> Color(0xFF0F172A)
            AppThemeMode.WARM_EDITORIAL -> Color(0xFF1C1917)
            AppThemeMode.CERAMIC_SAGE -> Color(0xFF111827)
        }

    val textSecondary: Color
        get() = when (ThemeState.currentTheme) {
            AppThemeMode.EXECUTIVE -> Color(0xFFA998C4)
            AppThemeMode.TACTICAL_CYAN,
            AppThemeMode.COBALT_DEFENSE,
            AppThemeMode.CRIMSON_DECEPTION -> Color(0xFFA0B0C0)
            AppThemeMode.EXECUTIVE_LIGHT,
            AppThemeMode.NORDIC_FROST -> Color(0xFF475569)
            AppThemeMode.WARM_EDITORIAL -> Color(0xFF57534E)
            AppThemeMode.CERAMIC_SAGE -> Color(0xFF4B5563)
        }

    val pillBackground: Color
        get() = when (ThemeState.currentTheme) {
            AppThemeMode.EXECUTIVE -> Color(0xFF1A0C2B)
            AppThemeMode.TACTICAL_CYAN -> Color(0xFF0A1F28)
            AppThemeMode.COBALT_DEFENSE -> Color(0xFF10233D)
            AppThemeMode.CRIMSON_DECEPTION -> Color(0xFF22080D)
            AppThemeMode.EXECUTIVE_LIGHT -> Color(0xFFE2E8F0)
            AppThemeMode.NORDIC_FROST -> Color(0xFFE6FFFA)
            AppThemeMode.WARM_EDITORIAL -> Color(0xFFFEF3C7)
            AppThemeMode.CERAMIC_SAGE -> Color(0xFFECFDF5)
        }
}
