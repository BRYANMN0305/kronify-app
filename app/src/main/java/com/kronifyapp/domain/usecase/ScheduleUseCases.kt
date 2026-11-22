package com.kronifyapp.domain.usecase

import com.kronifyapp.domain.model.OpeningHour
import com.kronifyapp.domain.repository.BusinessScheduleRepository

class GetOpeningHoursUseCase(private val repository: BusinessScheduleRepository) {
    suspend operator fun invoke(token: String) = repository.getOpeningHours(token)
}

class UpdateOpeningHoursUseCase(private val repository: BusinessScheduleRepository) {
    suspend operator fun invoke(token: String, hours: List<OpeningHour>) = repository.updateOpeningHours(token, hours)
}
