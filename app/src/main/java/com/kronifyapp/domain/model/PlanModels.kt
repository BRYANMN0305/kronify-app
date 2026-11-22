package com.kronifyapp.domain.model

data class Plan(
    val planId: Long = 0L,
    val name: String = "",
    val displayName: String = "",
    val description: String = "",
    val monthlyPriceCents: Int = 0,
    val serviceLimit: Int? = null,
    val monthlyAppointmentLimit: Int? = null,
    val employeeLimit: Int? = null,
    val requiresActivationCode: Boolean = false,
)

data class PlanUsage(
    val plan: Plan = Plan(),
    val subscriptionStatus: String = "",
    val serviceCount: Int = 0,
    val currentMonthAppointmentCount: Int = 0,
    val employeeCount: Int = 0,
    val serviceLimitReached: Boolean = false,
    val appointmentLimitReached: Boolean = false,
    val employeeLimitReached: Boolean = false,
    val serviceLimitExceeded: Boolean = false,
    val appointmentLimitExceeded: Boolean = false,
    val employeeLimitExceeded: Boolean = false,
)
