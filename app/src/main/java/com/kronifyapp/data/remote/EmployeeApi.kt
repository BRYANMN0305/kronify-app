package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.request.EmployeeSchedulePermissionRequest
import com.kronifyapp.data.remote.request.EmployeeServiceUpdateRequest
import com.kronifyapp.data.remote.request.OwnerEmployeeToggleRequest
import com.kronifyapp.data.remote.request.WeeklyScheduleRequest
import com.kronifyapp.data.remote.response.EmployeeResponse
import com.kronifyapp.data.remote.response.ServiceResponse
import com.kronifyapp.data.remote.response.WeeklyScheduleResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface EmployeeApi {
    @GET("business/employees/")
    suspend fun listEmployees(@Header("Authorization") token: String): List<EmployeeResponse>

    @GET("business/employees/me")
    suspend fun getMyEmployee(@Header("Authorization") token: String): EmployeeResponse

    @PATCH("business/employees/{employeeId}/schedule-permission")
    suspend fun updateSchedulePermission(
        @Header("Authorization") token: String,
        @Path("employeeId") employeeId: Long,
        @Body request: EmployeeSchedulePermissionRequest,
    ): EmployeeResponse

    @GET("business/employees/{employeeId}/weekly-schedules")
    suspend fun listWeeklySchedules(
        @Header("Authorization") token: String,
        @Path("employeeId") employeeId: Long,
    ): List<WeeklyScheduleResponse>

    @POST("business/employees/{employeeId}/weekly-schedules")
    suspend fun upsertWeeklySchedule(
        @Header("Authorization") token: String,
        @Path("employeeId") employeeId: Long,
        @Body request: WeeklyScheduleRequest,
    ): WeeklyScheduleResponse

    @DELETE("business/employees/{employeeId}/weekly-schedules/{weeklyScheduleId}")
    suspend fun deleteWeeklySchedule(
        @Header("Authorization") token: String,
        @Path("employeeId") employeeId: Long,
        @Path("weeklyScheduleId") weeklyScheduleId: Long,
    )

    @GET("business/employees/{employeeId}/services")
    suspend fun listEmployeeServices(
        @Header("Authorization") token: String,
        @Path("employeeId") employeeId: Long,
    ): List<ServiceResponse>

    @PATCH("business/employees/{employeeId}/services")
    suspend fun addEmployeeServices(
        @Header("Authorization") token: String,
        @Path("employeeId") employeeId: Long,
        @Body request: EmployeeServiceUpdateRequest,
    ): List<ServiceResponse>

    @DELETE("business/employees/{employeeId}/services/{serviceId}")
    suspend fun removeEmployeeService(
        @Header("Authorization") token: String,
        @Path("employeeId") employeeId: Long,
        @Path("serviceId") serviceId: Long,
    )

    @POST("business/employees/owner/toggle")
    suspend fun toggleOwnerEmployee(
        @Header("Authorization") token: String,
        @Body request: OwnerEmployeeToggleRequest,
    ): EmployeeResponse
}
