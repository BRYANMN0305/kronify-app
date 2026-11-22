package com.kronifyapp.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.runtime.collectAsState
import com.kronifyapp.ui.viewModel.AuthViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.CheckGlyph
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.MailGlyph
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.ui.theme.Acero1
import com.kronifyapp.ui.theme.Amber
import com.kronifyapp.ui.theme.DangerRed
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextQuaternary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import kotlinx.coroutines.launch

private enum class AccountType {
    BUSINESS, CLIENT
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onLogin: () -> Unit,
    onCompleteRegister: (String, Long) -> Unit = { _, _ -> },
    viewModel: AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var accountType by rememberSaveable { mutableStateOf(AccountType.BUSINESS) }
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var acceptTerms by rememberSaveable { mutableStateOf(false) }

    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var termsError by rememberSaveable { mutableStateOf<String?>(null) }

    var navigated by rememberSaveable { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(state.error) {
        state.error?.let { error ->
            if (error.contains("cuenta", ignoreCase = true) || error.contains("registr", ignoreCase = true)) {
                emailError = "Este correo ya está registrado. Inicia sesión o usa otro correo."
            }
        }
    }
    androidx.compose.runtime.LaunchedEffect(state.token, navigated) {
        if (!state.token.isNullOrBlank() && !navigated) {
            navigated = true
            if (accountType == AccountType.BUSINESS) {
                onCompleteRegister(state.token.orEmpty(), state.userId ?: return@LaunchedEffect)
            } else {
                onLogin()
            }
        }
    }

    val isBusiness = accountType == AccountType.BUSINESS

    // Cálculo de fortaleza de contraseña
    val (strengthFraction, strengthText) = when {
        password.isEmpty() -> 0.1f to "Ingresa una contraseña"
        password.length < 6 -> 0.35f to "Débil"
        password.length < 10 -> 0.7f to "Media"
        else -> 1f to "Segura"
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
                    // Barra superior de navegación (solo botón de regresar)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 16.dp),
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
                    }

                    // Título principal y subtítulo
                    Text(
                        text = "Crear una cuenta",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 33.sp,
                        letterSpacing = (-0.5).sp,
                        color = TextPrimary,
                    )
                    Text(
                        text = "Únete a Kronify y despega tu negocio",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
                    )

                    // Selector de tipo de cuenta: "Soy negocio" / "Soy cliente"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x66040A0C))
                            .border(1.dp, StrokeSubtle, RoundedCornerShape(14.dp))
                            .padding(4.dp),
                    ) {
                        Row(Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isBusiness) Color(0x33D5F0F7) else Color.Transparent)
                                    .border(
                                        if (isBusiness) 1.dp else 0.dp,
                                        if (isBusiness) StrokeStrong else Color.Transparent,
                                        RoundedCornerShape(10.dp),
                                    )
                                    .clickable { accountType = AccountType.BUSINESS },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Soy negocio",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isBusiness) TextPrimary else TextTertiary,
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (!isBusiness) Color(0x33D5F0F7) else Color.Transparent)
                                    .border(
                                        if (!isBusiness) 1.dp else 0.dp,
                                        if (!isBusiness) StrokeStrong else Color.Transparent,
                                        RoundedCornerShape(10.dp),
                                    )
                                    .clickable { accountType = AccountType.CLIENT },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Soy cliente",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (!isBusiness) TextPrimary else TextTertiary,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Tarjeta informativa explicativa
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x141A829B))
                            .border(1.dp, Color(0x283FE1FF), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(38.dp)
                                    .background(Neon, RoundedCornerShape(2.dp)),
                            )
                            Text(
                                text = buildAnnotatedString {
                                    if (isBusiness) {
                                        append("Gestiona tu agenda, tu equipo y tus reseñas desde el móvil. Si solo quieres reservar, ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Neon)) {
                                            append("entra como cliente")
                                        }
                                        append(".")
                                    } else {
                                        append("Encuentra y reserva en tus centros y barberías favoritas. Si tienes un negocio, ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Neon)) {
                                            append("crea tu cuenta de negocio")
                                        }
                                        append(".")
                                    }
                                },
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .clickable {
                                        accountType = if (isBusiness) AccountType.CLIENT else AccountType.BUSINESS
                                    },
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Campos del formulario
                    KronifyTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            if (nameError != null) nameError = null
                        },
                        label = "Nombre y apellidos",
                        placeholder = "Nombre y apellidos",
                        keyboardType = KeyboardType.Text,
                        error = nameError,
                    )

                    Spacer(Modifier.height(12.dp))

                    KronifyTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        label = "Correo",
                        placeholder = "tucorreo@ejemplo.com",
                        keyboardType = KeyboardType.Email,
                        error = emailError,
                    )

                    Spacer(Modifier.height(12.dp))

                    KronifyTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError != null) passwordError = null
                        },
                        label = "Contraseña",
                        placeholder = "Tu contraseña",
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        isPassword = true,
                        error = passwordError,
                    )

                    // Indicador de fortaleza de contraseña
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0x33D5F0F7)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = strengthFraction)
                                    .background(
                                        if (strengthFraction > 0.8f) Neon else if (strengthFraction > 0.4f) Amber else DangerRed,
                                        RoundedCornerShape(2.dp),
                                    ),
                            )
                        }
                        Text(
                            text = strengthText,
                            fontSize = 11.sp,
                            color = TextTertiary,
                            modifier = Modifier.padding(start = 10.dp),
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Casilla Acepto términos
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    acceptTerms = !acceptTerms
                                    if (termsError != null) termsError = null
                                },
                            )
                            .padding(vertical = 4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (acceptTerms) Neon else Color.Transparent)
                                .border(1.dp, if (acceptTerms) Neon else StrokeStrong, RoundedCornerShape(5.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (acceptTerms) {
                                CheckGlyph(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp),
                                    tint = Acero1,
                                )
                            }
                        }
                        Text(
                            text = "Acepto los términos y la política de tratamiento de datos de Kronify.",
                            fontSize = 11.5.sp,
                            color = TextTertiary,
                            modifier = Modifier.padding(start = 10.dp),
                        )
                    }

                    termsError?.let {
                        Text(
                            text = it,
                            color = DangerRed,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 28.dp, top = 4.dp),
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    PrimaryButton(
                        text = "Crear cuenta",
                        onClick = {
                            val nameTrim = fullName.trim()
                            val emailTrim = email.trim()
                            val passwordTrim = password.trim()

                            nameError = if (nameTrim.isBlank()) "El nombre es obligatorio" else null
                            emailError = if (emailTrim.isBlank()) {
                                "El correo es obligatorio"
                            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTrim).matches()) {
                                "Ingresa un correo válido"
                            } else {
                                null
                            }
                            passwordError = if (passwordTrim.isBlank()) {
                                "La contraseña es obligatoria"
                            } else if (passwordTrim.length < 8) {
                                "Mínimo 8 caracteres"
                            } else {
                                null
                            }

                            termsError = if (!acceptTerms) {
                                "Debes aceptar la política de tratamiento de datos."
                            } else {
                                null
                            }

                            if (termsError == null &&
                                nameError == null &&
                                emailError == null &&
                                passwordError == null
                            ) {
                                val parts = nameTrim.split(" ", limit = 2)
                                val name = parts.getOrNull(0) ?: nameTrim
                                val lastName = parts.getOrNull(1) ?: "Kronify"
                                viewModel.register(
                                    name = name,
                                    lastName = lastName,
                                    phone = "+34600000000",
                                    email = emailTrim,
                                    password = passwordTrim,
                                    profileType = if (isBusiness) "BUSINESS" else "CLIENT",
                                )
                            }
                        },
                    )
                }

                // Pie de página con enlace a Entrar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        fontSize = 12.5.sp,
                        color = TextTertiary,
                    )
                    Text(
                        text = "Entrar",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Neon,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onLogin,
                        ),
                    )
                }
            }

        }
    }
}
