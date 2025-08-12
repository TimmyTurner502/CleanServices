package com.sjocol.cleanservices.data.repository

import com.sjocol.cleanservices.data.local.dao.WorkEntryDao
import com.sjocol.cleanservices.data.local.entity.WorkEntryEntity
import com.sjocol.cleanservices.domain.model.WorkEntry
import com.sjocol.cleanservices.domain.model.WorkType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkEntryRepositoryImpl @Inject constructor(
    private val dao: WorkEntryDao
): WorkEntryRepository {
    override suspend fun upsert(entry: WorkEntry): Long = dao.upsert(entry.toEntity())
    override suspend fun delete(id: Long) {
        dao.getById(id)?.let { dao.delete(it) }
    }
    override suspend fun getById(id: Long): WorkEntry? = dao.getById(id)?.toDomain()
    override fun observeByHouseAndDateRange(houseId: Long, fromDateIso: String, toDateIso: String): Flow<List<WorkEntry>> =
        dao.observeByHouseAndDateRange(houseId, fromDateIso, toDateIso).map { it.map { e -> e.toDomain() } }

    override fun observeByDateRange(fromDateIso: String, toDateIso: String): Flow<List<WorkEntry>> =
        dao.observeByDateRange(fromDateIso, toDateIso).map { it.map { e -> e.toDomain() } }
}

private fun WorkEntryEntity.toDomain(): WorkEntry = WorkEntry(
    id = id,
    houseId = houseId,
    type = if (type == "LIMPIEZA") WorkType.LIMPIEZA else WorkType.SERVICIO,
    dateIso = dateIso,
    startTime = startTime,
    endTime = endTime,
    peopleCount = peopleCount,
    peopleNamesCsv = peopleNamesCsv,
    notes = notes,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis
)

private fun WorkEntry.toEntity(): WorkEntryEntity = WorkEntryEntity(
    id = id,
    houseId = houseId,
    type = type.name,
    dateIso = dateIso,
    startTime = startTime,
    endTime = endTime,
    peopleCount = peopleCount,
    peopleNamesCsv = peopleNamesCsv,
    notes = notes,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis
) 