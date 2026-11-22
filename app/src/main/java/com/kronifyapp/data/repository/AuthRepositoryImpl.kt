package com.kronifyapp.data.repository

import com.kronifyapp.data.mapper.request.toRequest
import com.kronifyapp.data.mapper.response.toDomain
import com.kronifyapp.data.remote.AuthApi
import com.kronifyapp.data.remote.BusinessApi
import com.kronifyapp.data.remote.UserApi
import com.kronifyapp.data.remote.UploadApi
import com.kronifyapp.data.remote.request.BusinessLogoUpdateRequest
import com.kronifyapp.data.remote.request.OAuthProfileRequest
import com.kronifyapp.domain.model.AuthSession
import com.kronifyapp.domain.model.BusinessInput
import com.kronifyapp.domain.model.LoginCredentials
import com.kronifyapp.domain.model.Registration
import com.kronifyapp.domain.model.LogoImage
import com.kronifyapp.domain.repository.AuthRepository
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val businessApi: BusinessApi,
    private val userApi: UserApi,
    private val uploadApi: UploadApi,
) : AuthRepository {
    override suspend fun login(credentials: LoginCredentials): AuthSession = session(authApi.login(credentials.toRequest()))

    override suspend fun register(registration: Registration): AuthSession = session(authApi.register(registration.toRequest()))

    override suspend fun selectOAuthProfile(token: String, profileType: String): AuthSession =
        session(authApi.selectOAuthProfile("Bearer $token", OAuthProfileRequest(profileType)))

    override suspend fun createBusiness(token: String, business: BusinessInput) {
        businessApi.createBusiness("Bearer $token", business.toRequest())
    }

    override suspend fun uploadBusinessLogo(token: String, logo: LogoImage) {
        val body = logo.bytes.toRequestBody(logo.mimeType.toMediaTypeOrNull())
        val file = MultipartBody.Part.createFormData("file", "logo", body)
        val uploaded = uploadApi.upload("Bearer $token", file)
        businessApi.updateLogo("Bearer $token", BusinessLogoUpdateRequest(uploaded.url))
    }

    private suspend fun session(response: com.kronifyapp.data.remote.response.TokenResponse): AuthSession {
        val profile = userApi.profile("Bearer ${response.accessToken}")
        return response.toDomain(profile.userId, profile.profileType)
    }
}
