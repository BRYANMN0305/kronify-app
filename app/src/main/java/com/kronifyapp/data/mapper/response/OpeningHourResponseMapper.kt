package com.kronifyapp.data.mapper.response

import com.kronifyapp.data.remote.response.OpeningHourResponse
import com.kronifyapp.domain.model.OpeningHour

fun OpeningHourResponse.toDomain() = OpeningHour(
    dayOfWeek = dayOfWeek,
    startTime = startTime.take(5),
    endTime = endTime.take(5),
)
