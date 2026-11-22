package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.Plan
import com.kronifyapp.domain.model.PlanUsage

interface PlanRepository {
    suspend fun getCurrentPlan(token: String): PlanUsage
    suspend fun getAvailablePlans(): List<Plan>
    suspend fun assignPlan(token: String, planId: Long, activationCode: String?)
}
