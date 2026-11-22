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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.KronifyTextField
import com.kronifyapp.ui.components.PrimaryButton
import com.kronifyapp.ui.components.SecondaryButton
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import kotlinx.coroutines.launch

@Composable
fun OnboardingStep3Screen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    userId: Long,
    viewModel: com.kronifyapp.ui.viewModel.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    var code by rememberSaveable { mutableStateOf("") }
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
                            text = "Paso 3 de 3",
                            fontSize = 11.5.sp,
                            color = TextTertiary,
                        )
                    }

                    // Barra de progreso (100%)
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
                                .fillMaxWidth(1f)
                                .background(Neon, RoundedCornerShape(2.dp)),
                        )
                    }

                    // Títulos
                    Text(
                        text = "¿Tienes un código\nde activación?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 33.sp,
                        letterSpacing = (-0.5).sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 20.dp),
                    )
                    Text(
                        text = "Si tienes un código para tu versión de pago o plan Pro, ingrésalo aquí. Si no lo tienes, puedes omitirlo y continuar con la versión gratuita.",
                        fontSize = 13.sp,
                        color = TextTertiary,
                        modifier = Modifier.padding(top = 6.dp, bottom = 24.dp),
                    )

                    KronifyTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = "Código de activación (opcional)",
                        placeholder = "Ej. KRONIFY-PRO-2026",
                    )

                    Spacer(Modifier.height(20.dp))

                    PrimaryButton(
                        text = if (code.isBlank()) "Continuar gratis" else "Canjear y continuar",
                        onClick = {
                            if (code.isBlank()) {
                                notify("Continuando con la versión gratuita")
                                viewModel.completeOnboarding(userId)
                                onFinish()
                            } else {
                                notify("¡Código canjeado con éxito! Plan Pro activado.")
                                viewModel.completeOnboarding(userId)
                                onFinish()
                            }
                        },
                    )

                    if (code.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        SecondaryButton(
                            text = "Omitir y continuar gratis",
                            onClick = {
                                notify("Continuando con la versión gratuita")
                                viewModel.completeOnboarding(userId)
                                onFinish()
                            },
                        )
                    }
                }
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
