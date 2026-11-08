package com.example.packguide.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.theme.AccentGreenLed
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography

@Composable
fun HudHeader(
    title: String,
    modifier: Modifier = Modifier,
    sysCode: String = "SYS // PACKING_OS",
    statusText: String = "[ ONLINE ]",
    ledColor: Color = AccentGreenLed,
    horizontalPadding: Dp = 20.dp
) {
    Column(modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = 18.dp)) {
        if (sysCode.isNotBlank() || statusText.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (sysCode.isNotBlank()) Text(sysCode, style = Typography.labelSmall,
                    color = PackGuideColors.secondaryText, modifier = Modifier.weight(1f))
                else Spacer(Modifier.weight(1f))
                if (statusText.isNotBlank()) LedIndicator(color = ledColor, size = 5.dp,
                    label = statusText, isPulsing = false)
            }
            Spacer(Modifier.height(10.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = Typography.headlineMedium, color = PackGuideColors.primaryText,
                modifier = Modifier.weight(1f))
            DotMatrixMark()
        }
    }
}
