package com.kronifyapp.data.repository

import com.kronifyapp.data.mapper.response.toDomain
import com.kronifyapp.data.remote.EmployeeApi
import com.kronifyapp.data.remote.request.EmployeeSchedulePermissionRequest
import com.kronifyapp.data.remote.request.EmployeeServiceUpdateRequest
import com.kronifyapp.data.remote.request.OwnerEmployeeToggleRequest
import com.kronifyapp.data.remote.request.WeeklyScheduleRequest
import com.kronifyapp.domain.model.BusinessService
import com.kronifyapp.domain.model.Employee
import com.kronifyapp.domain.model.WeeklySchedule
import com.kronifyapp.domain.repository.EmployeeRepository
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val employeeApi: EmployeeApi,
) : EmployeeRepository {
    override suspend fun listEmployees(token: String): List<Employee> =
        employeeApi.listEmployees("Bearer $token").map { it.toDomain() }

    override suspend fun getMyEmployee(token: String): Employee =
        employeeApi.getMyEmployee("Bearer $token").toDomain()

    override suspend fun updateSchedulePermission(
        token: String,
        employeeId: Long,
        selfManaged: Boolean,
    ): Employee = employeeApi.updateSchedulePermission(
        "Bearer $token",
        employeeId,
        EmployeeSchedulePermissionRequest(selfManaged),
    ).toDomain()

    override suspend fun listWeeklySchedules(token: String, employeeId: Long): List<WeeklySchedule> =
        employeeApi.listWeeklySchedules("Bearer $token", employeeId).map { it.toDomain() }

    override suspend fun upsertWeeklySchedule(
        token: String,
        employeeId: Long,
        dayOfWeek: Int,
        startTime: String,
        endTime: String,
    ): WeeklySchedule = employeeApi.upsertWeeklySchedule(
        "Bearer $token",
        employeeId,
        WeeklyScheduleRequest(
            dayOfWeek = dayOfWeek,
            startTime = normalizeTime(startTime),
            endTime = normalizeTime(endTime),
        ),
    ).toDomain()

    override suspend fun deleteWeeklySchedule(token: String, employeeId: Long, weeklyScheduleId: Long) {
        employeeApi.deleteWeeklySchedule("Bearer $token", employeeId, weeklyScheduleId)
    }

    override suspend fun listEmployeeServices(token: String, employeeId: Long): List<BusinessService> =
        employeeApi.listEmployeeServices("Bearer $token", employeeId).map { it.toDomain() }

    override suspend fun addEmployeeServices(
        token: String,
        employeeId: Long,
        serviceIds: List<Long>,
    ): List<BusinessService> = employeeApi.addEmployeeServices(
        "Bearer $token",
        employeeId,
        EmployeeServiceUpdateRequest(serviceIds),
    ).map { it.toDomain() }

    override suspend fun removeEmployeeService(token: String, employeeId: Long, serviceId: Long) {
        employeeApi.removeEmployeeService("Bearer $token", employeeId, serviceId)
    }

    override suspend fun toggleOwnerEmployee(token: String, enabled: Boolean): Employee =
        employeeApi.toggleOwnerEmployee("Bearer $token", OwnerEmployeeToggleRequest(enabled)).toDomain()

    private fun normalizeTime(value: String): String {
        val clean = value.trim()
        return if (clean.length == 5) "$clean:00" else clean
    }
}
