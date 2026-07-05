package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RemoteColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CharcoalBg,
    secondary = NeonBlue,
    onSecondary = TextLight,
    tertiary = GlowAmber,
    background = CharcoalBg,
    onBackground = TextLight,
    surface = CarbonSurface,
    onSurface = TextLight,
    surfaceVariant = CarbonSurfaceLight,
    onSurfaceVariant = TextMuted,
    error = PowerRed,
    onError = TextLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Fast-path forced immersive dark theme
    dynamicColor: Boolean = false, // Disable to preserve exact cinematic device UI design
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RemoteColorScheme,
        typography = Typography,
        content = content
    )
}
