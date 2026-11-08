package com.example.packguide.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged

private val Context.packGuideSettingsStore: DataStore<Preferences> by preferencesDataStore(
    name = "packguide_settings"
)

class SettingsRepository(context: Context) {
    private val settingsStore = context.applicationContext.packGuideSettingsStore

    val settings: Flow<AppSettings> = settingsStore.data.map { preferences ->
        AppSettings(
            isDarkTheme = preferences[DARK_THEME_KEY] ?: false,
            language = AppLanguage.fromStorage(preferences[LANGUAGE_KEY])
        )
    }.distinctUntilChanged()

    suspend fun setDarkTheme(enabled: Boolean) {
        settingsStore.edit { it[DARK_THEME_KEY] = enabled }
    }

    suspend fun setLanguage(language: AppLanguage) {
        settingsStore.edit { it[LANGUAGE_KEY] = language.storageKey }
    }

    private companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        val LANGUAGE_KEY = stringPreferencesKey("language")
    }
}
