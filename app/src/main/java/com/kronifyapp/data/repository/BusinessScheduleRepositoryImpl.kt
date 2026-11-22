package com.kronifyapp.data.repository

import com.kronifyapp.data.mapper.request.toRequest
import com.kronifyapp.data.mapper.response.toDomain
import com.kronifyapp.data.remote.BusinessApi
import com.kronifyapp.domain.model.OpeningHour
import com.kronifyapp.domain.repository.BusinessScheduleRepository
import javax.inject.Inject

class BusinessScheduleRepositoryImpl @Inject constructor(
    private val businessApi: BusinessApi,
) : BusinessScheduleRepository {
    override suspend fun getOpeningHours(token: String): List<OpeningHour> =
        businessApi.getOpeningHours("Bearer $token").map { it.toDomain() }

    override suspend fun updateOpeningHours(token: String, hours: List<OpeningHour>): List<OpeningHour> =
        businessApi.updateOpeningHours("Bearer $token", hours.map { it.toRequest() }).map { it.toDomain() }
}
