package com.kronifyapp.domain.model

/** Claims del JWT emitido por el backend que necesita la app. */
data class JwtClaims(
    val userId: Long? = null,
    val email: String? = null,
    val name: String? = null,
    val lastName: String? = null,
    val profileType: String? = null,
    val businessId: Long? = null,
    val slug: String? = null,
    val exp: Long? = null,
)
