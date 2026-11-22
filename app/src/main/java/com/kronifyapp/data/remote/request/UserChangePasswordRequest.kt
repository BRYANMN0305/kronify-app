package com.kronifyapp.data.remote.request

data class UserChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)
