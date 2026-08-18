package com.coloroslauncher.core.repository

import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.db.entity.GridItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GridLayoutLogicTest {

    private fun appItem(id: Long, page: Int = 0, column: Int = 0, row: Int = 0) = GridItemEntity(
        id = id,
        type = GridItemType.APP,
        page = page,
        column = column,
        row = row,
        componentKey = "pkg$id/Activity/0",
    )

    private fun folderItem(id: Long, page: Int = 0, column: Int = 0, row: Int = 0) = GridItemEntity(
        id = id,
        type = GridItemType.FOLDER,
        page = page,
        column = column,
        row = row,
        folderId = id,
    )

    @Test
    fun `two apps dropped on each other should create a folder`() {
        val moving = appItem(id = 1)
        val target = appItem(id = 2, column = 1)

        assertTrue(GridLayoutLogic.shouldCreateFolder(moving, target))
        assertFalse(GridLayoutLogic.shouldJoinFolder(moving, target))
    }

    @Test
    fun `an app cannot create a folder with itself`() {
        val item = appItem(id = 1)

        assertFalse(GridLayoutLogic.shouldCreateFolder(item, item))
    }

    @Test
    fun `an app dropped on a folder should join it, not create a new one`() {
        val moving = appItem(id = 1)
        val target = folderItem(id = 2, column = 1)

        assertTrue(GridLayoutLogic.shouldJoinFolder(moving, target))
        assertFalse(GridLayoutLogic.shouldCreateFolder(moving, target))
    }

    @Test
    fun `two folders dropped on each other do not merge`() {
        val moving = folderItem(id = 1)
        val target = folderItem(id = 2, column = 1)

        assertFalse(GridLayoutLogic.shouldCreateFolder(moving, target))
        assertFalse(GridLayoutLogic.shouldJoinFolder(moving, target))
    }

    @Test
    fun `nearest free cell returns the preferred cell when it is empty`() {
        val result = GridLayoutLogic.findNearestFreeCell(
            page = 0,
            columns = 4,
            rows = 6,
            preferredColumn = 2,
            preferredRow = 3,
            occupied = emptySet(),
        )

        assertEquals(2 to 3, result)
    }

    @Test
    fun `nearest free cell skips occupied cells and picks the closest alternative`() {
        val occupied = setOf(2 to 3, 1 to 3, 3 to 3, 2 to 2, 2 to 4)

        val result = GridLayoutLogic.findNearestFreeCell(
            page = 0,
            columns = 4,
            rows = 6,
            preferredColumn = 2,
            preferredRow = 3,
            occupied = occupied,
        )

        // All 4 direct neighbours are occupied; the nearest remaining free cell is one of the
        // diagonal corners equidistant from (2,3).
        assertTrue(result in setOf(1 to 2, 3 to 2, 1 to 4, 3 to 4))
    }

    @Test
    fun `nearest free cell returns null when the whole page is full`() {
        val occupied = (0 until 2).flatMap { row -> (0 until 2).map { col -> col to row } }.toSet()

        val result = GridLayoutLogic.findNearestFreeCell(
            page = 0,
            columns = 2,
            rows = 2,
            preferredColumn = 0,
            preferredRow = 0,
            occupied = occupied,
        )

        assertNull(result)
    }

    @Test
    fun `reindexing dock slots preserves order and renumbers from zero`() {
        val items = listOf(
            appItem(id = 5).copy(inDock = true, dockSlot = 3),
            appItem(id = 6).copy(inDock = true, dockSlot = 7),
            appItem(id = 7).copy(inDock = true, dockSlot = 9),
        )

        val reindexed = GridLayoutLogic.reindexDockSlots(items)

        assertEquals(listOf(0, 1, 2), reindexed.map { it.dockSlot })
        assertEquals(listOf(5L, 6L, 7L), reindexed.map { it.id })
    }
}
