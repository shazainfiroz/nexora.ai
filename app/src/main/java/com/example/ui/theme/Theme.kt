package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AzureGlow,
    onPrimary = Color.Black,
    primaryContainer = AzureElectric,
    onPrimaryContainer = Color.White,
    secondary = SafeEmerald,
    onSecondary = Color.Black,
    tertiary = AlertOrange,
    background = GuardianNavyDark,
    surface = GuardianNavySurface,
    surfaceVariant = GuardianNavyCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = EmergencyCrimson,
    onError = Color.White
)

private val HighContrastColorScheme = darkColorScheme(
    primary = HighContrastPrimary,
    onPrimary = Color.Black,
    primaryContainer = HighContrastPrimary,
    onPrimaryContainer = Color.Black,
    secondary = HighContrastSecondary,
    onSecondary = Color.Black,
    tertiary = HighContrastPrimary,
    background = HighContrastBackground,
    surface = HighContrastSurface,
    surfaceVariant = Color(0xFF222222),
    onBackground = HighContrastText,
    onSurface = HighContrastText,
    onSurfaceVariant = Color.White,
    error = HighContrastAlert,
    onError = Color.Black
)

@Composable
fun MyApplicationTheme(
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (highContrast) HighContrastColorScheme else DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
