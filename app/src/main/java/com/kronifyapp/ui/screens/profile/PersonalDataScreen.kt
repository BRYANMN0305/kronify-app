package com.kronifyapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.components.AnimatedStatusMessage
import com.kronifyapp.ui.components.AutoDismissSuccess
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.InfoMessage
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.KronifySwitch
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.viewModel.PersonalDataViewModel

@Composable
fun PersonalDataScreen(
    onBack: () -> Unit,
    viewModel: PersonalDataViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    AutoDismissSuccess(state.successMessage) { viewModel.clearMessages() }

    AuroraBackground {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
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
                        text = "TU CUENTA",
                        color = TextTertiary,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        text = "Mis datos personales",
                        color = TextPrimary,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }

            Text(
                text = "Actualiza tu nombre, apellidos y número de contacto.",
                color = TextTertiary,
                fontSize = 12.5.sp,
                modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Neon)
                }
                return@Column
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(Color(0xFF1B4F72), CircleShape)
                        .border(1.dp, Neon.copy(alpha = .6f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.initials,
                        color = Color(0xFFC8F7FF),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Column(modifier = Modifier.padding(start = 14.dp)) {
                    Text(
                        text = state.fullName.ifBlank { "Tu cuenta" },
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = state.email,
                        color = TextTertiary,
                        fontSize = 11.5.sp,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (state.isOwner) {
                val ownerShape = RoundedCornerShape(20.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ownerShape)
                        .background(SurfaceDeep.copy(alpha = .73f))
                        .border(1.dp, StrokeStrong, ownerShape)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Trabajo como empleado",
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Aparecerás en tu equipo y podrás recibir citas.",
                            color = TextTertiary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 3.dp),
                        )
                    }
                    KronifySwitch(
                        checked = state.ownerWorksAsEmployee,
                        onCheckedChange = viewModel::toggleOwnerWorksAsEmployee,
                        enabled = !state.togglingOwner,
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            val cardShape = RoundedCornerShape(22.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(SurfaceDeep.copy(alpha = .73f))
                    .border(1.dp, StrokeStrong, cardShape)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                KronifyTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Nombre",
                    placeholder = "Tu nombre",
                )
                KronifyTextField(
                    value = state.lastName,
                    onValueChange = viewModel::onLastNameChange,
                    label = "Apellidos",
                    placeholder = "Tus apellidos",
                )
                KronifyTextField(
                    value = state.phoneNumber,
                    onValueChange = viewModel::onPhoneChange,
                    label = "Teléfono",
                    placeholder = "+57 3006217237",
                    keyboardType = KeyboardType.Phone,
                )

                Column {
                    Text(
                        text = "Correo electrónico",
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
                            .background(Color(0x33040A0C))
                            .border(1.dp, StrokeSubtle, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = state.email,
                            color = TextTertiary,
                            fontSize = 14.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            PrimaryButton(
                text = if (state.isSaving) "Guardando…" else "Guardar cambios",
                onClick = { viewModel.save() },
            )

            AnimatedStatusMessage(text = state.error, isError = true)
            AnimatedStatusMessage(text = state.successMessage, isError = false)

            Spacer(Modifier.height(14.dp))
            InfoMessage("El correo electrónico no se puede cambiar desde la app.")
            Spacer(Modifier.height(20.dp))
        }
    }
}
