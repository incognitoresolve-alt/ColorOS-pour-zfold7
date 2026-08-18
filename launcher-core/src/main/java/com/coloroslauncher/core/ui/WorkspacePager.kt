package com.coloroslauncher.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.model.AppInfo

/**
 * Horizontal swipe between workspace pages with a light parallax effect on the background,
 * evoking ColorOS's depth transition (requirement #9).
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

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
}
