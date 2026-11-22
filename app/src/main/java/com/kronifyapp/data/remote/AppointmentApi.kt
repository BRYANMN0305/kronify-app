package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.response.AppointmentResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AppointmentApi {
    @GET("business/appointments/agenda")
    suspend fun getAgenda(
        @Header("Authorization") token: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): List<AppointmentResponse>
}
