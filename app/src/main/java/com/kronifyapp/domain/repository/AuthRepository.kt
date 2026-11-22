package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.AuthSession
import com.kronifyapp.domain.model.BusinessInput
import com.kronifyapp.domain.model.LoginCredentials
import com.kronifyapp.domain.model.Registration
import com.kronifyapp.domain.model.LogoImage

interface AuthRepository {
    suspend fun login(credentials: LoginCredentials): AuthSession
    suspend fun register(registration: Registration): AuthSession
    suspend fun createBusiness(token: String, business: BusinessInput)
    suspend fun uploadBusinessLogo(token: String, logo: LogoImage)
}
