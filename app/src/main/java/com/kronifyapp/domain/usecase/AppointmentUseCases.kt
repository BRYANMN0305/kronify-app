package com.kronifyapp.domain.usecase

import com.kronifyapp.domain.repository.AppointmentRepository

class GetBusinessAgendaUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(token: String, startDate: String, endDate: String) =
        repository.getAgenda(token, startDate, endDate)
}

class GetClientAppointmentHistoryUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(token: String) = repository.getClientHistory(token)
}

class GetRecentBusinessesUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(token: String) = repository.getRecentBusinesses(token)
}
