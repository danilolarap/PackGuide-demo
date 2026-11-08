package com.example.packguide.settings

enum class AppLanguage(val storageKey: String) {
    Spanish("es"),
    English("en");

    companion object {
        fun fromStorage(value: String?): AppLanguage =
            entries.firstOrNull { it.storageKey == value } ?: Spanish
    }
}

data class AppSettings(
    val isDarkTheme: Boolean = false,
    val language: AppLanguage = AppLanguage.Spanish
)
