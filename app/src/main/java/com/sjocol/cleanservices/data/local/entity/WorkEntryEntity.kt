package com.sjocol.cleanservices.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_entries",
    foreignKeys = [
        ForeignKey(
            entity = HouseEntity::class,
            parentColumns = ["id"],
            childColumns = ["houseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["houseId", "dateIso"]), Index(value = ["dateIso"])]
)
data class WorkEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val houseId: Long,
    val type: String, // "LIMPIEZA" | "SERVICIO"
    val dateIso: String, // yyyy-MM-dd
    val startTime: String? = null, // HH:mm
    val endTime: String? = null,   // HH:mm
    val peopleCount: Int = 1,
    val peopleNamesCsv: String? = null,
    val notes: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
) 