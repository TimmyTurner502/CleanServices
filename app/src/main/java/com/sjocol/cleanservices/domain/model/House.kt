package com.sjocol.cleanservices.domain.model

import java.time.Instant

data class House(
    val id: Long = 0L,
    val name: String,
    val address: String? = null,
    val photoUri: String? = null,
    val isActive: Boolean = true,
    val createdAtEpochMillis: Long = Instant.now().toEpochMilli(),
    val updatedAtEpochMillis: Long = Instant.now().toEpochMilli()
) 