package com.coloroslauncher.core.db

import androidx.room.TypeConverter
import com.coloroslauncher.core.db.entity.GridItemType

class Converters {
    @TypeConverter
    fun fromGridItemType(type: GridItemType): String = type.name

    @TypeConverter
    fun toGridItemType(value: String): GridItemType = GridItemType.valueOf(value)
}
