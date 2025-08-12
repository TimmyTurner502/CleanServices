package com.sjocol.cleanservices.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sjocol.cleanservices.data.local.dao.HouseDao
import com.sjocol.cleanservices.data.local.dao.WorkEntryDao
import com.sjocol.cleanservices.data.local.entity.HouseEntity
import com.sjocol.cleanservices.data.local.entity.WorkEntryEntity

@Database(
    entities = [HouseEntity::class, WorkEntryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun houseDao(): HouseDao
    abstract fun workEntryDao(): WorkEntryDao
} 