package com.kronifyapp.data.repository

import com.kronifyapp.data.mapper.response.toDomain
import com.kronifyapp.data.remote.AppointmentApi
import com.kronifyapp.domain.model.BusinessAppointment
import com.kronifyapp.domain.model.ClientAppointment
import com.kronifyapp.domain.model.RecentBusiness
import com.kronifyapp.domain.repository.AppointmentRepository
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(
    private val appointmentApi: AppointmentApi,
) : AppointmentRepository {
    override suspend fun getClientHistory(token: String): List<ClientAppointment> =
        appointmentApi.getClientHistory("Bearer $token").content.map { response ->
            ClientAppointment(
                appointmentId = response.appointmentId,
                businessId = response.businessId.takeIf { it > 0L },
                serviceId = response.serviceId.takeIf { it > 0L },
                serviceName = response.serviceName.ifBlank { "Servicio" },
                serviceDurationMinutes = response.serviceDurationMinutes,
                employeeId = response.employeeId.takeIf { it > 0L },
                employeeName = response.employeeName.ifBlank { "Profesional" },
                startAt = response.startAt,
                endAt = response.endAt,
                status = response.status.ifBlank { "PENDING" },
            )
        }

    override suspend fun getRecentBusinesses(token: String): List<RecentBusiness> =
        appointmentApi.getRecentBusinesses("Bearer $token")
            .filter { it.businessId > 0L && it.name.isNotBlank() }
            .map { response ->
                RecentBusiness(
                    businessId = response.businessId,
                    name = response.name,
                    slug = response.slug,
                    logoUrl = response.logoUrl,
                    category = response.category,
                    address = response.address,
                    lastAppointmentAt = response.lastAppointmentAt,
                )
            }

    override suspend fun getAgenda(
        token: String,
        startDate: String,
        endDate: String,
    ): List<BusinessAppointment> =
        appointmentApi.getAgenda("Bearer $token", startDate, endDate).map { it.toDomain() }
}
