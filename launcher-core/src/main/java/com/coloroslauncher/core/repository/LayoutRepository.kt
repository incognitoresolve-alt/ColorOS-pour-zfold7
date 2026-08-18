package com.coloroslauncher.core.repository

import com.coloroslauncher.core.db.dao.FolderDao
import com.coloroslauncher.core.db.dao.GridItemDao
import com.coloroslauncher.core.db.entity.FolderEntity
import com.coloroslauncher.core.db.entity.FolderMemberEntity
import com.coloroslauncher.core.db.entity.GridItemEntity
import com.coloroslauncher.core.db.entity.GridItemType
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.theme.ThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/** Persists and mutates the home screen grid + dock + folders, backed by Room. */
class LayoutRepository(
    private val gridItemDao: GridItemDao,
    private val folderDao: FolderDao,
) {
    fun observeWorkspaceItems(): Flow<List<GridItemEntity>> = gridItemDao.observeWorkspaceItems()

    fun observeDockItems(): Flow<List<GridItemEntity>> = gridItemDao.observeDockItems()

    fun observeFolderMembers(folderId: Long): Flow<List<FolderMemberEntity>> =
        folderDao.observeMembers(folderId)

    fun observeFolder(folderId: Long): Flow<FolderEntity?> = folderDao.observeFolder(folderId)

    suspend fun placeApp(page: Int, column: Int, row: Int, componentKey: String): Long =
        gridItemDao.insert(
            GridItemEntity(
                type = GridItemType.APP,
                page = page,
                column = column,
                row = row,
                componentKey = componentKey,
            ),
        )

    suspend fun moveItem(item: GridItemEntity, newPage: Int, newColumn: Int, newRow: Int) {
        gridItemDao.update(item.copy(page = newPage, column = newColumn, row = newRow))
    }

    suspend fun removeItem(item: GridItemEntity) {
        gridItemDao.delete(item)
    }

    /** Drops [moving] onto [target]; creates a new folder or joins an existing one per [GridLayoutLogic]. */
    suspend fun handleDrop(moving: GridItemEntity, target: GridItemEntity) {
        when {
            GridLayoutLogic.shouldCreateFolder(moving, target) -> {
                val folderId = folderDao.insertFolder(FolderEntity(name = DEFAULT_FOLDER_NAME))
                folderDao.addMember(FolderMemberEntity(folderId, requireNotNull(moving.componentKey), 0))
                folderDao.addMember(FolderMemberEntity(folderId, requireNotNull(target.componentKey), 1))
                gridItemDao.delete(moving)
                gridItemDao.update(
                    target.copy(
                        type = GridItemType.FOLDER,
                        folderId = folderId,
                        componentKey = null,
                    ),
                )
            }
            GridLayoutLogic.shouldJoinFolder(moving, target) -> {
                val folderId = requireNotNull(target.folderId)
                val count = folderDao.memberCount(folderId)
                folderDao.addMember(FolderMemberEntity(folderId, requireNotNull(moving.componentKey), count))
                gridItemDao.delete(moving)
            }
            else -> moveItem(moving, target.page, target.column, target.row)
        }
    }

    suspend fun renameFolder(folderId: Long, newName: String) {
        folderDao.getFolder(folderId)?.let { folderDao.updateFolder(it.copy(name = newName)) }
    }

    suspend fun removeFromFolder(folderId: Long, componentKey: String) {
        folderDao.removeMember(folderId, componentKey)
    }

    suspend fun pinToDock(slot: Int, componentKey: String) {
        gridItemDao.insert(
            GridItemEntity(
                type = GridItemType.APP,
                page = 0,
                column = 0,
                row = 0,
                componentKey = componentKey,
                inDock = true,
                dockSlot = slot,
            ),
        )
    }

    suspend fun reorderDock(newOrder: List<GridItemEntity>) {
        GridLayoutLogic.reindexDockSlots(newOrder).forEach { gridItemDao.update(it) }
    }

    /**
     * Populates an empty workspace with the device's installed apps on first launch (a fresh
     * Room database otherwise leaves the home screen completely empty). No-ops if the dock or
     * workspace already has anything in it, so this never overwrites a user's own layout.
     */
    suspend fun seedInitialLayoutIfEmpty(apps: List<AppInfo>, columns: Int, rows: Int) {
        if (apps.isEmpty()) return
        if (observeWorkspaceItems().first().isNotEmpty() || observeDockItems().first().isNotEmpty()) return

        val (dockKeys, placements) = GridLayoutLogic.planInitialSeed(
            componentKeys = apps.map(AppInfo::componentKey),
            dockCapacity = ThemeConfig.DOCK_ITEM_COUNT,
            columns = columns,
            rows = rows,
        )
        dockKeys.forEachIndexed { slot, componentKey -> pinToDock(slot, componentKey) }
        placements.forEach { placeApp(page = 0, column = it.column, row = it.row, componentKey = it.componentKey) }
    }

    companion object {
        private const val DEFAULT_FOLDER_NAME = "Dossier"
    }
}
