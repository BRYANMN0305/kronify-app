package com.kronifyapp.data.repository

import com.kronifyapp.data.mapper.response.toDomain
import com.kronifyapp.data.remote.AppointmentApi
import com.kronifyapp.domain.model.BusinessAppointment
import com.kronifyapp.domain.repository.AppointmentRepository
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(
    private val appointmentApi: AppointmentApi,
) : AppointmentRepository {
    override suspend fun getAgenda(
        token: String,
        startDate: String,
        endDate: String,
    ): List<BusinessAppointment> =
        appointmentApi.getAgenda("Bearer $token", startDate, endDate).map { it.toDomain() }
}
