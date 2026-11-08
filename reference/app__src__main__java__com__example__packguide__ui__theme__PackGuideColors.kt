package com.example.packguide.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object PackGuideColors {
    val background: Color @Composable get() = MaterialTheme.colorScheme.background
    val surface: Color @Composable get() = MaterialTheme.colorScheme.surface
    val surfaceVariant: Color @Composable get() = MaterialTheme.colorScheme.surfaceVariant
    val primaryText: Color @Composable get() = MaterialTheme.colorScheme.onBackground
    val secondaryText: Color @Composable get() = MaterialTheme.colorScheme.secondary
    val border: Color @Composable get() = MaterialTheme.colorScheme.outlineVariant
}
