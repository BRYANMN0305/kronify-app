package com.kronifyapp.ui.state

import com.kronifyapp.domain.model.ClientAppointment
import com.kronifyapp.domain.model.RecentBusiness

data class ClientAppointmentsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val appointments: List<ClientAppointment> = emptyList(),
    val recentLoading: Boolean = false,
    val recentError: String? = null,
    val recentBusinesses: List<RecentBusiness> = emptyList(),
    val showRecentSection: Boolean = true,
)
