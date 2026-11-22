package com.kronifyapp.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Preferencias locales y memoria de notificaciones de la app.
 * No reemplaza los correos del backend: controla solo los avisos del dispositivo.
 */
@Singleton
class NotificationStore @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences("kronify_notifications", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val mapType = object : TypeToken<Map<Long, String>>() {}.type

    var enabled: Boolean
        get() = preferences.getBoolean(KEY_ENABLED, true)
        set(value) {
            preferences.edit().putBoolean(KEY_ENABLED, value).apply()
        }

    fun knownAppointments(): Map<Long, String> {
        val json = preferences.getString(KEY_KNOWN, null) ?: return emptyMap()
        return runCatching { gson.fromJson<Map<Long, String>>(json, mapType) }.getOrDefault(emptyMap())
    }

    fun saveKnownAppointments(appointments: Map<Long, String>) {
        preferences.edit().putString(KEY_KNOWN, gson.toJson(appointments)).apply()
    }

    fun remindedAppointments(): Set<Long> {
        val values = preferences.getStringSet(KEY_REMINDED, emptySet()).orEmpty()
        return values.mapNotNull { it.toLongOrNull() }.toSet()
    }

    fun markReminded(appointmentId: Long) {
        val current = preferences.getStringSet(KEY_REMINDED, emptySet()).orEmpty().toMutableSet()
        current.add(appointmentId.toString())
        preferences.edit().putStringSet(KEY_REMINDED, current).apply()
    }

    fun clearHistory() {
        preferences.edit()
            .remove(KEY_KNOWN)
            .remove(KEY_REMINDED)
            .apply()
    }

    var lastCheckAt: Long
        get() = preferences.getLong(KEY_LAST_CHECK, 0L)
        set(value) {
            preferences.edit().putLong(KEY_LAST_CHECK, value).apply()
        }

    private companion object {
        const val KEY_ENABLED = "enabled"
        const val KEY_KNOWN = "known_appointments"
        const val KEY_REMINDED = "reminded_appointments"
        const val KEY_LAST_CHECK = "last_check_at"
    }
}
