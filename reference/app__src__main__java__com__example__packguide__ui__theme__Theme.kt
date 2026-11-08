package com.example.packguide.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TechLightColorScheme = lightColorScheme(
    primary = PrimaryBlack,
    onPrimary = CardSurfaceLight,
    primaryContainer = SurfaceVariantLight,
    onPrimaryContainer = PrimaryBlack,
    secondary = SecondarySlate,
    onSecondary = CardSurfaceLight,
    tertiary = AccentRedLed,
    onTertiary = CardSurfaceLight,
    background = BackgroundLight,
    onBackground = PrimaryBlack,
    surface = CardSurfaceLight,
    onSurface = PrimaryBlack,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = PrimaryBlack,
    outline = SecondarySlate,
    outlineVariant = BorderSlate,
    error = AccentRedLed,
    onError = CardSurfaceLight
)

private val TechDarkColorScheme = darkColorScheme(
    primary = CardSurfaceLight,
    onPrimary = PrimaryBlack,
    primaryContainer = PrimaryBlack,
    onPrimaryContainer = CardSurfaceLight,
    secondary = Color(0xFFB2B1AA),
    onSecondary = PrimaryBlack,
    tertiary = AccentRedLed,
    onTertiary = CardSurfaceLight,
    background = Color(0xFF101110),
    onBackground = CardSurfaceLight,
    surface = Color(0xFF1B1C1A),
    onSurface = CardSurfaceLight,
    surfaceVariant = Color(0xFF282925),
    onSurfaceVariant = CardSurfaceLight,
    outline = Color(0xFF8C8D86),
    outlineVariant = Color(0xFF393A35),
    error = AccentRedLed,
    onError = CardSurfaceLight
)

object TechTheme {
    val colors: TechExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTechExtendedColors.current
}

@Composable
fun PackGuideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) TechDarkColorScheme else TechLightColorScheme
    val techExtendedColors = TechExtendedColors()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalTechExtendedColors provides techExtendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
