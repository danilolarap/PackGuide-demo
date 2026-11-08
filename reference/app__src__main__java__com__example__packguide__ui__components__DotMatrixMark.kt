package com.example.packguide.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.PackGuideColors

@Composable
fun DotMatrixMark(modifier: Modifier = Modifier) {
    val neutral = PackGuideColors.primaryText.copy(alpha = 0.18f)
    Canvas(modifier.size(32.dp)) {
        val step = size.width / 6
        repeat(6) { row ->
            repeat(6) { column ->
                drawCircle(
                    color = if (column == 5 && row >= 3) AccentRedLed else neutral,
                    radius = step * 0.16f,
                    center = Offset(step * (column + 0.5f), step * (row + 0.5f))
                )
            }
        }
    }
}
