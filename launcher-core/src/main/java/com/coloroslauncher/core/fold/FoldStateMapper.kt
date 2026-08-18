package com.coloroslauncher.core.fold

import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowLayoutInfo
import com.coloroslauncher.core.model.FoldState

/**
 * Pure mapping from a [WindowLayoutInfo] snapshot to our simplified [FoldState].
 * Extracted from [FoldStateManager] so the branching logic can be unit-tested without any
 * Activity/Window dependency (WindowLayoutInfo/FoldingFeature are plain data holders here).
 */
object FoldStateMapper {

    fun map(windowLayoutInfo: WindowLayoutInfo): FoldState {
        val foldingFeature = windowLayoutInfo.displayFeatures
            .filterIsInstance<FoldingFeature>()
            .firstOrNull() ?: return FoldState.COVER_OR_UNKNOWN

        return when (foldingFeature.state) {
            FoldingFeature.State.FLAT -> FoldState.UNFOLDED_FLAT
            FoldingFeature.State.HALF_OPENED -> FoldState.FLEX
            else -> FoldState.COVER_OR_UNKNOWN
        }
    }
}
