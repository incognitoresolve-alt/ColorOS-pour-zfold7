package com.coloroslauncher.core.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.coloroslauncher.core.db.entity.GridItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GridItemDao {
    @Query("SELECT * FROM grid_items WHERE inDock = 0 ORDER BY page, row, column")
    fun observeWorkspaceItems(): Flow<List<GridItemEntity>>

    @Query("SELECT * FROM grid_items WHERE inDock = 1 ORDER BY dockSlot")
    fun observeDockItems(): Flow<List<GridItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GridItemEntity): Long

    @Update
    suspend fun update(item: GridItemEntity)

    @Delete
    suspend fun delete(item: GridItemEntity)

    @Query("SELECT * FROM grid_items WHERE page = :page AND column = :column AND row = :row AND inDock = 0 LIMIT 1")
    suspend fun findAt(page: Int, column: Int, row: Int): GridItemEntity?

    @Query("DELETE FROM grid_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
