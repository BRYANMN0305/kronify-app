package com.kronifyapp.data.remote

import com.kronifyapp.data.remote.request.InvitationRequest
import com.kronifyapp.data.remote.response.InvitationResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface InvitationApi {
    @GET("business/invitations/")
    suspend fun listInvitations(@Header("Authorization") token: String): List<InvitationResponse>

    @POST("business/invitations/")
    suspend fun createInvitation(
        @Header("Authorization") token: String,
        @Body request: InvitationRequest,
    ): InvitationResponse

    @POST("business/invitations/{invitationId}/resend")
    suspend fun resendInvitation(
        @Header("Authorization") token: String,
        @Path("invitationId") invitationId: Long,
    ): InvitationResponse

    @POST("business/invitations/{invitationId}/cancel")
    suspend fun cancelInvitation(
        @Header("Authorization") token: String,
        @Path("invitationId") invitationId: Long,
    ): InvitationResponse
}
