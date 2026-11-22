package com.kronifyapp.data.mapper.request

import com.kronifyapp.data.remote.request.OpeningHourRequest
import com.kronifyapp.domain.model.OpeningHour

/** El backend espera "HH:mm:ss"; la UI trabaja con "HH:mm". */
fun OpeningHour.toRequest() = OpeningHourRequest(
    dayOfWeek = dayOfWeek,
    startTime = normalizeTime(startTime),
    endTime = normalizeTime(endTime),
)

private fun normalizeTime(value: String): String {
    val clean = value.trim()
    return when (clean.length) {
        5 -> "$clean:00"
        8 -> clean
        else -> clean
    }
}
