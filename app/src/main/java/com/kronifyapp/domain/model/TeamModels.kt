package com.kronifyapp.domain.model

data class Employee(
    val employeeId: Long,
    val name: String,
    val lastName: String,
    val email: String,
    val owner: Boolean,
    val selfManagedSchedule: Boolean,
    val active: Boolean,
) {
    val fullName: String
        get() = "$name $lastName".trim().ifBlank { email }
}

data class WeeklySchedule(
    val weeklyScheduleId: Long,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
)

data class Invitation(
    val invitationId: Long,
    val email: String,
    val status: String,
    val expiresAt: String?,
    val createdAt: String?,
)
