package com.kronifyapp.ui.screens.intro

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.R
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextQuaternary
import com.kronifyapp.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val EaseOutExpo = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
private val EaseOutSoft = CubicBezierEasing(0.2f, 0f, 0f, 1f)

/**
 * Apertura de la app: el logo aparece, el anillo se expande y el wordmark
 * se asienta. Después de ~1.7 s cede el paso al login. Un toque salta la espera.
 */
@Composable
fun IntroScreen(onFinish: () -> Unit) {
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.86f) }
    val ringAlpha = remember { Animatable(0f) }
    val ringScale = remember { Animatable(0.9f) }
    val wordAlpha = remember { Animatable(0f) }
    val wordOffset = remember { Animatable(12f) }
    val taglineAlpha = remember { Animatable(0f) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch { logoAlpha.animateTo(1f, tween(600, easing = EaseOutSoft)) }
        launch { logoScale.animateTo(1f, tween(650, easing = EaseOutExpo)) }
        launch {
            ringScale.animateTo(
                targetValue = 1.62f,
                animationSpec = tween(820, delayMillis = 120, easing = EaseOutExpo),
            )
        }
        launch {
            ringAlpha.animateTo(0.26f, tween(240, delayMillis = 120, easing = EaseOutSoft))
            ringAlpha.animateTo(0f, tween(560, easing = EaseOutSoft))
        }
        launch { wordAlpha.animateTo(1f, tween(620, delayMillis = 250, easing = EaseOutSoft)) }
        launch {
            wordOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(620, delayMillis = 250, easing = EaseOutExpo),
            )
        }
        launch { taglineAlpha.animateTo(1f, tween(560, delayMillis = 450, easing = EaseOutSoft)) }

        delay(1700)
        if (!finished) {
            finished = true
            onFinish()
        }
    }

    val skip = {
        if (!finished) {
            finished = true
            onFinish()
        }
    }

    AuroraBackground(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = skip,
            ),
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-28).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.size(150.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(126.dp)
                            .graphicsLayer {
                                scaleX = ringScale.value
                                scaleY = ringScale.value
                                alpha = ringAlpha.value
                            }
                            .border(1.dp, Neon.copy(alpha = 0.55f), CircleShape),
                    )
                    Box(
                        modifier = Modifier
                            .size(132.dp)
                            .border(1.dp, StrokeStrong, CircleShape),
                    )
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .shadow(
                                elevation = 36.dp,
                                shape = CircleShape,
                                clip = false,
                                ambientColor = Neon.copy(alpha = 0.45f),
                                spotColor = Neon.copy(alpha = 0.5f),
                            )
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Neon.copy(alpha = 0.16f), Color.Transparent),
                                ),
                                shape = CircleShape,
                            ),
                    )
                    Image(
                        painter = painterResource(R.drawable.logo_kronify),
                        contentDescription = null,
                        modifier = Modifier
                            .size(112.dp)
                            .graphicsLayer {
                                alpha = logoAlpha.value
                                scaleX = logoScale.value
                                scaleY = logoScale.value
                            },
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Kronify",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 36.sp,
                    letterSpacing = (-0.6).sp,
                    color = TextPrimary,
                    modifier = Modifier.graphicsLayer {
                        alpha = wordAlpha.value
                        translationY = wordOffset.value.dp.toPx()
                    },
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Cada cita en su sitio.",
                    fontSize = 13.sp,
                    letterSpacing = 0.2.sp,
                    color = TextTertiary,
                    modifier = Modifier.graphicsLayer { alpha = taglineAlpha.value },
                )
            }

            Text(
                text = "Toca para continuar",
                fontSize = 10.5.sp,
                letterSpacing = 1.4.sp,
                textAlign = TextAlign.Center,
                color = TextQuaternary,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .graphicsLayer { alpha = taglineAlpha.value * 0.9f },
            )
        }
    }
}
