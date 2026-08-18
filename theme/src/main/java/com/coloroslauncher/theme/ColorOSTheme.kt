package com.coloroslauncher.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

enum class AppThemeMode { SYSTEM, LIGHT, DARK }

/**
 * On Android 12+, blends in with the rest of the OS by deriving Material You colors from the
 * user's wallpaper (like the system launcher does) instead of always using the fixed ColorOS
 * palette. Older devices, or [useDynamicColor] = false, fall back to [LightColors]/[DarkColors].
 */
@Composable
fun ColorOSTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    useDynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val useDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val dynamicColorSupported = useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = if (dynamicColorSupported) {
        val context = LocalContext.current
        if (useDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (useDark) DarkColors else LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ColorOSTypography,
        shapes = ColorOSShapes,
        content = content,
    )
}
