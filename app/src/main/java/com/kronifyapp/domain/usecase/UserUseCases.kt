package com.kronifyapp.domain.usecase

import com.kronifyapp.domain.repository.ReviewRepository
import com.kronifyapp.domain.repository.UserRepository

class GetUserProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(token: String) = repository.getProfile(token)
}

class UpdateUserProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(token: String, name: String, lastName: String, phoneNumber: String?) =
        repository.updateProfile(token, name, lastName, phoneNumber)
}

class ChangePasswordUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(token: String, currentPassword: String, newPassword: String) =
        repository.changePassword(token, currentPassword, newPassword)
}

class GetAuthMethodsUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(token: String) = repository.getAuthMethods(token)
}

class GetBusinessRatingUseCase(private val repository: ReviewRepository) {
    suspend operator fun invoke(slug: String) = repository.getBusinessRating(slug)
}
