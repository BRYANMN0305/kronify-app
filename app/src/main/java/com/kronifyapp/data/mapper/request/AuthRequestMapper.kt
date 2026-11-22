package com.kronifyapp.data.mapper.request

import com.kronifyapp.data.remote.request.BusinessCreateRequest
import com.kronifyapp.data.remote.request.LoginRequest
import com.kronifyapp.data.remote.request.UserRegisterRequest
import com.kronifyapp.domain.model.BusinessInput
import com.kronifyapp.domain.model.LoginCredentials
import com.kronifyapp.domain.model.Registration

fun LoginCredentials.toRequest() = LoginRequest(email.trim(), password)

fun Registration.toRequest() = UserRegisterRequest(
    name = name.trim(), lastName = lastName.trim(), phoneNumber = phoneNumber.trim(),
    email = email.trim(), passwordHash = password, profileType = profileType,
)

fun BusinessInput.toRequest() = BusinessCreateRequest(
    name = name.trim(), slug = slug.trim(), category = category.trim(),
    description = "Negocio registrado desde la app", address = address.trim(),
    phoneNumber = phoneNumber.trim(), email = email.trim(), whatsApp = phoneNumber.trim(),
)
