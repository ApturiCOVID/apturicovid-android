package lv.spkc.apturicovid.worker

import android.content.Context
import androidx.work.*
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lv.spkc.apturicovid.di.module.NetworkModule
import lv.spkc.apturicovid.network.ExposureApiConfig
import lv.spkc.apturicovid.network.FileLoader
import lv.spkc.apturicovid.persistance.ExposureCheckTokenDao
import lv.spkc.apturicovid.persistance.model.ExposureCheckToken
import lv.spkc.apturicovid.worker.util.BaseWorkerFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.buffer
import okio.sink
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ExposureKeyFetchWorker @Inject constructor(
    private val context: Context,
    params: WorkerParameters,
    private var okHttpClient: OkHttpClient,
    private var exposureCheckTokenDao: ExposureCheckTokenDao,
    private val fileLoader: FileLoader
) : CoroutineWorker(context, params) {
    class Factory @Inject constructor(
        private var okHttpClient: OkHttpClient,
        private var exposureCheckTokenDao: ExposureCheckTokenDao,
        private val fileLoader: FileLoader
    ) : BaseWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return ExposureKeyFetchWorker(
                appContext,
                params,
                okHttpClient,
                exposureCheckTokenDao,
                fileLoader
            )
        }
    }

    companion object {
        private const val SYNC_UPDATE_INTERVAL = 1L
        private const val SYNC_TAG = "Sync_worker_tag"
        private const val TEMP_CONFIG_FILE_PREFIX = "exp_config"

        fun scheduleWorkManager(context: Context) {
            Timber.d("Scheduling new exposure sync")
            val syncWorkRequest = PeriodicWorkRequest
                .Builder(
                    ExposureKeyFetchWorker::class.java,
                    SYNC_UPDATE_INTERVAL,
                    TimeUnit.HOURS
                )
                .addTag(SYNC_TAG)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(SYNC_TAG, ExistingPeriodicWorkPolicy.REPLACE, syncWorkRequest)
        }
    }

    private val exposureNotificationClient: ExposureNotificationClient = Nearby.getExposureNotificationClient(context)
    private val fallbackConfig = ExposureConfiguration.ExposureConfigurationBuilder()
        .setMinimumRiskScore(383)
        .setTransmissionRiskScores(1, 1, 1, 1, 1, 1, 1, 1)
        .setDurationScores(1, 1, 5, 6, 8, 8, 8, 8)
        .setDaysSinceLastExposureScores(1, 3, 8, 8, 8, 8, 8, 8)
        .setAttenuationScores(1, 1, 1, 3, 8, 8, 8, 8)
        .build()

    override suspend fun doWork(): Result {
        Timber.d("Starting exposure key syncing from the server")
        return withContext(Dispatchers.IO) {
            try {
                storeExposureCheckTokens()

                val apiExposureConfig = fileLoader.getJsonFromApiFile(context, NetworkModule.CONFIG_URL, TEMP_CONFIG_FILE_PREFIX)?.let {
                    Gson().fromJson(it, ExposureApiConfig::class.java)
                }

                runDiagnosis(apiExposureConfig?.mapToExposureConfiguration() ?: fallbackConfig)
            } catch (exception: Exception) {
                Result.retry()
                Timber.d("Error")
                return@withContext Result.retry()
            }

            Timber.d("Done")
            withContext(Dispatchers.Main) {
                Result.success()
            }
        }
    }

    private suspend fun storeExposureCheckTokens() {
        withContext(Dispatchers.IO) {
            Timber.d("Fetching index ${NetworkModule.INDEX_URL}")
            val request = Request.Builder().url(NetworkModule.INDEX_URL).build()
            val response = okHttpClient.newCall(request).execute()

            Timber.d("Got response")
            if (!response.isSuccessful) {
                return@withContext
            }
            val responseBody = response.body?.string() ?: return@withContext

            Timber.d("Got response body")

            val newFiles = responseBody.split("\n")
                .filter { exposureCheckTokenDao.findById(it) == null } // Keep only new entries ...

            Timber.d("Got ${newFiles.count()} new files")

            newFiles
                .map { ExposureCheckToken(it, containsExposure = false, isChecked = false) }
                .forEach {
                    if (it.id.isNotEmpty()) {
                        exposureCheckTokenDao.insert(it)// ... and save to local database
                    }
                }

            Timber.d("Done fetching index")
        }
    }

    private suspend fun runDiagnosis(config: ExposureConfiguration) {
        Timber.d("Reading unchecked items in DB")
        val unchecked = exposureCheckTokenDao.getUnchecked()

        Timber.d("Got ${unchecked.count()} items")

        unchecked.map { token ->
            if (token.id.isEmpty()) return@map null

            getBlob(token.id)?.let { file ->
                return@map Pair(file, token)
            }
        }
            .filterNotNull()
            .forEach {
                Timber.d("Feeding ${it.second.id} into exposureNotificationClient for diagnosis")
                val exposureCheckToken = it.second
                exposureNotificationClient.provideDiagnosisKeys(listOf(it.first), config, exposureCheckToken.id)

                exposureCheckToken.isChecked = true

                exposureCheckTokenDao.update(exposureCheckToken)
            }
        Timber.d("Done checking")
    }

    private suspend fun getBlob(url: String): File? {
        Timber.d("Reading file $url")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(url)
                .build()

            val response = okHttpClient.newCall(request).execute()

            Timber.d("Got response")
            if (!response.isSuccessful) {
                return@withContext null
            }
            val responseBody = response.body?.source() ?: return@withContext null

            val outputFile: File = File.createTempFile("exposure", "bin", context.cacheDir)
            val sink: BufferedSink = outputFile.sink().buffer()
            sink.writeAll(responseBody)
            sink.close()

            Timber.d("Wrote file to disk")

            outputFile
        }
    }

    private fun ExposureApiConfig.mapToExposureConfiguration() = ExposureConfiguration.ExposureConfigurationBuilder()
            .setMinimumRiskScore(minimumRiskScore)
            .setTransmissionRiskScores(*transmissionRiskScores)
            .setDurationScores(*durationScores)
            .setDaysSinceLastExposureScores(*daysSinceLastExposureScores)
            .setAttenuationScores(*attenuationScores)
            .setDurationAtAttenuationThresholds(*attenuationThreshold)
            .build()
}
