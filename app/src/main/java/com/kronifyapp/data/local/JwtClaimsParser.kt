package com.kronifyapp.data.local

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.kronifyapp.domain.model.JwtClaims

/** Decodifica el payload del JWT sin verificar la firma (el backend sigue siendo la autoridad). */
object JwtClaimsParser {
    private val gson = Gson()

    private data class RawClaims(
        @SerializedName("sub") val userId: String? = null,
        val email: String? = null,
        val name: String? = null,
        val lastName: String? = null,
        val profileType: String? = null,
        val businessId: Long? = null,
        val slug: String? = null,
        val exp: Long? = null,
    )

    fun parse(token: String): JwtClaims? {
        return runCatching {
            val payload = token.split('.').getOrNull(1) ?: return null
            val json = String(
                Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP),
                Charsets.UTF_8,
            )
            val raw = gson.fromJson(json, RawClaims::class.java)
            JwtClaims(
                userId = raw.userId?.toLongOrNull(),
                email = raw.email,
                name = raw.name,
                lastName = raw.lastName,
                profileType = raw.profileType,
                businessId = raw.businessId,
                slug = raw.slug,
                exp = raw.exp,
            )
        }.getOrNull()
    }
}
