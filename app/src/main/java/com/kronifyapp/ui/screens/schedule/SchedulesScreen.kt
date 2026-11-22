package com.kronifyapp.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.components.AnimatedStatusMessage
import com.kronifyapp.ui.components.AutoDismissSuccess
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.ChevronRightGlyph
import com.kronifyapp.ui.components.ClockGlyph
import com.kronifyapp.ui.components.KronifySwitch
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.domain.model.BusinessService
import com.kronifyapp.ui.state.EmployeeScheduleItem
import com.kronifyapp.ui.state.ScheduleDayUi
import com.kronifyapp.ui.state.ScheduleMode
import com.kronifyapp.ui.state.ScheduleUiState
import com.kronifyapp.ui.theme.Amber
import com.kronifyapp.ui.theme.Acero1
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.viewModel.ScheduleViewModel
import java.text.NumberFormat
import java.util.Locale

private val DayLabels = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
private val DayNames = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

private data class EditTarget(val employee: Boolean, val dayOfWeek: Int)

@Composable
fun SchedulesScreen(
    onOpenTeam: () -> Unit = {},
    viewModel: ScheduleViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    AutoDismissSuccess(state.successMessage) { viewModel.clearMessages() }
    var ownerSection by rememberSaveable { mutableStateOf(0) }
    var editTarget by remember { mutableStateOf<EditTarget?>(null) }
    var editStart by remember { mutableStateOf("") }
    var editEnd by remember { mutableStateOf("") }

    fun openEditor(employee: Boolean, day: ScheduleDayUi) {
        editStart = day.start.ifBlank { "09:00" }
        editEnd = day.end.ifBlank { "18:00" }
        editTarget = EditTarget(employee, day.dayOfWeek)
    }

    AuroraBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 58.dp, bottom = 130.dp),
        ) {
            item {
                Text(
                    text = if (state.mode == ScheduleMode.SELF) "TU CUENTA" else "TU NEGOCIO",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = "Horarios",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = when (state.mode) {
                        ScheduleMode.SELF -> "Consulta y actualiza los días en que trabajas."
                        else -> "Configura la apertura del negocio y el horario de tu equipo."
                    },
                    color = TextTertiary,
                    fontSize = 12.5.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp),
                )
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Neon)
                    }
                }
            } else {
                when (state.mode) {
                    ScheduleMode.OWNER -> {
                        item {
                            OwnerSectionSwitch(
                                selected = ownerSection,
                                onSelected = { ownerSection = it },
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                        item {
                            if (ownerSection == 0) {
                                BusinessScheduleContent(
                                    state = state,
                                    onToggle = viewModel::setBusinessOpen,
                                    onEdit = { openEditor(employee = false, day = it) },
                                    onSave = viewModel::saveBusiness,
                                )
                            } else {
                                EmployeeSchedulesContent(
                                    state = state,
                                    onSelectEmployee = viewModel::selectEmployee,
                                    onTogglePermission = viewModel::toggleSelfManagement,
                                    onToggleDay = viewModel::setEmployeeOpen,
                                    onEditDay = { openEditor(employee = true, day = it) },
                                    onSave = viewModel::saveEmployee,
                                    onToggleService = viewModel::toggleService,
                                    onSaveServices = viewModel::saveEmployeeServices,
                                    onOpenTeam = onOpenTeam,
                                )
                            }
                        }
                    }

                    ScheduleMode.SELF -> {
                        item {
                            SelfScheduleContent(
                                state = state,
                                onToggle = viewModel::setEmployeeOpen,
                                onEdit = { openEditor(employee = true, day = it) },
                                onSave = viewModel::saveEmployee,
                            )
                        }
                    }

                    null -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = state.error ?: "No pudimos cargar tus horarios.",
                                    color = TextTertiary,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    editTarget?.let { target ->
        val employeeName = state.selectedEmployee?.fullName.orEmpty()
        AlertDialog(
            onDismissRequest = { editTarget = null },
            title = {
                Text(
                    text = "Editar ${DayNames[target.dayOfWeek - 1].lowercase()}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    Text(
                        text = if (target.employee && employeeName.isNotBlank()) {
                            "Horario de $employeeName"
                        } else {
                            "Horario de atención del negocio"
                        },
                        color = TextTertiary,
                        fontSize = 12.sp,
                    )
                    Spacer(Modifier.height(14.dp))
                    KronifyTextField(
                        value = editStart,
                        onValueChange = { editStart = it.take(5) },
                        label = "Apertura",
                        placeholder = "09:00",
                        keyboardType = KeyboardType.Text,
                    )
                    Spacer(Modifier.height(12.dp))
                    KronifyTextField(
                        value = editEnd,
                        onValueChange = { editEnd = it.take(5) },
                        label = "Cierre",
                        placeholder = "18:00",
                        keyboardType = KeyboardType.Text,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (target.employee) {
                            viewModel.setEmployeeTimes(target.dayOfWeek, editStart, editEnd)
                        } else {
                            viewModel.setBusinessTimes(target.dayOfWeek, editStart, editEnd)
                        }
                        editTarget = null
                    },
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { editTarget = null }) {
                    Text("Cancelar")
                }
            },
            containerColor = SurfaceDeep,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
        )
    }
}

@Composable
private fun OwnerSectionSwitch(selected: Int, onSelected: (Int) -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(Color(0x66040A0C))
            .border(1.dp, StrokeSubtle, shape)
            .padding(4.dp),
    ) {
        OwnerSectionTab(
            text = "Horario del negocio",
            active = selected == 0,
            modifier = Modifier.weight(1f),
            onClick = { onSelected(0) },
        )
        OwnerSectionTab(
            text = "Empleados",
            active = selected == 1,
            modifier = Modifier.weight(1f),
            onClick = { onSelected(1) },
        )
    }
}

@Composable
private fun OwnerSectionTab(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) Color(0x33D5F0F7) else Color.Transparent)
            .border(
                if (active) 1.dp else 0.dp,
                if (active) StrokeStrong else Color.Transparent,
                RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (active) TextPrimary else TextTertiary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun BusinessScheduleContent(
    state: ScheduleUiState,
    onToggle: (Int, Boolean) -> Unit,
    onEdit: (ScheduleDayUi) -> Unit,
    onSave: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ClockGlyph(tint = TextTertiary)
        Text(
            text = "HORARIO DE ATENCIÓN",
            color = TextTertiary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 8.dp),
        )
    }

    if (state.businessLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = Neon)
        }
    } else {
        DaysCard(
            days = state.businessDays,
            onToggle = onToggle,
            onEdit = onEdit,
        )
        Spacer(Modifier.height(16.dp))
        PrimaryButton(
            text = if (state.businessSaving) "Guardando…" else "Guardar horario",
            onClick = onSave,
        )
        Feedback(state)
        Spacer(Modifier.height(14.dp))
        InfoNote(
            "Al cambiar el horario, las citas ya reservadas se mantienen. Los empleados no pueden trabajar fuera de este rango.",
        )
    }
}

@Composable
private fun EmployeeSchedulesContent(
    state: ScheduleUiState,
    onSelectEmployee: (Long) -> Unit,
    onTogglePermission: (Long, Boolean) -> Unit,
    onToggleDay: (Int, Boolean) -> Unit,
    onEditDay: (ScheduleDayUi) -> Unit,
    onSave: () -> Unit,
    onToggleService: (Long) -> Unit,
    onSaveServices: () -> Unit,
    onOpenTeam: () -> Unit,
) {
    if (state.employees.isEmpty()) {
        EmptyTeamCard(onOpenTeam)
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "EMPLEADOS",
            color = TextTertiary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = "Autogestión",
            color = TextTertiary,
            fontSize = 10.sp,
        )
    }

    EmployeeListCard(
        employees = state.employees,
        selectedId = state.selectedEmployeeId,
        permissionBusyId = state.permissionBusyId,
        onSelect = onSelectEmployee,
        onTogglePermission = onTogglePermission,
    )

    val selected = state.selectedEmployee
    if (selected != null) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = "HORARIO DE ${selected.fullName.uppercase()}",
            color = TextTertiary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 5.dp, bottom = 9.dp),
        )

        if (selected.selfManagedSchedule) {
            Text(
                text = "Este empleado puede editar su horario. Como dueño, también puedes ajustarlo.",
                color = Amber,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 5.dp, bottom = 9.dp),
            )
        }

        if (state.employeeLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Neon)
            }
        } else if (!selected.selfManagedSchedule) {
            Text(
                text = "Este empleado trabaja con el horario de atención del negocio.",
                color = TextSecondary,
                fontSize = 11.5.sp,
                modifier = Modifier.padding(start = 5.dp, bottom = 9.dp),
            )
            if (state.businessLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Neon)
                }
            } else if (state.businessDays.none { it.open }) {
                InfoNote("Primero configura el horario de atención del negocio en la pestaña anterior.")
            } else {
                DaysCard(
                    days = state.businessDays,
                    onToggle = { _, _ -> },
                    onEdit = {},
                    readOnly = true,
                )
            }
        } else {
            DaysCard(
                days = state.employeeDays,
                onToggle = onToggleDay,
                onEdit = onEditDay,
            )
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = if (state.employeeSaving) "Guardando…" else "Guardar horario",
                onClick = onSave,
            )
            Feedback(state)
            Spacer(Modifier.height(14.dp))
            InfoNote(
                "El horario del empleado debe estar dentro del horario de atención del negocio.",
            )
        }

        Spacer(Modifier.height(26.dp))
        Text(
            text = "SERVICIOS QUE PUEDE REALIZAR",
            color = TextTertiary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 5.dp, bottom = 4.dp),
        )
        Text(
            text = "Marca los servicios que este empleado puede atender.",
            color = TextTertiary,
            fontSize = 10.5.sp,
            modifier = Modifier.padding(start = 5.dp, bottom = 9.dp),
        )

            when {
                state.loadingServices -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Neon)
                }

                state.servicesCatalog.isEmpty() -> InfoNote(
                    "Aún no hay servicios creados. Créalos desde la sección Servicios para poder asignarlos.",
                )

                else -> {
                    ServicesSelectorCard(
                        services = state.servicesCatalog,
                        selectedIds = state.selectedServiceIds,
                        onToggle = onToggleService,
                    )
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(
                        text = if (state.savingServices) "Guardando…" else "Guardar servicios",
                        onClick = onSaveServices,
                    )
                    Feedback(state, services = true)
                }
            }
        }
    }

@Composable
private fun SelfScheduleContent(
    state: ScheduleUiState,
    onToggle: (Int, Boolean) -> Unit,
    onEdit: (ScheduleDayUi) -> Unit,
    onSave: () -> Unit,
) {
    val employee = state.selectedEmployee
    if (employee == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No encontramos tu registro de empleado.",
                color = TextTertiary,
                fontSize = 13.sp,
            )
        }
        return
    }

    Text(
        text = "MI HORARIO",
        color = TextTertiary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 5.dp, bottom = 9.dp),
    )

    if (!employee.selfManagedSchedule) {
        val shape = RoundedCornerShape(20.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(Color(0x26FFC75B))
                .border(1.dp, Amber.copy(alpha = .45f), shape)
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text("ⓘ", color = Amber, fontSize = 16.sp)
            Text(
                text = "El dueño aún no ha habilitado la autogestión de tu horario. Cuando lo haga, podrás editar tus días aquí.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 9.dp),
            )
        }
        return
    }

    if (state.employeeLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = Neon)
        }
    } else {
        DaysCard(
            days = state.employeeDays,
            onToggle = onToggle,
            onEdit = onEdit,
        )
        Spacer(Modifier.height(16.dp))
        PrimaryButton(
            text = if (state.employeeSaving) "Guardando…" else "Guardar mi horario",
            onClick = onSave,
        )
        Feedback(state)
        Spacer(Modifier.height(14.dp))
        InfoNote(
            "Tu horario debe estar dentro del horario de atención del negocio.",
        )
    }
}

@Composable
private fun EmployeeListCard(
    employees: List<EmployeeScheduleItem>,
    selectedId: Long?,
    permissionBusyId: Long?,
    onSelect: (Long) -> Unit,
    onTogglePermission: (Long, Boolean) -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape),
    ) {
        employees.forEachIndexed { index, employee ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (employee.employeeId == selectedId) Color(0x143FE1FF) else Color.Transparent)
                    .clickable { onSelect(employee.employeeId) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF696251))
                        .border(1.dp, Color(0xFF9E9478), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initials(employee),
                        color = Color(0xFFFFE9BD),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = employee.fullName,
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (employee.owner) {
                            Text(
                                text = "Dueño",
                                color = Amber,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .background(Color(0x26E7B96F), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 1.dp),
                            )
                        }
                    }
                    Text(
                        text = employee.email,
                        color = TextTertiary,
                        fontSize = 10.5.sp,
                        modifier = Modifier.padding(top = 1.dp),
                    )
                }

                if (!employee.owner) {
                    KronifySwitch(
                        checked = employee.selfManagedSchedule,
                        onCheckedChange = { onTogglePermission(employee.employeeId, it) },
                        enabled = permissionBusyId == null,
                    )
                } else {
                    Text(
                        text = "Autogestión",
                        color = TextTertiary,
                        fontSize = 9.5.sp,
                    )
                }

                ChevronRightGlyph(
                    tint = TextTertiary,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            if (index != employees.lastIndex) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(StrokeSubtle),
                )
            }
        }
    }
}

@Composable
private fun ServicesSelectorCard(
    services: List<BusinessService>,
    selectedIds: Set<Long>,
    onToggle: (Long) -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape),
    ) {
        services.forEachIndexed { index, service ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = service.id in selectedIds,
                    onCheckedChange = { onToggle(service.id) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Neon,
                        uncheckedColor = StrokeStrong,
                        checkmarkColor = Acero1,
                    ),
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp),
                ) {
                    Text(
                        text = service.name,
                        color = TextPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${service.durationMinutes} min · ${formatServicePrice(service.price)}",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 1.dp),
                    )
                }
            }
            if (index != services.lastIndex) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(StrokeSubtle),
                )
            }
        }
    }
}

private fun formatServicePrice(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))
    formatter.maximumFractionDigits = 0
    return formatter.format(value)
}

@Composable
private fun DaysCard(
    days: List<ScheduleDayUi>,
    onToggle: (Int, Boolean) -> Unit,
    onEdit: (ScheduleDayUi) -> Unit,
    readOnly: Boolean = false,
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape),
    ) {
        days.forEachIndexed { index, day ->
            ScheduleDayRow(
                day = day,
                onToggle = { onToggle(day.dayOfWeek, it) },
                onEdit = { onEdit(day) },
                readOnly = readOnly,
            )
            if (index != days.lastIndex) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(StrokeSubtle),
                )
            }
        }
    }
}

@Composable
private fun ScheduleDayRow(
    day: ScheduleDayUi,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    readOnly: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = DayLabels[day.dayOfWeek - 1],
            color = if (day.open) TextPrimary else TextTertiary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.size(width = 42.dp, height = 20.dp),
        )

        Text(
            text = if (day.open) "${day.start.ifBlank { "09:00" }} — ${day.end.ifBlank { "18:00" }}" else "Cerrado",
            color = if (day.open) TextSecondary else TextTertiary,
            fontSize = 13.sp,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(7.dp))
                .clickable(enabled = day.open && !readOnly, onClick = onEdit)
                .padding(horizontal = 6.dp, vertical = 3.dp),
        )

        KronifySwitch(
            checked = day.open,
            onCheckedChange = onToggle,
            enabled = !readOnly,
        )
    }
}

@Composable
private fun Feedback(state: ScheduleUiState, services: Boolean = false) {
    val isServiceMessage = { text: String -> text.contains("servicio", ignoreCase = true) }
    if (services) {
        state.error?.takeIf(isServiceMessage)?.let {
            AnimatedStatusMessage(text = it, isError = true)
        }
        state.successMessage?.takeIf(isServiceMessage)?.let {
            AnimatedStatusMessage(text = it, isError = false)
        }
    } else {
        state.error?.takeIf { !isServiceMessage(it) }?.let {
            AnimatedStatusMessage(text = it, isError = true)
        }
        state.successMessage?.takeIf { !isServiceMessage(it) }?.let {
            AnimatedStatusMessage(text = it, isError = false)
        }
    }
}

@Composable
private fun EmptyTeamCard(onOpenTeam: () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape)
            .padding(16.dp),
    ) {
        Text(
            text = "Aún no tienes empleados activos.",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Invita a tu equipo para configurar sus horarios y permisos.",
            color = TextTertiary,
            fontSize = 11.5.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(Modifier.height(12.dp))
        PrimaryButton(text = "Ir a Equipo", onClick = onOpenTeam)
    }
}

@Composable
private fun InfoNote(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x141A829B))
            .border(1.dp, Color(0x283FE1FF), RoundedCornerShape(14.dp))
            .padding(13.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text("ⓘ", color = Neon, fontSize = 15.sp)
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 11.5.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(start = 9.dp),
        )
    }
}

private fun initials(employee: EmployeeScheduleItem): String {
    val first = employee.name.trim().firstOrNull()?.toString().orEmpty()
    val last = employee.lastName.trim().firstOrNull()?.toString().orEmpty()
    return (first + last).uppercase().ifBlank { "E" }
}
