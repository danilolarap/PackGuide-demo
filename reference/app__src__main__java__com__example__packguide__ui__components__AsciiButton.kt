package com.example.packguide.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography

@Composable
fun AsciiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    isAccentRed: Boolean = false,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val containerColor = when {
        !enabled -> PackGuideColors.surfaceVariant
        isAccentRed -> AccentRedLed
        isPrimary -> PackGuideColors.primaryText
        else -> PackGuideColors.surface
    }
    val contentColor = when {
        !enabled -> PackGuideColors.secondaryText
        isAccentRed -> Color.White
        isPrimary -> PackGuideColors.surface
        else -> PackGuideColors.primaryText
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.985f else 1f, tween(120), label = "ButtonPress")
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier.graphicsLayer { scaleX = scale; scaleY = scale }
            .heightIn(min = 48.dp).clip(shape).background(containerColor)
            .border(1.dp, if (isPrimary && enabled) containerColor else PackGuideColors.border, shape)
            .clickable(enabled = enabled, role = Role.Button, interactionSource = interaction,
                indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (icon != null) Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            Text(text, style = Typography.labelLarge, color = contentColor)
        }
    }
}
