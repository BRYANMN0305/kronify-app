package com.kronifyapp.data.remote.request

data class WeeklyScheduleRequest(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
)

data class EmployeeSchedulePermissionRequest(
    val selfManagedSchedule: Boolean,
)

data class InvitationRequest(val email: String)

data class OwnerEmployeeToggleRequest(val enabled: Boolean)
