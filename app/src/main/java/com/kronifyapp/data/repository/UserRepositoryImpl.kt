package com.kronifyapp.data.repository

import com.kronifyapp.data.mapper.response.toDomain
import com.kronifyapp.data.remote.UserApi
import com.kronifyapp.data.remote.request.UserChangePasswordRequest
import com.kronifyapp.data.remote.request.UserUpdateRequest
import com.kronifyapp.domain.model.AuthMethod
import com.kronifyapp.domain.model.UserProfile
import com.kronifyapp.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
) : UserRepository {
    override suspend fun getProfile(token: String): UserProfile =
        userApi.profile("Bearer $token").toDomain()

    override suspend fun updateProfile(token: String, name: String, lastName: String, phoneNumber: String?) {
        userApi.updateProfile(
            "Bearer $token",
            UserUpdateRequest(
                name = name.trim(),
                lastName = lastName.trim(),
                phoneNumber = phoneNumber?.trim()?.ifBlank { null },
            ),
        )
    }

    override suspend fun changePassword(token: String, currentPassword: String, newPassword: String) {
        userApi.changePassword(
            "Bearer $token",
            UserChangePasswordRequest(currentPassword, newPassword),
        )
    }

    override suspend fun getAuthMethods(token: String): List<AuthMethod> =
        userApi.getAuthMethods("Bearer $token").map { it.toDomain() }
}
