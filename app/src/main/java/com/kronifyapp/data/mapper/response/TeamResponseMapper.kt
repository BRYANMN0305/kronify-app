package com.kronifyapp.data.mapper.response

import com.kronifyapp.data.remote.response.EmployeeResponse
import com.kronifyapp.data.remote.response.InvitationResponse
import com.kronifyapp.data.remote.response.WeeklyScheduleResponse
import com.kronifyapp.domain.model.Employee
import com.kronifyapp.domain.model.Invitation
import com.kronifyapp.domain.model.WeeklySchedule

fun EmployeeResponse.toDomain() = Employee(
    employeeId = employeeId,
    name = name,
    lastName = lastName,
    email = email,
    owner = owner,
    selfManagedSchedule = selfManagedSchedule,
    active = active,
)

fun WeeklyScheduleResponse.toDomain() = WeeklySchedule(
    weeklyScheduleId = weeklyScheduleId,
    dayOfWeek = dayOfWeek,
    startTime = startTime.take(5),
    endTime = endTime.take(5),
)

fun InvitationResponse.toDomain() = Invitation(
    invitationId = invitationId,
    email = email,
    status = status,
    expiresAt = expiresAt,
    createdAt = createdAt,
)
