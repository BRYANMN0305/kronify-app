package com.kronifyapp.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.domain.model.BusinessAppointment
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.state.UserRole
import com.kronifyapp.ui.viewModel.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val calendarSurface = Color(0xFF0E2025)
private val calendarHeader = Color(0xFF1D3C46)

@Composable
fun CalendarScreen(
    role: UserRole = UserRole.OWNER,
    viewModel: CalendarViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val locale = Locale("es", "CO")
    val isClient = role == UserRole.CLIENT
    LaunchedEffect(role) { viewModel.configureRole(role) }
    val filtered = state.appointments.filter { appointment ->
        (state.selectedEmployee == "Todos los empleados" || appointment.employeeName == state.selectedEmployee) &&
            (state.selectedService == "Todos los servicios" || appointment.serviceName == state.selectedService) &&
            (state.selectedStatus == "Todos los estados" || appointment.status.equals(state.selectedStatus, ignoreCase = true))
    }
    val days = calendarDays(state.month)
    val noAppointments = filtered.isEmpty()

    AuroraBackground {
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 92.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        if (isClient) "TU CALENDARIO" else "CALENDARIO OPERATIVO",
                        color = Neon,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp,
                    )
                    Text("Calendario", color = TextPrimary, fontSize = 27.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 5.dp))
                }
            }

            Row(Modifier.fillMaxWidth().padding(top = 17.dp, bottom = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MonthButton("‹") { viewModel.moveMonth(-1) }
                    Text(state.month.format(DateTimeFormatter.ofPattern("MMMM yyyy", locale)).replaceFirstChar { it.titlecase(locale) }, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp))
                    MonthButton("›") { viewModel.moveMonth(1) }
                }
            }

            if (!isClient) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterMenu(state.selectedEmployee, listOf("Todos los empleados") + state.employees.filter { it.active }.map { it.fullName }, viewModel::selectEmployee)
                    FilterMenu(state.selectedService, listOf("Todos los servicios") + state.services.filter { it.active }.map { it.name }, viewModel::selectService)
                    FilterMenu(state.selectedStatus, listOf("Todos los estados", "PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"), viewModel::selectStatus)
                }
            }

            if (noAppointments && !state.isLoading && state.error == null) {
                Text(
                    if (isClient) "No tienes citas agendadas este mes."
                    else "No hay citas en este rango. Prueba cambiando de mes o quitando filtros.",
                    color = TextTertiary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 13.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF102329)).border(1.dp, StrokeSubtle, RoundedCornerShape(10.dp)).padding(horizontal = 13.dp, vertical = 12.dp))
            } else Spacer(Modifier.height(13.dp))

            if (state.error != null && !state.isLoading) {
                Text(state.error.orEmpty(), color = TextTertiary, fontSize = 12.sp, lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF102329)).border(1.dp, StrokeSubtle, RoundedCornerShape(10.dp)).padding(12.dp))
            }
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Neon) }
                else -> MonthlyGrid(
                    month = state.month,
                    days = days,
                    appointments = filtered,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun MonthButton(label: String, onClick: () -> Unit) {
    Box(Modifier.size(36.dp).clip(RoundedCornerShape(9.dp)).background(calendarHeader).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, color = Color(0xFFB9E7F0), fontSize = 21.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun FilterMenu(selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(Color(0xFF416A78)).clickable { expanded = true }.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(selected, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            Text("⌄", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color(0xFF18333B))) {
            options.distinct().forEach { option ->
                DropdownMenuItem(text = { Text(option, color = TextPrimary, fontSize = 13.sp) }, onClick = { onSelect(option); expanded = false })
            }
        }
    }
}

@Composable
private fun MonthlyGrid(month: LocalDate, days: List<LocalDate?>, appointments: List<BusinessAppointment>, modifier: Modifier = Modifier) {
    val weekdays = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
    Column(modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(calendarSurface).border(1.dp, StrokeSubtle, RoundedCornerShape(12.dp))) {
        Row(Modifier.fillMaxWidth().height(54.dp).background(calendarHeader)) {
            weekdays.forEach { day -> Box(Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) { Text(day, color = Color(0xFFC5F3FF), fontSize = 10.sp, fontWeight = FontWeight.Bold) } }
        }
        days.chunked(7).forEachIndexed { weekIndex, week ->
            Row(Modifier.fillMaxWidth().weight(1f)) {
                week.forEachIndexed { columnIndex, date ->
                    val dateAppointments = date?.let { day -> appointments.filter { it.startAt.startsWith(day.toString()) } }.orEmpty()
                    Column(Modifier.weight(1f).fillMaxSize().background(if ((weekIndex + columnIndex) % 2 == 0) calendarSurface else Color(0xFF10252B)).border(.5.dp, StrokeSubtle).padding(6.dp)) {
                        if (date != null) {
                            Text(date.dayOfMonth.toString(), color = if (date.month == month.month) Color(0xFFC5E0E7) else Color(0xFF527C87), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            dateAppointments.take(3).forEach { appointment ->
                                Text("${time(appointment.startAt)} ${appointment.customerName}", color = Color(0xFFB9F1FF), fontSize = 8.sp, maxLines = 1,
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF205061)).padding(horizontal = 4.dp, vertical = 3.dp))
                            }
                            if (dateAppointments.size > 3) Text("+${dateAppointments.size - 3} más", color = TextTertiary, fontSize = 8.sp, modifier = Modifier.padding(top = 3.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun calendarDays(month: LocalDate): List<LocalDate?> {
    val first = month.withDayOfMonth(1)
    val mondayOffset = first.dayOfWeek.value - 1
    val cells = ((mondayOffset + month.lengthOfMonth() + 6) / 7) * 7
    val gridStart = first.minusDays(mondayOffset.toLong())
    return (0 until cells).map { index -> gridStart.plusDays(index.toLong()) }
}

private fun time(value: String): String = value.substringAfter('T', "--:--").take(5)
