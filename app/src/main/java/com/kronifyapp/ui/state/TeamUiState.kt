package com.kronifyapp.ui.state

import com.kronifyapp.domain.model.Invitation

data class TeamUiState(
    val isLoading: Boolean = true,
    val employees: List<EmployeeScheduleItem> = emptyList(),
    val invitations: List<Invitation> = emptyList(),
    val isInviting: Boolean = false,
    val busyInvitationId: Long? = null,
    val permissionBusyId: Long? = null,
    val error: String? = null,
    val successMessage: String? = null,
)
