package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.OpeningHour

interface BusinessScheduleRepository {
    suspend fun getOpeningHours(token: String): List<OpeningHour>
    suspend fun updateOpeningHours(token: String, hours: List<OpeningHour>): List<OpeningHour>
}
