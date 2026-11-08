package com.example.packguide.ui.components


import com.example.packguide.ui.theme.PackGuideColors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.packguide.ui.localization.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.TechMonospaceFontFamily
import com.example.packguide.ui.theme.Typography

@Composable
fun TechBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    ledColor: Color? = null
) {
    val resolvedContainerColor = if (containerColor == Color.Unspecified) PackGuideColors.primaryText else containerColor
    val resolvedContentColor = if (contentColor == Color.Unspecified) PackGuideColors.surface else contentColor
    val resolvedBorderColor = if (borderColor == Color.Unspecified) Color.Transparent else borderColor
    Box(
        modifier = modifier
            .background(resolvedContainerColor, shape = RoundedCornerShape(8.dp))
            .border(1.dp, resolvedBorderColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (ledColor != null) {
                LedIndicator(color = ledColor, size = 6.dp, isPulsing = false)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = Typography.labelSmall.copy(fontSize = 11.sp),
                fontFamily = TechMonospaceFontFamily,
                color = resolvedContentColor
            )
        }
    }
}
