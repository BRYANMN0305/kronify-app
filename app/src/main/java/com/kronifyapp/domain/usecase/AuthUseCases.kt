package com.kronifyapp.domain.usecase

import com.kronifyapp.domain.model.BusinessInput
import com.kronifyapp.domain.model.LoginCredentials
import com.kronifyapp.domain.model.Registration
import com.kronifyapp.domain.model.LogoImage
import com.kronifyapp.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(credentials: LoginCredentials) = repository.login(credentials)
}

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(registration: Registration) = repository.register(registration)
}

class CreateBusinessUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(token: String, business: BusinessInput) = repository.createBusiness(token, business)
}

class UploadBusinessLogoUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(token: String, logo: LogoImage) = repository.uploadBusinessLogo(token, logo)
}
