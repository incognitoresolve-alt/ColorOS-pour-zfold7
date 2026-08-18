package com.coloroslauncher.core.repository

import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.db.entity.GridItemType

/**
 * Pure grid-arithmetic used by [LayoutRepository]. Kept free of Room/Android types so it can be
 * unit-tested directly (see GridLayoutLogicTest).
 */
object GridLayoutLogic {

    /** True when moving [moving] onto the exact same cell as [target] should trigger folder creation. */
    fun isDropOnItem(
        movingPage: Int,
        movingColumn: Int,
        movingRow: Int,
        target: GridItemEntity,
    ): Boolean = target.page == movingPage && target.column == movingColumn && target.row == movingRow

    /** Whether two non-folder app items dropped on each other should merge into a new folder. */
    fun shouldCreateFolder(moving: GridItemEntity, target: GridItemEntity): Boolean =
        moving.type == GridItemType.APP && target.type == GridItemType.APP && moving.id != target.id

    /** Whether dropping [moving] onto [target] should add [moving] to an existing folder. */
    fun shouldJoinFolder(moving: GridItemEntity, target: GridItemEntity): Boolean =
        moving.type == GridItemType.APP && target.type == GridItemType.FOLDER

    /**
     * Finds the nearest free cell to (preferredColumn, preferredRow) on [page], scanning row-major,
     * skipping cells already occupied in [occupied]. Returns null if the page is completely full.
     */
    fun findNearestFreeCell(
        page: Int,
        columns: Int,
        rows: Int,
        preferredColumn: Int,
        preferredRow: Int,
        occupied: Set<Pair<Int, Int>>,
    ): Pair<Int, Int>? {
        if (preferredColumn to preferredRow !in occupied) return preferredColumn to preferredRow
        var best: Pair<Int, Int>? = null
        var bestDistance = Int.MAX_VALUE
        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val cell = col to row
                if (cell in occupied) continue
                val distance = (col - preferredColumn) * (col - preferredColumn) +
                    (row - preferredRow) * (row - preferredRow)
                if (distance < bestDistance) {
                    bestDistance = distance
                    best = cell
                }
            }
        }
        return best
    }

    /** Re-indexes dock slots 0..n-1 after a removal or reorder, preserving relative order. */
    fun reindexDockSlots(itemsInOrder: List<GridItemEntity>): List<GridItemEntity> =
        itemsInOrder.mapIndexed { index, item -> item.copy(dockSlot = index) }

    /** Where a single app should land once placed on the workspace grid, row-major from (0,0). */
    data class GridPlacement(val componentKey: String, val column: Int, val row: Int)

    /**
     * Splits [componentKeys] between the dock (first [dockCapacity]) and page-0 workspace cells
     * (row-major, up to `columns * rows`), for populating an empty home screen on first launch.
     * Any apps beyond dock + page-0 capacity are simply left reachable from the app drawer only.
     */
    fun planInitialSeed(
        componentKeys: List<String>,
        dockCapacity: Int,
        columns: Int,
        rows: Int,
    ): Pair<List<String>, List<GridPlacement>> {
        val dockKeys = componentKeys.take(dockCapacity)
        val gridKeys = componentKeys.drop(dockKeys.size).take(columns * rows)
        val placements = gridKeys.mapIndexed { index, key ->
            GridPlacement(key, column = index % columns, row = index / columns)
        }
        return dockKeys to placements
    }
}
