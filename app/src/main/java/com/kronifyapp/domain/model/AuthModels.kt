package com.kronifyapp.domain.model

data class LoginCredentials(val email: String, val password: String)

data class Registration(
    val name: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val password: String,
    val profileType: String,
)

data class AuthSession(
    val accessToken: String,
    val expiresIn: Long,
    val userId: Long,
    val profileType: String,
)

data class BusinessInput(
    val name: String,
    val slug: String,
    val category: String,
    val phoneNumber: String,
    val address: String,
    val email: String,
)

data class LogoImage(val bytes: ByteArray, val mimeType: String)
