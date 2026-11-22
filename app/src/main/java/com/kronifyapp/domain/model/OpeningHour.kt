package com.kronifyapp.domain.model

/** Un día del horario de atención del negocio. dayOfWeek: 1 = lunes, 7 = domingo. */
data class OpeningHour(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
)
