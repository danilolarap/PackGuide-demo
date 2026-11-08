package com.example.packguide.ui.components


import com.example.packguide.ui.theme.PackGuideColors

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 1.dp,
    gapLength: Dp = 5.dp
) {
    val resolvedColor = if (color == Color.Unspecified) {
        PackGuideColors.border
    } else color
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth)
    ) {
        val dashPx = dashLength.toPx()
        val gapPx = gapLength.toPx()
        val strokePx = strokeWidth.toPx()

        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f)

        drawLine(
            color = resolvedColor,
            start = androidx.compose.ui.geometry.Offset(0f, strokePx / 2f),
            end = androidx.compose.ui.geometry.Offset(size.width, strokePx / 2f),
            strokeWidth = strokePx,
            pathEffect = pathEffect
        )
    }
}
