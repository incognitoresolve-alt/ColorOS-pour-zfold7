package com.coloroslauncher.theme

import androidx.compose.ui.unit.dp

/**
 * Single entry point for tweaking the ColorOS-style look without touching any UI code.
 * Change the values below to re-skin the launcher (colors, corner radius, animation speed).
 */
object ThemeConfig {

    // --- Palette (light) ---
    const val LIGHT_PRIMARY = 0xFF3478F6
    const val LIGHT_SECONDARY = 0xFFFF6B4A
    const val LIGHT_BACKGROUND = 0xFFF5F6F8
    const val LIGHT_SURFACE = 0xFFFFFFFF
    const val LIGHT_ON_BACKGROUND = 0xFF1B1C1E

    // --- Palette (dark) ---
    const val DARK_PRIMARY = 0xFF7AA6FF
    const val DARK_SECONDARY = 0xFFFF9478
    const val DARK_BACKGROUND = 0xFF121316
    const val DARK_SURFACE = 0xFF1E1F22
    const val DARK_ON_BACKGROUND = 0xFFECEDEF

    // --- Shape: ColorOS "squircle" roundness ---
    val ICON_CORNER_RADIUS = 22.dp
    val FOLDER_CORNER_RADIUS = 28.dp
    val CARD_CORNER_RADIUS = 20.dp
    val DOCK_CORNER_RADIUS = 32.dp

    // --- Grid metrics ---
    const val COVER_SCREEN_COLUMNS = 4
    const val COVER_SCREEN_ROWS = 6
    const val UNFOLDED_COLUMNS = 6
    const val UNFOLDED_ROWS = 5
    const val FLEX_MODE_COLUMNS = 4
    const val FLEX_MODE_ROWS = 5
    const val DOCK_ITEM_COUNT = 5

    // --- Animation durations (ms), ColorOS-like zoom/fade open transition ---
    const val APP_OPEN_ANIM_DURATION_MS = 260
    const val PAGE_SWITCH_ANIM_DURATION_MS = 320
    const val ICON_DRAG_SCALE = 1.12f
}
