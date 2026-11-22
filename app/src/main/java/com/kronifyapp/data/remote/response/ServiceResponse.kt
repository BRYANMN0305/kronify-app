package com.kronifyapp.data.remote.response

data class ServiceResponse(
    val id: Long = 0L,
    val name: String = "",
    val description: String? = null,
    val price: Double? = null,
    val durationMinutes: Int = 0,
    val bufferMinutes: Int = 0,
    val active: Boolean = true,
    val createdAt: String? = null,
)
