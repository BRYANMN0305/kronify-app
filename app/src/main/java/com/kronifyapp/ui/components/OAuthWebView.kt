package com.kronifyapp.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.kronifyapp.ui.theme.Acero1
import com.kronifyapp.ui.theme.Neon
import com.kronifyapp.ui.theme.StrokeSubtle
import com.kronifyapp.ui.theme.TextPrimary
import com.kronifyapp.ui.theme.TextSecondary
import com.kronifyapp.ui.theme.TextTertiary

/**
 * WebView interna para completar el flujo OAuth sin salir a la web.
 * Intercepta la redirección /oauth/callback y entrega el JWT a la app.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OAuthWebView(
    url: String,
    title: String,
    onSuccess: (token: String, newProfile: Boolean) -> Unit,
    onError: (message: String) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val currentOnSuccess by rememberUpdatedState(onSuccess)
    val currentOnError by rememberUpdatedState(onError)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val handled = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    fun deliverResult(rawUrl: String): Boolean {
        val isCallback = rawUrl.contains("/oauth/callback") || rawUrl.startsWith("kronifyapp://oauth/callback")
        val isOAuthError = rawUrl.contains("error=oauth")
        if (!isCallback && !isOAuthError) return false
        if (handled.value) return true
        handled.value = true

        val uri = Uri.parse(rawUrl)
        val token = uri.getQueryParameter("token")
        val error = uri.getQueryParameter("error")
        val newProfile = uri.getQueryParameter("newProfile") == "true"

        when {
            isOAuthError || !error.isNullOrBlank() ->
                currentOnError("No se pudo iniciar sesión con el proveedor. Inténtalo nuevamente.")
            token.isNullOrBlank() -> currentOnError("La respuesta del proveedor no incluyó una sesión válida.")
            else -> currentOnSuccess(token, newProfile)
        }
        return true
    }

    BackHandler { currentOnDismiss() }

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true
            // Google bloquea los WebView identificados como "wv"; usamos una
            // cadena de un navegador móvil estándar para completar el flujo.
            settings.userAgentString = settings.userAgentString
                .replace("; wv", "")
                .replace("Version/4.0 ", "")

            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val target = request?.url?.toString() ?: return false
                    return deliverResult(target)
                }

                @Suppress("DEPRECATION")
                override fun shouldOverrideUrlLoading(view: WebView?, targetUrl: String?): Boolean {
                    return deliverResult(targetUrl ?: return false)
                }

                override fun onPageStarted(view: WebView?, pageUrl: String?, favicon: Bitmap?) {
                    if (pageUrl != null && deliverResult(pageUrl)) {
                        view?.stopLoading()
                        return
                    }
                    isLoading = true
                    super.onPageStarted(view, pageUrl, favicon)
                }

                override fun onPageFinished(view: WebView?, pageUrl: String?) {
                    isLoading = false
                    super.onPageFinished(view, pageUrl)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?,
                ) {
                    if (!handled.value && request?.isForMainFrame == true) {
                        isLoading = false
                        currentOnError("No se pudo cargar la página del proveedor. Revisa tu conexión.")
                    }
                    super.onReceivedError(view, request, error)
                }
            }

            loadUrl(url)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Acero1)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x0DD5F0F7))
                    .border(1.dp, StrokeSubtle, CircleShape)
                    .clickable { currentOnDismiss() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✕",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Continúa sin salir de Kronify",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 1.dp),
                )
            }
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = Neon,
                trackColor = Color(0x1FD5F0F7),
            )
        }

        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )

        Text(
            text = "Conexión segura con el proveedor",
            color = TextSecondary,
            fontSize = 10.5.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
