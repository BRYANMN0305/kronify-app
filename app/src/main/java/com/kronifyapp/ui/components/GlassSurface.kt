package com.kronifyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kronifyapp.ui.theme.GlassBottom
import com.kronifyapp.ui.theme.GlassTop
import com.kronifyapp.ui.theme.StrokeStrong

/**
 * Tarjeta de vidrio del sistema de diseño: gradiente translúcido,
 * borde luminoso de 1 dp y sombra profunda.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(28.dp),
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 26.dp,
                shape = shape,
                clip = false,
                ambientColor = Color.Black,
                spotColor = Color.Black,
            )
            .clip(shape)
            .background(Brush.verticalGradient(listOf(GlassTop, GlassBottom)))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(StrokeStrong, Color(0x0FD5F0F7)),
                ),
                shape = shape,
            )
            .padding(contentPadding),
        content = content,
    )
}
