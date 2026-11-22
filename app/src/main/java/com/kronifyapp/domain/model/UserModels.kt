package com.kronifyapp.domain.model

data class UserProfile(
    val userId: Long,
    val name: String,
    val lastName: String,
    val phoneNumber: String?,
    val email: String,
    val profileType: String,
    val active: Boolean,
)

data class AuthMethod(
    val type: String,
    val provider: String?,
    val email: String,
    val linkedAt: String?,
)

data class BusinessRating(
    val average: Double = 0.0,
    val count: Int = 0,
)
