package com.kronifyapp.ui.state

import com.kronifyapp.domain.model.BusinessService

data class ServicesUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val services: List<BusinessService> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
)
