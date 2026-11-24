package com.kronifyapp.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.domain.model.ClientAppointment
import com.kronifyapp.domain.model.RecentBusiness
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.viewModel.ClientAppointmentsViewModel
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val sectionOutline = Color(0xFF19353C)
private val sectionFill = Color(0x1A061115)
private val emptyFill = Color(0xFF102329)

@Composable
fun ClientAppointmentsScreen(
    viewModel: ClientAppointmentsViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    AuroraBackground {
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 108.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("TU ACTIVIDAD", color = Neon, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    Text("Mis citas", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 5.dp))
                }
                Text(
                    "Cliente",
                    color = Color(0xFFBDEBF4),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clip(CircleShape).background(Color(0xFF10272C)).border(1.dp, Color(0xFF18505B), CircleShape).padding(horizontal = 14.dp, vertical = 9.dp),
                )
            }

            if (state.showRecentSection) {
                SectionCard(
                    eyebrow = "AGENDA RÁPIDA",
                    title = "Negocios recientes",
                    modifier = Modifier.padding(top = 22.dp),
                ) {
                    when {
                        state.recentLoading -> LoadingMessage("Cargando negocios recientes…")
                        state.recentError != null -> EmptyMessage(state.recentError.orEmpty())
                        state.recentBusinesses.isEmpty() -> EmptyMessage("Aún no tienes negocios recientes. Cuando reserves una cita, aparecerá aquí para que vuelvas a agendar fácilmente.")
                        else -> Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            state.recentBusinesses.forEach { business -> RecentBusinessRow(business) }
                        }
                    }
                }
            }

            SectionCard(
                eyebrow = "HISTORIAL",
                title = "Mis citas",
                modifier = Modifier.padding(top = 22.dp),
            ) {
                when {
                    state.isLoading -> LoadingMessage("Cargando tus citas…")
                    state.error != null -> EmptyMessage(state.error.orEmpty())
                    state.appointments.isEmpty() -> EmptyMessage("Aún no tienes citas registradas.")
                    else -> Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        state.appointments.forEach { appointment -> AppointmentRow(appointment) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    eyebrow: String,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier.fillMaxWidth()
            .clip(RoundedCornerShape(21.dp))
            .background(sectionFill)
            .border(1.dp, sectionOutline, RoundedCornerShape(21.dp))
            .padding(17.dp),
    ) {
        Text(eyebrow, color = Neon, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text(title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp, bottom = 15.dp))
        content()
    }
}

@Composable
private fun EmptyMessage(message: String) {
    Text(
        message,
        color = Color(0xFF8DB9C3),
        fontSize = 13.sp,
        lineHeight = 20.sp,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(emptyFill).border(1.dp, StrokeSubtle, RoundedCornerShape(12.dp)).padding(horizontal = 14.dp, vertical = 15.dp),
    )
}

@Composable
private fun LoadingMessage(message: String) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(emptyFill).padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        CircularProgressIndicator(color = Neon, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
        Text(message, color = TextTertiary, fontSize = 13.sp)
    }
}

@Composable
private fun RecentBusinessRow(business: RecentBusiness) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp)).background(emptyFill).border(1.dp, StrokeSubtle, RoundedCornerShape(13.dp)).padding(13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(42.dp).clip(CircleShape).background(Color(0xFF1C414B)), contentAlignment = Alignment.Center) {
            Text(initials(business.name), color = Neon, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f)) {
            Text(business.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            val meta = listOfNotNull(business.category?.takeIf { it.isNotBlank() }, business.address?.takeIf { it.isNotBlank() }).joinToString(" · ")
            if (meta.isNotBlank()) Text(meta, color = TextTertiary, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
            Text("Última cita: ${formatDate(business.lastAppointmentAt)}", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 5.dp))
        }
    }
}

@Composable
private fun AppointmentRow(appointment: ClientAppointment) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp)).background(emptyFill).border(1.dp, StrokeSubtle, RoundedCornerShape(13.dp)).padding(13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Column(Modifier.size(width = 64.dp, height = 54.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF183740)), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(formatTime(appointment.startAt), color = Neon, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(formatDate(appointment.startAt), color = TextTertiary, fontSize = 9.sp, modifier = Modifier.padding(top = 3.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(appointment.serviceName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("${appointment.employeeName} · ${appointment.serviceDurationMinutes} min", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            Text(statusLabel(appointment.status), color = statusColor(appointment.status), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 5.dp))
        }
    }
}

private fun initials(value: String): String = value.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }.ifBlank { "N" }

private fun formatDate(value: String?): String = parseDate(value)?.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "CO"))) ?: "Sin fecha"

private fun formatTime(value: String): String = runCatching {
    val time = value.substringAfter('T').take(5)
    if (time.length == 5) time else "--:--"
}.getOrDefault("--:--")

private fun parseDate(value: String?): LocalDateTime? {
    if (value.isNullOrBlank()) return null
    return runCatching { OffsetDateTime.parse(value).toLocalDateTime() }
        .recoverCatching { LocalDateTime.parse(value) }
        .getOrNull()
}

private fun statusLabel(status: String): String = when (status.uppercase()) {
    "PENDING" -> "Pendiente"
    "CONFIRMED" -> "Confirmada"
    "COMPLETED" -> "Completada"
    "CANCELLED" -> "Cancelada"
    "NO_SHOW" -> "No asistió"
    else -> status.lowercase().replaceFirstChar { it.titlecase(Locale("es", "CO")) }
}

private fun statusColor(status: String): Color = when (status.uppercase()) {
    "CONFIRMED", "COMPLETED" -> Color(0xFF63E6B6)
    "CANCELLED", "NO_SHOW" -> Color(0xFFFF8A8A)
    else -> Color(0xFFE7B96F)
}
