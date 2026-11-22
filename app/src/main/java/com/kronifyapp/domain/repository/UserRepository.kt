package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.AuthMethod
import com.kronifyapp.domain.model.UserProfile

interface UserRepository {
    suspend fun getProfile(token: String): UserProfile
    suspend fun updateProfile(token: String, name: String, lastName: String, phoneNumber: String?)
    suspend fun changePassword(token: String, currentPassword: String, newPassword: String)
    suspend fun getAuthMethods(token: String): List<AuthMethod>
}
