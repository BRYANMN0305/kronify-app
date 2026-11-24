package com.kronifyapp.ui.screens.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.domain.model.BusinessReview
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.viewModel.ReviewsViewModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReviewsScreen(viewModel: ReviewsViewModel = androidx.hilt.navigation.compose.hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    AuroraBackground {
        Column(Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 30.dp)) {
            Text("Reseñas", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Lo que opinan tus clientes sobre tu negocio.", color = TextTertiary, fontSize = 13.sp, modifier = Modifier.padding(top = 5.dp))
            Row(Modifier.fillMaxWidth().padding(top = 23.dp, bottom = 22.dp).clip(RoundedCornerShape(22.dp)).background(Color(0x35182F36)).border(1.dp, StrokeSubtle, RoundedCornerShape(22.dp)).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("VALORACIÓN GENERAL", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 7.dp)) {
                        Text(if (state.reviews.isEmpty()) "—" else String.format(Locale.US, "%.1f", state.average), color = TextPrimary, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                        Text(" / 5", color = TextTertiary, fontSize = 14.sp, modifier = Modifier.padding(start = 5.dp, bottom = 7.dp))
                    }
                    Text("${state.reviews.size} ${if (state.reviews.size == 1) "reseña" else "reseñas"}", color = TextSecondary, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("★ ★ ★ ★ ★", color = Neon, fontSize = 18.sp, letterSpacing = 1.sp)
                    Text("Opiniones verificadas", color = TextTertiary, fontSize = 10.sp, modifier = Modifier.padding(top = 6.dp))
                }
            }
            Text("Opiniones de clientes", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))
            when {
                state.isLoading -> Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Neon) }
                state.error != null -> Text(state.error.orEmpty(), color = TextTertiary, modifier = Modifier.padding(top = 12.dp))
                state.reviews.isEmpty() -> Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.TopCenter) { Text("Aún no tienes reseñas.", color = TextTertiary, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 30.dp)) }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(11.dp), modifier = Modifier.weight(1f)) {
                    items(state.reviews, key = { it.reviewId }) { ReviewCard(it) }
                    item { Spacer(Modifier.height(76.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(review: BusinessReview) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0x35182F36)).border(1.dp, StrokeSubtle, RoundedCornerShape(18.dp)).padding(15.dp), verticalAlignment = Alignment.Top) {
        Box(Modifier.size(39.dp).clip(CircleShape).background(Color(0xFF1D424C)), contentAlignment = Alignment.Center) {
            Text(review.customerName.orEmpty().trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }.ifBlank { "C" }, color = Neon, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(review.customerName.orEmpty().ifBlank { "Cliente" }, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(formatDate(review.createdAt), color = TextTertiary, fontSize = 10.sp)
            }
            Text((1..5).joinToString(" ") { if (it <= review.rating.coerceIn(0, 5)) "★" else "☆" }, color = Neon, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
            if (!review.comment.isNullOrBlank()) Text(review.comment, color = TextSecondary, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 7.dp))
        }
    }
}

private fun formatDate(value: String?): String = runCatching {
    val date = runCatching { OffsetDateTime.parse(value).toLocalDate() }.getOrElse { Instant.parse(value).atOffset(java.time.ZoneOffset.UTC).toLocalDate() }
    date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "CO")))
}.getOrDefault("")
