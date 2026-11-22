package com.kronifyapp.data.repository

import com.kronifyapp.data.mapper.request.toRequest
import com.kronifyapp.data.mapper.response.toDomain
import com.kronifyapp.data.remote.BusinessApi
import com.kronifyapp.data.remote.UploadApi
import com.kronifyapp.data.remote.request.BusinessLogoUpdateRequest
import com.kronifyapp.domain.model.BusinessProfile
import com.kronifyapp.domain.model.BusinessUpdate
import com.kronifyapp.domain.model.LogoImage
import com.kronifyapp.domain.repository.BusinessRepository
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class BusinessRepositoryImpl @Inject constructor(
    private val businessApi: BusinessApi,
    private val uploadApi: UploadApi,
) : BusinessRepository {
    override suspend fun getBusiness(token: String): BusinessProfile =
        businessApi.getBusiness("Bearer $token").toDomain()

    override suspend fun updateBusiness(token: String, business: BusinessUpdate) {
        businessApi.updateBusiness("Bearer $token", business.toRequest())
    }

    override suspend fun uploadLogo(token: String, logo: LogoImage): String {
        val body = logo.bytes.toRequestBody(logo.mimeType.toMediaTypeOrNull())
        val file = MultipartBody.Part.createFormData("file", "logo", body)
        val uploaded = uploadApi.upload("Bearer $token", file)
        businessApi.updateLogo("Bearer $token", BusinessLogoUpdateRequest(uploaded.url))
        return uploaded.url
    }
}
