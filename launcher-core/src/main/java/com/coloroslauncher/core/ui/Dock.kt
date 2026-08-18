package com.coloroslauncher.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.theme.ThemeConfig

/** Fixed bottom row of favorite apps, always present regardless of the current workspace page. */
@Composable
fun Dock(
    items: List<GridItemEntity>,
    appsByComponentKey: Map<String, AppInfo>,
    onTap: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(ThemeConfig.DOCK_CORNER_RADIUS))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val app = item.componentKey?.let(appsByComponentKey::get) ?: return@forEach
            AppIconView(
                app = app,
                showLabel = false,
                modifier = Modifier.clickable { onTap(app.componentKey) },
            )
        }
    }
}
