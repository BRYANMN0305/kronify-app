package com.kronifyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.model.Employee
import com.kronifyapp.domain.model.OpeningHour
import com.kronifyapp.domain.model.WeeklySchedule
import com.kronifyapp.domain.usecase.DeleteWeeklyScheduleUseCase
import com.kronifyapp.domain.usecase.AddEmployeeServicesUseCase
import com.kronifyapp.domain.usecase.GetMyEmployeeUseCase
import com.kronifyapp.domain.usecase.GetOpeningHoursUseCase
import com.kronifyapp.domain.usecase.ListEmployeeServicesUseCase
import com.kronifyapp.domain.usecase.ListEmployeesUseCase
import com.kronifyapp.domain.usecase.ListServicesUseCase
import com.kronifyapp.domain.usecase.ListWeeklySchedulesUseCase
import com.kronifyapp.domain.usecase.RemoveEmployeeServiceUseCase
import com.kronifyapp.domain.usecase.UpdateOpeningHoursUseCase
import com.kronifyapp.domain.usecase.UpdateSchedulePermissionUseCase
import com.kronifyapp.domain.usecase.UpsertWeeklyScheduleUseCase
import com.kronifyapp.ui.state.EmployeeScheduleItem
import com.kronifyapp.ui.state.ScheduleDayUi
import com.kronifyapp.ui.state.ScheduleMode
import com.kronifyapp.ui.state.ScheduleUiState
import com.kronifyapp.ui.state.emptyScheduleDays
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val getOpeningHoursUseCase: GetOpeningHoursUseCase,
    private val updateOpeningHoursUseCase: UpdateOpeningHoursUseCase,
    private val listEmployeesUseCase: ListEmployeesUseCase,
    private val getMyEmployeeUseCase: GetMyEmployeeUseCase,
    private val listWeeklySchedulesUseCase: ListWeeklySchedulesUseCase,
    private val updateSchedulePermissionUseCase: UpdateSchedulePermissionUseCase,
    private val upsertWeeklyScheduleUseCase: UpsertWeeklyScheduleUseCase,
    private val deleteWeeklyScheduleUseCase: DeleteWeeklyScheduleUseCase,
    private val listServicesUseCase: ListServicesUseCase,
    private val listEmployeeServicesUseCase: ListEmployeeServicesUseCase,
    private val addEmployeeServicesUseCase: AddEmployeeServicesUseCase,
    private val removeEmployeeServiceUseCase: RemoveEmployeeServiceUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

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
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                val employees = listEmployeesUseCase(token)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mode = ScheduleMode.OWNER,
                        employees = employees.map { employee -> employee.toUi() },
                    )
                }
                loadBusinessHours(token)
                loadServicesCatalog(token)
                employees.firstOrNull()?.let { selectEmployee(it.employeeId) }
            } catch (ownerError: Exception) {
                try {
                    val me = getMyEmployeeUseCase(token)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mode = ScheduleMode.SELF,
                            employees = listOf(me.toUi()),
                            selectedEmployeeId = me.employeeId,
                            employeeDays = emptyScheduleDays(),
                        )
                    }
                    if (me.selfManagedSchedule) {
                        loadEmployeeSchedule(token, me.employeeId)
                    }
                } catch (selfError: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = selfError.userMessage(fallback = ownerError),
                        )
                    }
                }
            }
        }
    }

    fun selectEmployee(employeeId: Long) {
        val token = sessionStore.token
        if (token.isNullOrBlank()) return
        _uiState.update {
            it.copy(
                selectedEmployeeId = employeeId,
                employeeDays = emptyScheduleDays(),
                employeeLoading = true,
                linkedServiceIds = emptySet(),
                selectedServiceIds = emptySet(),
                error = null,
                successMessage = null,
            )
        }
        loadEmployeeSchedule(token, employeeId)
        if (_uiState.value.mode == ScheduleMode.OWNER) {
            loadServicesCatalog(token)
            loadEmployeeServices(token, employeeId)
        }
    }

    fun setBusinessOpen(dayOfWeek: Int, open: Boolean) {
        _uiState.update { state ->
            state.copy(
                error = null,
                successMessage = null,
                businessDays = state.businessDays.map { day ->
                    if (day.dayOfWeek != dayOfWeek) day
                    else if (open) day.copy(
                        open = true,
                        start = day.start.ifBlank { "09:00" },
                        end = day.end.ifBlank { "18:00" },
                    )
                    else day.copy(open = false)
                },
            )
        }
    }

    fun setBusinessTimes(dayOfWeek: Int, start: String, end: String) =
        updateBusinessDay(dayOfWeek) { it.copy(start = start.take(5), end = end.take(5), open = true) }

    fun setEmployeeOpen(dayOfWeek: Int, open: Boolean) {
        _uiState.update { state ->
            state.copy(
                error = null,
                successMessage = null,
                employeeDays = state.employeeDays.map { day ->
                    if (day.dayOfWeek != dayOfWeek) day
                    else if (open) day.copy(
                        open = true,
                        start = day.start.ifBlank { "09:00" },
                        end = day.end.ifBlank { "18:00" },
                    )
                    else day.copy(open = false)
                },
            )
        }
    }

    fun setEmployeeTimes(dayOfWeek: Int, start: String, end: String) =
        updateEmployeeDay(dayOfWeek) { it.copy(start = start.take(5), end = end.take(5), open = true) }

    fun toggleSelfManagement(employeeId: Long, selfManaged: Boolean) {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Inicia sesión nuevamente.") }
            return
        }
        if (_uiState.value.permissionBusyId != null) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    permissionBusyId = employeeId,
                    error = null,
                    successMessage = null,
                )
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

    fun saveBusiness() {
        val token = sessionStore.token
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Inicia sesión nuevamente.") }
            return
        }
        if (_uiState.value.businessSaving) return

        val openDays = _uiState.value.businessDays.filter { it.open }
        val validation = validateDays(openDays)
        if (validation != null) {
            _uiState.update { it.copy(error = validation) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(businessSaving = true, error = null, successMessage = null) }
            try {
                val updated = updateOpeningHoursUseCase(
                    token,
                    openDays.map { OpeningHour(it.dayOfWeek, it.start, it.end) },
                )
                _uiState.update {
                    it.copy(
                        businessSaving = false,
                        businessDays = mergeBusinessDays(updated),
                        successMessage = "Horario guardado.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(businessSaving = false, error = error.userMessage()) }
            }
        }
    }

    fun saveEmployee() {
        val state = _uiState.value
        val token = sessionStore.token
        val employeeId = state.selectedEmployeeId
        if (token.isNullOrBlank() || employeeId == null) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Inicia sesión nuevamente.") }
            return
        }
        if (state.employeeSaving) return

        val employee = state.selectedEmployee
        if (state.mode == ScheduleMode.SELF && employee?.selfManagedSchedule == false) {
            _uiState.update { it.copy(error = "El dueño todavía no habilitó la autogestión de tu horario.") }
            return
        }

        val openDays = state.employeeDays.filter { it.open }
        val validation = validateDays(openDays)
        if (validation != null) {
            _uiState.update { it.copy(error = validation) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(employeeSaving = true, error = null, successMessage = null) }
            try {
                openDays.forEach { day ->
                    upsertWeeklyScheduleUseCase(
                        token,
                        employeeId,
                        day.dayOfWeek,
                        day.start,
                        day.end,
                    )
                }
                state.employeeDays
                    .filter { !it.open && it.weeklyScheduleId != null }
                    .forEach { day ->
                        deleteWeeklyScheduleUseCase(token, employeeId, day.weeklyScheduleId!!)
                    }

                val refreshed = listWeeklySchedulesUseCase(token, employeeId)
                _uiState.update {
                    it.copy(
                        employeeSaving = false,
                        employeeDays = mergeEmployeeDays(refreshed),
                        successMessage = "Horario de ${employee?.fullName ?: "el empleado"} guardado.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(employeeSaving = false, error = error.userMessage()) }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }

    fun toggleService(serviceId: Long) {
        _uiState.update { state ->
            val selected = if (serviceId in state.selectedServiceIds) {
                state.selectedServiceIds - serviceId
            } else {
                state.selectedServiceIds + serviceId
            }
            state.copy(selectedServiceIds = selected, error = null, successMessage = null)
        }
    }

    fun saveEmployeeServices() {
        val token = sessionStore.token
        val employeeId = _uiState.value.selectedEmployeeId
        if (token.isNullOrBlank() || employeeId == null) {
            _uiState.update { it.copy(error = "Tu sesión expiró. Vuelve a iniciar sesión.") }
            return
        }
        if (_uiState.value.savingServices) return

        val state = _uiState.value
        val adds = state.selectedServiceIds - state.linkedServiceIds
        val removes = state.linkedServiceIds - state.selectedServiceIds
        if (adds.isEmpty() && removes.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(savingServices = true, error = null, successMessage = null) }
            try {
                if (adds.isNotEmpty()) {
                    addEmployeeServicesUseCase(token, employeeId, adds.toList())
                }
                removes.forEach { serviceId ->
                    removeEmployeeServiceUseCase(token, employeeId, serviceId)
                }
                val linked = listEmployeeServicesUseCase(token, employeeId)
                    .map { it.id }
                    .toSet()
                _uiState.update {
                    it.copy(
                        savingServices = false,
                        linkedServiceIds = linked,
                        selectedServiceIds = linked,
                        successMessage = "Servicios del empleado actualizados.",
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(savingServices = false, error = error.servicesUserMessage()) }
            }
        }
    }

    private fun loadServicesCatalog(token: String) {
        viewModelScope.launch {
            try {
                val services = listServicesUseCase(token)
                _uiState.update { it.copy(servicesCatalog = services) }
            } catch (error: Exception) {
                _uiState.update { it.copy(error = error.userMessage()) }
            }
        }
    }

    private fun loadEmployeeServices(token: String, employeeId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingServices = true) }
            try {
                val linked = listEmployeeServicesUseCase(token, employeeId)
                    .map { it.id }
                    .toSet()
                _uiState.update {
                    it.copy(
                        loadingServices = false,
                        linkedServiceIds = linked,
                        selectedServiceIds = linked,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(loadingServices = false, error = error.userMessage())
                }
            }
        }
    }

    private fun loadBusinessHours(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(businessLoading = true) }
            try {
                val hours = getOpeningHoursUseCase(token)
                _uiState.update {
                    it.copy(
                        businessLoading = false,
                        businessDays = mergeBusinessDays(hours),
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(businessLoading = false, error = error.userMessage()) }
            }
        }
    }

    private fun loadEmployeeSchedule(token: String, employeeId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(employeeLoading = true) }
            try {
                val schedules = listWeeklySchedulesUseCase(token, employeeId)
                _uiState.update {
                    it.copy(
                        employeeLoading = false,
                        employeeDays = mergeEmployeeDays(schedules),
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(employeeLoading = false, error = error.userMessage()) }
            }
        }
    }

    private fun updateBusinessDay(dayOfWeek: Int, transform: (ScheduleDayUi) -> ScheduleDayUi) {
        _uiState.update { state ->
            state.copy(
                error = null,
                successMessage = null,
                businessDays = state.businessDays.map {
                    if (it.dayOfWeek == dayOfWeek) transform(it) else it
                },
            )
        }
    }

    private fun updateEmployeeDay(dayOfWeek: Int, transform: (ScheduleDayUi) -> ScheduleDayUi) {
        _uiState.update { state ->
            state.copy(
                error = null,
                successMessage = null,
                employeeDays = state.employeeDays.map {
                    if (it.dayOfWeek == dayOfWeek) transform(it) else it
                },
            )
        }
    }

    private fun validateDays(openDays: List<ScheduleDayUi>): String? {
        if (openDays.isEmpty()) return "Elige al menos un día de atención."
        val invalid = openDays.firstOrNull { !TIME_REGEX.matches(it.start) || !TIME_REGEX.matches(it.end) }
        if (invalid != null) return "Usa horas como 09:00."
        if (openDays.any { it.start >= it.end }) return "La hora de cierre debe ser después de la de apertura."
        return null
    }

    private fun mergeBusinessDays(hours: List<OpeningHour>): List<ScheduleDayUi> {
        val byDay = hours.associateBy { it.dayOfWeek }
        return (1..7).map { day ->
            val hour = byDay[day]
            ScheduleDayUi(
                dayOfWeek = day,
                start = hour?.startTime.orEmpty(),
                end = hour?.endTime.orEmpty(),
                open = hour != null,
            )
        }
    }

    private fun mergeEmployeeDays(schedules: List<WeeklySchedule>): List<ScheduleDayUi> {
        val byDay = schedules.associateBy { it.dayOfWeek }
        return (1..7).map { day ->
            val schedule = byDay[day]
            ScheduleDayUi(
                dayOfWeek = day,
                weeklyScheduleId = schedule?.weeklyScheduleId,
                start = schedule?.startTime.orEmpty(),
                end = schedule?.endTime.orEmpty(),
                open = schedule != null,
            )
        }
    }

    private fun Employee.toUi() = EmployeeScheduleItem(
        employeeId = employeeId,
        name = name,
        lastName = lastName,
        email = email,
        owner = owner,
        selfManagedSchedule = selfManagedSchedule,
    )

    private fun Exception.userMessage(fallback: Exception? = null): String = when (this) {
        is HttpException -> when (code()) {
            400 -> "Revisa las horas e inténtalo de nuevo."
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            403 -> "No tienes permisos para cambiar este horario."
            else -> "No pudimos guardar el horario. Inténtalo de nuevo."
        }
        else -> message ?: fallback?.message ?: "No pudimos conectar. Revisa tu conexión."
    }

    private fun Exception.servicesUserMessage(): String = when (this) {
        is HttpException -> when (code()) {
            400 -> "Revisa los servicios seleccionados."
            401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
            403 -> "No tienes permisos para asignar servicios."
            else -> "No pudimos guardar los servicios."
        }
        else -> message ?: "No pudimos conectar. Revisa tu conexión."
    }

    private companion object {
        val TIME_REGEX = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
    }
}
