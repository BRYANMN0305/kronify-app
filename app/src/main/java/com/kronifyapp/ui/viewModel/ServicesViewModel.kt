package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.model.ServiceInput
import com.kronifyapp.domain.usecase.CreateServiceUseCase
import com.kronifyapp.domain.usecase.DeleteServiceUseCase
import com.kronifyapp.domain.usecase.ListServicesUseCase
import com.kronifyapp.domain.usecase.UpdateServiceUseCase
import com.kronifyapp.ui.state.ServicesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val listServicesUseCase: ListServicesUseCase,
    private val createServiceUseCase: CreateServiceUseCase,
    private val updateServiceUseCase: UpdateServiceUseCase,
    private val deleteServiceUseCase: DeleteServiceUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Tu sesión expiró. Vuelve a iniciar sesión.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val services = listServicesUseCase(token)
                _uiState.update { it.copy(isLoading = false, services = services) }
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, error = error.userMessage()) }
            }
        }
    }

    fun create(input: ServiceInput) {
        val token = sessionStore.token ?: return
        runSave(token, success = "Servicio creado.") {
            createServiceUseCase(token, input)
            load()
        }
    }

    fun update(serviceId: Long, input: ServiceInput) {
        val token = sessionStore.token ?: return
        runSave(token, success = "Servicio actualizado.") {
            updateServiceUseCase(token, serviceId, input)
            load()
        }
    }

    fun delete(serviceId: Long) {
        val token = sessionStore.token ?: return
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            try {
                deleteServiceUseCase(token, serviceId)
                val services = listServicesUseCase(token)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        services = services,
                        successMessage = "Servicio eliminado.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(isSaving = false, error = error.userMessage()) }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }

    private fun runSave(token: String, success: String, action: suspend () -> Unit) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }
            try {
                action()
                _uiState.update { it.copy(isSaving = false, successMessage = success) }
            } catch (error: Exception) {
                _uiState.update { it.copy(isSaving = false, error = error.userMessage()) }
            }
        }
    }

    private fun Exception.userMessage(): String = when (this) {
        is HttpException -> when (code()) {
            400 -> "Revisa los datos del servicio."
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            403 -> "No tienes permisos para gestionar servicios."
            404 -> "No encontramos ese servicio."
            else -> "No pudimos guardar el servicio."
        }
        else -> message ?: "No pudimos conectar. Revisa tu conexión."
    }
}
