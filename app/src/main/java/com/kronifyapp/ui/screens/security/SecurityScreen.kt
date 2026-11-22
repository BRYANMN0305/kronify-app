package com.kronifyapp.ui.screens.security

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.kronifyapp.domain.model.AuthMethod
import com.kronifyapp.ui.components.AnimatedStatusMessage
import com.kronifyapp.ui.components.AutoDismissSuccess
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.InfoMessage
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.ui.theme.Amber
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.OkGreen
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.viewModel.SecurityViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SecurityScreen(
    onBack: () -> Unit,
    viewModel: SecurityViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    AutoDismissSuccess(state.successMessage) { viewModel.clearMessages() }
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.successMessage) {
        if (!state.successMessage.isNullOrBlank()) {
            currentPassword = ""
            newPassword = ""
            confirmPassword = ""
        }
    }

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
                        text = "Seguridad",
                        color = TextPrimary,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }

            Text(
                text = "Administra tu contraseña y revisa cómo inicias sesión.",
                color = TextTertiary,
                fontSize = 12.5.sp,
                modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
            )

            val hasPasswordMethod = state.methods.any { it.type == "PASSWORD" }
            val showPasswordForm = state.isLoading || state.methods.isEmpty() || hasPasswordMethod

            if (showPasswordForm) {
                SectionTitle("CAMBIAR CONTRASEÑA")
                Spacer(Modifier.height(9.dp))

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
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = "Contraseña actual",
                        placeholder = "Tu contraseña actual",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                    )
                    KronifyTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = "Nueva contraseña",
                        placeholder = "Mínimo 8 caracteres",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                    )
                    KronifyTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirmar nueva contraseña",
                        placeholder = "Repite la nueva contraseña",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                    )
                }

                Spacer(Modifier.height(16.dp))

                PrimaryButton(
                    text = if (state.isSaving) "Actualizando…" else "Actualizar contraseña",
                    onClick = {
                        viewModel.changePassword(currentPassword, newPassword, confirmPassword)
                    },
                )

                AnimatedStatusMessage(text = state.error, isError = true)
                AnimatedStatusMessage(text = state.successMessage, isError = false)
            } else {
                SectionTitle("CONTRASEÑA")
                Spacer(Modifier.height(9.dp))
                InfoMessage(
                    "Tu cuenta inicia sesión con Google o Microsoft. No tiene una contraseña para cambiar.",
                )
            }

            Spacer(Modifier.height(26.dp))
            SectionTitle("MÉTODOS DE ACCESO")
            Spacer(Modifier.height(9.dp))

            MethodsCard(
                methods = state.methods,
                isLoading = state.isLoading,
                error = state.methodsError,
            )

            Spacer(Modifier.height(14.dp))
            Text(
                text = "Puedes iniciar sesión con cualquiera de estos métodos.",
                color = TextTertiary,
                fontSize = 10.5.sp,
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = TextTertiary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 5.dp),
    )
}

@Composable
private fun MethodsCard(methods: List<AuthMethod>, isLoading: Boolean, error: String?) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape),
    ) {
        when {
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Neon)
            }

            error != null -> Box(modifier = Modifier.padding(12.dp)) {
                AnimatedStatusMessage(text = error, isError = true)
            }

            methods.isEmpty() -> Text(
                text = "No hay métodos de acceso vinculados.",
                color = TextTertiary,
                fontSize = 12.sp,
                modifier = Modifier.padding(16.dp),
            )

            else -> methods.forEachIndexed { index, method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(methodColor(method).copy(alpha = .12f))
                            .border(1.dp, methodColor(method).copy(alpha = .28f), RoundedCornerShape(11.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = methodBadge(method),
                            color = methodColor(method),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                    ) {
                        Text(
                            text = methodTitle(method),
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = method.email,
                            color = TextTertiary,
                            fontSize = 10.5.sp,
                            modifier = Modifier.padding(top = 1.dp),
                        )
                    }

                    Text(
                        text = formatDate(method.linkedAt),
                        color = TextTertiary,
                        fontSize = 10.sp,
                    )
                }
                if (index != methods.lastIndex) {
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
}

private fun methodTitle(method: AuthMethod): String {
    if (method.type == "PASSWORD") return "Contraseña"
    val provider = method.provider.orEmpty().lowercase()
    return when {
        provider.contains("google") -> "Google"
        provider.contains("azure") || provider.contains("microsoft") -> "Microsoft"
        provider.isNotBlank() -> method.provider.orEmpty()
        else -> "Cuenta vinculada"
    }
}

private fun methodBadge(method: AuthMethod): String {
    if (method.type == "PASSWORD") return "••"
    val provider = method.provider.orEmpty().lowercase()
    return when {
        provider.contains("google") -> "G"
        provider.contains("azure") || provider.contains("microsoft") -> "M"
        else -> "◎"
    }
}

private fun methodColor(method: AuthMethod): Color {
    if (method.type == "PASSWORD") return Neon
    val provider = method.provider.orEmpty().lowercase()
    return when {
        provider.contains("google") -> OkGreen
        provider.contains("azure") || provider.contains("microsoft") -> Amber
        else -> TextSecondary
    }
}

private fun formatDate(value: String?): String {
    if (value.isNullOrBlank()) return ""
    return runCatching {
        LocalDateTime.parse(value).format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.forLanguageTag("es-ES")))
    }.getOrElse { value.take(7) }
}
