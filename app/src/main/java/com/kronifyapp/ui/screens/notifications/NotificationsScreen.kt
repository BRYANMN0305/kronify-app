package com.kronifyapp.ui.screens.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.kronifyapp.notifications.KronifyNotifications
import com.kronifyapp.ui.components.AuroraBackground
import com.kronifyapp.ui.components.KronifySwitch
import com.kronifyapp.ui.theme.Amber
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.OkGreen
import com.kronifyapp.ui.theme.StrokeStrong
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.SurfaceDeep
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary
import com.kronifyapp.ui.viewModel.NotificationsViewModel

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(KronifyNotifications.canPost(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionGranted = granted
        if (granted) viewModel.setEnabled(true)
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
                            text = "TU CUENTA",
                            color = TextTertiary,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            text = "Notificaciones",
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
                    text = "Recibe avisos en tu teléfono cuando algo cambie en tu agenda.",
                    color = TextTertiary,
                    fontSize = 12.5.sp,
                    modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
                )
            }

            item {
                val shape = RoundedCornerShape(22.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(SurfaceDeep.copy(alpha = .73f))
                        .border(1.dp, StrokeStrong, shape)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Notificaciones de la app",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = if (permissionGranted) {
                                "Activadas en este dispositivo"
                            } else {
                                "Necesitas permitir las notificaciones"
                            },
                            color = if (permissionGranted) OkGreen else Amber,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 3.dp),
                        )
                    }
                    KronifySwitch(
                        checked = state.enabled && permissionGranted,
                        onCheckedChange = { value ->
                            if (value && !permissionGranted) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                viewModel.setEnabled(value)
                            }
                        },
                        enabled = permissionGranted,
                    )
                }
            }

            if (!permissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                item {
                    Spacer(Modifier.height(10.dp))
                    val shape = RoundedCornerShape(14.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(Color(0x26FFC75B))
                            .border(1.dp, Amber.copy(alpha = .4f), shape)
                            .clickable {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Permitir notificaciones",
                            color = Amber,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(22.dp))
                Text(
                    text = "QUÉ TE VAMOS A AVISAR",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 5.dp, bottom = 9.dp),
                )
            }

            item {
                val shape = RoundedCornerShape(22.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(SurfaceDeep.copy(alpha = .73f))
                        .border(1.dp, StrokeStrong, shape)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(11.dp),
                ) {
                    NotificationInfoRow("Nueva cita", "Cuando entra una cita nueva en tu agenda.")
                    NotificationInfoRow("Recordatorio", "24 horas antes de una cita confirmada.")
                    NotificationInfoRow("Cambios", "Cuando una cita se cancela o se reprograma.")
                    NotificationInfoRow("Confirmaciones", "Cuando una cita pendiente queda confirmada.")
                }
            }

            item {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Los correos de confirmación del servidor siguen funcionando. Estas notificaciones son adicionales y se muestran en el teléfono.",
                    color = TextTertiary,
                    fontSize = 10.5.sp,
                    lineHeight = 15.sp,
                )
            }
        }
    }
}

@Composable
private fun NotificationInfoRow(title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Neon),
        )
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = TextTertiary,
                fontSize = 10.5.sp,
                modifier = Modifier.padding(top = 1.dp),
            )
        }
    }
}
