package com.coloroslauncher.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

enum class AppThemeMode { SYSTEM, LIGHT, DARK }

@Composable
fun ColorOSTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val useDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (useDark) DarkColors else LightColors,
        typography = ColorOSTypography,
        shapes = ColorOSShapes,
        content = content,
    )
}
