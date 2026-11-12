package com.vex.security.ui.theme
import com.vex.security.ui.theme.VexPalette

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun VEXTheme(
    themeMode: AppThemeMode = ThemeState.currentTheme,
    content: @Composable () -> Unit
) {
    val darkTheme = themeMode.isDark
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = VexPalette.primary,
            secondary = VexPalette.primaryVariant,
            tertiary = VexCyan,
            background = VexPalette.backgroundTop,
            surface = VexPalette.cardBackground,
            onPrimary = VexPalette.backgroundBottom,
            onSecondary = VexPalette.textPrimary,
            onBackground = VexPalette.textPrimary,
            onSurface = VexPalette.textPrimary
        )
    } else {
        lightColorScheme(
            primary = VexPalette.primary,
            secondary = VexPalette.primaryVariant,
            tertiary = VexCobalt,
            background = VexPalette.backgroundTop,
            surface = VexPalette.cardBackground,
            onPrimary = VexPalette.textPrimary,
            onSecondary = VexPalette.textSecondary,
            onBackground = VexPalette.textPrimary,
            onSurface = VexPalette.textPrimary
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = VexPalette.backgroundTop.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
