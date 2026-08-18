package com.coloroslauncher.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.db.entity.GridItemType
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.core.model.LaunchSource
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
    onLaunch: (app: AppInfo, source: LaunchSource?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var nameField by remember(folderId) { mutableStateOf(folderName) }

    // Pops the card in with a gentle spring instead of appearing instantly.
    var entered by remember(folderId) { mutableStateOf(false) }
    val entrance by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "folderEntrance",
    )
    LaunchedEffect(folderId) { entered = true }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = 0.85f + entrance * 0.15f, scaleY = 0.85f + entrance * 0.15f)
            .alpha(entrance),
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
                    val (positionModifier, launchSourceProvider) = rememberLaunchSourceCapture()
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .then(positionModifier)
                            .clickable { onLaunch(app, launchSourceProvider()) },
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
