package com.kronifyapp.data.mapper.response

import com.kronifyapp.data.remote.response.ServiceResponse
import com.kronifyapp.domain.model.BusinessService

fun ServiceResponse.toDomain() = BusinessService(
    id = id,
    name = name,
    description = description.orEmpty(),
    price = price ?: 0.0,
    durationMinutes = durationMinutes,
    bufferMinutes = bufferMinutes,
    active = active,
)
