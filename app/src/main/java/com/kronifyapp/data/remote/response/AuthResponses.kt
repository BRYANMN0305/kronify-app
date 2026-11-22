package com.kronifyapp.data.remote.response

data class TokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
)

data class UserProfileResponse(
    val userId: Long,
    val name: String = "",
    val lastName: String = "",
    val phoneNumber: String? = null,
    val email: String = "",
    val profileType: String,
    val active: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class UploadResponse(val url: String)

data class LinkedAuthMethodResponse(
    val type: String = "",
    val provider: String? = null,
    val email: String = "",
    val linkedAt: String? = null,
)
