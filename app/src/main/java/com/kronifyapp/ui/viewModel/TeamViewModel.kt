package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.model.Employee
import com.kronifyapp.domain.usecase.CancelInvitationUseCase
import com.kronifyapp.domain.usecase.CreateInvitationUseCase
import com.kronifyapp.domain.usecase.ListEmployeesUseCase
import com.kronifyapp.domain.usecase.ListInvitationsUseCase
import com.kronifyapp.domain.usecase.ResendInvitationUseCase
import com.kronifyapp.domain.usecase.UpdateSchedulePermissionUseCase
import com.kronifyapp.ui.state.EmployeeScheduleItem
import com.kronifyapp.ui.state.TeamUiState
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
class TeamViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val listEmployeesUseCase: ListEmployeesUseCase,
    private val updateSchedulePermissionUseCase: UpdateSchedulePermissionUseCase,
    private val listInvitationsUseCase: ListInvitationsUseCase,
    private val createInvitationUseCase: CreateInvitationUseCase,
    private val resendInvitationUseCase: ResendInvitationUseCase,
    private val cancelInvitationUseCase: CancelInvitationUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TeamUiState())
    val uiState: StateFlow<TeamUiState> = _uiState.asStateFlow()

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
                val (employees, invitations) = coroutineScope {
                    val employeesDeferred = async { listEmployeesUseCase(token) }
                    val invitationsDeferred = async { listInvitationsUseCase(token) }
                    employeesDeferred.await() to invitationsDeferred.await()
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        employees = employees.map { employee -> employee.toUi() },
                        invitations = invitations,
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, error = error.userMessage()) }
            }
        }
    }

    fun toggleSelfManagement(employeeId: Long, selfManaged: Boolean) {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Inicia sesión nuevamente.") }
            return
        }
        if (_uiState.value.permissionBusyId != null) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(permissionBusyId = employeeId, error = null, successMessage = null)
            }
            try {
                val updated = updateSchedulePermissionUseCase(token, employeeId, selfManaged)
                _uiState.update { state ->
                    state.copy(
                        permissionBusyId = null,
                        employees = state.employees.map {
                            if (it.employeeId == updated.employeeId) updated.toUi() else it
                        },
                        successMessage = if (selfManaged) {
                            "Listo, ahora puede editar su horario."
                        } else {
                            "Listo, ya no puede editar su horario."
                        },
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(permissionBusyId = null, error = error.userMessage()) }
            }
        }
    }

    fun invite(email: String) {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Inicia sesión nuevamente.") }
            return
        }
        val clean = email.trim()
        if (clean.isBlank()) {
            _uiState.update { it.copy(error = "Escribe el correo del empleado.") }
            return
        }
        if (!EMAIL_REGEX.matches(clean)) {
            _uiState.update { it.copy(error = "Ese correo no parece válido.") }
            return
        }
        if (_uiState.value.isInviting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isInviting = true, error = null, successMessage = null) }
            try {
                createInvitationUseCase(token, clean)
                val invitations = listInvitationsUseCase(token)
                _uiState.update {
                    it.copy(
                        isInviting = false,
                        invitations = invitations,
                        successMessage = "Invitación enviada.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(isInviting = false, error = error.userMessage()) }
            }
        }
    }

    fun resend(invitationId: Long) {
        val token = sessionStore.token ?: return
        if (_uiState.value.busyInvitationId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(busyInvitationId = invitationId, error = null, successMessage = null) }
            try {
                resendInvitationUseCase(token, invitationId)
                val invitations = listInvitationsUseCase(token)
                _uiState.update {
                    it.copy(
                        busyInvitationId = null,
                        invitations = invitations,
                        successMessage = "Invitación reenviada.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(busyInvitationId = null, error = error.userMessage()) }
            }
        }
    }

    fun cancel(invitationId: Long) {
        val token = sessionStore.token ?: return
        if (_uiState.value.busyInvitationId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(busyInvitationId = invitationId, error = null, successMessage = null) }
            try {
                cancelInvitationUseCase(token, invitationId)
                val invitations = listInvitationsUseCase(token)
                _uiState.update {
                    it.copy(
                        busyInvitationId = null,
                        invitations = invitations,
                        successMessage = "Invitación cancelada.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(busyInvitationId = null, error = error.userMessage()) }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }

    private fun Employee.toUi() = EmployeeScheduleItem(
        employeeId = employeeId,
        name = name,
        lastName = lastName,
        email = email,
        owner = owner,
        selfManagedSchedule = selfManagedSchedule,
    )

    private fun Exception.userMessage(): String = when (this) {
        is HttpException -> when (code()) {
            400 -> "Revisa el correo e inténtalo de nuevo."
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            403 -> "Solo el dueño del negocio puede gestionar el equipo."
            409 -> "Ese correo ya tiene una invitación pendiente."
            else -> "No pudimos completar la acción. Inténtalo de nuevo."
        }
        else -> message ?: "No pudimos conectar. Revisa tu conexión."
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }
}
