package com.kronifyapp

import android.app.Application
import com.kronifyapp.notifications.KronifyNotifications
import com.kronifyapp.notifications.NotificationScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KronifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KronifyNotifications.createChannel(this)
        NotificationScheduler.schedule(this)
        NotificationScheduler.runNow(this)
    }
}
