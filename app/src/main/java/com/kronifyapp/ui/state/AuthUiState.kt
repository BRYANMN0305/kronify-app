package com.kronifyapp.ui.state

data class AuthUiState(
    val isLoading: Boolean = false,
    val token: String? = null,
    val userId: Long? = null,
    val onboardingStep: Int = 1,
    val profileType: String? = null,
    val error: String? = null,
    val successMessage: String? = null,
    val businessCreated: Boolean = false,
    val logoUploaded: Boolean = false,
    val openingHoursSaved: Boolean = false,
    val needsOAuthProfile: Boolean = false,
)
