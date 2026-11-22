package com.kronifyapp.ui.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.screens.profile.ProfileScreen
import com.kronifyapp.ui.screens.schedule.SchedulesScreen
import com.kronifyapp.ui.screens.services.ServicesScreen
import com.kronifyapp.ui.state.UserRole
import com.kronifyapp.ui.viewModel.RoleViewModel
import com.kronifyapp.ui.viewModel.SessionViewModel

private data class MainDestination(
    val label: String,
    val type: NavIcon,
    val title: String,
    val detail: String
)

private enum class NavIcon { AGENDA, SERVICES, HOURS, REVIEWS, PROFILE, MIS_CITAS }

private val ownerDestinations = listOf(
    MainDestination("Agenda", NavIcon.AGENDA, "Agenda", "Organiza tus citas y disponibilidad desde aquí."),
    MainDestination("Servicios", NavIcon.SERVICES, "Servicios", "Crea y administra los servicios que ofreces."),
    MainDestination("Horarios", NavIcon.HOURS, "Horarios", "Configura jornadas, descansos y excepciones."),
    MainDestination("Reseñas", NavIcon.REVIEWS, "Reseñas", "Consulta y responde las opiniones de tus clientes."),
    MainDestination("Perfil", NavIcon.PROFILE, "Perfil", "Gestiona tu negocio y preferencias de cuenta."),
)

private val employeeDestinations = listOf(
    MainDestination("Calendario", NavIcon.AGENDA, "Calendario", "Consulta las citas asignadas a tu agenda."),
    MainDestination("Horarios", NavIcon.HOURS, "Horarios", "Revisa y actualiza tus días de trabajo."),
    MainDestination("Perfil", NavIcon.PROFILE, "Perfil", "Tus datos, seguridad y preferencias."),
)

private val clientDestinations = listOf(
    MainDestination("Calendario", NavIcon.AGENDA, "Calendario", "Revisa tus próximas citas."),
    MainDestination("Mis citas", NavIcon.MIS_CITAS, "Mis citas", "Consulta y gestiona tus reservas."),
    MainDestination("Perfil", NavIcon.PROFILE, "Perfil", "Tus datos, seguridad y preferencias."),
)

private fun destinationsFor(role: UserRole): List<MainDestination> = when (role) {
    UserRole.OWNER -> ownerDestinations
    UserRole.EMPLOYEE -> employeeDestinations
    UserRole.CLIENT -> clientDestinations
    UserRole.LOADING -> emptyList()
}

@Composable
fun MainScreen(
    onOpenBusinessData: () -> Unit = {},
    onOpenPlan: () -> Unit = {},
    onOpenTeam: () -> Unit = {},
    onOpenSecurity: () -> Unit = {},
    onOpenPersonalData: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onLogout: () -> Unit = {},
    sessionViewModel: SessionViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    roleViewModel: RoleViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val role by roleViewModel.role.collectAsState()
    val items = remember(role) { destinationsFor(role) }

    if (role == UserRole.LOADING || items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = Neon)
        }
        return
    }

    LaunchedEffect(role, items.size) {
        if (selectedIndex !in items.indices) selectedIndex = 0
    }

    BackHandler(enabled = selectedIndex != 0) {
        selectedIndex = 0
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = selectedIndex,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (
                    fadeIn(tween(200)) +
                        slideInHorizontally(tween(240)) { width -> direction * width / 24 }
                    ).togetherWith(
                    fadeOut(tween(140)) +
                        slideOutHorizontally(tween(180)) { width -> -direction * width / 24 }
                )
            },
            label = "mainContent",
        ) { index ->
            MainContent(
                items[index],
                Modifier.fillMaxSize(),
                role = role,
                onOpenBusinessData = onOpenBusinessData,
                onOpenPlan = onOpenPlan,
                onOpenTeam = onOpenTeam,
                onOpenSecurity = onOpenSecurity,
                onOpenPersonalData = onOpenPersonalData,
                onOpenHelp = onOpenHelp,
                onOpenNotifications = onOpenNotifications,
                onOpenServices = {
                    val target = items.indexOfFirst { it.type == NavIcon.SERVICES }
                    if (target >= 0) selectedIndex = target
                },
                onOpenSchedules = {
                    val target = items.indexOfFirst { it.type == NavIcon.HOURS }
                    if (target >= 0) selectedIndex = target
                },
                onOpenMyAppointments = {
                    val target = items.indexOfFirst { it.type == NavIcon.MIS_CITAS }
                    if (target >= 0) selectedIndex = target
                },
                onLogout = {
                    sessionViewModel.clearSession()
                    onLogout()
                },
            )
        }
        OwnerBottomNav(
            items = items,
            selectedIndex = selectedIndex,
            onSelected = { selectedIndex = it },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}


@Composable
private fun OwnerBottomNav(
    items: List<MainDestination>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(43.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 30.dp)
            .shadow(14.dp, shape, ambientColor = Color.Black, spotColor = Neon.copy(alpha = .08f))
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xE01C3035), Color(0xE6081215))))
            .border(1.dp, Color(0x42D8F0F5), shape)
            .padding(horizontal = 7.dp, vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, destination ->
                val selected = index == selectedIndex
                val tint by animateColorAsState(
                    targetValue = if (selected) Neon else Color(0xFF90A4AA),
                    animationSpec = tween(180),
                    label = "navTint",
                )
                val labelColor by animateColorAsState(
                    targetValue = if (selected) Color(0xFFE5FBFF) else Color(0xFF8FA2A8),
                    animationSpec = tween(180),
                    label = "navLabel",
                )
                val itemBackground by animateColorAsState(
                    targetValue = if (selected) Color(0x203FE1FF) else Color.Transparent,
                    animationSpec = tween(200),
                    label = "navBackground",
                )
                val itemBorder by animateColorAsState(
                    targetValue = if (selected) Neon.copy(alpha = .35f) else Color.Transparent,
                    animationSpec = tween(200),
                    label = "navBorder",
                )
                val itemScale by animateFloatAsState(
                    targetValue = if (selected) 1.03f else 1f,
                    animationSpec = tween(180),
                    label = "navScale",
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = itemScale
                            scaleY = itemScale
                        }
                        .clip(RoundedCornerShape(22.dp))
                        .background(itemBackground, RoundedCornerShape(22.dp))
                        .border(1.dp, itemBorder, RoundedCornerShape(22.dp))
                        .clickable { onSelected(index) }
                        .padding(top = 6.dp, bottom = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    BottomNavIcon(destination.type, tint)
                    Text(
                        destination.label,
                        color = labelColor,
                        fontSize = 9.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavIcon(icon: NavIcon, tint: Color) {
    androidx.compose.foundation.Canvas(Modifier.size(20.dp)) {
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        when (icon) {
            NavIcon.AGENDA -> {
                drawRoundRect(
                    tint,
                    Offset(size.width * .12f, size.height * .2f),
                    Size(size.width * .76f, size.height * .68f),
                    androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    stroke
                )
                drawLine(
                    tint,
                    Offset(size.width * .12f, size.height * .43f),
                    Offset(size.width * .88f, size.height * .43f),
                    stroke.width
                )
                drawLine(
                    tint,
                    Offset(size.width * .31f, size.height * .1f),
                    Offset(size.width * .31f, size.height * .3f),
                    stroke.width,
                    StrokeCap.Round
                )
                drawLine(
                    tint,
                    Offset(size.width * .69f, size.height * .1f),
                    Offset(size.width * .69f, size.height * .3f),
                    stroke.width,
                    StrokeCap.Round
                )
                drawCircle(tint, size.width * .07f, Offset(size.width * .36f, size.height * .63f))
                drawCircle(tint, size.width * .07f, Offset(size.width * .63f, size.height * .63f))
            }

            NavIcon.SERVICES -> {
                drawLine(
                    tint,
                    Offset(size.width * .22f, size.height * .18f),
                    Offset(size.width * .78f, size.height * .82f),
                    stroke.width,
                    StrokeCap.Round
                )
                drawLine(
                    tint,
                    Offset(size.width * .78f, size.height * .18f),
                    Offset(size.width * .22f, size.height * .82f),
                    stroke.width,
                    StrokeCap.Round
                )
                drawCircle(
                    tint,
                    size.width * .11f,
                    Offset(size.width * .25f, size.height * .25f),
                    style = stroke
                )
                drawCircle(
                    tint,
                    size.width * .11f,
                    Offset(size.width * .25f, size.height * .75f),
                    style = stroke
                )
            }

            NavIcon.HOURS, NavIcon.MIS_CITAS -> {
                drawCircle(
                    tint,
                    size.width * .39f,
                    Offset(size.width / 2, size.height / 2),
                    style = stroke
                )
                drawLine(
                    tint,
                    Offset(size.width / 2, size.height * .29f),
                    Offset(size.width / 2, size.height * .52f),
                    stroke.width,
                    StrokeCap.Round
                )
                drawLine(
                    tint,
                    Offset(size.width / 2, size.height * .52f),
                    Offset(size.width * .65f, size.height * .62f),
                    stroke.width,
                    StrokeCap.Round
                )
            }

            NavIcon.REVIEWS -> {
                val star = Path().apply {
                    for (i in 0..9) {
                        val a = Math.toRadians((-90 + i * 36).toDouble());
                        val r = if (i % 2 == 0) size.width * .39f else size.width * .17f;
                        val p = Offset(
                            size.width / 2 + (kotlin.math.cos(a) * r).toFloat(),
                            size.height / 2 + (kotlin.math.sin(a) * r).toFloat()
                        ); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }; close()
                }
                drawPath(star, tint)
            }

            NavIcon.PROFILE -> {
                drawCircle(
                    tint,
                    size.width * .17f,
                    Offset(size.width / 2, size.height * .32f),
                    style = stroke
                )
                val shoulders = Path().apply {
                    moveTo(size.width * .18f, size.height * .86f)
                    quadraticTo(
                        size.width * .22f,
                        size.height * .57f,
                        size.width * .5f,
                        size.height * .57f
                    )
                    quadraticTo(
                        size.width * .78f,
                        size.height * .57f,
                        size.width * .82f,
                        size.height * .86f
                    )
                }
                drawPath(shoulders, tint, style = stroke)
            }
        }
    }
}

@Composable
private fun MainContent(
    destination: MainDestination,
    modifier: Modifier = Modifier,
    role: UserRole = UserRole.OWNER,
    onOpenBusinessData: () -> Unit = {},
    onOpenPlan: () -> Unit = {},
    onOpenTeam: () -> Unit = {},
    onOpenSecurity: () -> Unit = {},
    onOpenPersonalData: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenServices: () -> Unit = {},
    onOpenSchedules: () -> Unit = {},
    onOpenMyAppointments: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    if (destination.type == NavIcon.PROFILE) {
        Box(modifier = modifier) {
            ProfileScreen(
                role = role,
                onOpenBusinessData = onOpenBusinessData,
                onOpenServices = onOpenServices,
                onOpenSchedules = onOpenSchedules,
                onOpenMyAppointments = onOpenMyAppointments,
                onOpenPlan = onOpenPlan,
                onOpenTeam = onOpenTeam,
                onOpenSecurity = onOpenSecurity,
                onOpenPersonalData = onOpenPersonalData,
                onOpenHelp = onOpenHelp,
                onOpenNotifications = onOpenNotifications,
                onLogout = onLogout,
            )
        }
        return
    }

    if (destination.type == NavIcon.HOURS) {
        Box(modifier = modifier) { SchedulesScreen(onOpenTeam = onOpenTeam) }
        return
    }

    if (destination.type == NavIcon.SERVICES) {
        Box(modifier = modifier) { ServicesScreen() }
        return
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingValues(horizontal = 24.dp, vertical = 36.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            destination.title,
            color = TextPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            destination.detail,
            color = TextTertiary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}
