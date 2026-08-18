package com.coloroslauncher.core.fold

import android.graphics.Rect
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowLayoutInfo
import com.coloroslauncher.core.model.FoldState
import org.junit.Assert.assertEquals
import org.junit.Test

/** Fake used instead of pulling in Robolectric just to exercise the pure mapping logic. */
private class FakeFoldingFeature(override val state: FoldingFeature.State) : FoldingFeature {
    override val bounds: Rect = Rect()
    override val isSeparating: Boolean = true
    override val occlusionType: FoldingFeature.OcclusionType = FoldingFeature.OcclusionType.NONE
    override val orientation: FoldingFeature.Orientation = FoldingFeature.Orientation.VERTICAL
}

class FoldStateMapperTest {

    @Test
    fun `no display features maps to cover-or-unknown`() {
        val info = WindowLayoutInfo(emptyList())

        assertEquals(FoldState.COVER_OR_UNKNOWN, FoldStateMapper.map(info))
    }

    @Test
    fun `flat folding feature maps to unfolded flat`() {
        val info = WindowLayoutInfo(listOf(FakeFoldingFeature(FoldingFeature.State.FLAT)))

        assertEquals(FoldState.UNFOLDED_FLAT, FoldStateMapper.map(info))
    }

    @Test
    fun `half-opened folding feature maps to flex`() {
        val info = WindowLayoutInfo(listOf(FakeFoldingFeature(FoldingFeature.State.HALF_OPENED)))

        assertEquals(FoldState.FLEX, FoldStateMapper.map(info))
    }

    @Test
    fun `non-folding display features are ignored`() {
        val nonFolding = object : DisplayFeature {
            override val bounds: Rect = Rect()
        }
        val info = WindowLayoutInfo(listOf(nonFolding))

        assertEquals(FoldState.COVER_OR_UNKNOWN, FoldStateMapper.map(info))
    }
}
