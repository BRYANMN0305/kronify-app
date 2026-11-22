package com.kronifyapp.ui.screens.auth

import android.graphics.BitmapFactory
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.ClockGlyph
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.ui.theme.Acero1
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import kotlinx.coroutines.launch

@Composable
fun OnboardingStep2Screen(
    onBack: () -> Unit,
    onFinish: (String, Long) -> Unit,
    token: String,
    userId: Long,
    viewModel: com.kronifyapp.ui.viewModel.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var logoUri by rememberSaveable { mutableStateOf<String?>(null) }
    val logoBitmap = remember(logoUri) {
        logoUri?.let { uri ->
            context.contentResolver.openInputStream(android.net.Uri.parse(uri))?.use(BitmapFactory::decodeStream)
        }
    }
    val logoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            logoUri = it.toString()
            val mimeType = context.contentResolver.getType(it) ?: "image/jpeg"
            val bytes = context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }
            if (bytes != null) viewModel.uploadLogo(token, bytes, mimeType)
        }
    }
    val photoPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) logoPicker.launch("image/*")
    }
    val daysState = remember {
        mutableStateMapOf(
            "Lun" to ("09:00 — 20:00" to true),
            "Mar" to ("09:00 — 20:00" to true),
            "Mié" to ("09:00 — 20:00" to true),
            "Jue" to ("09:00 — 20:00" to true),
            "Vie" to ("09:00 — 20:00" to true),
            "Sáb" to ("10:00 — 14:00" to true),
            "Dom" to ("Cerrado" to false),
        )
    }

    var editingDay by remember { mutableStateOf<String?>(null) }
    var tempTimeRange by rememberSaveable { mutableStateOf("") }

    var editingBreak by remember { mutableStateOf(false) }
    var tempBreakTime by rememberSaveable { mutableStateOf("14:00 – 15:30") }

    val snackbarHostState = remember { SnackbarHostState() }
    val notify: (String) -> Unit = { }

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
                            text = "Paso 2 de 3",
                            fontSize = 11.5.sp,
                            color = TextTertiary,
                        )
                    }

                    // Barra de progreso (66%)
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
                                .fillMaxWidth(0.66f)
                                .background(Neon, RoundedCornerShape(2.dp)),
                        )
                    }

                    // Títulos (en una sola línea)
                    Text(
                        text = "¿Cuándo trabajas?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 20.dp),
                    )
                    Text(
                        text = "El calendario solo ofrecerá huecos dentro de este horario.",
                        fontSize = 13.sp,
                        color = TextTertiary,
                        modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
                    )

                    // El selector visual del sistema solo autoriza la imagen elegida; no requiere
                    // permiso de lectura completo sobre la galería.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x0DD5F0F7))
                            .border(1.dp, StrokeSubtle, RoundedCornerShape(16.dp))
                            .clickable {
                                if (ContextCompat.checkSelfPermission(context, photoPermission) == PackageManager.PERMISSION_GRANTED) {
                                    logoPicker.launch("image/*")
                                } else {
                                    permissionLauncher.launch(photoPermission)
                                }
                            }
                            .padding(14.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0x223FE1FF)),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (logoBitmap != null) {
                                    Image(
                                        bitmap = logoBitmap.asImageBitmap(),
                                        contentDescription = "Logo seleccionado",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                    )
                                } else {
                                    Text(
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
                                        logoBitmap == null -> "Añade el logo de tu negocio"
                                        state.isLoading -> "Guardando logo…"
                                        state.logoUploaded -> "Logo guardado"
                                        else -> "Logo seleccionado"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                )
                                Text(
                                    text = if (logoBitmap == null) "JPEG, PNG, WebP o GIF" else "Toca para cambiar la imagen",
                                    fontSize = 11.sp,
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

                    // Lista de días (con horas ajustables)
                    val daysOrder = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x0DD5F0F7))
                            .border(1.dp, StrokeSubtle, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        daysOrder.forEach { day ->
                            val state = daysState[day] ?: ("09:00 — 20:00" to true)
                            val (timeStr, isOn) = state
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isOn) Color(0x14D5F0F7) else Color.Transparent)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isOn) TextPrimary else TextTertiary,
                                )
                                Text(
                                    text = timeStr,
                                    fontSize = 12.5.sp,
                                    color = if (isOn) TextSecondary else TextTertiary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable(enabled = isOn) {
                                            editingDay = day
                                            tempTimeRange = timeStr
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                                // Switch toggle
                                Box(
                                    modifier = Modifier
                                        .size(width = 40.dp, height = 22.dp)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(if (isOn) Neon else Color(0x33D5F0F7))
                                        .clickable {
                                            daysState[day] = timeStr to !isOn
                                        }
                                        .padding(2.dp),
                                    contentAlignment = if (isOn) Alignment.CenterEnd else Alignment.CenterStart,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(if (isOn) Acero1 else TextTertiary),
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Tarjeta de Descanso (ajustable)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x0DD5F0F7))
                            .border(1.dp, StrokeSubtle, RoundedCornerShape(16.dp))
                            .clickable {
                                editingBreak = true
                                tempBreakTime = "14:00 – 15:30"
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                ClockGlyph(tint = TextTertiary)
                                Text(
                                    text = "Descanso",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary,
                                )
                            }
                            Text(
                                text = tempBreakTime,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    PrimaryButton(
                        text = "Continuar",
                        onClick = {
                            viewModel.advanceOnboarding(userId, 3)
                            onFinish(token, userId)
                        },
                    )

                    Text(
                        text = "Podrás ajustar días, descansos y excepciones en Horarios.",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = TextTertiary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 4.dp),
                    )
                }
            }

            // Diálogo para ajustar horario del día
            if (editingDay != null) {
                AlertDialog(
                    onDismissRequest = { editingDay = null },
                    title = { Text("Ajustar horario: $editingDay") },
                    text = {
                        Column {
                            Text("Ingresa el horario de apertura y cierre:")
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = tempTimeRange,
                                onValueChange = { tempTimeRange = it },
                                singleLine = true,
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            editingDay?.let { day ->
                                val currentIsOn = daysState[day]?.second ?: true
                                daysState[day] = tempTimeRange to currentIsOn
                            }
                            editingDay = null
                            notify("Horario actualizado")
                        }) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { editingDay = null }) {
                            Text("Cancelar")
                        }
                    },
                )
            }

            // Diálogo para ajustar descanso
            if (editingBreak) {
                AlertDialog(
                    onDismissRequest = { editingBreak = false },
                    title = { Text("Ajustar descanso") },
                    text = {
                        Column {
                            Text("Ingresa el horario de descanso:")
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = tempBreakTime,
                                onValueChange = { tempBreakTime = it },
                                singleLine = true,
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            editingBreak = false
                            notify("Descanso actualizado")
                        }) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { editingBreak = false }) {
                            Text("Cancelar")
                        }
                    },
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 22.dp, vertical = 12.dp),
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(14.dp),
                    containerColor = SurfaceDeep,
                    contentColor = TextSecondary,
                    actionColor = Neon,
                    dismissActionContentColor = TextTertiary,
                    modifier = Modifier.border(1.dp, StrokeStrong, RoundedCornerShape(14.dp)),
                )
            }
        }
    }
}
