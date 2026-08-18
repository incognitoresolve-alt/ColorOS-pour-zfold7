package com.coloroslauncher.core.model

/**
 * Simplified representation of the device's physical posture, derived from
 * [androidx.window.layout.FoldingFeature]. Kept as a pure enum + mapping function so the
 * mapping logic is unit-testable without any window/activity dependency.
 */
enum class FoldState {
    /** No folding feature reported, or the device is a single-screen phone. Also used for cover screen. */
    COVER_OR_UNKNOWN,

    /** Fully unfolded, flat inner screen. */
    UNFOLDED_FLAT,

    /** Half-opened / tabletop / book posture (Flex Mode). */
    FLEX,
}

/** Grid dimensions the home screen should use for a given [FoldState]. */
data class GridDimensions(val columns: Int, val rows: Int)
