package com.kronifyapp.ui.state

data class ProfileUiState(
    val isLoading: Boolean = true,
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val businessName: String = "",
    val businessCategory: String = "",
    val planName: String = "",
    val rating: Double? = null,
    val reviewCount: Int = 0,
    val monthlyAppointments: Int = 0,
    val serviceCount: Int = 0,
    val activeServiceCount: Int = 0,
    val employeeCount: Int = 0,
    val employeeLimit: Int? = null,
    val scheduleSummary: String = "",
    val notificationsEnabled: Boolean = false,
    val slug: String = "",
) {
    val fullName: String
        get() = "$name $lastName".trim().ifBlank { businessName }.ifBlank { "Mi cuenta" }

    val initials: String
        get() {
            val first = name.trim().firstOrNull()?.toString().orEmpty()
            val last = lastName.trim().firstOrNull()?.toString().orEmpty()
            return (first + last).uppercase().ifBlank {
                businessName.trim().firstOrNull()?.uppercase() ?: "K"
            }
        }

    val businessMeta: String
        get() = listOf(businessName, businessCategory)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
            .ifBlank { email }
}
