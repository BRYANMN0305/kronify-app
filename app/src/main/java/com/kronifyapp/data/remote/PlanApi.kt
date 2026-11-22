package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.request.AssignPlanRequest
import com.kronifyapp.data.remote.response.BusinessPlanAssignmentResponse
import com.kronifyapp.data.remote.response.BusinessPlanUsageResponse
import com.kronifyapp.data.remote.response.PlanResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface PlanApi {
    @GET("business/plan")
    suspend fun getCurrentPlan(@Header("Authorization") token: String): BusinessPlanUsageResponse

    @POST("business/plan")
    suspend fun assignPlan(
        @Header("Authorization") token: String,
        @Body request: AssignPlanRequest,
    ): BusinessPlanAssignmentResponse

    @GET("public/plans")
    suspend fun getAvailablePlans(): List<PlanResponse>
}
