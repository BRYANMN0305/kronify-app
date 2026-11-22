package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.BusinessService
import com.kronifyapp.domain.model.Employee
import com.kronifyapp.domain.model.WeeklySchedule

interface EmployeeRepository {
    suspend fun listEmployees(token: String): List<Employee>
    suspend fun getMyEmployee(token: String): Employee
    suspend fun updateSchedulePermission(token: String, employeeId: Long, selfManaged: Boolean): Employee
    suspend fun listWeeklySchedules(token: String, employeeId: Long): List<WeeklySchedule>
    suspend fun upsertWeeklySchedule(
        token: String,
        employeeId: Long,
        dayOfWeek: Int,
        startTime: String,
        endTime: String,
    ): WeeklySchedule
    suspend fun deleteWeeklySchedule(token: String, employeeId: Long, weeklyScheduleId: Long)
    suspend fun listEmployeeServices(token: String, employeeId: Long): List<BusinessService>
    suspend fun addEmployeeServices(token: String, employeeId: Long, serviceIds: List<Long>): List<BusinessService>
    suspend fun removeEmployeeService(token: String, employeeId: Long, serviceId: Long)
    suspend fun toggleOwnerEmployee(token: String, enabled: Boolean): Employee
}
