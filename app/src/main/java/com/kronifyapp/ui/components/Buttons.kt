package com.kronifyapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.theme.ButtonBottom
import com.kronifyapp.ui.theme.ButtonTop
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "primaryPress",
    )
    val shape = RoundedCornerShape(15.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 18.dp,
                shape = shape,
                clip = false,
                ambientColor = Neon.copy(alpha = 0.4f),
                spotColor = Neon.copy(alpha = 0.5f),
            )
            .clip(shape)
            .background(Brush.verticalGradient(listOf(ButtonTop, ButtonBottom)))
            .border(1.dp, Neon.copy(alpha = 0.42f), shape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.1.sp,
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(15.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape)
            .background(Color(0x0DD5F0F7))
            .border(1.dp, StrokeStrong, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.1.sp,
        )
    }
}

@Composable
fun SocialButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit = {},
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .height(46.dp)
            .clip(shape)
            .background(Color(0x0DD5F0F7))
            .border(1.dp, StrokeSubtle, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading()
        Box(Modifier.padding(start = 8.dp)) {
            Text(
                text = text,
                color = TextSecondary,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
