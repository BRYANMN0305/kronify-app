package com.kronifyapp.data.remote.response

data class EmployeeResponse(
    val employeeId: Long = 0L,
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val owner: Boolean = false,
    val selfManagedSchedule: Boolean = false,
    val active: Boolean = true,
)

data class WeeklyScheduleResponse(
    val weeklyScheduleId: Long = 0L,
    val dayOfWeek: Int = 1,
    val startTime: String = "",
    val endTime: String = "",
)

data class InvitationResponse(
    val invitationId: Long = 0L,
    val businessId: Long = 0L,
    val email: String = "",
    val status: String = "",
    val expiresAt: String? = null,
    val createdAt: String? = null,
)
