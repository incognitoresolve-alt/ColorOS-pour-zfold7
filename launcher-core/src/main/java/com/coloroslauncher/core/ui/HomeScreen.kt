package com.coloroslauncher.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.db.entity.GridItemType
import com.coloroslauncher.core.model.LaunchSource
import com.coloroslauncher.core.viewmodel.HomeViewModel

/**
 * Top-level home screen: workspace pages + fixed dock, with the "Smart Assistant" panel shown
 * side-by-side when unfolded flat (per the Z Fold7 requirement) instead of as a swipe page.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (uiState.showSidePanel) {
                SmartAssistantPanel(modifier = Modifier.weight(0.32f))
            }

            Column(modifier = Modifier.weight(1f)) {
                val visibleItems = uiState.visibleWorkspaceItems()
                val itemsByPage = visibleItems.groupBy { it.page }

                ClockHeader()

                WorkspacePager(
                    pageCount = uiState.pageCount,
                    itemsByPage = itemsByPage,
                    columns = uiState.gridDimensions.columns,
                    rows = uiState.gridDimensions.rows,
                    appsByComponentKey = uiState.appsByComponentKey,
                    modifier = Modifier.weight(1f),
                    onTap = { item, source -> onItemTap(item, source, viewModel) },
                    onMove = viewModel::moveItem,
                    onDrop = viewModel::handleDrop,
                )

                Dock(
                    items = uiState.dockItems,
                    appsByComponentKey = uiState.appsByComponentKey,
                    onTap = viewModel::launchApp,
                )
            }
        }

        uiState.openFolderId?.let { folderId ->
            val folderDetailsFlow = remember(folderId) { viewModel.observeFolderDetails(folderId) }
            val details by folderDetailsFlow.collectAsState(initial = HomeViewModel.FolderDetails())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
                    .clickable(onClick = viewModel::closeFolder),
                contentAlignment = Alignment.Center,
            ) {
                FolderOverlay(
                    folderId = folderId,
                    folderName = details.name,
                    members = details.members,
                    onDismiss = viewModel::closeFolder,
                    onRename = { name -> viewModel.renameFolder(folderId, name) },
                    onLaunch = { app, source -> viewModel.launchApp(app.componentKey, source) },
                )
            }
        }
    }
}

private fun onItemTap(item: GridItemEntity, source: LaunchSource?, viewModel: HomeViewModel) {
    when (item.type) {
        GridItemType.APP -> item.componentKey?.let { viewModel.launchApp(it, source) }
        GridItemType.FOLDER -> viewModel.openFolder(item.id)
        GridItemType.WIDGET -> Unit
    }
}

/** Phase-3 stub: left-side "Smart Assistant" page, rendered side-by-side when unfolded. */
@Composable
private fun SmartAssistantPanel(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Smart Assistant\n(widgets à venir)",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
