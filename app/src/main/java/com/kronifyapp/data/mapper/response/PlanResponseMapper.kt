package com.kronifyapp.data.mapper.response

import com.kronifyapp.data.remote.response.BusinessPlanUsageResponse
import com.kronifyapp.data.remote.response.PlanResponse
import com.kronifyapp.domain.model.Plan
import com.kronifyapp.domain.model.PlanUsage

fun PlanResponse.toDomain() = Plan(
    planId = planId,
    name = name,
    displayName = displayName,
    description = description.orEmpty(),
    monthlyPriceCents = monthlyPriceCents,
    serviceLimit = serviceLimit,
    monthlyAppointmentLimit = monthlyAppointmentLimit,
    employeeLimit = employeeLimit,
    requiresActivationCode = requiresActivationCode,
)

fun BusinessPlanUsageResponse.toDomain() = PlanUsage(
    plan = plan.toDomain(),
    subscriptionStatus = subscriptionStatus,
    serviceCount = serviceCount,
    currentMonthAppointmentCount = currentMonthAppointmentCount,
    employeeCount = employeeCount,
    serviceLimitReached = serviceLimitReached,
    appointmentLimitReached = appointmentLimitReached,
    employeeLimitReached = employeeLimitReached,
    serviceLimitExceeded = serviceLimitExceeded,
    appointmentLimitExceeded = appointmentLimitExceeded,
    employeeLimitExceeded = employeeLimitExceeded,
)
