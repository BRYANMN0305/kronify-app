package com.kronifyapp.data.remote.request

/** Cuerpo de PATCH /business/. Todos los campos son opcionales en el backend. */
data class BusinessUpdateRequest(
    val name: String? = null,
    val category: String? = null,
    val description: String? = null,
    val address: String? = null,
    val logoUrl: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val whatsApp: String? = null,
)
