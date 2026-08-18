package com.coloroslauncher.core.model

import android.view.View

/**
 * Screen bounds (relative to [view]'s window) of the icon that was tapped to launch an app,
 * used to anchor a ColorOS-style zoom+fade open transition instead of a generic activity swap.
 */
data class LaunchSource(val view: View, val left: Int, val top: Int, val width: Int, val height: Int)
