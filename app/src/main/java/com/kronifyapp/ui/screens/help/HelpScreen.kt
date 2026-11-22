package com.kronifyapp.ui.screens.help

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.ChevronRightGlyph
import com.kronifyapp.ui.components.GlobeGlyph
import com.kronifyapp.ui.components.MailGlyph
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary

private data class FaqItem(val question: String, val answer: String)

private val faqs = listOf(
    FaqItem(
        "¿Cómo creo una cita?",
        "Ve a la pestaña Agenda y toca el botón de nueva cita. Elige el servicio, el profesional y el horario disponible.",
    ),
    FaqItem(
        "¿Cómo configuro mis horarios?",
        "En Perfil entra a Horarios. Allí puedes ajustar el horario del negocio y, si eres dueño, el de cada empleado.",
    ),
    FaqItem(
        "¿Cómo invito a mi equipo?",
        "Abre Perfil, entra a Equipo y toca Invitar miembro. La persona recibirá un correo para unirse a tu negocio.",
    ),
    FaqItem(
        "¿Cómo cambio mi plan?",
        "Desde Perfil entra a Plan y facturación. Allí verás tu plan actual, el uso de tus límites y los planes disponibles.",
    ),
    FaqItem(
        "¿Cómo cambio mi contraseña?",
        "Ve a Perfil, Seguridad. Si tu cuenta tiene contraseña, podrás actualizarla desde ahí.",
    ),
    FaqItem(
        "¿Cómo habilito la autogestión de horarios?",
        "En Perfil, Equipo, activa el interruptor de Autogestión del empleado. También puedes hacerlo desde Horarios, Empleados.",
    ),
)

@Composable
fun HelpScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var expandedIndex by remember { mutableStateOf<Int?>(null) }
    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }

    fun openUrl(url: String) {
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
    }

    fun sendSupportEmail() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:noreply.kronify@gmail.com")).apply {
            putExtra(Intent.EXTRA_SUBJECT, "Ayuda con Kronify")
        }
        runCatching { context.startActivity(intent) }
    }

    AuroraBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 28.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x0DD5F0F7))
                            .border(1.dp, StrokeSubtle, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "‹",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.padding(bottom = 2.dp),
                        )
                    }
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            text = "SOPORTE",
                            color = TextTertiary,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            text = "Ayuda",
                            color = TextPrimary,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Respuestas rápidas y un canal directo para contactarnos.",
                    color = TextTertiary,
                    fontSize = 12.5.sp,
                    modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
                )
            }

            item {
                val contactShape = RoundedCornerShape(22.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(contactShape)
                        .background(Color(0x30133A44))
                        .border(1.dp, Neon.copy(alpha = .35f), contactShape)
                        .padding(16.dp),
                ) {
                    Text(
                        text = "¿Necesitas ayuda?",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Escríbenos y te responderemos lo antes posible.",
                        color = TextTertiary,
                        fontSize = 11.5.sp,
                        modifier = Modifier.padding(top = 3.dp),
                    )

                    Spacer(Modifier.height(13.dp))

                    ContactRow(
                        icon = { MailGlyph(tint = Neon) },
                        title = "noreply.kronify@gmail.com",
                        subtitle = "Soporte por correo",
                        onClick = ::sendSupportEmail,
                    )
                    Spacer(Modifier.height(9.dp))
                    ContactRow(
                        icon = { GlobeGlyph(tint = Neon) },
                        title = "kronify.page.gd",
                        subtitle = "Visitar sitio web",
                        onClick = { openUrl("https://kronify.page.gd") },
                    )
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
                SectionTitle("PREGUNTAS FRECUENTES")
                Spacer(Modifier.height(9.dp))
            }

            itemsIndexed(faqs) { index, faq ->
                FaqCard(
                    faq = faq,
                    expanded = expandedIndex == index,
                    onToggle = { expandedIndex = if (expandedIndex == index) null else index },
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                SectionTitle("ACERCA DE KRONIFY")
                Spacer(Modifier.height(9.dp))
            }

            item {
                val aboutShape = RoundedCornerShape(22.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(aboutShape)
                        .background(SurfaceDeep.copy(alpha = .73f))
                        .border(1.dp, StrokeStrong, aboutShape)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x223FE1FF))
                            .border(1.dp, Neon.copy(alpha = .3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "K",
                            color = Neon,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = "Kronify",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Versión $version - Sistema de agendamiento",
                            color = TextTertiary,
                            fontSize = 10.5.sp,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqCard(faq: FaqItem, expanded: Boolean, onToggle: () -> Unit) {
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(180),
        label = "faqChevron",
    )
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = .73f))
            .border(1.dp, StrokeStrong, shape)
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = faq.question,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.rotate(chevronRotation)) {
                ChevronRightGlyph(tint = TextTertiary)
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(160)) + expandVertically(tween(210)),
            exit = fadeOut(tween(120)) + shrinkVertically(tween(170)),
        ) {
            Text(
                text = faq.answer,
                color = TextSecondary,
                fontSize = 11.5.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 8.dp, end = 12.dp),
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = TextTertiary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 5.dp),
    )
}

@Composable
private fun ContactRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0x14D5F0F7))
            .border(1.dp, StrokeSubtle, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(22.dp), contentAlignment = Alignment.Center) {
            icon()
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 9.dp),
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = TextTertiary,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 1.dp),
            )
        }
        ChevronRightGlyph(tint = TextTertiary)
    }
}
