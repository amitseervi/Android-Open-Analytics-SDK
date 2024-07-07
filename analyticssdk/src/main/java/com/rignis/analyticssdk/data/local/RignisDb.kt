package com.rignis.analyticssdk.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rignis.analyticssdk.data.local.converters.MapTypeConverter
import com.rignis.analyticssdk.data.local.dao.EventDao
import com.rignis.analyticssdk.data.local.entities.EventEntity

@Database(entities = [EventEntity::class], version = 1)
@TypeConverters(
    value = [
        MapTypeConverter::class
    ]
)
abstract class RignisDb : RoomDatabase() {
    abstract fun eventDao(): EventDao
}