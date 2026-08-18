package com.coloroslauncher.core.viewmodel

import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.core.model.FoldState
import com.coloroslauncher.core.model.GridDimensions
import com.coloroslauncher.theme.AppThemeMode

data class HomeUiState(
    val appsByComponentKey: Map<String, AppInfo> = emptyMap(),
    val workspaceItems: List<GridItemEntity> = emptyList(),
    val dockItems: List<GridItemEntity> = emptyList(),
    val foldState: FoldState = FoldState.COVER_OR_UNKNOWN,
    val gridDimensions: GridDimensions = GridDimensions(4, 6),
    val showSidePanel: Boolean = false,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val hiddenPackages: Set<String> = emptySet(),
    val pageCount: Int = 1,
    val openFolderId: Long? = null,
) {
    /** Workspace items with any Focus-mode-hidden apps filtered out. */
    fun visibleWorkspaceItems(): List<GridItemEntity> = workspaceItems.filter { item ->
        val key = item.componentKey ?: return@filter true
        appsByComponentKey[key]?.packageName !in hiddenPackages
    }
}
