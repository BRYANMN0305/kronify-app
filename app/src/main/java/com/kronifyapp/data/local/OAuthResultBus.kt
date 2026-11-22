package com.kronifyapp.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Resultado del login OAuth que vuelve a la app por el deep link
 * `kronifyapp://oauth/callback`.
 *
 * Vive fuera de la Activity para no perder la respuesta si el sistema
 * recrea la app mientras el usuario completa el login en el navegador.
 */
data class OAuthResult(
    val token: String?,
    val newProfile: Boolean,
    val isError: Boolean,
)

object OAuthResultBus {
    private val _result = MutableStateFlow<OAuthResult?>(null)
    val result: StateFlow<OAuthResult?> = _result.asStateFlow()

    fun publish(value: OAuthResult) {
        _result.value = value
    }

    fun consume() {
        _result.value = null
    }
}
