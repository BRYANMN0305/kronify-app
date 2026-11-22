package com.kronifyapp.data.remote.response

data class UserUpdateResponse(
    val name: String = "",
    val lastName: String = "",
    val phoneNumber: String? = null,
    val updatedAt: String? = null,
)
