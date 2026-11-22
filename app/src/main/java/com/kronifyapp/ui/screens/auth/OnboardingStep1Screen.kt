package com.kronifyapp.ui.screens.auth

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.AnimatedStatusMessage
import com.kronifyapp.ui.components.ChevronDownGlyph
import com.kronifyapp.ui.components.GlobeGlyph
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import androidx.compose.runtime.collectAsState
import com.kronifyapp.ui.viewModel.AuthViewModel
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import kotlinx.coroutines.launch

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

@Composable
fun OnboardingStep1Screen(
    onBack: () -> Unit,
    onContinue: (String, Long) -> Unit,
    token: String = "",
    userId: Long = 0L,
    viewModel: AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var businessName by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var customCategory by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var formError by rememberSaveable { mutableStateOf<String?>(null) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    val previewSlug = businessName
        .trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')

    var navigated by rememberSaveable { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(state.businessCreated, navigated) {
        if (state.businessCreated && !navigated) {
            navigated = true
            onContinue(token, userId)
        }
    }

    AuroraBackground {
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
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    // Barra superior
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
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

                        Text(
                            text = "Paso 1 de 3",
                            fontSize = 11.5.sp,
                            color = TextTertiary,
                        )
                    }

                    // Barra de progreso (33%)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0x33D5F0F7))
                            .padding(top = 8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.33f)
                                .background(Neon, RoundedCornerShape(2.dp)),
                        )
                    }

                    // Títulos
                    Text(
                        text = "Cuéntanos de\ntu negocio.",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 33.sp,
                        letterSpacing = (-0.5).sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 20.dp),
                    )
                    Text(
                        text = "Esto es lo que verán tus clientes cuando busquen dónde reservar.",
                        fontSize = 13.sp,
                        color = TextTertiary,
                        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp),
                    )

                    // Campos del formulario
                    KronifyTextField(
                        value = businessName,
                        onValueChange = {
                            businessName = it
                            if (formError != null) formError = null
                        },
                        label = "Nombre del negocio",
                        placeholder = "Nombre del negocio",
                    )

                    Spacer(Modifier.height(12.dp))

                    // Categoría selector
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
                                    text = when {
                                        selectedCategory.isBlank() -> "Selecciona una categoría"
                                        selectedCategory == "Otro" && customCategory.isNotBlank() -> customCategory
                                        else -> selectedCategory
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedCategory.isBlank()) TextTertiary else TextPrimary,
                                )
                                ChevronDownGlyph(tint = TextTertiary)
                            }
                        }
                    }

                    // Si selecciona "Otro", muestra input manual
                    if (selectedCategory == "Otro") {
                        Spacer(Modifier.height(12.dp))
                        KronifyTextField(
                            value = customCategory,
                            onValueChange = {
                                customCategory = it
                                if (formError != null) formError = null
                            },
                            label = "Especifica tu categoría",
                            placeholder = "Escribe tu categoría manualmente",
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    KronifyTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            if (formError != null) formError = null
                        },
                        label = "Teléfono",
                        placeholder = "Teléfono de contacto",
                        keyboardType = KeyboardType.Phone,
                    )

                    Spacer(Modifier.height(12.dp))

                    KronifyTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            if (formError != null) formError = null
                        },
                        label = "Dirección",
                        placeholder = "Dirección del negocio",
                    )

                    Spacer(Modifier.height(14.dp))

                    // Enlace público
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x141A829B))
                            .border(1.dp, Color(0x283FE1FF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            GlobeGlyph(tint = Neon)
                            Text(
                                text = "kronifytech.vercel.app/negocio/${previewSlug.ifBlank { "tu-negocio" }}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.weight(1f),
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x333FE1FF))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text(
                                    text = "Libre",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Neon,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    PrimaryButton(
                        text = "Continuar",
                        onClick = {
                            val nameClean = businessName.trim()
                            val phoneClean = phone.trim()
                            val addressClean = address.trim()
                            formError = when {
                                nameClean.isBlank() -> "Ingresa el nombre de tu negocio"
                                selectedCategory.isBlank() -> "Selecciona una categoría"
                                selectedCategory == "Otro" && customCategory.isBlank() ->
                                    "Escribe tu categoría manualmente"
                                else -> null
                            }
                            if (formError == null) {
                                val slug = nameClean.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
                                val categoryFinal = if (selectedCategory == "Otro") customCategory else selectedCategory
                                viewModel.createBusiness(
                                    userId = userId,
                                    token = token.ifBlank { state.token.orEmpty() },
                                    name = nameClean,
                                    slug = slug,
                                    category = categoryFinal,
                                    phone = phoneClean,
                                    address = addressClean,
                                    email = "contacto@$slug.es",
                                )
                            }
                        },
                    )

                    AnimatedStatusMessage(text = formError, isError = true)
                }
            }

            // Diálogo de selección de categoría
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
                            CategoriesList.forEach { cat ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedCategory == cat) Color(0x333FE1FF) else Color.Transparent)
                                        .clickable {
                                            selectedCategory = cat
                                            formError = null
                                            showCategoryDialog = false
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 14.sp,
                                        fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal,
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
}
