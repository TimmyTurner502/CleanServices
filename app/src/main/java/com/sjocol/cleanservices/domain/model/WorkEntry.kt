package com.sjocol.cleanservices.domain.model

data class WorkEntry(
    val id: Long = 0L,
    val houseId: Long,
    val type: WorkType,
    val dateIso: String, // yyyy-MM-dd
    val startTime: String? = null, // HH:mm
    val endTime: String? = null,   // HH:mm
    val peopleCount: Int = 1,
    val peopleNamesCsv: String? = null,
    val notes: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
) 