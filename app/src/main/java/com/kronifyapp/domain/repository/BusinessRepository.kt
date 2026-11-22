package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.BusinessProfile
import com.kronifyapp.domain.model.BusinessUpdate
import com.kronifyapp.domain.model.LogoImage

interface BusinessRepository {
    suspend fun getBusiness(token: String): BusinessProfile
    suspend fun updateBusiness(token: String, business: BusinessUpdate)
    suspend fun uploadLogo(token: String, logo: LogoImage): String
}
