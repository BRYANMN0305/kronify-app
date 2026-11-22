package com.kronifyapp.ui.state

data class PersonalDataUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val name: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val isOwner: Boolean = false,
    val ownerWorksAsEmployee: Boolean = false,
    val togglingOwner: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
) {
    val initials: String
        get() {
            val first = name.trim().firstOrNull()?.toString().orEmpty()
            val last = lastName.trim().firstOrNull()?.toString().orEmpty()
            return (first + last).uppercase().ifBlank { "K" }
        }

    val fullName: String
        get() = "$name $lastName".trim()
}
