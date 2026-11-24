package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.BusinessAppointment
import com.kronifyapp.domain.model.ClientAppointment
import com.kronifyapp.domain.model.RecentBusiness

interface AppointmentRepository {
    suspend fun getAgenda(token: String, startDate: String, endDate: String): List<BusinessAppointment>
    suspend fun getClientHistory(token: String): List<ClientAppointment>
    suspend fun getRecentBusinesses(token: String): List<RecentBusiness>
}
