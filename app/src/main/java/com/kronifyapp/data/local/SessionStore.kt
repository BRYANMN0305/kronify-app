package com.kronifyapp.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Guarda los datos mínimos de la sesión autenticada para poder
 * consultar la API desde cualquier pantalla (por ejemplo, Perfil).
 */
@Singleton
class SessionStore @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences("kronify_session", Context.MODE_PRIVATE)

    val token: String?
        get() = preferences.getString(KEY_TOKEN, null)

    val userId: Long?
        get() = preferences.getLong(KEY_USER_ID, 0L).takeIf { it > 0L }

    val profileType: String?
        get() = preferences.getString(KEY_PROFILE_TYPE, null)

    val businessId: Long?
        get() = preferences.getLong(KEY_BUSINESS_ID, 0L).takeIf { it > 0L }

    val slug: String?
        get() = preferences.getString(KEY_SLUG, null)

    val email: String?
        get() = preferences.getString(KEY_EMAIL, null)

    val name: String?
        get() = preferences.getString(KEY_NAME, null)

    fun save(
        token: String,
        userId: Long,
        profileType: String,
        businessId: Long? = null,
        slug: String? = null,
        email: String? = null,
        name: String? = null,
    ) {
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_PROFILE_TYPE, profileType)
            .putLong(KEY_BUSINESS_ID, businessId ?: 0L)
            .putString(KEY_SLUG, slug)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, name)
            .apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val KEY_TOKEN = "access_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_PROFILE_TYPE = "profile_type"
        const val KEY_BUSINESS_ID = "business_id"
        const val KEY_SLUG = "slug"
        const val KEY_EMAIL = "email"
        const val KEY_NAME = "name"
    }
}
