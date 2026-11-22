package com.kronifyapp.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.R
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.CheckGlyph
import com.kronifyapp.ui.components.GlassSurface
import com.kronifyapp.ui.components.GoogleGlyph
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.MailGlyph
import com.kronifyapp.ui.components.MicrosoftGlyph
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.ui.components.SocialButton
import com.kronifyapp.ui.theme.Acero1
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kronifyapp.ui.viewModel.AuthViewModel
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextQuaternary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onRegister: () -> Unit = {},
    onLoginSuccess: (String, Long, String, Int) -> Unit = { _, _, _, _ -> },
    viewModel: AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var keepSession by rememberSaveable { mutableStateOf(true) }

    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    androidx.compose.runtime.LaunchedEffect(state.error) {
        state.error?.let { emailError = it }
    }

    var navigated by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(state.token, navigated) {
        if (!state.token.isNullOrBlank() && !navigated) {
            navigated = true
            onLoginSuccess(
                state.token.orEmpty(),
                state.userId ?: return@LaunchedEffect,
                state.profileType.orEmpty(),
                state.onboardingStep,
            )
        }
    }

    AuroraBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                LoginHeader()

                KronifyTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError != null) emailError = null
                    },
                    label = "Correo",
                    placeholder = "Correo electrónico",
                    keyboardType = KeyboardType.Email,
                    error = emailError,
                    leading = { MailGlyph(tint = TextTertiary) },
                )

                Spacer(Modifier.height(10.dp))

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

                Spacer(Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { keepSession = !keepSession },
                        )
                        .padding(vertical = 2.dp),
                ) {
                    RememberBox(checked = keepSession)
                    Text(
                        text = "Mantener sesión",
                        fontSize = 12.sp,
                        color = TextTertiary,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }

                Spacer(Modifier.height(12.dp))

                PrimaryButton(
                    text = "Iniciar Sesión",
                    onClick = {
                        val emailTrim = email.trim()
                        val passwordTrim = password.trim()

                        emailError = if (emailTrim.isBlank()) {
                            "El correo es obligatorio"
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTrim).matches()) {
                            "Ingresa un correo válido"
                        } else {
                            null
                        }

                        passwordError = if (passwordTrim.isBlank()) {
                            "La contraseña es obligatoria"
                        } else if (passwordTrim.length < 6) {
                            "Mínimo 6 caracteres"
                        } else {
                            null
                        }

                        if (emailError == null && passwordError == null) {
                            viewModel.login(emailTrim, passwordTrim)
                        }
                    },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = StrokeSubtle,
                    )
                    Text(
                        text = "O CONTINÚA CON",
                        fontSize = 10.sp,
                        letterSpacing = 0.6.sp,
                        color = TextQuaternary,
                        modifier = Modifier.padding(horizontal = 10.dp),
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = StrokeSubtle,
                    )
                }

                val context = androidx.compose.ui.platform.LocalContext.current
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SocialButton(
                        text = "Google",
                        onClick = {
                            val url = "https://kronify-apis.duckdns.org/oauth2/authorization/google"
                            try {
                                val intent = androidx.browser.customtabs.CustomTabsIntent.Builder().build()
                                intent.launchUrl(context, android.net.Uri.parse(url))
                            } catch (e: Exception) {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        leading = { GoogleGlyph() },
                    )
                    SocialButton(
                        text = "Microsoft",
                        onClick = {
                            val url = "https://kronify-apis.duckdns.org/oauth2/authorization/azure"
                            try {
                                val intent = androidx.browser.customtabs.CustomTabsIntent.Builder().build()
                                intent.launchUrl(context, android.net.Uri.parse(url))
                            } catch (e: Exception) {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        leading = { MicrosoftGlyph() },
                    )
                }

                LoginFooter(onRegister = onRegister)
            }

        }
    }
}

@Composable
private fun LoginHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
    ) {
        Box(
            modifier = Modifier.size(126.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(126.dp)
                    .border(1.dp, Color(0x12D5F0F7), CircleShape),
            )
            Box(
                Modifier
                    .size(98.dp)
                    .shadow(
                        elevation = 28.dp,
                        shape = CircleShape,
                        clip = false,
                        ambientColor = Neon.copy(alpha = 0.38f),
                        spotColor = Neon.copy(alpha = 0.5f),
                    ),
            )
            Image(
                painter = painterResource(R.drawable.logo_kronify),
                contentDescription = "Kronify",
                modifier = Modifier.size(98.dp),
            )
        }

        Text(
            text = "Bienvenido de vuelta",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp),
        )
        Text(
            text = "Inicia sesión en tu cuenta de Kronify",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun LoginFooter(onRegister: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "¿Primera vez aquí? ",
                fontSize = 12.sp,
                color = TextTertiary,
            )
            Text(
                text = "Crea tu cuenta",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Neon.copy(alpha = 0.9f),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onRegister,
                ),
            )
        }

        Text(
            text = "Al continuar aceptas los términos del servicio y la política de privacidad.",
            fontSize = 10.5.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center,
            color = TextQuaternary,
            modifier = Modifier.padding(top = 6.dp, start = 12.dp, end = 12.dp),
        )
    }
}

@Composable
private fun RememberBox(checked: Boolean) {
    val shape = RoundedCornerShape(5.dp)
    Box(
        modifier = Modifier
            .size(17.dp)
            .clip(shape)
            .background(if (checked) Neon else Color.Transparent)
            .border(1.dp, if (checked) Neon else StrokeStrong, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            CheckGlyph(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                tint = Acero1,
            )
        }
    }
}
