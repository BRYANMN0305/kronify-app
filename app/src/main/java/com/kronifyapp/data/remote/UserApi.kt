package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.request.UserChangePasswordRequest
import com.kronifyapp.data.remote.request.UserUpdateRequest
import com.kronifyapp.data.remote.response.LinkedAuthMethodResponse
import com.kronifyapp.data.remote.response.UserChangePasswordResponse
import com.kronifyapp.data.remote.response.UserProfileResponse
import com.kronifyapp.data.remote.response.UserUpdateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface UserApi {
    @GET("user/profile")
    suspend fun profile(@Header("Authorization") token: String): UserProfileResponse

    @PATCH("user/updatePassword")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: UserChangePasswordRequest,
    ): UserChangePasswordResponse

    @GET("user/auth-methods")
    suspend fun getAuthMethods(@Header("Authorization") token: String): List<LinkedAuthMethodResponse>

    @PATCH("user/update")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UserUpdateRequest,
    ): UserUpdateResponse
}
