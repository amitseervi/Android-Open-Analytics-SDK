package com.rignis.analyticssdk.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [EventEntity::class], version = 1)
@TypeConverters(
    value = [
        MapTypeConverter::class
    ]
)
internal abstract class RignisEventDB : RoomDatabase() {
    abstract fun eventDao(): EventDao
}