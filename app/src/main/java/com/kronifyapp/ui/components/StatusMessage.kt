package com.kronifyapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.theme.DangerRed
import com.kronifyapp.ui.theme.OkGreen
import kotlinx.coroutines.delay

/**
 * Mensaje compacto de resultado. Se usa debajo de la acción que lo provoca,
 * no como bloque informativo al inicio de la pantalla.
 */
@Composable
fun StatusMessage(
    text: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (isError) DangerRed else OkGreen
    val shape = RoundedCornerShape(11.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color.copy(alpha = if (isError) .10f else .08f))
            .border(1.dp, color.copy(alpha = .28f), shape)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (isError) "!" else "✓",
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = text,
            color = color,
            fontSize = 11.5.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

/** Igual que StatusMessage, pero aparece y desaparece con una transición suave. */
@Composable
fun AnimatedStatusMessage(
    text: String?,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = !text.isNullOrBlank(),
        enter = fadeIn(tween(180)) + expandVertically(tween(220)),
        exit = fadeOut(tween(120)) + shrinkVertically(tween(180)),
    ) {
        StatusMessage(
            text = text.orEmpty(),
            isError = isError,
            modifier = modifier.padding(top = 10.dp),
        )
    }
}

/** Limpia automáticamente un mensaje de éxito después de 5 segundos. */
@Composable
fun AutoDismissSuccess(
    message: String?,
    durationMillis: Long = 5_000L,
    onDismiss: () -> Unit,
) {
    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            delay(durationMillis)
            onDismiss()
        }
    }
}

/** Variante con color neutro para avisos informativos. */
@Composable
fun InfoMessage(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFA6C0C7),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(color.copy(alpha = .07f))
            .border(1.dp, color.copy(alpha = .22f), RoundedCornerShape(11.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("i", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(
            text = text,
            color = color,
            fontSize = 11.5.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
