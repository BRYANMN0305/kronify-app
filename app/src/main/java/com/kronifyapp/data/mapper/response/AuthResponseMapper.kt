package com.kronifyapp.data.mapper.response

import com.kronifyapp.data.remote.response.TokenResponse
import com.kronifyapp.domain.model.AuthSession

fun TokenResponse.toDomain(userId: Long, profileType: String) = AuthSession(
    accessToken = accessToken,
    expiresIn = expiresIn,
    userId = userId,
    profileType = profileType,
)
