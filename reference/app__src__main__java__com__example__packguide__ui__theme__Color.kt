package com.example.packguide.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Nothing OS / Tech ASCII Palette
val PrimaryBlack = Color(0xFF191919)
val BackgroundLight = Color(0xFFF3F2EE)
val CardSurfaceLight = Color(0xFFFFFEFA)
val AccentRedLed = Color(0xFFD63830)
val AccentGreenLed = Color(0xFF54816A)
val SecondarySlate = Color(0xFF686862)
val BorderSlate = Color(0xFFDEDED7)
val SurfaceVariantLight = Color(0xFFEAE9E3)

@Immutable
data class TechExtendedColors(
    val primaryBlack: Color = PrimaryBlack,
    val backgroundLight: Color = BackgroundLight,
    val cardSurfaceLight: Color = CardSurfaceLight,
    val accentRedLed: Color = AccentRedLed,
    val accentGreenLed: Color = AccentGreenLed,
    val secondarySlate: Color = SecondarySlate,
    val borderSlate: Color = BorderSlate,
    val surfaceVariantLight: Color = SurfaceVariantLight
)

val LocalTechExtendedColors = staticCompositionLocalOf { TechExtendedColors() }
