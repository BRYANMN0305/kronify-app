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

data class PageAppointmentResponse(
    val content: List<AppointmentResponse> = emptyList(),
    val totalElements: Long = 0L,
    val totalPages: Int = 0,
    val number: Int = 0,
    val size: Int = 0,
)

data class RecentBusinessResponse(
    val businessId: Long = 0L,
    val name: String = "",
    val slug: String = "",
    val logoUrl: String? = null,
    val category: String? = null,
    val address: String? = null,
    val lastAppointmentAt: String? = null,
)
