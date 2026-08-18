package com.coloroslauncher.core.fold

import com.coloroslauncher.core.model.FoldState
import com.coloroslauncher.theme.ThemeConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FoldStateManagerCompanionTest {

    @Test
    fun `cover state uses the compact grid`() {
        val dimensions = FoldStateManager.gridDimensionsFor(FoldState.COVER_OR_UNKNOWN)

        assertEquals(ThemeConfig.COVER_SCREEN_COLUMNS, dimensions.columns)
        assertEquals(ThemeConfig.COVER_SCREEN_ROWS, dimensions.rows)
    }

    @Test
    fun `unfolded flat uses the extended grid`() {
        val dimensions = FoldStateManager.gridDimensionsFor(FoldState.UNFOLDED_FLAT)

        assertEquals(ThemeConfig.UNFOLDED_COLUMNS, dimensions.columns)
        assertEquals(ThemeConfig.UNFOLDED_ROWS, dimensions.rows)
    }

    @Test
    fun `flex mode falls back to a compact default grid instead of crashing`() {
        val dimensions = FoldStateManager.gridDimensionsFor(FoldState.FLEX)

        assertEquals(ThemeConfig.FLEX_MODE_COLUMNS, dimensions.columns)
        assertEquals(ThemeConfig.FLEX_MODE_ROWS, dimensions.rows)
    }

    @Test
    fun `only the fully unfolded flat posture shows the side panel`() {
        assertTrue(FoldStateManager.shouldShowSidePanel(FoldState.UNFOLDED_FLAT))
        assertFalse(FoldStateManager.shouldShowSidePanel(FoldState.FLEX))
        assertFalse(FoldStateManager.shouldShowSidePanel(FoldState.COVER_OR_UNKNOWN))
    }
}
