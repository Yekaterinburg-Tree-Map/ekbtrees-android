package ru.ekbtrees.treemap

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import net.gotev.uploadservice.UploadServiceConfig

@HiltAndroidApp
class TreeMapApplication : Application() {

    companion object {
        private const val notificationChannelID = "TreeMapNotificationChannel"
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        UploadServiceConfig.initialize(
            context = this,
            debug = BuildConfig.DEBUG,
            defaultNotificationChannel = notificationChannelID
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                notificationChannelID,
                "Photo upload",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}