package com.kronifyapp.domain.model

data class BusinessService(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val durationMinutes: Int,
    val bufferMinutes: Int,
    val active: Boolean,
)

data class ServiceInput(
    val name: String,
    val description: String?,
    val durationMinutes: Int,
    val bufferMinutes: Int,
    val price: Double,
)
