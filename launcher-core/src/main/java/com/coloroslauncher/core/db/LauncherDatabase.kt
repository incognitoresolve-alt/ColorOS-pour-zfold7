package com.coloroslauncher.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coloroslauncher.core.db.dao.FolderDao
import com.coloroslauncher.core.db.dao.GridItemDao
import com.coloroslauncher.core.db.entity.FolderEntity
import com.coloroslauncher.core.db.entity.FolderMemberEntity
import com.coloroslauncher.core.db.entity.GridItemEntity

@Database(
    entities = [GridItemEntity::class, FolderEntity::class, FolderMemberEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class LauncherDatabase : RoomDatabase() {
    abstract fun gridItemDao(): GridItemDao
    abstract fun folderDao(): FolderDao

    companion object {
        @Volatile
        private var instance: LauncherDatabase? = null

        fun getInstance(context: Context): LauncherDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LauncherDatabase::class.java,
                    "launcher.db",
                ).build().also { instance = it }
            }
    }
}
