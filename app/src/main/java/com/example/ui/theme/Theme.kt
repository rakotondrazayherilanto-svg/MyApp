package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.data.AppColorTheme
import com.example.data.FontScale
import com.example.data.ThemeMode

// ==========================================
// PALETTES PAR THÈME DE COULEUR
// ==========================================

// 1. Navy & Gold (Traditionnel LP3F)
private val LightNavyGold = lightColorScheme(
    primary = Color(0xFF0D3B66),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2EAF4),
    onPrimaryContainer = Color(0xFF051B30),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF064E3B),
    error = Color(0xFFDC2626),
    errorContainer = Color(0xFFFEE2E2),
    onError = Color.White,
    onErrorContainer = Color(0xFF7F1D1D),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F5F9),
    outline = Color(0xFFE2E8F0),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569)
)

private val DarkNavyGold = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF1E5B94),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFF59E0B),
    onSecondary = Color(0xFF422800),
    secondaryContainer = Color(0xFF603C00),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = Color(0xFF6EE7B7),
    background = Color(0xFF0A131F),
    surface = Color(0xFF111D2D),
    surfaceVariant = Color(0xFF1A2B42),
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0)
)

// 2. Emerald & Gold
private val LightEmerald = lightColorScheme(
    primary = Color(0xFF047857),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF022C22),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = Color(0xFF0284C7),
    background = Color(0xFFF6FBF9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEDF7F2),
    outline = Color(0xFFD1E7DD),
    onBackground = Color(0xFF0A2219),
    onSurface = Color(0xFF0A2219)
)

private val DarkEmerald = darkColorScheme(
    primary = Color(0xFF6EE7B7),
    onPrimary = Color(0xFF023E2C),
    primaryContainer = Color(0xFF065F46),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = Color(0xFFFBBF24),
    background = Color(0xFF061A13),
    surface = Color(0xFF0D281E),
    surfaceVariant = Color(0xFF13362A),
    onBackground = Color(0xFFE6F4EA),
    onSurface = Color(0xFFE6F4EA)
)

// 3. Royal Blue
private val LightRoyalBlue = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFFD97706),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF0F4F8),
    outline = Color(0xFFCBD5E1),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

private val DarkRoyalBlue = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF1E3A8A),
    primaryContainer = Color(0xFF1E40AF),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF38BDF8),
    background = Color(0xFF0B132B),
    surface = Color(0xFF111D42),
    surfaceVariant = Color(0xFF1C2A59),
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0)
)

// 4. Imperial Purple
private val LightImperialPurple = lightColorScheme(
    primary = Color(0xFF6B21A8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF3B0764),
    secondary = Color(0xFFF59E0B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = Color(0xFF10B981),
    background = Color(0xFFFAF7FD),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF5EEFB),
    outline = Color(0xFFE9D5FF),
    onBackground = Color(0xFF1E1035),
    onSurface = Color(0xFF1E1035)
)

private val DarkImperialPurple = darkColorScheme(
    primary = Color(0xFFD8B4FE),
    onPrimary = Color(0xFF3B0764),
    primaryContainer = Color(0xFF581C87),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = Color(0xFFFCD34D),
    background = Color(0xFF12071F),
    surface = Color(0xFF1D0E30),
    surfaceVariant = Color(0xFF2B1647),
    onBackground = Color(0xFFF3E8FF),
    onSurface = Color(0xFFF3E8FF)
)

// 5. Burgundy & Copper
private val LightBurgundy = lightColorScheme(
    primary = Color(0xFF881337),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE4E6),
    onPrimaryContainer = Color(0xFF4C0519),
    secondary = Color(0xFFEA580C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFF7C2D12),
    tertiary = Color(0xFF0D9488),
    background = Color(0xFFFDF8F9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF8ECEF),
    outline = Color(0xFFFECDD3),
    onBackground = Color(0xFF280711),
    onSurface = Color(0xFF280711)
)

private val DarkBurgundy = darkColorScheme(
    primary = Color(0xFFFDA4AF),
    onPrimary = Color(0xFF4C0519),
    primaryContainer = Color(0xFF701A31),
    onPrimaryContainer = Color(0xFFFFE4E6),
    secondary = Color(0xFFFB923C),
    background = Color(0xFF1C050B),
    surface = Color(0xFF2A0A13),
    surfaceVariant = Color(0xFF3D121F),
    onBackground = Color(0xFFFFE4E6),
    onSurface = Color(0xFFFFE4E6)
)

fun getColorScheme(colorTheme: AppColorTheme, isDark: Boolean): ColorScheme {
    return when (colorTheme) {
        AppColorTheme.NAVY_GOLD -> if (isDark) DarkNavyGold else LightNavyGold
        AppColorTheme.EMERALD -> if (isDark) DarkEmerald else LightEmerald
        AppColorTheme.ROYAL_BLUE -> if (isDark) DarkRoyalBlue else LightRoyalBlue
        AppColorTheme.IMPERIAL_PURPLE -> if (isDark) DarkImperialPurple else LightImperialPurple
        AppColorTheme.BURGUNDY -> if (isDark) DarkBurgundy else LightBurgundy
    }
}

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    colorTheme: AppColorTheme = AppColorTheme.NAVY_GOLD,
    fontScale: FontScale = FontScale.NORMAL,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> getColorScheme(colorTheme, isDark)
    }

    val currentDensity = LocalDensity.current
    val scaleFactor = when (fontScale) {
        FontScale.COMPACT -> 0.90f
        FontScale.NORMAL -> 1.00f
        FontScale.LARGE -> 1.15f
    }

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = currentDensity.density,
            fontScale = currentDensity.fontScale * scaleFactor
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
