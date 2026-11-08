package com.example.packguide.ui.screens


import com.example.packguide.ui.theme.PackGuideColors

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.packguide.ui.navigation.NavTab
import com.example.packguide.ui.navigation.TechBottomNavBar
import com.example.packguide.settings.AppLanguage
import com.example.packguide.settings.AppSettings
import com.example.packguide.ui.components.SettingsDialog

@Composable
fun MainScreen(
    settings: AppSettings,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit
) {
    var currentTab by remember { mutableStateOf<NavTab>(NavTab.Scanner) }
    var showSettings by remember { mutableStateOf(false) }
    val scanner: com.example.packguide.scanning.ScannerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val packing = remember { PackingState() }
    val preview: com.example.packguide.ai.PackingPreviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    androidx.compose.runtime.LaunchedEffect(packing.selectedLuggage) { preview.retain(packing.selectedLuggage) }
    androidx.compose.runtime.DisposableEffect(preview) { onDispose { preview.clear() } }

    fun navigateTo(tab: NavTab) {
        currentTab = tab
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PackGuideColors.background,
        bottomBar = {
            TechBottomNavBar(
                selectedTab = currentTab,
                onTabSelected = ::navigateTo,
                tabs = listOf(NavTab.Scanner, NavTab.Catalog, NavTab.Guide, NavTab.Summary),
                currentDestination = currentTab,
                onSettingsClick = { showSettings = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    is NavTab.Scanner -> ScannerScreen(packing = packing, scanner = scanner, onOpenCatalog = { navigateTo(NavTab.Catalog) })
                    is NavTab.Catalog -> CatalogScreen(packing = packing, onCreatePlan = { navigateTo(NavTab.Guide) })
                    is NavTab.Guide -> GuideScreen(packing = packing, scanner = scanner, preview = preview, onNavigateToTab = ::navigateTo)
                    is NavTab.Summary -> SummaryScreen(

                        packing = packing,
                        preview = preview,
                        onNavigateToTab = ::navigateTo
                    )
                }
            }
            if (showSettings) {
                SettingsDialog(
                    language = settings.language,
                    isDarkTheme = settings.isDarkTheme,
                    showLogout = true,
                    onToggleTheme = { onThemeChange(!settings.isDarkTheme) },
                    onToggleLanguage = {
                        onLanguageChange(if (settings.language == AppLanguage.Spanish) AppLanguage.English else AppLanguage.Spanish)
                    },
                    onLogout = {
                        showSettings = false
                        scanner.clearPhotos()
                        preview.clear()
                        onLogout()
                    },
                    onDismiss = { showSettings = false }
                )
            }
        }
    }
}
