package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.usecase.AssignPlanUseCase
import com.kronifyapp.domain.usecase.GetAvailablePlansUseCase
import com.kronifyapp.domain.usecase.GetCurrentPlanUseCase
import com.kronifyapp.ui.state.PlanUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val getCurrentPlanUseCase: GetCurrentPlanUseCase,
    private val getAvailablePlansUseCase: GetAvailablePlansUseCase,
    private val assignPlanUseCase: AssignPlanUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlanUiState())
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Tu sesión expiró. Inicia sesión nuevamente.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val (current, available) = coroutineScope {
                    val currentDeferred = async { getCurrentPlanUseCase(token) }
                    val availableDeferred = async { getAvailablePlansUseCase() }
                    currentDeferred.await() to availableDeferred.await()
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        current = current,
                        availablePlans = available,
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, error = error.userMessage()) }
            }
        }
    }

    fun assignPlan(planId: Long, activationCode: String?) {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Inicia sesión nuevamente.") }
            return
        }
        if (_uiState.value.isAssigning) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAssigning = true,
                    assigningPlanId = planId,
                    error = null,
                    successMessage = null,
                )
            }
            try {
                assignPlanUseCase(token, planId, activationCode)
                val current = getCurrentPlanUseCase(token)
                _uiState.update {
                    it.copy(
                        isAssigning = false,
                        assigningPlanId = null,
                        current = current,
                        successMessage = "Listo, tu plan ya está actualizado.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isAssigning = false,
                        assigningPlanId = null,
                        error = error.userMessage(),
                    )
                }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }

    private fun Exception.userMessage(): String = when (this) {
        is HttpException -> when (code()) {
            400 -> "Revisa el código de activación."
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            403 -> "No tienes permisos para cambiar el plan."
            404 -> "No encontramos ese plan."
            else -> "No pudimos cambiar el plan. Inténtalo de nuevo."
        }
        else -> message ?: "No pudimos conectar. Revisa tu conexión."
    }
}
