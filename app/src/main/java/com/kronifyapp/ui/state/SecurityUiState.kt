package com.kronifyapp.ui.state

import com.kronifyapp.domain.model.AuthMethod

data class SecurityUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val methods: List<AuthMethod> = emptyList(),
    val methodsError: String? = null,
    val error: String? = null,
    val successMessage: String? = null,
)
