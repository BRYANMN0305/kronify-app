package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.JwtClaimsParser
import com.kronifyapp.data.local.OnboardingProgressStore
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.model.AuthSession
import com.kronifyapp.domain.model.BusinessInput
import com.kronifyapp.domain.model.JwtClaims
import com.kronifyapp.domain.model.LoginCredentials
import com.kronifyapp.domain.model.OpeningHour
import com.kronifyapp.domain.model.Registration
import com.kronifyapp.domain.usecase.CreateBusinessUseCase
import com.kronifyapp.domain.usecase.LoginUseCase
import com.kronifyapp.domain.usecase.RegisterUseCase
import com.kronifyapp.domain.usecase.SelectOAuthProfileUseCase
import com.kronifyapp.domain.usecase.UpdateOpeningHoursUseCase
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
    private val selectOAuthProfileUseCase: SelectOAuthProfileUseCase,
    private val createBusinessUseCase: CreateBusinessUseCase,
    private val updateOpeningHoursUseCase: UpdateOpeningHoursUseCase,
    private val onboardingProgressStore: OnboardingProgressStore,
    private val sessionStore: SessionStore,
    private val uploadBusinessLogoUseCase: UploadBusinessLogoUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var pendingOAuthToken: String? = null

    fun login(email: String, password: String) = execute {
        val session = loginUseCase(LoginCredentials(email, password))
        completeSession(session, JwtClaimsParser.parse(session.accessToken))
    }

    fun register(name: String, lastName: String, phone: String, email: String, password: String, profileType: String) =
        execute {
            val session = registerUseCase(Registration(name, lastName, phone, email, password, profileType))
            if (session.profileType == "BUSINESS") onboardingProgressStore.saveStep(session.userId, 1)
            completeSession(session, JwtClaimsParser.parse(session.accessToken))
        }

    /**
     * Recibe el JWT que devolvió el proveedor OAuth dentro de la app.
     * Si la cuenta es nueva, primero se elige el tipo de perfil.
     */
    fun handleOAuthToken(token: String, newProfile: Boolean) {
        val claims = JwtClaimsParser.parse(token)
        if (claims?.userId == null) {
            _uiState.update { it.copy(error = "La respuesta de autenticación no es válida.") }
            return
        }

        if (newProfile) {
            pendingOAuthToken = token
            _uiState.update { it.copy(needsOAuthProfile = true, error = null) }
        } else {
            completeSession(
                AuthSession(
                    accessToken = token,
                    expiresIn = 0,
                    userId = claims.userId,
                    profileType = claims.profileType.orEmpty(),
                ),
                claims,
            )
        }
    }

    /** Primer login OAuth: guarda el tipo de perfil y continúa con el token renovado. */
    fun chooseOAuthProfile(profileType: String) {
        val token = pendingOAuthToken
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "No se pudo completar el inicio de sesión.") }
            return
        }

        execute {
            val session = selectOAuthProfileUseCase(token, profileType)
            pendingOAuthToken = null
            _uiState.update { it.copy(needsOAuthProfile = false) }
            completeSession(session, JwtClaimsParser.parse(session.accessToken))
        }
    }

    fun cancelOAuthProfile() {
        pendingOAuthToken = null
        _uiState.update { it.copy(needsOAuthProfile = false, error = null) }
    }

    fun createBusiness(userId: Long, token: String, name: String, slug: String, category: String, phone: String, address: String, email: String) =
        execute {
            require(token.isNotBlank()) { "Tu sesión expiró. Inicia sesión nuevamente." }
            createBusinessUseCase(token, BusinessInput(name, slug, category, phone, address, email))
            onboardingProgressStore.saveStep(userId, 2)
            _uiState.update { it.copy(businessCreated = true) }
        }

    fun saveOpeningHours(userId: Long, token: String, hours: List<OpeningHour>) = execute {
        require(token.isNotBlank()) { "Tu sesión expiró. Inicia sesión nuevamente." }
        require(hours.isNotEmpty()) { "Configura al menos un día de atención." }
        updateOpeningHoursUseCase(token, hours)
        onboardingProgressStore.saveStep(userId, 3)
        _uiState.update { it.copy(openingHoursSaved = true) }
    }

    fun consumeOpeningHoursSaved() = _uiState.update { it.copy(openingHoursSaved = false) }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }

    fun advanceOnboarding(userId: Long, step: Int) = onboardingProgressStore.saveStep(userId, step)

    fun uploadLogo(token: String, bytes: ByteArray, mimeType: String) = execute {
        require(bytes.size <= 5 * 1024 * 1024) { "La imagen debe pesar como máximo 5 MB." }
        uploadBusinessLogoUseCase(token, LogoImage(bytes, mimeType))
        _uiState.update { it.copy(logoUploaded = true) }
    }

    fun completeOnboarding(userId: Long) = onboardingProgressStore.complete(userId)

    /**
     * Guarda la sesión y calcula el siguiente paso.
     * Si el JWT ya trae businessId, la cuenta ya tiene negocio (aunque se haya
     * creado desde la web) y se entra directo a la app, sin repetir onboarding.
     */
    private fun completeSession(session: AuthSession, claims: JwtClaims?) {
        val profileType = claims?.profileType?.takeIf { it.isNotBlank() } ?: session.profileType
        val onboardingStep = when {
            profileType != "BUSINESS" -> OnboardingProgressStore.COMPLETE
            claims?.businessId != null -> {
                onboardingProgressStore.complete(session.userId)
                OnboardingProgressStore.COMPLETE
            }
            else -> onboardingProgressStore.stepFor(session.userId)
        }

        sessionStore.save(
            token = session.accessToken,
            userId = session.userId,
            profileType = profileType,
            businessId = claims?.businessId,
            slug = claims?.slug,
            email = claims?.email,
            name = claims?.name,
        )

        _uiState.update {
            it.copy(
                token = session.accessToken,
                userId = session.userId,
                profileType = profileType,
                onboardingStep = onboardingStep,
                needsOAuthProfile = false,
            )
        }
    }

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
