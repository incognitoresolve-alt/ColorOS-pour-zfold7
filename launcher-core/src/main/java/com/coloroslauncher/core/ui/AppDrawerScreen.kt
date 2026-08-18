package com.coloroslauncher.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.core.model.LaunchSource
import com.coloroslauncher.core.viewmodel.AppDrawerUiState

/** Vertically scrollable app tiroir with a search bar, per requirement #3. */
@Composable
fun AppDrawerScreen(
    uiState: AppDrawerUiState,
    columns: Int,
    onQueryChange: (String) -> Unit,
    onLaunch: (app: AppInfo, source: LaunchSource?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        DrawerSearchBar(query = uiState.query, onQueryChange = onQueryChange)
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            items(uiState.filteredApps, key = { it.componentKey }) { app ->
                val (positionModifier, launchSourceProvider) = rememberLaunchSourceCapture()
                Column(
                    modifier = Modifier
                        .aspectRatio(0.8f)
                        .then(positionModifier)
                        .clickable { onLaunch(app, launchSourceProvider()) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AppIconView(app = app)
                }
            }
        }
    }
}
