package com.coloroslauncher.core.fold

import androidx.activity.ComponentActivity
import androidx.window.layout.WindowInfoTracker
import com.coloroslauncher.core.model.FoldState
import com.coloroslauncher.core.model.GridDimensions
import com.coloroslauncher.theme.ThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Observes [FoldState] changes live via Jetpack WindowManager, so the home screen can react to
 * fold/unfold and rotation without any Activity restart (the flow is collected from a
 * lifecycle-aware coroutine scope by the caller, e.g. HomeViewModel).
 */
class FoldStateManager(private val activity: ComponentActivity) {

    fun foldStateFlow(): Flow<FoldState> =
        WindowInfoTracker.getOrCreate(activity)
            .windowLayoutInfo(activity)
            .map(FoldStateMapper::map)

    companion object {
        fun gridDimensionsFor(state: FoldState): GridDimensions = when (state) {
            FoldState.COVER_OR_UNKNOWN -> GridDimensions(
                ThemeConfig.COVER_SCREEN_COLUMNS,
                ThemeConfig.COVER_SCREEN_ROWS,
            )
            FoldState.UNFOLDED_FLAT -> GridDimensions(
                ThemeConfig.UNFOLDED_COLUMNS,
                ThemeConfig.UNFOLDED_ROWS,
            )
            FoldState.FLEX -> GridDimensions(
                ThemeConfig.FLEX_MODE_COLUMNS,
                ThemeConfig.FLEX_MODE_ROWS,
            )
        }

        /** Whether the "Smart Assistant" panel should render side-by-side instead of as a swipe page. */
        fun shouldShowSidePanel(state: FoldState): Boolean = state == FoldState.UNFOLDED_FLAT
    }
}
