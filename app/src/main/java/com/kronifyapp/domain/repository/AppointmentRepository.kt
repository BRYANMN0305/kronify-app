package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.BusinessAppointment

interface AppointmentRepository {
    suspend fun getAgenda(token: String, startDate: String, endDate: String): List<BusinessAppointment>
}
