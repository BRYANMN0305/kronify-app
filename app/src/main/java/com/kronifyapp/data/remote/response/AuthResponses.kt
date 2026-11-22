package com.kronifyapp.data.remote.response

data class TokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
)

data class UserProfileResponse(
    val userId: Long,
    val profileType: String,
)

data class UploadResponse(val url: String)
