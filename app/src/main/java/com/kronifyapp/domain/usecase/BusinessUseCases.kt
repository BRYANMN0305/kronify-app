package com.kronifyapp.domain.usecase

import com.kronifyapp.domain.model.BusinessUpdate
import com.kronifyapp.domain.model.LogoImage
import com.kronifyapp.domain.repository.BusinessRepository

class GetBusinessUseCase(private val repository: BusinessRepository) {
    suspend operator fun invoke(token: String) = repository.getBusiness(token)
}

class UpdateBusinessUseCase(private val repository: BusinessRepository) {
    suspend operator fun invoke(token: String, business: BusinessUpdate) = repository.updateBusiness(token, business)
}

class UploadLogoUseCase(private val repository: BusinessRepository) {
    suspend operator fun invoke(token: String, logo: LogoImage) = repository.uploadLogo(token, logo)
}
