package com.kronifyapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kronifyapp.notifications.NotificationScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kronifyapp.data.local.JwtClaimsParser
import com.kronifyapp.data.local.OAuthResult
import com.kronifyapp.data.local.OAuthResultBus
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.navigation.AppNavigation
import com.kronifyapp.navigation.Routes
import com.kronifyapp.ui.theme.Acero1
import com.kronifyapp.ui.theme.KronifyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionStore: SessionStore

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        val oauthCallback = handleOAuthIntent(intent)
        val token = sessionStore.token
        val claims = token?.let { JwtClaimsParser.parse(it) }
        val tokenValid = !token.isNullOrBlank() &&
            (claims?.exp?.let { it * 1000 > System.currentTimeMillis() } ?: true)
        val profileType = claims?.profileType ?: sessionStore.profileType
        val hasBusiness = claims?.businessId != null
        val startDestination = when {
            // Si la app se abrió desde el callback de OAuth hay que mostrar el
            // login para que LoginScreen consuma el token del deep link.
            oauthCallback -> Routes.LOGIN
            tokenValid && (profileType == "CLIENT" || hasBusiness) -> Routes.MAIN
            else -> Routes.INTRO
        }

        setContent {
            KronifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Acero1,
                ) {
                    AppNavigation(startDestination = startDestination)
                }
            }
        }

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (isActive) {
                    NotificationScheduler.runNow(this@MainActivity)
                    delay(30_000)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleOAuthIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        NotificationScheduler.runNow(this)
    }

    /**
     * Recibe el token que el backend devuelve al navegador con el esquema
     * `kronifyapp://oauth/callback`. Devuelve true cuando el intent era el
     * callback de OAuth.
     */
    private fun handleOAuthIntent(intent: Intent?): Boolean {
        val data: Uri = intent?.data ?: return false
        if (data.scheme != "kronifyapp" || data.host != "oauth") return false

        val error = data.getQueryParameter("error")
        val token = data.getQueryParameter("token")
        OAuthResultBus.publish(
            OAuthResult(
                token = token,
                newProfile = data.getQueryParameter("newProfile") == "true",
                isError = !error.isNullOrBlank() || token.isNullOrBlank(),
            ),
        )
        return true
    }
}
