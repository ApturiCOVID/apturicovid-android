package lv.spkc.apturicovid.worker

import android.content.Context
import androidx.work.*
import lv.spkc.apturicovid.persistance.ExposureRepository
import lv.spkc.apturicovid.ui.settings.SettingsRepository
import lv.spkc.apturicovid.worker.util.BaseWorkerFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SummarySendingWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val exposureRepository: ExposureRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParams) {
    class Factory @Inject constructor(
        private val exposureRepository: ExposureRepository,
        private val settingsRepository: SettingsRepository
    ) : BaseWorkerFactory {
        override fun create(
            appContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return SummarySendingWorker(
                appContext,
                params,
                exposureRepository,
                settingsRepository
            )
        }
    }

    companion object {
        private const val SYNC_UPDATE_INTERVAL = 1L
        private const val SYNC_TAG = "Summary_worker_tag"

        fun scheduleWorkManager(context: Context) {
            Timber.d("Scheduling new summary sending worker")
            val syncWorkRequest = PeriodicWorkRequest
                .Builder(
                    SummarySendingWorker::class.java,
                    SYNC_UPDATE_INTERVAL,
                    TimeUnit.HOURS
                )
                .addTag(SYNC_TAG)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(SYNC_TAG, ExistingPeriodicWorkPolicy.REPLACE, syncWorkRequest)
        }
    }

    override suspend fun doWork(): Result {
        Timber.d("Starting")

        settingsRepository.exposureToken?.let {
            exposureRepository.sendExposureSummaries(it)
        }

        Timber.d("Done")
        return Result.success()
    }
}