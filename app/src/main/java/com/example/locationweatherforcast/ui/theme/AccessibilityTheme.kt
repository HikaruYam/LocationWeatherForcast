package com.example.locationweatherforcast.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * High contrast color schemes for accessibility
 */
val HighContrastLightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = Color.Black,
    secondary = Color.Black,
    onSecondary = Color.White,
    secondaryContainer = Color.White,
    onSecondaryContainer = Color.Black,
    tertiary = Color.Black,
    onTertiary = Color.White,
    tertiaryContainer = Color.White,
    onTertiaryContainer = Color.Black,
    error = Color.Red,
    onError = Color.White,
    errorContainer = Color.White,
    onErrorContainer = Color.Red,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color.Black,
    outline = Color.Black,
    outlineVariant = Color.Black,
    scrim = Color.Black,
    inverseSurface = Color.Black,
    inverseOnSurface = Color.White,
    inversePrimary = Color.White,
    surfaceDim = Color(0xFFE0E0E0),
    surfaceBright = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF8F8F8),
    surfaceContainer = Color(0xFFF0F0F0),
    surfaceContainerHigh = Color(0xFFE8E8E8),
    surfaceContainerHighest = Color(0xFFE0E0E0)
)

val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color.Black,
    onPrimaryContainer = Color.White,
    secondary = Color.White,
    onSecondary = Color.Black,
    secondaryContainer = Color.Black,
    onSecondaryContainer = Color.White,
    tertiary = Color.White,
    onTertiary = Color.Black,
    tertiaryContainer = Color.Black,
    onTertiaryContainer = Color.White,
    error = Color.Red,
    onError = Color.White,
    errorContainer = Color.Black,
    onErrorContainer = Color.Red,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color.White,
    outline = Color.White,
    outlineVariant = Color.White,
    scrim = Color.Black,
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    inversePrimary = Color.Black,
    surfaceDim = Color(0xFF0A0A0A),
    surfaceBright = Color(0xFF2A2A2A),
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color(0xFF0F0F0F),
    surfaceContainer = Color(0xFF1F1F1F),
    surfaceContainerHigh = Color(0xFF2F2F2F),
    surfaceContainerHighest = Color(0xFF3F3F3F)
)

/**
 * Accessibility preferences
 */
data class AccessibilityPreferences(
    val isHighContrastEnabled: Boolean = false,
    val isLargeTextEnabled: Boolean = false,
    val isReduceMotionEnabled: Boolean = false
)

val LocalAccessibilityPreferences = staticCompositionLocalOf { AccessibilityPreferences() }

/**
 * Accessible theme with high contrast and other accessibility features
 */
@Composable
fun AccessibleLocationWeatherTheme(
    accessibilityPreferences: AccessibilityPreferences = AccessibilityPreferences(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        accessibilityPreferences.isHighContrastEnabled && darkTheme -> HighContrastDarkColorScheme
        accessibilityPreferences.isHighContrastEnabled && !darkTheme -> HighContrastLightColorScheme
        darkTheme -> darkColorScheme(
            primary = Color(0xFFD0BCFF),
            secondary = Color(0xFFCCC2DC),
            tertiary = Color(0xFFEFB8C8)
        )
        else -> lightColorScheme(
            primary = Color(0xFF6650a4),
            secondary = Color(0xFF625b71),
            tertiary = Color(0xFF7D5260)
        )
    }

    CompositionLocalProvider(LocalAccessibilityPreferences provides accessibilityPreferences) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Helper to check if high contrast is enabled
 */
@Composable
fun isHighContrastEnabled(): Boolean {
    return LocalAccessibilityPreferences.current.isHighContrastEnabled
}

/**
 * Helper to check if large text is enabled
 */
@Composable
fun isLargeTextEnabled(): Boolean {
    return LocalAccessibilityPreferences.current.isLargeTextEnabled
}

/**
 * Helper to check if reduce motion is enabled
 */
@Composable
fun isReduceMotionEnabled(): Boolean {
    return LocalAccessibilityPreferences.current.isReduceMotionEnabled
}