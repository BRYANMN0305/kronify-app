package com.kronifyapp.data.remote.response

/** Respuesta de GET /business/me. Gson ignora los campos que no usamos. */
data class BusinessSettingsResponse(
    val name: String? = null,
    val category: String? = null,
    val description: String? = null,
    val address: String? = null,
    val logoUrl: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val whatsApp: String? = null,
)

/** Respuesta de PATCH /business/. */
data class BusinessUpdateResponse(
    val updatedAt: String? = null,
)
