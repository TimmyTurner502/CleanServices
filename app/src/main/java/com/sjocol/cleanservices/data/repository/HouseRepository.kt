package com.sjocol.cleanservices.data.repository

import com.sjocol.cleanservices.domain.model.House
import kotlinx.coroutines.flow.Flow

interface HouseRepository {
    fun observeAllSorted(): Flow<List<House>>
    fun search(query: String): Flow<List<House>>
    suspend fun upsert(house: House): Long
    suspend fun delete(id: Long)
    suspend fun getById(id: Long): House?
} 