package com.coloroslauncher.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)

/** Membership row: which app belongs to which folder, and in what order. */
@Entity(tableName = "folder_members", primaryKeys = ["folderId", "componentKey"])
data class FolderMemberEntity(
    val folderId: Long,
    val componentKey: String,
    val sortOrder: Int,
)
