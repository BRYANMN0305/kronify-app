package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.model.BusinessUpdate
import com.kronifyapp.domain.model.LogoImage
import com.kronifyapp.domain.usecase.GetBusinessUseCase
import com.kronifyapp.domain.usecase.UpdateBusinessUseCase
import com.kronifyapp.domain.usecase.UploadLogoUseCase
import com.kronifyapp.ui.state.BusinessDataUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

@HiltViewModel
class BusinessDataViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val getBusinessUseCase: GetBusinessUseCase,
    private val updateBusinessUseCase: UpdateBusinessUseCase,
    private val uploadLogoUseCase: UploadLogoUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BusinessDataUiState())
    val uiState: StateFlow<BusinessDataUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Tu sesión expiró. Inicia sesión nuevamente.",
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val business = getBusinessUseCase(token)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = business.name,
                        category = business.category,
                        description = business.description,
                        address = business.address,
                        email = business.email,
                        phoneNumber = business.phoneNumber,
                        whatsApp = business.whatsApp,
                        logoUrl = business.logoUrl,
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, error = error.userMessage()) }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, error = null, successMessage = null) }
    fun onCategoryChange(value: String) = _uiState.update { it.copy(category = value, error = null, successMessage = null) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value, error = null, successMessage = null) }
    fun onAddressChange(value: String) = _uiState.update { it.copy(address = value, error = null, successMessage = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null, successMessage = null) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phoneNumber = value, error = null, successMessage = null) }
    fun onWhatsAppChange(value: String) = _uiState.update { it.copy(whatsApp = value, error = null, successMessage = null) }

    fun uploadLogo(bytes: ByteArray, mimeType: String) {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Inicia sesión nuevamente.") }
            return
        }
        if (bytes.size > MAX_LOGO_BYTES) {
            _uiState.update { it.copy(error = "La imagen supera los 5 MB.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingLogo = true, error = null, successMessage = null) }
            try {
                val url = uploadLogoUseCase(token, LogoImage(bytes, mimeType))
                _uiState.update {
                    it.copy(
                        isUploadingLogo = false,
                        logoUrl = url,
                        successMessage = "Logo actualizado.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(isUploadingLogo = false, error = error.userMessage()) }
            }
        }
    }

    fun save() {
        val form = _uiState.value
        if (form.isSaving || form.isUploadingLogo) return

        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Inicia sesión nuevamente.") }
            return
        }

        val validation = validate(form)
        if (validation != null) {
            _uiState.update { it.copy(error = validation, successMessage = null) }
            return
        }

        val update = BusinessUpdate(
            name = form.name.trim(),
            category = form.category.trim(),
            description = form.description.trim().ifBlank { null },
            address = form.address.trim().ifBlank { null },
            logoUrl = form.logoUrl,
            email = form.email.trim(),
            phoneNumber = form.phoneNumber.trim(),
            whatsApp = form.whatsApp.trim(),
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            try {
                updateBusinessUseCase(token, update)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Cambios guardados.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(isSaving = false, error = error.userMessage()) }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }

    private fun validate(form: BusinessDataUiState): String? {
        if (form.name.isBlank()) return "El nombre del negocio es obligatorio."
        if (form.category.isBlank()) return "La categoría es obligatoria."
        if (form.email.isBlank()) return "El email de contacto es obligatorio."
        if (!EMAIL_REGEX.matches(form.email.trim())) return "Ingresa un email válido."
        if (form.phoneNumber.isBlank()) return "El teléfono es obligatorio."
        if (form.whatsApp.isBlank()) return "El WhatsApp es obligatorio."
        return null
    }

    private fun Exception.userMessage(): String = when (this) {
        is HttpException -> when (code()) {
            400 -> "Revisa los datos e inténtalo de nuevo."
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            403 -> "No tienes permisos para editar este negocio."
            404 -> "No encontramos el negocio."
            else -> "No pudimos guardar los cambios."
        }
        else -> message ?: "No pudimos conectar. Revisa tu conexión."
    }

    private companion object {
        const val MAX_LOGO_BYTES = 5 * 1024 * 1024
        val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }
}
