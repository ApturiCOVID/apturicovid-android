package lv.spkc.apturicovid.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import lv.spkc.apturicovid.R

class LocationServicesNotificationManager {
    companion object {
        private const val LOCATION_SERVICES_NOTIFICATION_CHANNEL_ID = "lv.spkc.apturicovid.LOCATION_SERVICES_NOTIFICATION_CHANNEL_ID"
        private const val NO_LOCATION_SERVICES_NOTIFICATION_ID = 45950

        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    LOCATION_SERVICES_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.location_services_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.description = context.getString(R.string.location_services_notification_channel_description)
                val notificationManager =
                    context.getSystemService(
                        NotificationManager::class.java
                    )
                notificationManager?.createNotificationChannel(channel)
            }
        }

        fun showNotification(context: Context) {
            createNotificationChannel(context)
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val builder =
                NotificationCompat.Builder(context, LOCATION_SERVICES_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(context.getString(R.string.location_services_notification_title))
                    .setContentText(context.getString(R.string.location_services_notification_message))
                    .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.location_services_notification_message)))
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NO_LOCATION_SERVICES_NOTIFICATION_ID, builder.build())
        }

        fun hideNotificationIfPresent(context: Context) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(NO_LOCATION_SERVICES_NOTIFICATION_ID)
        }
    }
}