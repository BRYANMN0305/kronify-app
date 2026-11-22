package com.kronifyapp.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.kronifyapp.ui.components.ExternalLinkGlyph
import com.kronifyapp.ui.components.GlobeGlyph
import com.kronifyapp.ui.state.ProfileUiState
import com.kronifyapp.ui.state.UserRole
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.viewModel.ProfileViewModel
import java.util.Locale

private data class ProfileOption(
    val icon: String,
    val title: String,
    val subtitle: String,
    val value: String? = null,
    val accent: Boolean = false,
    val onClick: () -> Unit = {},
)

@Composable
fun ProfileScreen(
    role: UserRole = UserRole.OWNER,
    onOpenBusinessData: () -> Unit = {},
    onOpenServices: () -> Unit = {},
    onOpenSchedules: () -> Unit = {},
    onOpenMyAppointments: () -> Unit = {},
    onOpenPlan: () -> Unit = {},
    onOpenTeam: () -> Unit = {},
    onOpenSecurity: () -> Unit = {},
    onOpenPersonalData: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onLogout: () -> Unit = {},
    profileViewModel: ProfileViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val profile by profileViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.load()
    }

    val isOwner = role == UserRole.OWNER
    val employeeLimit = profile.employeeLimit
    val teamSubtitle = when {
        employeeLimit != null && employeeLimit > 0 ->
            "${profile.employeeCount} de $employeeLimit plazas ocupadas"
        profile.employeeCount > 0 -> "${profile.employeeCount} empleados"
        else -> "Invita a tu equipo"
    }
    val servicesSubtitle = if (profile.serviceCount == 0) {
        "Crea tu primer servicio"
    } else {
        "${profile.serviceCount} servicios · ${profile.activeServiceCount} activos"
    }
    val scheduleSubtitle = profile.scheduleSummary.ifBlank {
        "Configura el horario del negocio"
    }

    val businessOptions = if (isOwner) {
        listOf(
            ProfileOption("⚙", "Datos del negocio", "Nombre, dirección y categoría", onClick = onOpenBusinessData),
            ProfileOption("♧", "Equipo", teamSubtitle, onClick = onOpenTeam),
            ProfileOption("✂", "Servicios y precios", servicesSubtitle, onClick = onOpenServices),
            ProfileOption("◷", "Horarios", scheduleSubtitle, onClick = onOpenSchedules),
        )
    } else {
        emptyList()
    }

    val accountOptions = buildList {
        add(ProfileOption("☰", "Mis datos personales", "Nombre, teléfono y correo", onClick = onOpenPersonalData))
        if (isOwner) {
            add(
                ProfileOption(
                    icon = "▤",
                    title = "Plan y facturación",
                    subtitle = "${profile.monthlyAppointments} citas este mes",
                    value = profile.planName.takeIf { it.isNotBlank() },
                    onClick = onOpenPlan,
                )
            )
        }
        if (role == UserRole.EMPLOYEE) {
            add(ProfileOption("◷", "Mis horarios", "Consulta y actualiza tus días", onClick = onOpenSchedules))
        }
        if (role == UserRole.CLIENT) {
            add(ProfileOption("◷", "Mis citas", "Consulta tus reservas", onClick = onOpenMyAppointments))
        }
        add(ProfileOption("♙", "Seguridad", "Contraseña y métodos de acceso", onClick = onOpenSecurity))
        add(
            ProfileOption(
                icon = "♧",
                title = "Notificaciones",
                subtitle = "Nuevas citas y recordatorios",
                value = if (profile.notificationsEnabled) "Activadas" else "Desactivadas",
                onClick = onOpenNotifications,
            )
        )
        add(ProfileOption("?", "Ayuda", "Preguntas frecuentes y soporte", onClick = onOpenHelp))
    }

    AuroraBackground {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 58.dp, bottom = 130.dp),
        ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("TU CUENTA", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("Perfil", color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
            }
        }

        Spacer(Modifier.height(14.dp))
        ProfileSummary(profile, showBusinessStats = isOwner)
        Spacer(Modifier.height(14.dp))
        if (isOwner && profile.slug.isNotBlank()) {
            PublicLink(slug = profile.slug)
            Spacer(Modifier.height(26.dp))
        } else {
            Spacer(Modifier.height(12.dp))
        }
        if (businessOptions.isNotEmpty()) {
            SectionTitle("NEGOCIO")
            Spacer(Modifier.height(10.dp))
            OptionsCard(businessOptions)
            Spacer(Modifier.height(25.dp))
        }
        SectionTitle("CUENTA")
        Spacer(Modifier.height(10.dp))
        OptionsCard(accountOptions)
        Spacer(Modifier.height(22.dp))
        val logoutShape = RoundedCornerShape(18.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color(0x382A1216), logoutShape)
                .border(1.dp, Color(0x28FF8A8A), logoutShape)
                .clickable { onLogout() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "↪",
                color = Color(0xFFFF7A7A),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "Cerrar sesión",
                color = Color(0xFFFF7A7A),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp),
            )
        }
        Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ProfileSummary(profile: ProfileUiState, showBusinessStats: Boolean) {
    val shape = RoundedCornerShape(29.dp)
    Column(
        modifier = Modifier.fillMaxWidth().background(Color(0x9A10262B), shape).border(1.dp, StrokeStrong, shape).padding(19.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(Color(0xFF1B4F72), CircleShape)
                    .border(1.dp, Neon.copy(alpha = .6f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = profile.initials,
                    color = Color(0xFFC8F7FF),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                Text(profile.fullName, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    profile.businessMeta,
                    color = TextTertiary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
            if (showBusinessStats && profile.planName.isNotBlank()) {
                Text(
                    text = profile.planName,
                    color = Color(0xFFC8F7FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0x263FE1FF), RoundedCornerShape(18.dp))
                        .border(1.dp, Neon.copy(alpha = .5f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 11.dp, vertical = 7.dp),
                )
            }
        }
        Spacer(Modifier.height(17.dp))
        if (showBusinessStats) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(StrokeSubtle))
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Metric(
                    value = formatRating(profile),
                    label = if (profile.reviewCount > 0) "Valoración (${profile.reviewCount})" else "Valoración",
                    modifier = Modifier.weight(1f),
                )
                Box(Modifier.size(width = 1.dp, height = 42.dp).background(StrokeSubtle))
                Metric(
                    value = if (profile.isLoading) "—" else profile.monthlyAppointments.toString(),
                    label = "Citas / mes",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun formatRating(profile: ProfileUiState): String {
    val rating = profile.rating
    if (rating == null || profile.reviewCount == 0) return "—"
    return String.format(Locale.forLanguageTag("es-CO"), "%.1f", rating).replace('.', ',')
}

@Composable private fun Metric(value: String, label: String, modifier: Modifier = Modifier) = Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    Text(label, color = TextTertiary, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
}

@Composable
private fun PublicLink(slug: String) {
    val context = LocalContext.current
    val url = "https://kronifytech.vercel.app/negocio/$slug"
    val label = url.removePrefix("https://")
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x30133A44), shape)
            .border(1.dp, Neon.copy(alpha = .35f), shape)
            .clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            .padding(horizontal = 17.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlobeGlyph(tint = Neon)
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f).padding(start = 13.dp),
        )
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color(0x1F3FE1FF), CircleShape)
                .border(1.dp, Neon.copy(alpha = .25f), CircleShape),
            contentAlignment = Alignment.Center,
        ) { ExternalLinkGlyph(tint = Neon) }
    }
}

@Composable private fun SectionTitle(text: String) = Text(text, color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(start = 5.dp))

@Composable
private fun OptionsCard(options: List<ProfileOption>) {
    val shape = RoundedCornerShape(25.dp)
    Column(modifier = Modifier.fillMaxWidth().background(SurfaceDeep.copy(alpha = .73f), shape).border(1.dp, StrokeStrong, shape)) {
        options.forEachIndexed { index, option ->
            ProfileRow(option)
            if (index != options.lastIndex) Box(Modifier.fillMaxWidth().height(1.dp).background(StrokeSubtle))
        }
    }
}

@Composable
private fun ProfileRow(option: ProfileOption) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { option.onClick() }.padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(40.dp).background(if (option.accent) Color(0x352A2112) else Color(0x142D4850), RoundedCornerShape(12.dp)).border(1.dp, if (option.accent) Color(0x806B562A) else StrokeStrong, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Text(option.icon, color = if (option.accent) Color(0xFFE7B96F) else Color(0xFFA6C0C7), fontSize = 18.sp)
        }
        Column(modifier = Modifier.weight(1f).padding(start = 15.dp)) {
            Text(option.title, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            if (option.subtitle.isNotBlank()) Text(option.subtitle, color = TextTertiary, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
        option.value?.let { Text(it, color = TextTertiary, fontSize = 13.sp, modifier = Modifier.padding(end = 11.dp)) }
        Text("›", color = TextTertiary, fontSize = 28.sp, fontWeight = FontWeight.Light)
    }
}
