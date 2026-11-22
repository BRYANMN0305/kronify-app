package com.kronifyapp.domain.repository

import com.kronifyapp.domain.model.Invitation

interface InvitationRepository {
    suspend fun listInvitations(token: String): List<Invitation>
    suspend fun createInvitation(token: String, email: String): Invitation
    suspend fun resendInvitation(token: String, invitationId: Long): Invitation
    suspend fun cancelInvitation(token: String, invitationId: Long): Invitation
}
