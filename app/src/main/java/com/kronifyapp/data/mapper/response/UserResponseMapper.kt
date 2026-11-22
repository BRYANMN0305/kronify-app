package com.kronifyapp.data.mapper.response

import com.kronifyapp.data.remote.response.LinkedAuthMethodResponse
import com.kronifyapp.data.remote.response.UserProfileResponse
import com.kronifyapp.domain.model.AuthMethod
import com.kronifyapp.domain.model.UserProfile

fun UserProfileResponse.toDomain() = UserProfile(
    userId = userId,
    name = name,
    lastName = lastName,
    phoneNumber = phoneNumber,
    email = email,
    profileType = profileType,
    active = active,
)

fun LinkedAuthMethodResponse.toDomain() = AuthMethod(
    type = type,
    provider = provider,
    email = email,
    linkedAt = linkedAt,
)
