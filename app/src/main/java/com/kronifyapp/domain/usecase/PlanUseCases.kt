package com.kronifyapp.domain.usecase

import com.kronifyapp.domain.repository.PlanRepository

class GetCurrentPlanUseCase(private val repository: PlanRepository) {
    suspend operator fun invoke(token: String) = repository.getCurrentPlan(token)
}

class GetAvailablePlansUseCase(private val repository: PlanRepository) {
    suspend operator fun invoke() = repository.getAvailablePlans()
}

class AssignPlanUseCase(private val repository: PlanRepository) {
    suspend operator fun invoke(token: String, planId: Long, activationCode: String?) =
        repository.assignPlan(token, planId, activationCode)
}
