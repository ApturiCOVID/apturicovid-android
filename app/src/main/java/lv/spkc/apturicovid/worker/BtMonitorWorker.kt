package lv.spkc.apturicovid.worker

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.work.*
import lv.spkc.apturicovid.ui.settings.SettingsRepository
import lv.spkc.apturicovid.utils.BtNotificationManager
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
        BluetoothAdapter.getDefaultAdapter()?.isEnabled?.let { isEnabled ->
            Timber.d("Bluetooth state is : $this")
            if (isEnabled) {
                BtNotificationManager.hideNotificationIfPresent(context)
            } else {
                if (settingsRepository.isTrackingStateNotificationsEnabled && isItTimeToShowNotification() && settingsRepository.isOnboardingFinished) {
                    BtNotificationManager.showNotification(context)
                }
            }
        }

        return Result.success()
    }

    private fun isItTimeToShowNotification(): Boolean {
        val now = LocalDateTime.now()
        val startOfTheAcceptedPeriod = now.withHourOfDay(START_HOURS_FROM_START_OF_THE_DAY)
        val endOfTheAcceptedPeriod = now.withHourOfDay(END_HOURS_FROM_START_OF_THE_DAY)

        return now.isAfter(startOfTheAcceptedPeriod) && now.isBefore(endOfTheAcceptedPeriod)
    }
}