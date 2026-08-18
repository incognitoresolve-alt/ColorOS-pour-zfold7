package com.coloroslauncher.core.model

/** Position of an item within a workspace page's grid. */
data class GridPosition(val page: Int, val column: Int, val row: Int)

/** A single placed element on the home screen grid. */
sealed class GridItem {
    abstract val id: Long
    abstract val position: GridPosition

    data class AppShortcut(
        override val id: Long,
        override val position: GridPosition,
        val componentKey: String,
    ) : GridItem()

    data class Folder(
        override val id: Long,
        override val position: GridPosition,
        val name: String,
        val memberComponentKeys: List<String>,
    ) : GridItem()

    data class Widget(
        override val id: Long,
        override val position: GridPosition,
        val appWidgetId: Int,
        val spanColumns: Int,
        val spanRows: Int,
    ) : GridItem()
}

/** Items pinned to the fixed dock, ordered left to right. */
data class DockItem(val slot: Int, val componentKey: String)
