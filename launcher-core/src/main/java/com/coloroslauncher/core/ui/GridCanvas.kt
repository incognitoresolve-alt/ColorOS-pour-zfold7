package com.coloroslauncher.core.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.db.entity.GridItemType
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.core.model.LaunchSource
import com.coloroslauncher.theme.ThemeConfig
import kotlin.math.roundToInt

/**
 * Renders one workspace page as an absolute grid of [items], supporting long-press drag to
 * reorder and drop-on-item to trigger folder creation/merge (delegated to [onDrop]). Icons
 * settle into place with a spring animation whenever their grid position changes (e.g. after a
 * drop elsewhere), and scale up with a haptic tick while actively being dragged.
 */
@Composable
fun GridCanvas(
    items: List<GridItemEntity>,
    columns: Int,
    rows: Int,
    appsByComponentKey: Map<String, AppInfo>,
    modifier: Modifier = Modifier,
    onTap: (item: GridItemEntity, source: LaunchSource?) -> Unit,
    onMove: (item: GridItemEntity, column: Int, row: Int) -> Unit,
    onDrop: (moving: GridItemEntity, target: GridItemEntity) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val cellWidth = maxWidth / columns
        val cellHeight = maxHeight / rows

        var draggingId by remember { mutableStateOf<Long?>(null) }
        var dragOffset by remember { mutableStateOf(Offset.Zero) }

        items.forEach { item ->
            val isDragging = draggingId == item.id
            val baseX = cellWidth * item.column
            val baseY = cellHeight * item.row
            val (dragDx, dragDy) = if (isDragging) pxToDp(dragOffset, density) else 0.dp to 0.dp

            GridCell(
                item = item,
                app = item.componentKey?.let(appsByComponentKey::get),
                cellWidth = cellWidth,
                cellHeight = cellHeight,
                baseX = baseX,
                baseY = baseY,
                dragOffsetX = dragDx,
                dragOffsetY = dragDy,
                isDragging = isDragging,
                onTap = { source -> onTap(item, source) },
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
    baseX: Dp,
    baseY: Dp,
    dragOffsetX: Dp,
    dragOffsetY: Dp,
    isDragging: Boolean,
    onTap: (LaunchSource?) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
) {
    // While dragging, track the finger 1:1 (no spring lag); once released, animate to the
    // resting grid cell so a reorder/drop settles with a gentle spring instead of a hard jump.
    val settledX by animateDpAsState(targetValue = baseX, label = "gridCellX")
    val settledY by animateDpAsState(targetValue = baseY, label = "gridCellY")
    val x = if (isDragging) baseX + dragOffsetX else settledX
    val y = if (isDragging) baseY + dragOffsetY else settledY

    val scale by animateFloatAsState(
        targetValue = if (isDragging) ThemeConfig.ICON_DRAG_SCALE else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "gridCellScale",
    )

    val haptic = LocalHapticFeedback.current
    val (positionModifier, launchSourceProvider) = rememberLaunchSourceCapture()

    Box(
        modifier = Modifier
            .offset(x = x, y = y)
            .size(cellWidth, cellHeight)
            .zIndex(if (isDragging) 1f else 0f)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .then(positionModifier)
            .pointerInput(item.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDragStart()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragEnd,
                )
            }
            .pointerInput(item.id) {
                detectTapGestures(onTap = { onTap(launchSourceProvider()) })
            },
        contentAlignment = Alignment.Center,
    ) {
        when {
            app != null -> AppIconView(app = app)
            item.type == GridItemType.FOLDER -> FolderIconView(item = item)
            else -> Unit
        }
    }
}

private fun pxToDp(offset: Offset, density: Density): Pair<Dp, Dp> = with(density) {
    offset.x.toDp() to offset.y.toDp()
}
