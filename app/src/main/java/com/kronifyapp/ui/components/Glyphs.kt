package com.kronifyapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Sobre dibujado a mano para no arrastrar la librería de iconos. */
@Composable
fun MailGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier.size(18.dp)) {
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        val inset = size.width * 0.06f
        drawRoundRect(
            color = tint,
            topLeft = Offset(inset, size.height * 0.2f),
            size = Size(size.width - inset * 2f, size.height * 0.6f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = stroke,
        )
        val flap = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.3f)
            lineTo(size.width * 0.5f, size.height * 0.56f)
            lineTo(size.width * 0.82f, size.height * 0.3f)
        }
        drawPath(flap, color = tint, style = stroke)
    }
}

/** Cuatro cuadros de Microsoft. */
@Composable
fun MicrosoftGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier.size(14.dp)) {
        val gap = size.width * 0.08f
        val cell = (size.width - gap) / 2f
        drawRect(Color(0xFFF25022), topLeft = Offset.Zero, size = Size(cell, cell))
        drawRect(Color(0xFF7FBA00), topLeft = Offset(cell + gap, 0f), size = Size(cell, cell))
        drawRect(Color(0xFF00A4EF), topLeft = Offset(0f, cell + gap), size = Size(cell, cell))
        drawRect(Color(0xFFFFB900), topLeft = Offset(cell + gap, cell + gap), size = Size(cell, cell))
    }
}

/** Icono G tricolor de Google. */
@Composable
fun GoogleGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier.size(15.dp)) {
        val strokeWidth = size.width * 0.22f
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        val inset = strokeWidth / 2f
        val arcSize = Size(size.width - inset * 2, size.height - inset * 2)

        // Rojo superior
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 190f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = stroke,
        )
        // Amarillo inferior izquierdo
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 120f,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = stroke,
        )
        // Verde inferior derecho
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 20f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = stroke,
        )
        // Azul derecho + barra central
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -45f,
            sweepAngle = 65f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = stroke,
        )
        val barW = size.width * 0.45f
        val barH = strokeWidth
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(size.width * 0.45f, (size.height - barH) / 2f),
            size = Size(barW, barH),
        )
    }
}

/** Marca de verificación del checkbox. */
@Composable
fun CheckGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier) {
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        val path = Path().apply {
            moveTo(size.width * 0.24f, size.height * 0.52f)
            lineTo(size.width * 0.43f, size.height * 0.7f)
            lineTo(size.width * 0.78f, size.height * 0.32f)
        }
        drawPath(path, color = tint, style = stroke)
    }
}

/** Icono de ojo para mostrar/ocultar contraseña. */
@Composable
fun EyeGlyph(visible: Boolean, modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier.size(18.dp)) {
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        val path = Path().apply {
            moveTo(size.width * 0.12f, size.height * 0.5f)
            quadraticTo(
                size.width * 0.5f, size.height * 0.18f,
                size.width * 0.88f, size.height * 0.5f,
            )
            quadraticTo(
                size.width * 0.5f, size.height * 0.82f,
                size.width * 0.12f, size.height * 0.5f,
            )
        }
        drawPath(path, color = tint, style = stroke)
        drawCircle(
            color = tint,
            radius = size.width * 0.16f,
            center = Offset(size.width * 0.5f, size.height * 0.5f),
        )
        if (!visible) {
            drawLine(
                color = tint,
                start = Offset(size.width * 0.22f, size.height * 0.22f),
                end = Offset(size.width * 0.78f, size.height * 0.78f),
                strokeWidth = 1.6.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun CameraGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier.size(20.dp)) {
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(
            color = tint,
            topLeft = Offset(size.width * 0.08f, size.height * 0.3f),
            size = Size(size.width * 0.84f, size.height * 0.6f),
            cornerRadius = CornerRadius(4.dp.toPx()),
            style = stroke,
        )
        drawCircle(
            color = tint,
            radius = size.width * 0.22f,
            center = Offset(size.width * 0.5f, size.height * 0.6f),
            style = stroke,
        )
        val path = Path().apply {
            moveTo(size.width * 0.32f, size.height * 0.3f)
            lineTo(size.width * 0.4f, size.height * 0.16f)
            lineTo(size.width * 0.6f, size.height * 0.16f)
            lineTo(size.width * 0.68f, size.height * 0.3f)
        }
        drawPath(path, color = tint, style = stroke)
    }
}

@Composable
fun ClockGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier.size(16.dp)) {
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(color = tint, radius = size.width * 0.44f, style = stroke)
        drawLine(
            color = tint,
            start = Offset(size.width * 0.5f, size.height * 0.25f),
            end = Offset(size.width * 0.5f, size.height * 0.5f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(size.width * 0.5f, size.height * 0.5f),
            end = Offset(size.width * 0.7f, size.height * 0.65f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun GlobeGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier.size(16.dp)) {
        val stroke = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(color = tint, radius = size.width * 0.44f, style = stroke)
        drawOval(
            color = tint,
            topLeft = Offset(size.width * 0.25f, size.height * 0.1f),
            size = Size(size.width * 0.5f, size.height * 0.8f),
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(size.width * 0.1f, size.height * 0.5f),
            end = Offset(size.width * 0.9f, size.height * 0.5f),
            strokeWidth = 1.2.dp.toPx(),
        )
    }
}

@Composable
fun ChevronRightGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier.size(16.dp)) {
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(size.width * 0.35f, size.height * 0.2f)
            lineTo(size.width * 0.65f, size.height * 0.5f)
            lineTo(size.width * 0.35f, size.height * 0.8f)
        }
        drawPath(path, color = tint, style = stroke)
    }
}

@Composable
fun ChevronDownGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier.size(16.dp)) {
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.35f)
            lineTo(size.width * 0.5f, size.height * 0.65f)
            lineTo(size.width * 0.8f, size.height * 0.35f)
        }
        drawPath(path, color = tint, style = stroke)
    }
}

@Composable
fun ExternalLinkGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier.size(19.dp)) {
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(
            color = tint,
            topLeft = Offset(size.width * .12f, size.height * .28f),
            size = Size(size.width * .60f, size.height * .60f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = stroke,
        )
        drawLine(tint, Offset(size.width * .48f, size.height * .52f), Offset(size.width * .88f, size.height * .12f), stroke.width, StrokeCap.Round)
        drawLine(tint, Offset(size.width * .60f, size.height * .12f), Offset(size.width * .88f, size.height * .12f), stroke.width, StrokeCap.Round)
        drawLine(tint, Offset(size.width * .88f, size.height * .12f), Offset(size.width * .88f, size.height * .40f), stroke.width, StrokeCap.Round)
    }
}


