package com.kronifyapp.ui.state

import com.kronifyapp.domain.model.BusinessAppointment
import com.kronifyapp.domain.model.BusinessService
import com.kronifyapp.domain.model.Employee
import java.time.LocalDate

data class CalendarUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val appointments: List<BusinessAppointment> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val month: LocalDate = LocalDate.now().withDayOfMonth(1),
    val employees: List<Employee> = emptyList(),
    val services: List<BusinessService> = emptyList(),
    val selectedEmployee: String = "Todos los empleados",
    val selectedService: String = "Todos los servicios",
    val selectedStatus: String = "Todos los estados",
)
