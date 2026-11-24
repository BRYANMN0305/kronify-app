package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.usecase.ListEmployeesUseCase
import com.kronifyapp.domain.usecase.ListServicesUseCase
import com.kronifyapp.domain.usecase.GetBusinessAgendaUseCase
import com.kronifyapp.ui.state.CalendarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val getBusinessAgendaUseCase: GetBusinessAgendaUseCase,
    private val listEmployeesUseCase: ListEmployeesUseCase,
    private val listServicesUseCase: ListServicesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMonth(_uiState.value.month)
        loadFilters()
    }

    private fun loadFilters() {
        val token = sessionStore.token ?: return
        viewModelScope.launch {
            val employees = runCatching { listEmployeesUseCase(token) }.getOrDefault(emptyList())
            val services = runCatching { listServicesUseCase(token) }.getOrDefault(emptyList())
            _uiState.update { it.copy(employees = employees, services = services) }
        }
    }

    fun selectEmployee(value: String) = _uiState.update { it.copy(selectedEmployee = value) }
    fun selectService(value: String) = _uiState.update { it.copy(selectedService = value) }
    fun selectStatus(value: String) = _uiState.update { it.copy(selectedStatus = value) }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        if (date.month != _uiState.value.month.month || date.year != _uiState.value.month.year) loadMonth(date.withDayOfMonth(1))
    }

    fun moveMonth(amount: Long) {
        val month = _uiState.value.month.plusMonths(amount).withDayOfMonth(1)
        _uiState.update { it.copy(month = month, selectedDate = month) }
        loadMonth(month)
    }

    fun loadMonth(month: LocalDate = _uiState.value.month) {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Inicia sesión para consultar la agenda.") }
            return
        }
        val start = month.withDayOfMonth(1)
        val end = start.plusMonths(1).minusDays(1)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, month = start) }
            runCatching { getBusinessAgendaUseCase(token, start.toString(), end.toString()) }
                .onSuccess { list -> _uiState.update { it.copy(isLoading = false, appointments = list) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, error = error.message ?: "No se pudo cargar la agenda.") } }
        }
    }
}
    
