package com.sjocol.cleanservices.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sjocol.cleanservices.data.local.entity.HouseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(house: HouseEntity): Long

    @Update
    suspend fun update(house: HouseEntity)

    @Delete
    suspend fun delete(house: HouseEntity)

    @Query("SELECT * FROM houses WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): HouseEntity?

    @Query("SELECT * FROM houses ORDER BY name ASC")
    fun observeAllSortedByName(): Flow<List<HouseEntity>>

    @Query("SELECT * FROM houses WHERE name LIKE :query OR address LIKE :query ORDER BY name ASC")
    fun search(query: String): Flow<List<HouseEntity>>
} 