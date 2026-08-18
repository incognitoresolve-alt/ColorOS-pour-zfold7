package com.coloroslauncher.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.model.AppInfo

/**
 * Horizontal swipe between workspace pages with a light parallax effect on the background,
 * evoking ColorOS's depth transition (requirement #9). Deliberately paints no background of its
 * own so the system wallpaper (shown behind the window, see Theme.ColorOSLauncher) stays visible
 * through the grid, like a real launcher.
 */
@Composable
fun WorkspacePager(
    pageCount: Int,
    itemsByPage: Map<Int, List<GridItemEntity>>,
    columns: Int,
    rows: Int,
    appsByComponentKey: Map<String, AppInfo>,
    onTap: (GridItemEntity) -> Unit,
    onMove: (item: GridItemEntity, page: Int, column: Int, row: Int) -> Unit,
    onDrop: (moving: GridItemEntity, target: GridItemEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Column(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            GridCanvas(
                items = itemsByPage[page].orEmpty(),
                columns = columns,
                rows = rows,
                appsByComponentKey = appsByComponentKey,
                modifier = Modifier.fillMaxSize(),
                onTap = onTap,
                onMove = { item, column, row -> onMove(item, page, column, row) },
                onDrop = onDrop,
            )
        }

        if (pageCount > 1) {
            PageIndicator(pageCount = pageCount, currentPage = pagerState.currentPage)
        }
    }
}

@Composable
private fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(pageCount) { page ->
            val selected = page == currentPage
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(if (selected) 7.dp else 5.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.onBackground.copy(alpha = if (selected) 0.9f else 0.4f),
                    ),
            )
        }
    }
}
