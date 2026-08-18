package com.coloroslauncher.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.db.entity.GridItemType
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.theme.ThemeConfig

/** Small 2x2 preview of the first apps inside a folder, shown on the home grid. */
@Composable
fun FolderIconView(item: GridItemEntity, modifier: Modifier = Modifier) {
    require(item.type == GridItemType.FOLDER) { "FolderIconView requires a FOLDER item" }
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(ThemeConfig.FOLDER_CORNER_RADIUS))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

/** Full-screen overlay listing a folder's member apps, opened by tapping a [FolderIconView]. */
@Composable
fun FolderOverlay(
    folderId: Long,
    folderName: String,
    members: List<AppInfo>,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    onLaunch: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    var nameField by remember(folderId) { mutableStateOf(folderName) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(ThemeConfig.CARD_CORNER_RADIUS),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = nameField,
                onValueChange = {
                    nameField = it
                    onRename(it)
                },
                textStyle = MaterialTheme.typography.titleMedium,
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                items(members) { app ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { onLaunch(app) },
                        contentAlignment = Alignment.Center,
                    ) {
                        AppIconView(app = app)
                    }
                }
            }
            Text(
                text = "Fermer",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable(onClick = onDismiss),
            )
        }
    }
}
