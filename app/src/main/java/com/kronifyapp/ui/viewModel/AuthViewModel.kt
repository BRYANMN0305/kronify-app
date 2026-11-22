package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.OnboardingProgressStore
import com.kronifyapp.domain.model.BusinessInput
import com.kronifyapp.domain.model.LoginCredentials
import com.kronifyapp.domain.model.Registration
import com.kronifyapp.domain.usecase.CreateBusinessUseCase
import com.kronifyapp.domain.usecase.LoginUseCase
import com.kronifyapp.domain.usecase.RegisterUseCase
import com.kronifyapp.domain.usecase.UploadBusinessLogoUseCase
import com.kronifyapp.domain.model.LogoImage
import com.kronifyapp.ui.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val createBusinessUseCase: CreateBusinessUseCase,
    private val onboardingProgressStore: OnboardingProgressStore,
    private val uploadBusinessLogoUseCase: UploadBusinessLogoUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) = execute {
        val session = loginUseCase(LoginCredentials(email, password))
        _uiState.update {
            it.copy(
                token = session.accessToken,
                userId = session.userId,
                profileType = session.profileType,
                onboardingStep = onboardingProgressStore.stepFor(session.userId),
            )
        }
    }

    fun register(name: String, lastName: String, phone: String, email: String, password: String, profileType: String) =
        execute {
            val session = registerUseCase(Registration(name, lastName, phone, email, password, profileType))
            if (session.profileType == "BUSINESS") onboardingProgressStore.saveStep(session.userId, 1)
            _uiState.update {
                it.copy(
                    token = session.accessToken,
                    userId = session.userId,
                    profileType = session.profileType,
                    onboardingStep = onboardingProgressStore.stepFor(session.userId),
                )
            }
        }

    fun createBusiness(userId: Long, token: String, name: String, slug: String, category: String, phone: String, address: String, email: String) =
        execute {
            require(token.isNotBlank()) { "Tu sesión expiró. Inicia sesión nuevamente." }
            createBusinessUseCase(token, BusinessInput(name, slug, category, phone, address, email))
            onboardingProgressStore.saveStep(userId, 2)
            _uiState.update { it.copy(businessCreated = true) }
        }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }

    fun advanceOnboarding(userId: Long, step: Int) = onboardingProgressStore.saveStep(userId, step)

    fun uploadLogo(token: String, bytes: ByteArray, mimeType: String) = execute {
        require(bytes.size <= 5 * 1024 * 1024) { "La imagen debe pesar como máximo 5 MB." }
        uploadBusinessLogoUseCase(token, LogoImage(bytes, mimeType))
        _uiState.update { it.copy(logoUploaded = true) }
    }

    fun completeOnboarding(userId: Long) = onboardingProgressStore.complete(userId)

    private fun execute(action: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                action()
                _uiState.update { it.copy(isLoading = false) }
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, error = error.userMessage()) }
            }
        }
    }

    private fun Exception.userMessage() = when (this) {
        is HttpException -> when (code()) {
            400 -> "Revisa los datos ingresados."
            401 -> "Correo o contraseña incorrectos."
            409 -> "Ya existe una cuenta con esos datos."
            else -> "El servidor no pudo procesar la solicitud."
        }
        else -> message ?: "No fue posible conectar con Kronify."
    }
}
