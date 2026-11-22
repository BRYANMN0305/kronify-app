package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.request.LoginRequest
import com.kronifyapp.data.remote.request.OAuthProfileRequest
import com.kronifyapp.data.remote.request.UserRegisterRequest
import com.kronifyapp.data.remote.response.TokenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("auth/register")
    suspend fun register(@Body request: UserRegisterRequest): TokenResponse

    @POST("auth/oauth/profile")
    suspend fun selectOAuthProfile(
        @Header("Authorization") token: String,
        @Body request: OAuthProfileRequest,
    ): TokenResponse
}
