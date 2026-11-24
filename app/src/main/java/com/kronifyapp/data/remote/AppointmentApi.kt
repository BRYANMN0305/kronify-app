package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.response.AppointmentResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import com.kronifyapp.data.remote.response.PageAppointmentResponse
import com.kronifyapp.data.remote.response.RecentBusinessResponse

interface AppointmentApi {
    @GET("appointments/history")
    suspend fun getClientHistory(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100,
        @Query("sort") sort: List<String> = listOf("startAt,desc"),
    ): PageAppointmentResponse

    @GET("appointments/recent-businesses")
    suspend fun getRecentBusinesses(
        @Header("Authorization") token: String,
    ): List<RecentBusinessResponse>

    @GET("business/appointments/agenda")
    suspend fun getAgenda(
        @Header("Authorization") token: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): List<AppointmentResponse>
}
