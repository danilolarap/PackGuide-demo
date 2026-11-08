package com.example.packguide.ui.components


import com.example.packguide.ui.theme.PackGuideColors

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.example.packguide.ui.localization.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.packguide.ui.theme.AccentGreenLed
import com.example.packguide.ui.theme.TechMonospaceFontFamily
import com.example.packguide.ui.theme.Typography

@Composable
fun LedIndicator(
    modifier: Modifier = Modifier,
    color: Color = AccentGreenLed,
    size: Dp = 8.dp,
    label: String? = null,
    isPulsing: Boolean = false
) {
    val alphaAnim by if (isPulsing) {
        val transition = rememberInfiniteTransition(label = "LedPulse")
        transition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "LedPulseAlpha"
        )
    } else {
        androidx.compose.runtime.rememberUpdatedState(1.0f)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(size * 1.8f)) {
            val centerPt = center
            val radiusPx = size.toPx() / 2f

            // Glow outer halo
            drawCircle(
                color = color.copy(alpha = 0.12f * alphaAnim),
                radius = radiusPx * 1.4f,
                center = centerPt
            )
            // Core LED circle
            drawCircle(
                color = color.copy(alpha = alphaAnim),
                radius = radiusPx,
                center = centerPt
            )
        }

        if (label != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = Typography.labelSmall,
                fontFamily = TechMonospaceFontFamily,
                color = PackGuideColors.secondaryText
            )
        }
    }
}
