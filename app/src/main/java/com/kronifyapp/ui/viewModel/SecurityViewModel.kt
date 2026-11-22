package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.usecase.ChangePasswordUseCase
import com.kronifyapp.domain.usecase.GetAuthMethodsUseCase
import com.kronifyapp.ui.state.SecurityUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val getAuthMethodsUseCase: GetAuthMethodsUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    init {
        loadMethods()
    }

    fun loadMethods() {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Tu sesión expiró. Vuelve a iniciar sesión.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, methodsError = null) }
            try {
                val methods = getAuthMethodsUseCase(token)
                _uiState.update { it.copy(isLoading = false, methods = methods) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        methodsError = "No pudimos cargar tus métodos de acceso.",
                    )
                }
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Vuelve a iniciar sesión.") }
            return
        }
        if (_uiState.value.isSaving) return

        if (currentPassword.isBlank()) {
            _uiState.update { it.copy(error = "Escribe tu contraseña actual.") }
            return
        }
        if (newPassword.length < 8) {
            _uiState.update { it.copy(error = "La nueva contraseña debe tener al menos 8 caracteres.") }
            return
        }
        if (newPassword != confirmPassword) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            try {
                changePasswordUseCase(token, currentPassword, newPassword)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Contraseña actualizada.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(isSaving = false, error = error.userMessage()) }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }

    private fun Exception.userMessage(): String = when (this) {
        is HttpException -> when (code()) {
            400 -> "La contraseña actual no es correcta."
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            else -> "No pudimos actualizar la contraseña."
        }
        else -> message ?: "No pudimos conectar. Revisa tu conexión."
    }
}
