package com.example.packguide.ui.components

import com.example.packguide.ui.theme.PackGuideColors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Icon
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.packguide.settings.AppLanguage
import com.example.packguide.ui.localization.Text as LocalizedText
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.Typography

@Composable
fun SettingsDialog(
    language: AppLanguage,
    isDarkTheme: Boolean,
    showLogout: Boolean,
    onToggleTheme: () -> Unit,
    onToggleLanguage: () -> Unit,
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().background(PackGuideColors.surface, RoundedCornerShape(22.dp))
                .border(1.dp, PackGuideColors.border, RoundedCornerShape(22.dp)).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LocalizedText("[ AJUSTES DE SISTEMA ]", style = Typography.titleMedium, color = PackGuideColors.primaryText)
            LocalizedText("PERSONALIZA PACKGUIDE", style = Typography.labelSmall, color = PackGuideColors.secondaryText)
            Spacer(Modifier.height(4.dp))
            SettingsRow(
                icon = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                title = "TEMA",
                value = if (isDarkTheme) "OSCURO" else "CLARO",
                onClick = onToggleTheme
            )
            SettingsRow(
                icon = Icons.Outlined.Language,
                title = "IDIOMA",
                value = if (language == AppLanguage.Spanish) "ESPAÑOL" else "ENGLISH",
                onClick = onToggleLanguage
            )
            if (showLogout) {
                SettingsRow(
                    icon = Icons.Outlined.Logout,
                    title = "CERRAR SESIÓN",
                    value = "SALIR DE LA CUENTA",
                    accent = AccentRedLed,
                    onClick = onLogout
                )
            }
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                LocalizedText("[ CERRAR ]", color = PackGuideColors.primaryText, style = Typography.labelMedium)
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String,
    accent: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    val resolvedAccent = if (accent == Color.Unspecified) PackGuideColors.primaryText else accent
    Row(
        modifier = Modifier.fillMaxWidth().background(PackGuideColors.background, RoundedCornerShape(2.dp))
            .border(1.dp, PackGuideColors.border, RoundedCornerShape(2.dp)).clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = resolvedAccent)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            LocalizedText(title, style = Typography.labelMedium, color = resolvedAccent)
            Spacer(Modifier.height(3.dp))
            LocalizedText(value, style = Typography.labelSmall, color = PackGuideColors.secondaryText)
        }
        LocalizedText("›", style = Typography.titleMedium, color = resolvedAccent)
    }
}
