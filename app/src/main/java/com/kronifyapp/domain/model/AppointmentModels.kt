package com.kronifyapp.domain.model

data class BusinessAppointment(
    val appointmentId: Long,
    val serviceName: String,
    val employeeName: String,
    val customerName: String,
    val startAt: String,
    val endAt: String,
    val status: String,
    val createdAt: String,
)
