package com.example.packguide

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.example.packguide.data.auth.AuthRepository
import com.example.packguide.data.auth.PackGuideDatabase
import com.example.packguide.settings.AppSettings
import com.example.packguide.settings.SettingsRepository
import com.example.packguide.ui.localization.LocalAppLanguage
import com.example.packguide.ui.screens.AuthScreen
import com.example.packguide.ui.screens.MainScreen
import com.example.packguide.ui.theme.PackGuideTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val authRepository by lazy {
        AuthRepository(PackGuideDatabase.getInstance(applicationContext).userAccountDao())
    }
    private val settingsRepository by lazy { SettingsRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                PackGuideRoot(authRepository, settingsRepository)
        }
    }
}

@Composable
private fun PackGuideRoot(authRepository: AuthRepository, settingsRepository: SettingsRepository) {
    var isAuthenticated by remember { mutableStateOf(false) }
    val settings by settingsRepository.settings.collectAsStateWithLifecycle(initialValue = AppSettings())
    val scope = rememberCoroutineScope()

    PackGuideTheme(darkTheme = settings.isDarkTheme) {
        CompositionLocalProvider(LocalAppLanguage provides settings.language) {
            if (isAuthenticated) {
                MainScreen(
                    settings = settings,
                    onThemeChange = { enabled -> scope.launch { settingsRepository.setDarkTheme(enabled) } },
                    onLanguageChange = { language -> scope.launch { settingsRepository.setLanguage(language) } },
                    onLogout = { isAuthenticated = false }
                )
            } else {
                AuthScreen(
                    repository = authRepository,
                    settings = settings,
                    onThemeChange = { enabled -> scope.launch { settingsRepository.setDarkTheme(enabled) } },
                    onLanguageChange = { language -> scope.launch { settingsRepository.setLanguage(language) } },
                    onAuthenticated = { isAuthenticated = true }
                )
            }
        }
    }
}
