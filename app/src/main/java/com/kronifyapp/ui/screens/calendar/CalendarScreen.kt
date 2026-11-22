package com.kronifyapp.ui.screens.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextTertiary

@Composable
fun CalendarScreen() {
    AuroraBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Tu calendario", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Aquí aparecerán tus próximas citas.", color = TextTertiary, fontSize = 14.sp)
        }
    }
}
