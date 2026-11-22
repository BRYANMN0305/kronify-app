package com.kronifyapp.data.remote.request

data class LoginRequest(val email: String, val password: String)

data class UserRegisterRequest(
    val name: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val passwordHash: String,
    val profileType: String,
)

data class BusinessCreateRequest(
    val name: String,
    val slug: String,
    val category: String,
    val description: String? = null,
    val address: String? = null,
    val phoneNumber: String,
    val email: String,
    val whatsApp: String,
    val ownerWorksAsEmployee: Boolean = true,
)

data class BusinessLogoUpdateRequest(val logoUrl: String)
