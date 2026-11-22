package com.kronifyapp.data.mapper.request

import com.kronifyapp.data.remote.request.BusinessUpdateRequest
import com.kronifyapp.domain.model.BusinessUpdate

fun BusinessUpdate.toRequest() = BusinessUpdateRequest(
    name = name.trim().ifBlank { null },
    category = category.trim().ifBlank { null },
    description = description?.trim()?.ifBlank { null },
    address = address?.trim()?.ifBlank { null },
    logoUrl = logoUrl?.takeIf { it.isNotBlank() },
    email = email.trim().ifBlank { null },
    phoneNumber = phoneNumber.trim().ifBlank { null },
    whatsApp = whatsApp.trim().ifBlank { null },
)
