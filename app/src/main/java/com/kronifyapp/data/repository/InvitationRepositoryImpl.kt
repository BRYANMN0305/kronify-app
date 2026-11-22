package com.kronifyapp.data.repository

import com.kronifyapp.data.mapper.response.toDomain
import com.kronifyapp.data.remote.InvitationApi
import com.kronifyapp.data.remote.request.InvitationRequest
import com.kronifyapp.domain.model.Invitation
import com.kronifyapp.domain.repository.InvitationRepository
import javax.inject.Inject

class InvitationRepositoryImpl @Inject constructor(
    private val invitationApi: InvitationApi,
) : InvitationRepository {
    override suspend fun listInvitations(token: String): List<Invitation> =
        invitationApi.listInvitations("Bearer $token").map { it.toDomain() }

    override suspend fun createInvitation(token: String, email: String): Invitation =
        invitationApi.createInvitation("Bearer $token", InvitationRequest(email.trim())).toDomain()

    override suspend fun resendInvitation(token: String, invitationId: Long): Invitation =
        invitationApi.resendInvitation("Bearer $token", invitationId).toDomain()

    override suspend fun cancelInvitation(token: String, invitationId: Long): Invitation =
        invitationApi.cancelInvitation("Bearer $token", invitationId).toDomain()
}
