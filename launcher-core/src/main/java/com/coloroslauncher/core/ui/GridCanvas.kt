package com.coloroslauncher.core.ui

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.model.AppInfo
import kotlin.math.roundToInt

/**
 * Renders one workspace page as an absolute grid of [items], supporting long-press drag to
 * reorder and drop-on-item to trigger folder creation/merge (delegated to [onDrop]).
 */
@Composable
fun GridCanvas(
    items: List<GridItemEntity>,
    columns: Int,
    rows: Int,
    appsByComponentKey: Map<String, AppInfo>,
    modifier: Modifier = Modifier,
    onTap: (GridItemEntity) -> Unit,
    onMove: (item: GridItemEntity, column: Int, row: Int) -> Unit,
    onDrop: (moving: GridItemEntity, target: GridItemEntity) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val cellWidth = maxWidth / columns
        val cellHeight = maxHeight / rows

        var draggingId by remember { mutableStateOf<Long?>(null) }
        var dragOffset by remember { mutableStateOf(Offset.Zero) }

        items.forEach { item ->
            val isDragging = draggingId == item.id
            val baseX = cellWidth * item.column
            val baseY = cellHeight * item.row
            val (offsetX, offsetY) = if (isDragging) {
                pxToDp(dragOffset, density)
            } else {
                0.dp to 0.dp
            }

            GridCell(
                item = item,
                app = item.componentKey?.let(appsByComponentKey::get),
                cellWidth = cellWidth,
                cellHeight = cellHeight,
                x = baseX + offsetX,
                y = baseY + offsetY,
                elevated = isDragging,
                onTap = { onTap(item) },
                onDragStart = {
                    draggingId = item.id
                    dragOffset = Offset.Zero
                },
                onDrag = { delta -> dragOffset += delta },
                onDragEnd = {
                    val targetColumn = (item.column + (dragOffset.x / with(density) { cellWidth.toPx() }))
                        .roundToInt()
                        .coerceIn(0, columns - 1)
                    val targetRow = (item.row + (dragOffset.y / with(density) { cellHeight.toPx() }))
                        .roundToInt()
                        .coerceIn(0, rows - 1)
                    draggingId = null
                    dragOffset = Offset.Zero

                    val occupant = items.firstOrNull {
                        it.id != item.id && it.column == targetColumn && it.row == targetRow && it.page == item.page
                    }
                    if (occupant != null) {
                        onDrop(item, occupant)
                    } else {
                        onMove(item, targetColumn, targetRow)
                    }
                },
            )
        }
    }
}

@Composable
private fun GridCell(
    item: GridItemEntity,
    app: AppInfo?,
    cellWidth: Dp,
    cellHeight: Dp,
    x: Dp,
    y: Dp,
    elevated: Boolean,
    onTap: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .offset(x = x, y = y)
            .size(cellWidth, cellHeight)
            .zIndex(if (elevated) 1f else 0f)
            .pointerInput(item.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragEnd,
                )
            }
            .pointerInput(item.id) {
                detectTapGestures(onTap = { onTap() })
            },
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        when {
            app != null -> AppIconView(app = app)
            item.type == com.coloroslauncher.core.db.entity.GridItemType.FOLDER ->
                FolderIconView(item = item)
            else -> Unit
        }
    }
}

private fun pxToDp(offset: Offset, density: Density): Pair<Dp, Dp> = with(density) {
    offset.x.toDp() to offset.y.toDp()
}
