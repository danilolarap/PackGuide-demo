package com.example.packguide.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavTab(
    val route: String,
    val title: String,
    val asciiLabel: String,
    val icon: ImageVector,
    val sysCode: String
) {
    object Scanner : NavTab(
        route = "scanner",
        title = "[ESCÁNER]",
        asciiLabel = "[ ESCÁNER ]",
        icon = Icons.Outlined.QrCodeScanner,
        sysCode = "SYS // CAM_MODULE"
    )

    object Catalog : NavTab(
        route = "catalog",
        title = "[CATÁLOGO]",
        asciiLabel = "[ CATÁLOGO ]",
        icon = Icons.Outlined.Inventory2,
        sysCode = "SYS // INVENTORY_DB"
    )

    object Guide : NavTab(
        route = "guide",
        title = "[GUÍA]",
        asciiLabel = "[ GUÍA ]",
        icon = Icons.Outlined.Checklist,
        sysCode = "SYS // PACKING_GUIDE"
    )

    object Summary : NavTab(
        route = "summary",
        title = "[RESUMEN]",
        asciiLabel = "[ RESUMEN ]",
        icon = Icons.Outlined.Analytics,
        sysCode = "SYS // OVERVIEW_METRICS"
    )

    companion object {
        val tabs = listOf(Scanner, Catalog, Guide, Summary)
    }
}
