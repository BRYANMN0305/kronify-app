package com.kronifyapp.domain.model

/** Datos públicos del negocio que se muestran y editan en la app. */
data class BusinessProfile(
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val address: String = "",
    val logoUrl: String? = null,
    val email: String = "",
    val phoneNumber: String = "",
    val whatsApp: String = "",
)

/** Campos editables que se envían al backend al guardar el negocio. */
data class BusinessUpdate(
    val name: String,
    val category: String,
    val description: String?,
    val address: String?,
    val logoUrl: String?,
    val email: String,
    val phoneNumber: String,
    val whatsApp: String,
)
