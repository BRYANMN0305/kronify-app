package com.kronifyapp.data.repository

import com.kronifyapp.data.mapper.response.toDomain
import com.kronifyapp.data.remote.PlanApi
import com.kronifyapp.data.remote.request.AssignPlanRequest
import com.kronifyapp.domain.model.Plan
import com.kronifyapp.domain.model.PlanUsage
import com.kronifyapp.domain.repository.PlanRepository
import javax.inject.Inject

class PlanRepositoryImpl @Inject constructor(
    private val planApi: PlanApi,
) : PlanRepository {
    override suspend fun getCurrentPlan(token: String): PlanUsage =
        planApi.getCurrentPlan("Bearer $token").toDomain()

    override suspend fun getAvailablePlans(): List<Plan> =
        planApi.getAvailablePlans().map { it.toDomain() }

    override suspend fun assignPlan(token: String, planId: Long, activationCode: String?) {
        planApi.assignPlan(
            "Bearer $token",
            AssignPlanRequest(planId = planId, activationCode = activationCode?.ifBlank { null }),
        )
    }
}
