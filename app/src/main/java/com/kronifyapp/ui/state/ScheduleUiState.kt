package com.kronifyapp.ui.state

import com.kronifyapp.domain.model.BusinessService

enum class ScheduleMode { OWNER, SELF }

data class ScheduleDayUi(
    val dayOfWeek: Int,
    val weeklyScheduleId: Long? = null,
    val start: String = "",
    val end: String = "",
    val open: Boolean = false,
)

data class EmployeeScheduleItem(
    val employeeId: Long,
    val name: String,
    val lastName: String,
    val email: String,
    val owner: Boolean,
    val selfManagedSchedule: Boolean,
) {
    val fullName: String
        get() = "$name $lastName".trim().ifBlank { email }
}

data class ScheduleUiState(
    val isLoading: Boolean = true,
    val mode: ScheduleMode? = null,

    val businessDays: List<ScheduleDayUi> = emptyScheduleDays(),
    val businessLoading: Boolean = false,
    val businessSaving: Boolean = false,

    val employees: List<EmployeeScheduleItem> = emptyList(),
    val selectedEmployeeId: Long? = null,
    val employeeDays: List<ScheduleDayUi> = emptyScheduleDays(),
    val employeeLoading: Boolean = false,
    val employeeSaving: Boolean = false,
    val permissionBusyId: Long? = null,

    val servicesCatalog: List<BusinessService> = emptyList(),
    val linkedServiceIds: Set<Long> = emptySet(),
    val selectedServiceIds: Set<Long> = emptySet(),
    val loadingServices: Boolean = false,
    val savingServices: Boolean = false,

    val error: String? = null,
    val successMessage: String? = null,
) {
    val selectedEmployee: EmployeeScheduleItem?
        get() = employees.firstOrNull { it.employeeId == selectedEmployeeId }
}

fun emptyScheduleDays(): List<ScheduleDayUi> = (1..7).map { ScheduleDayUi(it) }
