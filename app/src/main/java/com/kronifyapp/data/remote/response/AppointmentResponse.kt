package com.kronifyapp.data.remote.response

data class AppointmentResponse(
    val appointmentId: Long = 0L,
    val businessId: Long = 0L,
    val serviceId: Long = 0L,
    val serviceName: String = "",
    val serviceDurationMinutes: Int = 0,
    val employeeId: Long = 0L,
    val employeeName: String = "",
    val customerId: Long? = null,
    val customerName: String? = null,
    val customerPhone: String? = null,
    val customerEmail: String? = null,
    val startAt: String = "",
    val endAt: String = "",
    val status: String = "",
    val origin: String = "",
    val createdAt: String? = null,
)
