package lv.spkc.apturicovid.worker

import android.content.Context
import androidx.work.*
import lv.spkc.apturicovid.persistance.ExposureRepository
import lv.spkc.apturicovid.worker.util.BaseWorkerFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DeleteExpiredDataWorker (
    context: Context,
    params: WorkerParameters,
    private var exposureRepository: ExposureRepository
) : CoroutineWorker(context, params) {
    class Factory @Inject constructor(private var exposureRepository: ExposureRepository) : BaseWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return DeleteExpiredDataWorker(
                appContext,
                params,
                exposureRepository
            )
        }
    }

    companion object {
        private const val SYNC_UPDATE_INTERVAL = 4L
        private const val SYNC_TAG = "DeleteExpiredDataWorker_worker_tag"

        fun scheduleWorkManager(context: Context) {
            val workManager = WorkManager.getInstance(context)

            Timber.d("Cancelling existing expired data delete jobs")
            workManager.cancelAllWorkByTag(SYNC_TAG)

            Timber.d("Scheduling new expired data delete job")
            val syncWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest
                .Builder(
                    DeleteExpiredDataWorker::class.java,
                    SYNC_UPDATE_INTERVAL,
                    TimeUnit.HOURS
                )
                .addTag(SYNC_TAG)
                .build()
            workManager.enqueue(syncWorkRequest)
        }
    }

    override suspend fun doWork(): Result {
        Timber.d("Delete expired data job start")

        exposureRepository.cleanExpiredData()

        Timber.d("Delete expired data job end")

        return Result.success()
    }
}
