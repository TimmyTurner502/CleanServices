package com.sjocol.cleanservices.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "houses")
data class HouseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val address: String? = null,
    val photoUri: String? = null,
    val isActive: Boolean = true,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
) 