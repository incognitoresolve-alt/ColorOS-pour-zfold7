package com.coloroslauncher.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = Color(ThemeConfig.LIGHT_PRIMARY),
    secondary = Color(ThemeConfig.LIGHT_SECONDARY),
    background = Color(ThemeConfig.LIGHT_BACKGROUND),
    surface = Color(ThemeConfig.LIGHT_SURFACE),
    onBackground = Color(ThemeConfig.LIGHT_ON_BACKGROUND),
    onSurface = Color(ThemeConfig.LIGHT_ON_BACKGROUND),
)

val DarkColors = darkColorScheme(
    primary = Color(ThemeConfig.DARK_PRIMARY),
    secondary = Color(ThemeConfig.DARK_SECONDARY),
    background = Color(ThemeConfig.DARK_BACKGROUND),
    surface = Color(ThemeConfig.DARK_SURFACE),
    onBackground = Color(ThemeConfig.DARK_ON_BACKGROUND),
    onSurface = Color(ThemeConfig.DARK_ON_BACKGROUND),
)
