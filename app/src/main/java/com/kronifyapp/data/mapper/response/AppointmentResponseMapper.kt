package com.kronifyapp.data.mapper.response

import com.kronifyapp.data.remote.response.AppointmentResponse
import com.kronifyapp.domain.model.BusinessAppointment

fun AppointmentResponse.toDomain() = BusinessAppointment(
    appointmentId = appointmentId,
    serviceName = serviceName,
    employeeName = employeeName,
    customerName = customerName.orEmpty().ifBlank { "Cliente" },
    startAt = startAt,
    endAt = endAt,
    status = status,
    createdAt = createdAt.orEmpty(),
)
