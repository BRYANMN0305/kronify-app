package com.kronifyapp.data.remote.request

data class ServiceRequest(
    val name: String,
    val description: String?,
    val durationMinutes: Int,
    val bufferMinutes: Int,
    val price: Double,
)

data class EmployeeServiceUpdateRequest(
    val serviceIds: List<Long>,
)
