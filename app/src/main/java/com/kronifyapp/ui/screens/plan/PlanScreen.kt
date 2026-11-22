package com.kronifyapp.ui.screens.plan

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.domain.model.Plan
import com.kronifyapp.domain.model.PlanUsage
import com.kronifyapp.ui.components.AnimatedStatusMessage
import com.kronifyapp.ui.components.AutoDismissSuccess
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.PrimaryButton
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
import com.kronifyapp.ui.theme.TextQuaternary
import com.kronifyapp.ui.viewModel.PlanViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PlanScreen(
    onBack: () -> Unit,
    viewModel: PlanViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    AutoDismissSuccess(state.successMessage) { viewModel.clearMessages() }
    var codes by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }

    AuroraBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 30.dp),
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
                            text = "SUSCRIPCIÓN",
                            color = TextTertiary,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            text = "Plan y uso",
                            color = TextPrimary,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (state.isLoading && state.current == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Neon)
                    }
                }
            } else {
                state.current?.let { current ->
                    item {
                        CurrentPlanCard(current)
                        Spacer(Modifier.height(18.dp))
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, bottom = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "PLANES DISPONIBLES",
                            color = TextTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                    }
                }

                if (state.availablePlans.isEmpty()) {
                    item {
                        Text(
                            text = "No hay planes disponibles en este momento.",
                            color = TextTertiary,
                            fontSize = 12.5.sp,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                } else {
                    val currentPlanId = state.current?.plan?.planId
                    items(state.availablePlans, key = { it.planId }) { plan ->
                        PlanCard(
                            plan = plan,
                            isCurrent = plan.planId == currentPlanId,
                            isChanging = state.isAssigning && state.assigningPlanId == plan.planId,
                            code = codes[plan.planId].orEmpty(),
                            onCodeChange = { value -> codes = codes + (plan.planId to value) },
                            onSelect = {
                                viewModel.assignPlan(plan.planId, codes[plan.planId])
                            },
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                item {
                    AnimatedStatusMessage(text = state.error, isError = true)
                    AnimatedStatusMessage(text = state.successMessage, isError = false)

                    Text(
                        text = "Los cambios de plan se aplican al negocio y se reflejan en el uso de tus límites.",
                        color = TextQuaternary,
                        fontSize = 10.5.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentPlanCard(current: PlanUsage) {
    val plan = current.plan
    val shape = RoundedCornerShape(25.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape)
            .padding(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PLAN ACTUAL",
                    color = TextTertiary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = plan.displayName.ifBlank { plan.name },
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = formatPrice(plan.monthlyPriceCents) + " / mes",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            StatusTag(current.subscriptionStatus)
        }

        Spacer(Modifier.height(18.dp))

        UsageMeter(
            label = "Citas este mes",
            used = current.currentMonthAppointmentCount,
            limit = plan.monthlyAppointmentLimit,
            reached = current.appointmentLimitReached,
            exceeded = current.appointmentLimitExceeded,
        )
        Spacer(Modifier.height(12.dp))
        UsageMeter(
            label = "Servicios activos",
            used = current.serviceCount,
            limit = plan.serviceLimit,
            reached = current.serviceLimitReached,
            exceeded = current.serviceLimitExceeded,
        )
        Spacer(Modifier.height(12.dp))
        UsageMeter(
            label = "Miembros del equipo",
            used = current.employeeCount,
            limit = plan.employeeLimit,
            reached = current.employeeLimitReached,
            exceeded = current.employeeLimitExceeded,
        )
    }
}

@Composable
private fun UsageMeter(
    label: String,
    used: Int,
    limit: Int?,
    reached: Boolean,
    exceeded: Boolean,
) {
    val unlimited = limit == null || limit <= 0
    val fraction = if (unlimited) 1f else (used.toFloat() / limit.toFloat()).coerceIn(0f, 1f)
    val color = when {
        exceeded -> DangerRed
        reached -> Amber
        else -> Neon
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, color = TextSecondary, fontSize = 12.sp)
            Text(
                text = if (unlimited) "$used · Sin límite" else "$used / $limit",
                color = if (exceeded) DangerRed else if (reached) Amber else TextPrimary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0x1FD5F0F7)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun PlanCard(
    plan: Plan,
    isCurrent: Boolean,
    isChanging: Boolean,
    code: String,
    onCodeChange: (String) -> Unit,
    onSelect: () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    val borderColor = if (isCurrent) Neon.copy(alpha = .55f) else StrokeStrong
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isCurrent) Color(0x143FE1FF) else SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, borderColor, shape)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.displayName.ifBlank { plan.name },
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = formatPrice(plan.monthlyPriceCents) + " / mes",
                    color = Neon,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            if (isCurrent) {
                Text(
                    text = "Tu plan",
                    color = Neon,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0x263FE1FF), RoundedCornerShape(20.dp))
                        .border(1.dp, Neon.copy(alpha = .45f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp),
                )
            }
        }

        if (plan.description.isNotBlank()) {
            Text(
                text = plan.description,
                color = TextTertiary,
                fontSize = 11.5.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Spacer(Modifier.height(10.dp))
        PlanLimit("servicios", plan.serviceLimit)
        PlanLimit("citas al mes", plan.monthlyAppointmentLimit)
        PlanLimit("empleados", plan.employeeLimit)

        if (plan.requiresActivationCode && !isCurrent) {
            Spacer(Modifier.height(12.dp))
            KronifyTextField(
                value = code,
                onValueChange = onCodeChange,
                label = "Código de activación",
                placeholder = "Ingresa tu código",
            )
        }

        if (isCurrent) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Estás en este plan",
                color = TextTertiary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        } else {
            Spacer(Modifier.height(14.dp))
            PrimaryButton(
                text = if (isChanging) "Cambiando…" else "Cambiar a este plan",
                onClick = onSelect,
            )
        }
    }
}

@Composable
private fun PlanLimit(label: String, limit: Int?) {
    Text(
        text = if (limit == null || limit <= 0) "• $label ilimitados" else "• Hasta $limit $label",
        color = TextSecondary,
        fontSize = 11.5.sp,
        modifier = Modifier.padding(top = 3.dp),
    )
}

@Composable
private fun StatusTag(status: String) {
    val (label, color) = when (status) {
        "TRIAL" -> "Prueba" to Amber
        "ACTIVE" -> "Activo" to OkGreen
        "PAST_DUE" -> "Mora" to DangerRed
        "CANCELLED" -> "Cancelado" to DangerRed
        "EXPIRED" -> "Expirado" to TextTertiary
        else -> status.ifBlank { "—" } to TextTertiary
    }
    Text(
        text = label,
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color.copy(alpha = .12f), RoundedCornerShape(20.dp))
            .border(1.dp, color.copy(alpha = .35f), RoundedCornerShape(20.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
    )
}

private fun formatPrice(cents: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))
    formatter.maximumFractionDigits = 0
    return formatter.format(cents / 100.0)
}
