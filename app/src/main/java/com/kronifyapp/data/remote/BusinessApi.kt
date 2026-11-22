package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.request.BusinessCreateRequest
import com.kronifyapp.data.remote.request.BusinessLogoUpdateRequest
import com.kronifyapp.data.remote.request.BusinessUpdateRequest
import com.kronifyapp.data.remote.request.OpeningHourRequest
import com.kronifyapp.data.remote.response.BusinessSettingsResponse
import com.kronifyapp.data.remote.response.BusinessUpdateResponse
import com.kronifyapp.data.remote.response.OpeningHourResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.PUT

interface BusinessApi {
    @POST("business/")
    suspend fun createBusiness(
        @Header("Authorization") token: String,
        @Body request: BusinessCreateRequest,
    )

    @GET("business/me")
    suspend fun getBusiness(
        @Header("Authorization") token: String,
    ): BusinessSettingsResponse

    @PATCH("business/")
    suspend fun updateBusiness(
        @Header("Authorization") token: String,
        @Body request: BusinessUpdateRequest,
    ): BusinessUpdateResponse

    @PATCH("business/")
    suspend fun updateLogo(
        @Header("Authorization") token: String,
        @Body request: BusinessLogoUpdateRequest,
    )

    @GET("business/opening-hours")
    suspend fun getOpeningHours(
        @Header("Authorization") token: String,
    ): List<OpeningHourResponse>

    @PUT("business/opening-hours")
    suspend fun updateOpeningHours(
        @Header("Authorization") token: String,
        @Body request: List<OpeningHourRequest>,
    ): List<OpeningHourResponse>
}
