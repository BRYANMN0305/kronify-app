package com.kronifyapp.domain.usecase

import com.kronifyapp.domain.repository.EmployeeRepository

class ListEmployeesUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(token: String) = repository.listEmployees(token)
}

class GetMyEmployeeUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(token: String) = repository.getMyEmployee(token)
}

class UpdateSchedulePermissionUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(token: String, employeeId: Long, selfManaged: Boolean) =
        repository.updateSchedulePermission(token, employeeId, selfManaged)
}

class ListWeeklySchedulesUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(token: String, employeeId: Long) =
        repository.listWeeklySchedules(token, employeeId)
}

class UpsertWeeklyScheduleUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(
        token: String,
        employeeId: Long,
        dayOfWeek: Int,
        startTime: String,
        endTime: String,
    ) = repository.upsertWeeklySchedule(token, employeeId, dayOfWeek, startTime, endTime)
}

class DeleteWeeklyScheduleUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(token: String, employeeId: Long, weeklyScheduleId: Long) =
        repository.deleteWeeklySchedule(token, employeeId, weeklyScheduleId)
}

class ListEmployeeServicesUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(token: String, employeeId: Long) =
        repository.listEmployeeServices(token, employeeId)
}

class AddEmployeeServicesUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(token: String, employeeId: Long, serviceIds: List<Long>) =
        repository.addEmployeeServices(token, employeeId, serviceIds)
}

class RemoveEmployeeServiceUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(token: String, employeeId: Long, serviceId: Long) =
        repository.removeEmployeeService(token, employeeId, serviceId)
}

class ToggleOwnerEmployeeUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(token: String, enabled: Boolean) =
        repository.toggleOwnerEmployee(token, enabled)
}
