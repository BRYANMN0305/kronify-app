package com.kronifyapp.data.mapper.response

import com.kronifyapp.data.remote.response.BusinessSettingsResponse
import com.kronifyapp.domain.model.BusinessProfile

fun BusinessSettingsResponse.toDomain() = BusinessProfile(
    name = name.orEmpty(),
    category = category.orEmpty(),
    description = description.orEmpty(),
    address = address.orEmpty(),
    logoUrl = logoUrl?.takeIf { it.isNotBlank() },
    email = email.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    whatsApp = whatsApp.orEmpty(),
)
