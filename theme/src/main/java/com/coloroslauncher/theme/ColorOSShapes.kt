package com.coloroslauncher.theme

import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * ColorOS-style "squircle" corner radii, applied via RoundedCornerShape for a flat/minimal look
 * (a true superellipse squircle path is intentionally avoided to keep rendering cheap on the grid).
 */
val ColorOSShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(ThemeConfig.ICON_CORNER_RADIUS),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(ThemeConfig.CARD_CORNER_RADIUS),
    large = androidx.compose.foundation.shape.RoundedCornerShape(ThemeConfig.FOLDER_CORNER_RADIUS),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(ThemeConfig.DOCK_CORNER_RADIUS),
)
