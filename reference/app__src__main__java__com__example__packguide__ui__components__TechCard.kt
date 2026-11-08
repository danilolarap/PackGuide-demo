package com.example.packguide.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography

@Composable
fun TechCard(
    modifier: Modifier = Modifier,
    headerTag: String? = null,
    subTag: String? = null,
    borderColor: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val stroke = when (borderColor) {
        Color.Unspecified -> PackGuideColors.border
        PackGuideColors.primaryText -> PackGuideColors.primaryText.copy(alpha = 0.22f)
        else -> borderColor
    }
    Column(
        modifier.fillMaxWidth().clip(shape)
            .background(PackGuideColors.surface)
            .border(1.dp, stroke, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp)
    ) {
        if (headerTag != null || subTag != null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.width(3.dp).height(10.dp).background(AccentRedLed, RoundedCornerShape(2.dp)))
                if (headerTag != null) Text(headerTag, style = Typography.labelSmall,
                    color = PackGuideColors.secondaryText, modifier = Modifier.weight(1f))
                else Spacer(Modifier.weight(1f))
                if (subTag != null) Text(subTag, style = Typography.labelSmall, color = PackGuideColors.secondaryText)
            }
            Spacer(Modifier.height(14.dp))
        }
        content()
    }
}
