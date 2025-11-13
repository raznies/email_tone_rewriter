package com.runanywhere.startup_hackathon20.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark Premium Color Scheme (Apple-inspired)
private val DarkPremiumColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = TextPrimary,
    primaryContainer = AccentBlue,
    onPrimaryContainer = TextPrimary,

    secondary = AccentBlue,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = TextPrimary,

    tertiary = AccentBlue,
    onTertiary = TextPrimary,

    background = TrueBlack,
    onBackground = TextPrimary,

    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    surfaceTint = AccentBlue,

    error = Color(0xFFFF453A),
    onError = TextPrimary
)

@Composable
fun Startup_hackathon20Theme(
    darkTheme: Boolean = true, // Always use dark theme for premium look
    // Disable dynamic color to maintain consistent premium branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always use dark premium color scheme
    val colorScheme = DarkPremiumColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = TrueBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}