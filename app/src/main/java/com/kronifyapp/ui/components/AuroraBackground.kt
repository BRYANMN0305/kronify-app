package com.kronifyapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kronifyapp.ui.theme.Acero1

/**
 * Fondo base de Kronify: degradado profundo con dos auroras muy suaves.
 * Se dibuja una sola vez y no captura gestos.
 */
@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val backgroundBrush = remember {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF081418), Acero1, Color(0xFF03080A)),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x3D2C7F92), Color(0x1A1E3D49), Color.Transparent),
                    center = Offset(size.width * 0.24f, -size.height * 0.02f),
                    radius = size.width * 1.05f,
                ),
                radius = size.width * 1.05f,
                center = Offset(size.width * 0.24f, -size.height * 0.02f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x14E7B96F), Color.Transparent),
                    center = Offset(size.width * 0.02f, size.height * 0.94f),
                    radius = size.width * 0.7f,
                ),
                radius = size.width * 0.7f,
                center = Offset(size.width * 0.02f, size.height * 0.94f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x173FE1FF), Color.Transparent),
                    center = Offset(size.width * 1.02f, size.height * 0.88f),
                    radius = size.width * 0.55f,
                ),
                radius = size.width * 0.55f,
                center = Offset(size.width * 1.02f, size.height * 0.88f),
            )
        }
        content()
    }
}
