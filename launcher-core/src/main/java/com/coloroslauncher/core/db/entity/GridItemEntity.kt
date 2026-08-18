package com.coloroslauncher.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Kind discriminator stored alongside a grid item row. */
enum class GridItemType { APP, FOLDER, WIDGET }

@Entity(tableName = "grid_items")
data class GridItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: GridItemType,
    val page: Int,
    val column: Int,
    val row: Int,
    /** Populated for APP items; null otherwise. */
    val componentKey: String? = null,
    /** Populated for FOLDER items; null otherwise. */
    val folderId: Long? = null,
    /** Populated for WIDGET items; null otherwise. */
    val appWidgetId: Int? = null,
    val spanColumns: Int = 1,
    val spanRows: Int = 1,
    /** True when this row lives in the fixed dock instead of a workspace page. */
    val inDock: Boolean = false,
    val dockSlot: Int? = null,
)
