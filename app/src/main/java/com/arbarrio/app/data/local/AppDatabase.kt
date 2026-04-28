package com.arbarrio.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PropertyEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
}
