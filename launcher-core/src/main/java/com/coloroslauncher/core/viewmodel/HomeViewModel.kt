package com.coloroslauncher.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.fold.FoldStateManager
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.core.model.FoldState
import com.coloroslauncher.core.repository.AppRepository
import com.coloroslauncher.core.repository.LayoutRepository
import com.coloroslauncher.core.repository.SettingsRepository
import com.coloroslauncher.core.repository.WallpaperRepository
import com.coloroslauncher.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val appRepository: AppRepository,
    private val layoutRepository: LayoutRepository,
    private val wallpaperRepository: WallpaperRepository,
    private val settingsRepository: SettingsRepository,
    foldStateFlow: kotlinx.coroutines.flow.Flow<FoldState>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshApps()
        appRepository.observeAppListChanges().onEach { refreshApps() }.launchIn(viewModelScope)

        combine(
            layoutRepository.observeWorkspaceItems(),
            layoutRepository.observeDockItems(),
            foldStateFlow,
            settingsRepository.observeThemeMode(),
            settingsRepository.observeHiddenPackages(),
        ) { workspaceItems, dockItems, foldState, themeMode, hidden ->
            HomeUpdate(workspaceItems, dockItems, foldState, themeMode, hidden)
        }.onEach { update ->
            val dimensions = FoldStateManager.gridDimensionsFor(update.foldState)
            _uiState.value = _uiState.value.copy(
                workspaceItems = update.workspaceItems,
                dockItems = update.dockItems,
                foldState = update.foldState,
                gridDimensions = dimensions,
                showSidePanel = FoldStateManager.shouldShowSidePanel(update.foldState),
                themeMode = update.themeMode,
                hiddenPackages = update.hidden,
                pageCount = maxOf(1, (update.workspaceItems.maxOfOrNull { it.page } ?: 0) + 1),
            )
        }.launchIn(viewModelScope)

        // Only swap the applied system wallpaper when the posture actually transitions
        // (cover <-> unfolded <-> flex), never on unrelated state updates like a theme toggle.
        foldStateFlow.distinctUntilChanged().onEach { foldState ->
            wallpaperRepository.applyForFoldState(foldState)
        }.launchIn(viewModelScope)
    }

    private fun refreshApps() {
        viewModelScope.launch {
            val apps = appRepository.loadInstalledApps()
            _uiState.value = _uiState.value.copy(
                appsByComponentKey = apps.associateBy(AppInfo::componentKey),
            )
            val dimensions = _uiState.value.gridDimensions
            layoutRepository.seedInitialLayoutIfEmpty(apps, dimensions.columns, dimensions.rows)
        }
    }

    fun launchApp(componentKey: String) {
        val app = _uiState.value.appsByComponentKey[componentKey] ?: return
        appRepository.launch(app)
    }

    fun moveItem(item: GridItemEntity, newPage: Int, newColumn: Int, newRow: Int) {
        viewModelScope.launch { layoutRepository.moveItem(item, newPage, newColumn, newRow) }
    }

    fun handleDrop(moving: GridItemEntity, target: GridItemEntity) {
        viewModelScope.launch { layoutRepository.handleDrop(moving, target) }
    }

    fun openFolder(folderId: Long) {
        _uiState.value = _uiState.value.copy(openFolderId = folderId)
    }

    fun closeFolder() {
        _uiState.value = _uiState.value.copy(openFolderId = null)
    }

    fun renameFolder(folderId: Long, newName: String) {
        viewModelScope.launch { layoutRepository.renameFolder(folderId, newName) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setHiddenPackages(packages: Set<String>) {
        viewModelScope.launch { settingsRepository.setHiddenPackages(packages) }
    }

    fun setWallpaper(uri: android.net.Uri) {
        viewModelScope.launch { wallpaperRepository.setWallpaper(_uiState.value.foldState, uri) }
    }

    /** Live folder name + resolved member [AppInfo]s, for [com.coloroslauncher.core.ui.FolderOverlay]. */
    fun observeFolderDetails(folderId: Long): kotlinx.coroutines.flow.Flow<FolderDetails> =
        combine(
            layoutRepository.observeFolder(folderId),
            layoutRepository.observeFolderMembers(folderId),
            uiState,
        ) { folder, members, state ->
            FolderDetails(
                name = folder?.name.orEmpty(),
                members = members.mapNotNull { state.appsByComponentKey[it.componentKey] },
            )
        }

    data class FolderDetails(val name: String = "", val members: List<AppInfo> = emptyList())

    private data class HomeUpdate(
        val workspaceItems: List<GridItemEntity>,
        val dockItems: List<GridItemEntity>,
        val foldState: FoldState,
        val themeMode: AppThemeMode,
        val hidden: Set<String>,
    )

    class Factory(
        private val appRepository: AppRepository,
        private val layoutRepository: LayoutRepository,
        private val wallpaperRepository: WallpaperRepository,
        private val settingsRepository: SettingsRepository,
        private val foldStateFlow: kotlinx.coroutines.flow.Flow<FoldState>,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                appRepository,
                layoutRepository,
                wallpaperRepository,
                settingsRepository,
                foldStateFlow,
            ) as T
        }
    }
}
