package com.kronifyapp.ui.screens.team

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.domain.model.Invitation
import com.kronifyapp.ui.components.AnimatedStatusMessage
import com.kronifyapp.ui.components.AutoDismissSuccess
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.KronifySwitch
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.ui.state.EmployeeScheduleItem
import com.kronifyapp.ui.state.TeamUiState
import com.kronifyapp.ui.theme.Amber
import com.kronifyapp.ui.theme.DangerRed
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.OkGreen
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.viewModel.TeamViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TeamScreen(
    onBack: () -> Unit,
    viewModel: TeamViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    AutoDismissSuccess(state.successMessage) { viewModel.clearMessages() }
    var showInvite by remember { mutableStateOf(false) }
    var inviteEmail by remember { mutableStateOf("") }

    LaunchedEffect(state.successMessage) {
        if (showInvite && state.successMessage?.contains("Invitación enviada") == true) {
            showInvite = false
            inviteEmail = ""
        }
    }

    AuroraBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 28.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x0DD5F0F7))
                            .border(1.dp, StrokeSubtle, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "‹",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.padding(bottom = 2.dp),
                        )
                    }
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            text = "NEGOCIO",
                            color = TextTertiary,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            text = "Equipo",
                            color = TextPrimary,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Administra a tu equipo, sus permisos y las invitaciones pendientes.",
                    color = TextTertiary,
                    fontSize = 12.5.sp,
                    modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
                )
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Neon)
                    }
                }
            } else {
                item {
                    SectionHeader(
                        title = "EQUIPO",
                        action = "Invitar miembro",
                        onAction = {
                            viewModel.clearMessages()
                            inviteEmail = ""
                            showInvite = true
                        },
                    )
                    Spacer(Modifier.height(9.dp))
                }

                if (state.employees.isEmpty()) {
                    item {
                        EmptyCard("Aún no hay empleados activos en tu negocio.")
                    }
                } else {
                    items(state.employees, key = { it.employeeId }) { employee ->
                        EmployeeRowItem(
                            employee = employee,
                            permissionBusyId = state.permissionBusyId,
                            onTogglePermission = viewModel::toggleSelfManagement,
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(22.dp))
                    SectionHeader(title = "INVITACIONES")
                    Spacer(Modifier.height(9.dp))
                }

                if (state.invitations.isEmpty()) {
                    item {
                        EmptyCard("No hay invitaciones registradas.")
                    }
                } else {
                    items(state.invitations, key = { it.invitationId }) { invitation ->
                        InvitationRowItem(
                            invitation = invitation,
                            busyId = state.busyInvitationId,
                            onResend = viewModel::resend,
                            onCancel = viewModel::cancel,
                        )
                    }
                }

                if (!showInvite) {
                    item {
                        AnimatedStatusMessage(text = state.error, isError = true)
                        AnimatedStatusMessage(text = state.successMessage, isError = false)
                    }
                }

                item {
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = "Los empleados con autogestión habilitada pueden editar su propio horario desde la app.",
                        color = TextTertiary,
                        fontSize = 10.5.sp,
                    )
                }
            }
        }
    }

    if (showInvite) {
        AlertDialog(
            onDismissRequest = { if (!state.isInviting) showInvite = false },
            title = {
                Text(
                    text = "Invitar miembro",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enviaremos una invitación por correo para que se una a tu equipo.",
                        color = TextTertiary,
                        fontSize = 12.sp,
                    )
                    Spacer(Modifier.height(14.dp))
                    KronifyTextField(
                        value = inviteEmail,
                        onValueChange = { inviteEmail = it },
                        label = "Correo del empleado",
                        placeholder = "correo@empleado.com",
                        keyboardType = KeyboardType.Email,
                    )
                    state.error?.let {
                        AnimatedStatusMessage(text = it, isError = true)
                    }
                    if (state.isInviting) {
                        Text(
                            text = "Enviando invitación…",
                            color = TextTertiary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 10.dp),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.invite(inviteEmail) },
                    enabled = !state.isInviting,
                ) {
                    Text("Enviar invitación")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInvite = false }, enabled = !state.isInviting) {
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
private fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = TextTertiary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        if (action != null && onAction != null) {
            Spacer(Modifier.weight(1f))
            Text(
                text = action,
                color = Neon,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAction)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun EmptyCard(message: String) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape)
            .padding(16.dp),
    ) {
        Text(
            text = message,
            color = TextTertiary,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun EmployeeRowItem(
    employee: EmployeeScheduleItem,
    permissionBusyId: Long?,
    onTogglePermission: (Long, Boolean) -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape)
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

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Autogestión",
                color = TextTertiary,
                fontSize = 9.sp,
            )
            Spacer(Modifier.height(4.dp))
            if (employee.owner) {
                Text(
                    text = "Siempre",
                    color = Amber,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            } else {
                KronifySwitch(
                    checked = employee.selfManagedSchedule,
                    onCheckedChange = { onTogglePermission(employee.employeeId, it) },
                    enabled = permissionBusyId == null,
                )
            }
        }
    }
}

@Composable
private fun InvitationRowItem(
    invitation: Invitation,
    busyId: Long?,
    onResend: (Long) -> Unit,
    onCancel: (Long) -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = invitation.email,
                    color = TextPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Enviada ${formatDate(invitation.createdAt)} · expira ${formatDate(invitation.expiresAt)}",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            InvitationStatusTag(invitation.status)
        }

        if (invitation.status == "PENDING") {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = "Reenviar",
                    color = Neon,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(enabled = busyId == null) { onResend(invitation.invitationId) }
                        .padding(horizontal = 2.dp, vertical = 3.dp),
                )
                Text(
                    text = "Cancelar",
                    color = DangerRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(enabled = busyId == null) { onCancel(invitation.invitationId) }
                        .padding(horizontal = 2.dp, vertical = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun InvitationStatusTag(status: String) {
    val (label, color) = when (status) {
        "PENDING" -> "Pendiente" to Amber
        "ACCEPTED" -> "Aceptada" to OkGreen
        "CANCELLED" -> "Cancelada" to DangerRed
        "EXPIRED" -> "Expirada" to TextTertiary
        else -> status.ifBlank { "—" } to TextTertiary
    }
    Text(
        text = label,
        color = color,
        fontSize = 9.5.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color.copy(alpha = .12f), RoundedCornerShape(20.dp))
            .border(1.dp, color.copy(alpha = .35f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

private fun initials(employee: EmployeeScheduleItem): String {
    val first = employee.name.trim().firstOrNull()?.toString().orEmpty()
    val last = employee.lastName.trim().firstOrNull()?.toString().orEmpty()
    return (first + last).uppercase().ifBlank { "E" }
}

private fun formatDate(value: String?): String {
    if (value.isNullOrBlank()) return "—"
    return runCatching {
        LocalDateTime.parse(value).format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("es-ES")))
    }.getOrElse { value.take(10) }
}
