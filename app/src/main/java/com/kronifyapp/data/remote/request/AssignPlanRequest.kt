package com.kronifyapp.data.remote.request

/** Cuerpo de POST /business/plan. */
data class AssignPlanRequest(
    val planId: Long,
    val activationCode: String? = null,
)
