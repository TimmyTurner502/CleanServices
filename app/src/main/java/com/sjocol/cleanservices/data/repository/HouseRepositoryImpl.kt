package com.sjocol.cleanservices.data.repository

import com.sjocol.cleanservices.data.local.dao.HouseDao
import com.sjocol.cleanservices.data.local.entity.HouseEntity
import com.sjocol.cleanservices.domain.model.House
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseRepositoryImpl @Inject constructor(
    private val houseDao: HouseDao
) : HouseRepository {

    override fun observeAllSorted(): Flow<List<House>> =
        houseDao.observeAllSortedByName().map { list -> list.map { it.toDomain() } }

    override fun search(query: String): Flow<List<House>> =
        houseDao.search("%$query%").map { list -> list.map { it.toDomain() } }

    override suspend fun upsert(house: House): Long =
        houseDao.upsert(house.toEntity())

    override suspend fun delete(id: Long) {
        val entity = houseDao.getById(id) ?: return
        houseDao.delete(entity)
    }

    override suspend fun getById(id: Long): House? = houseDao.getById(id)?.toDomain()
}

private fun HouseEntity.toDomain(): House = House(
    id = id,
    name = name,
    address = address,
    photoUri = photoUri,
    isActive = isActive,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis
)

private fun House.toEntity(): HouseEntity = HouseEntity(
    id = id,
    name = name,
    address = address,
    photoUri = photoUri,
    isActive = isActive,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis
) 