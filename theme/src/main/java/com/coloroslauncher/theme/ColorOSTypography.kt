package com.coloroslauncher.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Thin, light-weight type scale evoking the ColorOS clock/weather widget typography. */
val ColorOSTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Light, fontSize = 96.sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Light, fontSize = 56.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 11.sp),
)
