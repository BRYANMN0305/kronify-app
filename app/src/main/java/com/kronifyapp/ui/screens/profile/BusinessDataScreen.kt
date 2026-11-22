package com.kronifyapp.ui.screens.profile

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.ChevronDownGlyph
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.AnimatedStatusMessage
import com.kronifyapp.ui.components.AutoDismissSuccess
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.viewModel.BusinessDataViewModel
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val CategoriesList = listOf(
    "Salud y Bienestar",
    "Belleza y Estética",
    "Consultoría y Coaching",
    "Educación y Cursos",
    "Servicios Profesionales",
    "Hogar y Mantenimiento",
    "Tecnología y Soporte",
    "Eventos y Fotografía",
    "Fitness y Deporte",
    "Alimentación y Nutrición",
    "Otro",
)

/**
 * Pantalla de edición de los datos públicos del negocio.
 * Carga GET /business/me y guarda con PATCH /business/.
 */
@Composable
fun BusinessDataScreen(
    onBack: () -> Unit,
    viewModel: BusinessDataViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    AutoDismissSuccess(state.successMessage) { viewModel.clearMessages() }
    val context = LocalContext.current
    var showCategoryDialog by remember { mutableStateOf(false) }
    var logoBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val logoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        if (bytes != null) viewModel.uploadLogo(bytes, mimeType)
    }

    LaunchedEffect(state.logoUrl) {
        logoBitmap = state.logoUrl?.let { url ->
            withContext(Dispatchers.IO) {
                runCatching {
                    URL(url).openStream().use { BitmapFactory.decodeStream(it) }?.asImageBitmap()
                }.getOrNull()
            }
        }
    }

    val categories = remember(state.category) {
        if (state.category.isNotBlank() && state.category !in CategoriesList) {
            CategoriesList + state.category
        } else {
            CategoriesList
        }
    }

    AuroraBackground {
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Neon)
            }
            return@AuroraBackground
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            val viewportHeight = maxHeight

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = viewportHeight)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                // Barra superior
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            color = TextPrimary,
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
                            text = "Datos del negocio",
                            color = TextPrimary,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }

                Text(
                    text = "Esta información es la que ven tus clientes en tu página pública.",
                    fontSize = 12.5.sp,
                    color = TextTertiary,
                    modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
                )

                // Logo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDeep.copy(alpha = .73f))
                        .border(1.dp, StrokeStrong, RoundedCornerShape(20.dp))
                        .clickable { logoPicker.launch("image/*") }
                        .padding(14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x223FE1FF))
                                .border(1.dp, StrokeSubtle, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            when {
                                logoBitmap != null -> Image(
                                    bitmap = logoBitmap!!,
                                    contentDescription = "Logo del negocio",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )

                                state.name.isNotBlank() -> Text(
                                    text = state.name.trim().first().uppercase(),
                                    color = Color(0xFFC8F7FF),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                )

                                else -> Text(
                                    text = "+",
                                    color = Neon,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    state.isUploadingLogo -> "Guardando logo…"
                                    logoBitmap != null -> "Logo del negocio"
                                    else -> "Añade el logo de tu negocio"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                            )
                            Text(
                                text = if (state.isUploadingLogo) "Espera un momento" else "JPEG, PNG, WebP o GIF · máx. 5 MB",
                                fontSize = 10.5.sp,
                                color = TextTertiary,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }

                        Text(
                            text = if (logoBitmap == null) "Elegir" else "Cambiar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Neon,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Formulario
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDeep.copy(alpha = .73f))
                        .border(1.dp, StrokeStrong, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    KronifyTextField(
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        label = "Nombre del negocio",
                        placeholder = "Ej: Barbería El Clásico",
                    )

                    // Categoría
                    Column {
                        Text(
                            text = "Categoría",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.3.sp,
                            color = TextTertiary,
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x66040A0C))
                                .border(1.dp, StrokeSubtle, RoundedCornerShape(14.dp))
                                .clickable { showCategoryDialog = true }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = state.category.ifBlank { "Selecciona una categoría" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (state.category.isBlank()) TextTertiary else TextPrimary,
                                )
                                ChevronDownGlyph(tint = TextTertiary)
                            }
                        }
                    }

                    KronifyTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Email de contacto",
                        placeholder = "contacto@negocio.com",
                        keyboardType = KeyboardType.Email,
                    )

                    KronifyTextField(
                        value = state.phoneNumber,
                        onValueChange = viewModel::onPhoneChange,
                        label = "Teléfono",
                        placeholder = "+57 3006217237",
                        keyboardType = KeyboardType.Phone,
                    )

                    KronifyTextField(
                        value = state.whatsApp,
                        onValueChange = viewModel::onWhatsAppChange,
                        label = "WhatsApp",
                        placeholder = "+57 3006217237",
                        keyboardType = KeyboardType.Phone,
                    )

                    KronifyTextField(
                        value = state.address,
                        onValueChange = viewModel::onAddressChange,
                        label = "Dirección",
                        placeholder = "Calle 123 #45-67",
                    )

                    KronifyTextField(
                        value = state.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = "Descripción",
                        placeholder = "Describe brevemente tu negocio",
                    )
                }

                Spacer(Modifier.height(20.dp))

                PrimaryButton(
                    text = if (state.isSaving) "Guardando…" else "Guardar cambios",
                    onClick = { viewModel.save() },
                )

                AnimatedStatusMessage(text = state.error, isError = true)
                AnimatedStatusMessage(text = state.successMessage, isError = false)

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(enabled = !state.isSaving) { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Cancelar",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(Modifier.height(20.dp))
            }
        }

        if (showCategoryDialog) {
            AlertDialog(
                onDismissRequest = { showCategoryDialog = false },
                title = { Text("Selecciona una categoría") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        categories.forEach { category ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (state.category == category) Color(0x333FE1FF) else Color.Transparent)
                                    .clickable {
                                        viewModel.onCategoryChange(category)
                                        showCategoryDialog = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 14.sp,
                                    fontWeight = if (state.category == category) FontWeight.Bold else FontWeight.Normal,
                                    color = TextPrimary,
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCategoryDialog = false }) {
                        Text("Cerrar")
                    }
                },
            )
        }
    }
}

