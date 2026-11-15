package com.vex.security.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VexDarkColorScheme = darkColorScheme(
    primary = VexCyan,
    onPrimary = VexVoid,
    primaryContainer = VexCardElevated,
    onPrimaryContainer = VexCyan,
    secondary = VexEmerald,
    onSecondary = VexVoid,
    secondaryContainer = VexCardDark,
    onSecondaryContainer = VexEmerald,
    tertiary = VexGreenGlow,
    onTertiary = VexVoid,
    error = VexCrimson,
    onError = VexTextPrimary,
    background = VexVoid,
    onBackground = VexTextPrimary,
    surface = VexCardDark,
    onSurface = VexTextPrimary,
    surfaceVariant = VexCardElevated,
    onSurfaceVariant = VexTextSecondary,
    outline = VexCardBorder
)

@Composable
fun VEXTheme(
    darkTheme: Boolean = true,
    // Set dynamicColor to false by default to preserve the high-tech cyber security identity on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = VexDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = VexBackgroundTop.toArgb()
                window.navigationBarColor = VexNavContainer.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
