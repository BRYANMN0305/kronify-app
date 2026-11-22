package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.request.ServiceRequest
import com.kronifyapp.data.remote.response.ServiceResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ServiceApi {
    @GET("business/services/")
    suspend fun listServices(@Header("Authorization") token: String): List<ServiceResponse>

    @POST("business/services/")
    suspend fun createService(
        @Header("Authorization") token: String,
        @Body request: ServiceRequest,
    ): ServiceResponse

    @PATCH("business/services/{serviceId}")
    suspend fun updateService(
        @Header("Authorization") token: String,
        @Path("serviceId") serviceId: Long,
        @Body request: ServiceRequest,
    ): ServiceResponse

    @DELETE("business/services/{serviceId}")
    suspend fun deleteService(
        @Header("Authorization") token: String,
        @Path("serviceId") serviceId: Long,
    )
}
