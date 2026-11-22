package com.kronifyapp.data.mapper.request

import com.kronifyapp.data.remote.request.ServiceRequest
import com.kronifyapp.domain.model.ServiceInput

fun ServiceInput.toRequest() = ServiceRequest(
    name = name.trim(),
    description = description?.trim()?.ifBlank { null },
    durationMinutes = durationMinutes,
    bufferMinutes = bufferMinutes,
    price = price,
)
