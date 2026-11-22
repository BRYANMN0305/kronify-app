package com.kronifyapp.data.remote.response

data class OpeningHourResponse(
    val businessOpeningHourId: Long = 0L,
    val dayOfWeek: Int = 1,
    val startTime: String = "",
    val endTime: String = "",
)
