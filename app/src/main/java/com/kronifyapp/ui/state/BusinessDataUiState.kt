package com.kronifyapp.ui.state

data class BusinessDataUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isUploadingLogo: Boolean = false,
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val address: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val whatsApp: String = "",
    val logoUrl: String? = null,
    val error: String? = null,
    val successMessage: String? = null,
)
