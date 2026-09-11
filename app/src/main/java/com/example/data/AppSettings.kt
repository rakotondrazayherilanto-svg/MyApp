package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppColorTheme {
    NAVY_GOLD,       // Traditionnel LP3F
    EMERALD,         // Vert Émeraude & Or
    ROYAL_BLUE,      // Bleu Roi Académique
    IMPERIAL_PURPLE, // Violet & Ambre
    BURGUNDY         // Bordeaux & Cuivre
}

enum class FontScale {
    COMPACT, // 0.90x
    NORMAL,  // 1.00x
    LARGE    // 1.15x
}

data class SchoolProfile(
    val schoolName: String = "Lycée Privé FJKM Fenoarivobe Fahasoavana",
    val schoolAcronym: String = "LP3F",
    val schoolMotto: String = "Finoana • Fahazavana • Fandrosoana",
    val schoolAddress: String = "B.P. 12 - Fenoarivobe, Région Bongolava",
    val schoolPhone: String = "034 12 345 67",
    val schoolEmail: String = "direction@lp3f.mg",
    val schoolYear: String = "2026 - 2027",
    val principalTitle: String = "Le Chef d'Établissement (LP3F)",
    val cashierTitle: String = "Le Service Comptabilité (LP3F)",
    val ownerEmail: String = "rakotondrazayherilanto@gmail.com"
)

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorTheme: AppColorTheme = AppColorTheme.NAVY_GOLD,
    val fontScale: FontScale = FontScale.NORMAL,
    val schoolProfile: SchoolProfile = SchoolProfile()
)

class AppSettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lp3f_app_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        val themeModeStr = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val colorThemeStr = prefs.getString(KEY_COLOR_THEME, AppColorTheme.NAVY_GOLD.name) ?: AppColorTheme.NAVY_GOLD.name
        val fontScaleStr = prefs.getString(KEY_FONT_SCALE, FontScale.NORMAL.name) ?: FontScale.NORMAL.name

        val themeMode = runCatching { ThemeMode.valueOf(themeModeStr) }.getOrDefault(ThemeMode.SYSTEM)
        val colorTheme = runCatching { AppColorTheme.valueOf(colorThemeStr) }.getOrDefault(AppColorTheme.NAVY_GOLD)
        val fontScale = runCatching { FontScale.valueOf(fontScaleStr) }.getOrDefault(FontScale.NORMAL)

        val profile = SchoolProfile(
            schoolName = prefs.getString(KEY_SCHOOL_NAME, "Lycée Privé FJKM Fenoarivobe Fahasoavana") ?: "Lycée Privé FJKM Fenoarivobe Fahasoavana",
            schoolAcronym = prefs.getString(KEY_SCHOOL_ACRONYM, "LP3F") ?: "LP3F",
            schoolMotto = prefs.getString(KEY_SCHOOL_MOTTO, "Finoana • Fahazavana • Fandrosoana") ?: "Finoana • Fahazavana • Fandrosoana",
            schoolAddress = prefs.getString(KEY_SCHOOL_ADDRESS, "B.P. 12 - Fenoarivobe, Région Bongolava") ?: "B.P. 12 - Fenoarivobe, Région Bongolava",
            schoolPhone = prefs.getString(KEY_SCHOOL_PHONE, "034 12 345 67") ?: "034 12 345 67",
            schoolEmail = prefs.getString(KEY_SCHOOL_EMAIL, "direction@lp3f.mg") ?: "direction@lp3f.mg",
            schoolYear = prefs.getString(KEY_SCHOOL_YEAR, "2026 - 2027") ?: "2026 - 2027",
            principalTitle = prefs.getString(KEY_PRINCIPAL_TITLE, "Le Chef d'Établissement (LP3F)") ?: "Le Chef d'Établissement (LP3F)",
            cashierTitle = prefs.getString(KEY_CASHIER_TITLE, "Le Service Comptabilité (LP3F)") ?: "Le Service Comptabilité (LP3F)",
            ownerEmail = prefs.getString(KEY_OWNER_EMAIL, "rakotondrazayherilanto@gmail.com") ?: "rakotondrazayherilanto@gmail.com"
        )

        return AppSettings(
            themeMode = themeMode,
            colorTheme = colorTheme,
            fontScale = fontScale,
            schoolProfile = profile
        )
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun setColorTheme(theme: AppColorTheme) {
        prefs.edit().putString(KEY_COLOR_THEME, theme.name).apply()
        _settings.value = _settings.value.copy(colorTheme = theme)
    }

    fun setFontScale(scale: FontScale) {
        prefs.edit().putString(KEY_FONT_SCALE, scale.name).apply()
        _settings.value = _settings.value.copy(fontScale = scale)
    }

    fun updateSchoolProfile(profile: SchoolProfile) {
        prefs.edit()
            .putString(KEY_SCHOOL_NAME, profile.schoolName)
            .putString(KEY_SCHOOL_ACRONYM, profile.schoolAcronym)
            .putString(KEY_SCHOOL_MOTTO, profile.schoolMotto)
            .putString(KEY_SCHOOL_ADDRESS, profile.schoolAddress)
            .putString(KEY_SCHOOL_PHONE, profile.schoolPhone)
            .putString(KEY_SCHOOL_EMAIL, profile.schoolEmail)
            .putString(KEY_SCHOOL_YEAR, profile.schoolYear)
            .putString(KEY_PRINCIPAL_TITLE, profile.principalTitle)
            .putString(KEY_CASHIER_TITLE, profile.cashierTitle)
            .putString(KEY_OWNER_EMAIL, profile.ownerEmail)
            .apply()

        _settings.value = _settings.value.copy(schoolProfile = profile)
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
        _settings.value = AppSettings()
    }

    companion object {
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_COLOR_THEME = "pref_color_theme"
        private const val KEY_FONT_SCALE = "pref_font_scale"
        private const val KEY_SCHOOL_NAME = "pref_school_name"
        private const val KEY_SCHOOL_ACRONYM = "pref_school_acronym"
        private const val KEY_SCHOOL_MOTTO = "pref_school_motto"
        private const val KEY_SCHOOL_ADDRESS = "pref_school_address"
        private const val KEY_SCHOOL_PHONE = "pref_school_phone"
        private const val KEY_SCHOOL_EMAIL = "pref_school_email"
        private const val KEY_SCHOOL_YEAR = "pref_school_year"
        private const val KEY_PRINCIPAL_TITLE = "pref_principal_title"
        private const val KEY_CASHIER_TITLE = "pref_cashier_title"
        private const val KEY_OWNER_EMAIL = "pref_owner_email"
    }
}
