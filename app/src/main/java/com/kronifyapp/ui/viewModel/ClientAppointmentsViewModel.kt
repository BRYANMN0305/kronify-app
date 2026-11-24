package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.usecase.GetClientAppointmentHistoryUseCase
import com.kronifyapp.domain.usecase.GetRecentBusinessesUseCase
import com.kronifyapp.ui.state.ClientAppointmentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

@HiltViewModel
class ClientAppointmentsViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val getClientAppointmentHistoryUseCase: GetClientAppointmentHistoryUseCase,
    private val getRecentBusinessesUseCase: GetRecentBusinessesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ClientAppointmentsUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, recentLoading = false, error = "Inicia sesión para consultar tus citas.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, recentLoading = true, recentError = null) }
            val historyDeferred = async { runCatching { getClientAppointmentHistoryUseCase(token) } }
            val recentDeferred = async { runCatching { getRecentBusinessesUseCase(token) } }
            val history = historyDeferred.await()
            val recent = recentDeferred.await()

            history.onSuccess { appointments ->
                _uiState.update { it.copy(isLoading = false, appointments = appointments) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "No se pudo cargar tu historial de citas.") }
            }

            recent.onSuccess { businesses ->
                _uiState.update { it.copy(recentLoading = false, recentBusinesses = businesses, showRecentSection = true) }
            }.onFailure { error ->
                val unavailable = error is HttpException && error.code() in setOf(404, 405, 501)
                _uiState.update {
                    it.copy(
                        recentLoading = false,
                        recentBusinesses = emptyList(),
                        showRecentSection = !unavailable,
                        recentError = if (unavailable) null else error.message ?: "No se pudieron cargar tus negocios recientes.",
                    )
                }
            }
        }
    }
}
