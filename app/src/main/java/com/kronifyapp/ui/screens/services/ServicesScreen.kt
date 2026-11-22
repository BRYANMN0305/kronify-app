package com.kronifyapp.ui.screens.services

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kronifyapp.domain.model.BusinessService
import com.kronifyapp.domain.model.ServiceInput
import com.kronifyapp.ui.components.AnimatedStatusMessage
import com.kronifyapp.ui.components.AutoDismissSuccess
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.ui.components.StatusMessage
import com.kronifyapp.ui.theme.Amber
import com.kronifyapp.ui.theme.DangerRed
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.viewModel.ServicesViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ServicesScreen(
    viewModel: ServicesViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    AutoDismissSuccess(state.successMessage) { viewModel.clearMessages() }
    var showForm by remember { mutableStateOf(false) }
    var editingService by remember { mutableStateOf<BusinessService?>(null) }
    var deletingService by remember { mutableStateOf<BusinessService?>(null) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("30") }
    var buffer by remember { mutableStateOf("0") }
    var formError by remember { mutableStateOf<String?>(null) }

    fun openCreate() {
        viewModel.clearMessages()
        editingService = null
        name = ""
        description = ""
        price = ""
        duration = "30"
        buffer = "0"
        formError = null
        showForm = true
    }

    fun openEdit(service: BusinessService) {
        viewModel.clearMessages()
        editingService = service
        name = service.name
        description = service.description
        price = if (service.price % 1.0 == 0.0) service.price.toInt().toString() else service.price.toString()
        duration = service.durationMinutes.toString()
        buffer = service.bufferMinutes.toString()
        formError = null
        showForm = true
    }

    fun submitForm() {
        val parsedPrice = price.replace(",", ".").replace("[^\\d.]".toRegex(), "").toDoubleOrNull()
        val parsedDuration = duration.filter { it.isDigit() }.toIntOrNull()
        val parsedBuffer = buffer.filter { it.isDigit() }.toIntOrNull() ?: 0

        formError = when {
            name.isBlank() -> "Escribe el nombre del servicio."
            parsedPrice == null || parsedPrice <= 0 -> "Ingresa un precio mayor a 0."
            parsedDuration == null || parsedDuration <= 0 -> "Ingresa una duración mayor a 0."
            parsedBuffer < 0 -> "El buffer no puede ser negativo."
            else -> null
        }
        if (formError != null) return

        val input = ServiceInput(
            name = name,
            description = description.ifBlank { null },
            durationMinutes = parsedDuration!!,
            bufferMinutes = parsedBuffer,
            price = parsedPrice!!,
        )
        val editing = editingService
        if (editing == null) viewModel.create(input) else viewModel.update(editing.id, input)
    }

    LaunchedEffect(state.successMessage) {
        val message = state.successMessage.orEmpty()
        if (showForm && (message.contains("creado") || message.contains("actualizado"))) {
            showForm = false
        }
        if (deletingService != null && message.contains("eliminado")) {
            deletingService = null
        }
    }

    AuroraBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 58.dp, bottom = 130.dp),
        ) {
            item {
                Text(
                    text = "TU NEGOCIO",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = "Servicios",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = "Crea y administra lo que ofreces a tus clientes.",
                    color = TextTertiary,
                    fontSize = 12.5.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp),
                )
                PrimaryButton(
                    text = "Nuevo servicio",
                    onClick = { openCreate() },
                )
                Spacer(Modifier.height(18.dp))
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Neon)
                    }
                }
            } else {
                if (state.services.isEmpty()) {
                    item {
                        val shape = RoundedCornerShape(20.dp)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape)
                                .background(SurfaceDeep.copy(alpha = .73f))
                                .border(1.dp, StrokeStrong, shape)
                                .padding(16.dp),
                        ) {
                            Text(
                                text = "Aún no tienes servicios.",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Crea tu primer servicio para empezar a recibir reservas.",
                                color = TextTertiary,
                                fontSize = 11.5.sp,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                } else {
                    items(state.services, key = { it.id }) { service ->
                        ServiceCard(
                            service = service,
                            onEdit = { openEdit(service) },
                            onDelete = {
                                viewModel.clearMessages()
                                deletingService = service
                            },
                        )
                    }
                }

                item {
                    AnimatedStatusMessage(text = state.error, isError = true)
                    AnimatedStatusMessage(text = state.successMessage, isError = false)
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    if (showForm) {
        AlertDialog(
            onDismissRequest = { if (!state.isSaving) showForm = false },
            title = {
                Text(
                    text = if (editingService == null) "Nuevo servicio" else "Editar servicio",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    KronifyTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre",
                        placeholder = "Ej. Corte de cabello",
                    )
                    Spacer(Modifier.height(10.dp))
                    KronifyTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Descripción",
                        placeholder = "Opcional",
                    )
                    Spacer(Modifier.height(10.dp))
                    KronifyTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = "Precio",
                        placeholder = "0",
                        keyboardType = KeyboardType.Decimal,
                    )
                    Spacer(Modifier.height(10.dp))
                    KronifyTextField(
                        value = duration,
                        onValueChange = { duration = it.filter { char -> char.isDigit() } },
                        label = "Duración (min)",
                        placeholder = "30",
                        keyboardType = KeyboardType.Number,
                    )
                    Spacer(Modifier.height(10.dp))
                    KronifyTextField(
                        value = buffer,
                        onValueChange = { buffer = it.filter { char -> char.isDigit() } },
                        label = "Buffer entre citas (min)",
                        placeholder = "0",
                        keyboardType = KeyboardType.Number,
                    )
                    formError?.let {
                        StatusMessage(
                            text = it,
                            isError = true,
                            modifier = Modifier.padding(top = 10.dp),
                        )
                    }
                    state.error?.let {
                        StatusMessage(
                            text = it,
                            isError = true,
                            modifier = Modifier.padding(top = 10.dp),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { submitForm() }, enabled = !state.isSaving) {
                    Text(if (editingService == null) "Crear servicio" else "Guardar cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForm = false }, enabled = !state.isSaving) {
                    Text("Cancelar")
                }
            },
            containerColor = SurfaceDeep,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
        )
    }

    deletingService?.let { service ->
        AlertDialog(
            onDismissRequest = { if (!state.isSaving) deletingService = null },
            title = {
                Text(
                    text = "Eliminar servicio",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres eliminar \"${service.name}\"? Esta acción no se puede deshacer.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.delete(service.id) },
                    enabled = !state.isSaving,
                ) {
                    Text("Eliminar", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingService = null }, enabled = !state.isSaving) {
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
private fun ServiceCard(
    service: BusinessService,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape)
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (service.description.isNotBlank()) {
                    Text(
                        text = service.description,
                        color = TextTertiary,
                        fontSize = 11.5.sp,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
            Text(
                text = formatPrice(service.price),
                color = Neon,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${service.durationMinutes} min",
                color = TextSecondary,
                fontSize = 11.sp,
            )
            if (service.bufferMinutes > 0) {
                Text(
                    text = " · ${service.bufferMinutes} min de buffer",
                    color = TextTertiary,
                    fontSize = 11.sp,
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "Editar",
                color = Neon,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onEdit)
                    .padding(horizontal = 6.dp, vertical = 3.dp),
            )
            Text(
                text = "Eliminar",
                color = DangerRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onDelete)
                    .padding(horizontal = 6.dp, vertical = 3.dp),
            )
        }
    }
}

private fun formatPrice(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))
    formatter.maximumFractionDigits = 0
    return formatter.format(value)
}
