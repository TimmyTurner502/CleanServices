package com.sjocol.cleanservices.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sjocol.cleanservices.data.local.entity.WorkEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: WorkEntryEntity): Long

    @Update
    suspend fun update(entry: WorkEntryEntity)

    @Delete
    suspend fun delete(entry: WorkEntryEntity)

    @Query("SELECT * FROM work_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WorkEntryEntity?

    @Query("SELECT * FROM work_entries WHERE dateIso BETWEEN :fromDate AND :toDate ORDER BY dateIso DESC")
    fun observeByDateRange(fromDate: String, toDate: String): Flow<List<WorkEntryEntity>>

    @Query("SELECT * FROM work_entries WHERE houseId = :houseId AND dateIso BETWEEN :fromDate AND :toDate ORDER BY dateIso DESC")
    fun observeByHouseAndDateRange(houseId: Long, fromDate: String, toDate: String): Flow<List<WorkEntryEntity>>

    @Query("SELECT * FROM work_entries WHERE type = :type AND dateIso BETWEEN :fromDate AND :toDate ORDER BY dateIso DESC")
    fun observeByTypeAndDateRange(type: String, fromDate: String, toDate: String): Flow<List<WorkEntryEntity>>
} 