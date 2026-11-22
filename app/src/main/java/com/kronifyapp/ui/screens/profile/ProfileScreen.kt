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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.kronifyapp.ui.components.ExternalLinkGlyph
import com.kronifyapp.ui.components.GlobeGlyph
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.components.AuroraBackground

private data class ProfileOption(val icon: String, val title: String, val subtitle: String, val value: String? = null, val accent: Boolean = false)

@Composable
fun ProfileScreen() {
    val businessOptions = listOf(
        ProfileOption("⚙", "Datos del negocio", "Nombre, dirección y categoría"),
        ProfileOption("♧", "Equipo", "2 de 3 plazas ocupadas"),
        ProfileOption("✂", "Servicios y precios", "5 servicios · 4 activos"),
        ProfileOption("◷", "Horarios", "Lun–Vie 09:00–20:00 · Sáb 10:00–14:00"),
    )
    val accountOptions = listOf(
        ProfileOption("▣", "Plan y facturación", "76 de 80 citas este mes", "Free", true),
        ProfileOption("♧", "Notificaciones", "Email y recordatorios", "Activadas"),
        ProfileOption("♙", "Seguridad", "Contraseña y sesiones abiertas"),
        ProfileOption("?", "Ayuda", ""),
    )

    AuroraBackground {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 58.dp, bottom = 28.dp),
        ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("TU CUENTA", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("Perfil", color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
            }
        }

        Spacer(Modifier.height(14.dp))
        ProfileSummary()
        Spacer(Modifier.height(14.dp))
        PublicLink()
        Spacer(Modifier.height(26.dp))
        SectionTitle("NEGOCIO")
        Spacer(Modifier.height(10.dp))
        OptionsCard(businessOptions)
        Spacer(Modifier.height(25.dp))
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
                .clickable { }
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
private fun ProfileSummary() {
    val shape = RoundedCornerShape(29.dp)
    Column(
        modifier = Modifier.fillMaxWidth().background(Color(0x9A10262B), shape).border(1.dp, StrokeStrong, shape).padding(19.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(68.dp).background(Color(0xFF696251), CircleShape).border(1.dp, Color(0xFF9E9478), CircleShape), contentAlignment = Alignment.Center) {
                Text("MR", color = Color(0xFFFFE9BD), fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
            }
            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                Text("Mateo Ríos", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Nova Barber Studio · Barbería", color = TextTertiary, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
            }
            Text("Plan Free", color = Color(0xFFC8F7FF), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(Color(0x263FE1FF), RoundedCornerShape(18.dp)).border(1.dp, Neon.copy(alpha = .5f), RoundedCornerShape(18.dp)).padding(horizontal = 11.dp, vertical = 7.dp))
        }
        Spacer(Modifier.height(17.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(StrokeSubtle))
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Metric("4,8", "Valoración", Modifier.weight(1f))
            Box(Modifier.size(width = 1.dp, height = 42.dp).background(StrokeSubtle))
            Metric("76", "Citas / mes", Modifier.weight(1f))
        }
    }
}

@Composable private fun Metric(value: String, label: String, modifier: Modifier = Modifier) = Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    Text(label, color = TextTertiary, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
}

@Composable
private fun PublicLink() {
    val context = LocalContext.current
    val url = "https://kronifytech.vercel.app/negocio/nova-barber-studio"
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
        Text("kronifytech.vercel.app/negocio/nova-barber-studio", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(start = 13.dp))
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
        modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 16.dp, vertical = 15.dp),
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
