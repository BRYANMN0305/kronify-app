package com.kronifyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.theme.DangerRed
import com.kronifyapp.ui.theme.FieldFocused
import com.kronifyapp.ui.theme.FieldIdle
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextQuaternary
import com.kronifyapp.ui.theme.TextTertiary

@Composable
fun KronifyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false,
    error: String? = null,
    leading: (@Composable () -> Unit)? = null,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(14.dp)
    val hasError = !error.isNullOrBlank()

    Column(modifier) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp,
            color = if (hasError) DangerRed else TextTertiary,
        )
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .fillMaxWidth()
                .height(50.dp)
                .clip(shape)
                .background(if (focused) FieldFocused else FieldIdle)
                .border(
                    width = 1.dp,
                    color = if (hasError) DangerRed else if (focused) Neon.copy(alpha = 0.5f) else StrokeSubtle,
                    shape = shape,
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (leading != null) {
                Box(Modifier.padding(start = 14.dp)) { leading() }
            }

            if (value.isEmpty() && placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = TextQuaternary,
                    modifier = Modifier.padding(
                        start = if (leading != null) 42.dp else 14.dp,
                        end = if (isPassword) 64.dp else 14.dp,
                    ),
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                ),
                cursorBrush = SolidColor(Neon),
                visualTransformation = if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction,
                ),
                interactionSource = interaction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = if (leading != null) 42.dp else 14.dp,
                        end = if (isPassword) 64.dp else 14.dp,
                    ),
            )

            if (isPassword) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { passwordVisible = !passwordVisible }
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                ) {
                    EyeGlyph(
                        visible = passwordVisible,
                        tint = if (passwordVisible) Neon else TextTertiary,
                    )
                }
            }
        }

        if (hasError) {
            Text(
                text = error,
                fontSize = 11.sp,
                color = DangerRed,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }
    }
}
