package lv.spkc.apturicovid.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.ui.settings.SettingsRepository
import lv.spkc.apturicovid.worker.util.BaseWorkerFactory
import org.joda.time.LocalDateTime
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BtMonitorWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParams) {
    companion object {
        const val BT_MONITOR_NOTIFICATION_CHANNEL_ID = "lv.spkc.apturicovid.BT_MONITOR_NOTIFICATION_CHANNEL_ID"
        const val START_HOURS_FROM_START_OF_THE_DAY = 9
        const val END_HOURS_FROM_START_OF_THE_DAY = 21

        private const val BT_UPDATE_INTERVAL = 12L
        private const val BT_MONITOR_TAG = "BT_monitor_worker_tag"

        fun scheduleWorkManager(context: Context) {
            Timber.d("Scheduling BT monitor")

            Timber.d("Cancelling existing exposure sync")

            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(BT_MONITOR_TAG)

            val btMonitor: PeriodicWorkRequest = PeriodicWorkRequest
                .Builder(
                    BtMonitorWorker::class.java,
                    BT_UPDATE_INTERVAL,
                    TimeUnit.HOURS
                )
                .addTag(BT_MONITOR_TAG)
                .build()

            WorkManager.getInstance(context).enqueue(btMonitor)
        }
    }

    class Factory @Inject constructor(private val settingsRepository: SettingsRepository) : BaseWorkerFactory {
        override fun create(
            appContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return BtMonitorWorker(
                appContext,
                params,
                settingsRepository
            )
        }
    }

    override suspend fun doWork(): Result {
        BluetoothAdapter.getDefaultAdapter()?.isEnabled?.apply {
            Timber.d("Bluetooth state is : $this")
            if (!this && settingsRepository.isTrackingStateNotificationsEnabled && isItTimeToShowNotification() && settingsRepository.isOnboardingFinished) {
                showNotification()
            }
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BT_MONITOR_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.bt_monitor_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = context.getString(R.string.bt_monitor_notification_channel_description)
            val notificationManager =
                context.getSystemService(
                    NotificationManager::class.java
                )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        createNotificationChannel()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder =
            NotificationCompat.Builder(
                context,
                BT_MONITOR_NOTIFICATION_CHANNEL_ID
            )
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.bt_monitor_notification_title))
                .setContentText(context.getString(R.string.bt_monitor_notification_message))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.bt_monitor_notification_message))
                )
                .setOngoing(true)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
        val notificationManager = NotificationManagerCompat
            .from(context)
        notificationManager.notify(0, builder.build())
    }

    private fun isItTimeToShowNotification(): Boolean {
        val now = LocalDateTime.now()
        val startOfTheAcceptedPeriod = now.withHourOfDay(START_HOURS_FROM_START_OF_THE_DAY)
        val endOfTheAcceptedPeriod = now.withHourOfDay(END_HOURS_FROM_START_OF_THE_DAY)

        return now.isAfter(startOfTheAcceptedPeriod) && now.isBefore(endOfTheAcceptedPeriod)
    }
}