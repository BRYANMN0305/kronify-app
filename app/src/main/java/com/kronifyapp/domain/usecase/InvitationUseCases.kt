package com.kronifyapp.domain.usecase

import com.kronifyapp.domain.repository.InvitationRepository

class ListInvitationsUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(token: String) = repository.listInvitations(token)
}

class CreateInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(token: String, email: String) = repository.createInvitation(token, email)
}

class ResendInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(token: String, invitationId: Long) = repository.resendInvitation(token, invitationId)
}

class CancelInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(token: String, invitationId: Long) = repository.cancelInvitation(token, invitationId)
}
