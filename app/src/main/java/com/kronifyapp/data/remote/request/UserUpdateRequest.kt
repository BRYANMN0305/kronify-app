package com.kronifyapp.data.remote.request

data class UserUpdateRequest(
    val name: String,
    val lastName: String,
    val phoneNumber: String?,
)
