package com.sjocol.cleanservices.data.repository

import com.sjocol.cleanservices.domain.model.WorkEntry
import kotlinx.coroutines.flow.Flow

interface WorkEntryRepository {
    suspend fun upsert(entry: WorkEntry): Long
    suspend fun delete(id: Long)
    suspend fun getById(id: Long): WorkEntry?
    fun observeByHouseAndDateRange(houseId: Long, fromDateIso: String, toDateIso: String): Flow<List<WorkEntry>>
    fun observeByDateRange(fromDateIso: String, toDateIso: String): Flow<List<WorkEntry>>
} 