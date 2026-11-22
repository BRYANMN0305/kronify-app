package com.kronifyapp.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingProgressStore @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences("onboarding_progress", Context.MODE_PRIVATE)

    fun stepFor(userId: Long): Int = preferences.getInt("business_$userId", 1)

    fun saveStep(userId: Long, step: Int) {
        preferences.edit().putInt("business_$userId", step).apply()
    }

    fun complete(userId: Long) = saveStep(userId, COMPLETE)

    companion object {
        const val COMPLETE = 4
    }
}
