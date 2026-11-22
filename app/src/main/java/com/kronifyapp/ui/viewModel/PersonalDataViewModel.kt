package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.usecase.GetUserProfileUseCase
import com.kronifyapp.domain.usecase.ListEmployeesUseCase
import com.kronifyapp.domain.usecase.ToggleOwnerEmployeeUseCase
import com.kronifyapp.domain.usecase.UpdateUserProfileUseCase
import com.kronifyapp.ui.state.PersonalDataUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

@HiltViewModel
class PersonalDataViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val listEmployeesUseCase: ListEmployeesUseCase,
    private val toggleOwnerEmployeeUseCase: ToggleOwnerEmployeeUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PersonalDataUiState())
    val uiState: StateFlow<PersonalDataUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Tu sesión expiró. Vuelve a iniciar sesión.",
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val profile = getUserProfileUseCase(token)
                val employees = runCatching { listEmployeesUseCase(token) }.getOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = profile.name,
                        lastName = profile.lastName,
                        phoneNumber = profile.phoneNumber.orEmpty(),
                        email = profile.email,
                        isOwner = employees != null,
                        ownerWorksAsEmployee = employees?.any { employee -> employee.owner } == true,
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, error = error.userMessage()) }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update {
        it.copy(name = value, error = null, successMessage = null)
    }

    fun onLastNameChange(value: String) = _uiState.update {
        it.copy(lastName = value, error = null, successMessage = null)
    }

    fun onPhoneChange(value: String) = _uiState.update {
        it.copy(phoneNumber = value, error = null, successMessage = null)
    }

    fun toggleOwnerWorksAsEmployee(enabled: Boolean) {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Vuelve a iniciar sesión.") }
            return
        }
        if (_uiState.value.togglingOwner) return

        viewModelScope.launch {
            _uiState.update { it.copy(togglingOwner = true, error = null, successMessage = null) }
            try {
                toggleOwnerEmployeeUseCase(token, enabled)
                _uiState.update {
                    it.copy(
                        togglingOwner = false,
                        ownerWorksAsEmployee = enabled,
                        successMessage = if (enabled) {
                            "Ahora apareces como empleado de tu negocio."
                        } else {
                            "Ya no apareces como empleado."
                        },
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(togglingOwner = false, error = error.userMessage()) }
            }
        }
    }

    fun save() {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Vuelve a iniciar sesión.") }
            return
        }
        if (_uiState.value.isSaving) return

        val form = _uiState.value
        if (form.name.isBlank()) {
            _uiState.update { it.copy(error = "Escribe tu nombre.") }
            return
        }
        if (form.lastName.isBlank()) {
            _uiState.update { it.copy(error = "Escribe tus apellidos.") }
            return
        }
        if (!NAME_REGEX.matches(form.name.trim()) || !NAME_REGEX.matches(form.lastName.trim())) {
            _uiState.update { it.copy(error = "El nombre y los apellidos solo pueden tener letras.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            try {
                updateUserProfileUseCase(
                    token,
                    form.name,
                    form.lastName,
                    form.phoneNumber.ifBlank { null },
                )
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Datos personales actualizados.",
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
            400 -> "Revisa el nombre y los apellidos."
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            else -> "No pudimos guardar tus datos."
        }
        else -> message ?: "No pudimos conectar. Revisa tu conexión."
    }

    private companion object {
        val NAME_REGEX = Regex("^[a-zA-Z ]+$")
    }
}
