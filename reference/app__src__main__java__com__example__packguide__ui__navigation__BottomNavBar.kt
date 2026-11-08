package com.example.packguide.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.packguide.ui.localization.LocalAppLanguage
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.localization.localizedText
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography

@Composable
fun TechBottomNavBar(
    selectedTab: NavTab?,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
    tabs: List<NavTab?> = NavTab.tabs,
    currentDestination: NavTab? = selectedTab,
    onSettingsClick: () -> Unit = {}
) {
    val language = LocalAppLanguage.current
    Surface(modifier = modifier.fillMaxWidth(), color = PackGuideColors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), shadowElevation = 2.dp) {
        Row(
            Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            tabs.filterNotNull().forEach { tab ->
                val selected = currentDestination?.route == tab.route
                val tint by animateColorAsState(
                    if (selected) PackGuideColors.primaryText else PackGuideColors.secondaryText,
                    tween(180), label = "NavigationTint")
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(18.dp))
                        .background(if (selected) PackGuideColors.surfaceVariant else Color.Transparent)
                        .selectable(selected = selected, role = Role.Tab, onClick = { onTabSelected(tab) })
                        .padding(horizontal = 2.dp, vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(tab.icon, contentDescription = null, tint = tint, modifier = Modifier.size(21.dp))
                    Text(localizedText(tab.title, language).trim('[', ']'),
                        style = Typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.sp),
                        color = tint, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Box(Modifier.size(3.dp).background(if (selected) AccentRedLed else Color.Transparent, CircleShape))
                }
            }
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(18.dp))
                    .clickable(role = Role.Button, onClick = onSettingsClick)
                    .padding(horizontal = 2.dp, vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Outlined.Settings,
                    contentDescription = localizedText("Configuración", language),
                    tint = PackGuideColors.secondaryText, modifier = Modifier.size(21.dp))
                Text(localizedText("[AJUSTES]", language).trim('[', ']'),
                    style = Typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.sp),
                    color = PackGuideColors.secondaryText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.size(3.dp))
            }
        }
    }
}
