package com.kronifyapp.data.remote.request

/** Cuerpo de PUT /business/opening-hours. */
data class OpeningHourRequest(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
)
