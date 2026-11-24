package com.kronifyapp.domain.model

data class BusinessAppointment(
    val appointmentId: Long,
    val serviceName: String,
    val employeeName: String,
    val customerName: String,
    val startAt: String,
    val endAt: String,
    val status: String,
    val createdAt: String,
)

data class ClientAppointment(
    val appointmentId: Long,
    val businessId: Long?,
    val serviceId: Long?,
    val serviceName: String,
    val serviceDurationMinutes: Int,
    val employeeId: Long?,
    val employeeName: String,
    val startAt: String,
    val endAt: String?,
    val status: String,
)

data class RecentBusiness(
    val businessId: Long,
    val name: String,
    val slug: String,
    val logoUrl: String?,
    val category: String?,
    val address: String?,
    val lastAppointmentAt: String?,
)
