package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.request.BusinessCreateRequest
import com.kronifyapp.data.remote.request.BusinessLogoUpdateRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PATCH

interface BusinessApi {
    @POST("business/")
    suspend fun createBusiness(
        @Header("Authorization") token: String,
        @Body request: BusinessCreateRequest,
    )

    @PATCH("business/")
    suspend fun updateLogo(
        @Header("Authorization") token: String,
        @Body request: BusinessLogoUpdateRequest,
    )
}
