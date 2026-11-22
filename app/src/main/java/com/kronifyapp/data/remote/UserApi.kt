package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.response.UserProfileResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {
    @GET("user/profile")
    suspend fun profile(@Header("Authorization") token: String): UserProfileResponse
}
