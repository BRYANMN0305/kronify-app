package com.kronifyapp.ui.screens.welcome

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.R
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.theme.ButtonBottom
import com.kronifyapp.ui.theme.ButtonTop
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import kotlinx.coroutines.launch

private val Accent = Neon
private val Ink = TextPrimary
private val SubInk = TextSecondary
private val MutedInk = TextTertiary
private val Hairline = StrokeSubtle
private val TileBg = Color(0x0DD5F0F7)
private val TileBorder = StrokeSubtle
private val OutlineStroke = StrokeStrong

/**
 * Presentación previa al acceso: explica el producto y ofrece crear cuenta
 * o entrar con una cuenta existente.
 */
@Composable
fun WelcomeScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = viewportHeight)
                    .padding(horizontal = 24.dp, vertical = 26.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo_kronify),
                        contentDescription = null,
                        modifier = Modifier.size(62.dp),
                    )
                    Text(
                        text = "Kronify",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        letterSpacing = (-0.8).sp,
                        color = Ink,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                    Text(
                        text = "®",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedInk,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .offset(y = (-12).dp),
                    )
                }

                Spacer(Modifier.height(40.dp))

                Text(
                    text = "Tu negocio,",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    lineHeight = 39.sp,
                    letterSpacing = (-1).sp,
                    color = Ink,
                )
                Text(
                    text = "siempre a mano.",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    lineHeight = 39.sp,
                    letterSpacing = (-1).sp,
                    color = Accent,
                )
                Text(
                    text = "Agenda, equipo y reservas en un solo lugar.",
                    fontSize = 14.5.sp,
                    lineHeight = 21.sp,
                    color = SubInk,
                    modifier = Modifier.padding(top = 14.dp),
                )

                Spacer(Modifier.height(34.dp))

                Feature(
                    title = "Agenda inteligente",
                    detail = "Día, semana y huecos libres de un vistazo.",
                    icon = { tint -> CalendarGlyph(tint) },
                )
                FeatureDivider()
                Feature(
                    title = "Tu equipo, coordinado",
                    detail = "Horarios, permisos y citas sin cruces.",
                    icon = { tint -> TeamGlyph(tint) },
                )
                FeatureDivider()
                Feature(
                    title = "Reservas 24/7",
                    detail = "Tus clientes reservan cuando quieran.",
                    icon = { tint -> CalendarCheckGlyph(tint) },
                )

                Spacer(Modifier.height(36.dp))

                PillButton(
                    text = "Crear cuenta gratis",
                    onClick = onRegister,
                )

                OutlinedPillButton(
                    text = "Ya tengo cuenta",
                    onClick = onLogin,
                    modifier = Modifier.padding(top = 12.dp),
                )

                Text(
                    text = buildAnnotatedString {
                        append("Al continuar aceptas los ")
                        withStyle(
                            SpanStyle(
                                color = Neon.copy(alpha = 0.85f),
                                textDecoration = TextDecoration.Underline,
                            ),
                        ) { append("términos del servicio") }
                        append(" y la ")
                        withStyle(
                            SpanStyle(
                                color = Neon.copy(alpha = 0.85f),
                                textDecoration = TextDecoration.Underline,
                            ),
                        ) { append("política de privacidad") }
                        append(".")
                    },
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MutedInk,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp),
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 18.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                shape = RoundedCornerShape(16.dp),
                containerColor = SurfaceDeep,
                contentColor = Ink,
                actionColor = Accent,
                dismissActionContentColor = SubInk,
                modifier = Modifier.border(1.dp, OutlineStroke, RoundedCornerShape(16.dp)),
            )
        }
    }
}

@Composable
private fun Feature(
    title: String,
    detail: String,
    icon: @Composable (Color) -> Unit,
) {
    Row(
        modifier = Modifier.padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(TileBg)
                .border(1.dp, TileBorder, RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center,
        ) {
            icon(Accent)
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = title,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
                color = Ink,
            )
            Text(
                text = detail,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = SubInk,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
    }
}

@Composable
private fun FeatureDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 62.dp)
            .height(1.dp)
            .background(Hairline),
    )
}

@Composable
private fun PillButton(text: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(
                elevation = 20.dp,
                shape = shape,
                clip = false,
                ambientColor = Accent.copy(alpha = 0.4f),
                spotColor = Accent.copy(alpha = 0.5f),
            )
            .clip(shape)
            .background(Brush.verticalGradient(listOf(ButtonTop, ButtonBottom)))
            .border(1.dp, Neon.copy(alpha = 0.5f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.1).sp,
            color = Color.White,
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp),
        ) {
            ArrowGlyph(tint = Color.White)
        }
    }
}

@Composable
private fun OutlinedPillButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape)
            .background(Color(0x0DD5F0F7))
            .border(1.dp, OutlineStroke, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.1).sp,
            color = Ink,
        )
    }
}

@Composable
private fun ArrowGlyph(tint: Color) {
    Canvas(Modifier.size(20.dp)) {
        val stroke = 2.dp.toPx()
        drawLine(
            color = tint,
            start = Offset(size.width * 0.1f, size.height * 0.5f),
            end = Offset(size.width * 0.86f, size.height * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(size.width * 0.58f, size.height * 0.22f),
            end = Offset(size.width * 0.88f, size.height * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(size.width * 0.58f, size.height * 0.78f),
            end = Offset(size.width * 0.88f, size.height * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun CalendarGlyph(tint: Color) {
    Canvas(Modifier.size(23.dp)) {
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(
            color = tint,
            topLeft = Offset(size.width * 0.12f, size.height * 0.22f),
            size = Size(size.width * 0.76f, size.height * 0.64f),
            cornerRadius = CornerRadius(size.width * 0.15f),
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(size.width * 0.12f, size.height * 0.4f),
            end = Offset(size.width * 0.88f, size.height * 0.4f),
            strokeWidth = 1.4.dp.toPx(),
        )
        val leg = 1.6.dp.toPx()
        drawLine(tint, Offset(size.width * 0.34f, size.height * 0.12f), Offset(size.width * 0.34f, size.height * 0.3f), leg, StrokeCap.Round)
        drawLine(tint, Offset(size.width * 0.66f, size.height * 0.12f), Offset(size.width * 0.66f, size.height * 0.3f), leg, StrokeCap.Round)
    }
}

@Composable
private fun TeamGlyph(tint: Color) {
    Canvas(Modifier.size(23.dp)) {
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(
            color = tint,
            radius = size.width * 0.14f,
            center = Offset(size.width * 0.42f, size.height * 0.33f),
            style = stroke,
        )
        drawCircle(
            color = tint,
            radius = size.width * 0.1f,
            center = Offset(size.width * 0.72f, size.height * 0.38f),
            style = stroke,
        )
        val path = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.8f)
            cubicTo(
                size.width * 0.2f, size.height * 0.58f,
                size.width * 0.64f, size.height * 0.58f,
                size.width * 0.66f, size.height * 0.8f,
            )
        }
        drawPath(path, color = tint, style = stroke)
        val second = Path().apply {
            moveTo(size.width * 0.66f, size.height * 0.58f)
            cubicTo(
                size.width * 0.8f, size.height * 0.58f,
                size.width * 0.86f, size.height * 0.68f,
                size.width * 0.86f, size.height * 0.78f,
            )
        }
        drawPath(second, color = tint, style = stroke)
    }
}

@Composable
private fun CalendarCheckGlyph(tint: Color) {
    Canvas(Modifier.size(23.dp)) {
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(
            color = tint,
            topLeft = Offset(size.width * 0.12f, size.height * 0.22f),
            size = Size(size.width * 0.76f, size.height * 0.64f),
            cornerRadius = CornerRadius(size.width * 0.15f),
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(size.width * 0.12f, size.height * 0.4f),
            end = Offset(size.width * 0.88f, size.height * 0.4f),
            strokeWidth = 1.4.dp.toPx(),
        )
        val leg = 1.6.dp.toPx()
        drawLine(tint, Offset(size.width * 0.34f, size.height * 0.12f), Offset(size.width * 0.34f, size.height * 0.3f), leg, StrokeCap.Round)
        drawLine(tint, Offset(size.width * 0.66f, size.height * 0.12f), Offset(size.width * 0.66f, size.height * 0.3f), leg, StrokeCap.Round)
        val check = Path().apply {
            moveTo(size.width * 0.36f, size.height * 0.66f)
            lineTo(size.width * 0.46f, size.height * 0.75f)
            lineTo(size.width * 0.64f, size.height * 0.55f)
        }
        drawPath(check, color = tint, style = stroke)
    }
}
