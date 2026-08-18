package com.coloroslauncher.core.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.coloroslauncher.core.db.entity.FolderEntity
import com.coloroslauncher.core.db.entity.FolderMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity): Long

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Delete
    suspend fun deleteFolder(folder: FolderEntity)

    @Query("SELECT * FROM folders WHERE id = :folderId")
    suspend fun getFolder(folderId: Long): FolderEntity?

    @Query("SELECT * FROM folders WHERE id = :folderId")
    fun observeFolder(folderId: Long): Flow<FolderEntity?>

    @Query("SELECT * FROM folder_members WHERE folderId = :folderId ORDER BY sortOrder")
    fun observeMembers(folderId: Long): Flow<List<FolderMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMember(member: FolderMemberEntity)

    @Query("DELETE FROM folder_members WHERE folderId = :folderId AND componentKey = :componentKey")
    suspend fun removeMember(folderId: Long, componentKey: String)

    @Query("SELECT COUNT(*) FROM folder_members WHERE folderId = :folderId")
    suspend fun memberCount(folderId: Long): Int
}
