package com.kronifyapp.ui.state

import com.kronifyapp.domain.model.Plan
import com.kronifyapp.domain.model.PlanUsage

data class PlanUiState(
    val isLoading: Boolean = true,
    val isAssigning: Boolean = false,
    val assigningPlanId: Long? = null,
    val current: PlanUsage? = null,
    val availablePlans: List<Plan> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
)
