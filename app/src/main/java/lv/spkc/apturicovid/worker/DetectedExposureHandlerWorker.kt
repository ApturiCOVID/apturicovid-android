package lv.spkc.apturicovid.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import lv.spkc.apturicovid.BuildConfig
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.MainActivity
import lv.spkc.apturicovid.network.ExposureApiConfig
import lv.spkc.apturicovid.network.FileLoader
import lv.spkc.apturicovid.persistance.ExposureRepository
import lv.spkc.apturicovid.ui.settings.SettingsRepository
import lv.spkc.apturicovid.worker.util.BaseWorkerFactory
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject

class DetectedExposureHandlerWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val fileLoader: FileLoader,
    private val exposureRepository: ExposureRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParams) {
    companion object {
        private const val EXPOSURE_NOTIFICATION_CHANNEL_ID = "lv.spkc.apturicovid.EXPOSURE_NOTIFICATION_CHANNEL_ID"
        const val ACTION_LAUNCH_FROM_EXPOSURE_NOTIFICATION = "lv.spkc.apturicovid.ACTION_LAUNCH_FROM_EXPOSURE_NOTIFICATION"
        private const val TEMP_CONFIG_FILE_PREFIX = "exp_config"
    }

    class Factory @Inject constructor(private val fileLoader: FileLoader, private val settingsRepository: SettingsRepository, private val exposureRepository: ExposureRepository) : BaseWorkerFactory {
        override fun create(
            appContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return DetectedExposureHandlerWorker(
                appContext,
                params,
                fileLoader,
                exposureRepository,
                settingsRepository
            )
        }
    }

    override suspend fun doWork(): Result {
        Timber.d("Starting")
        val token = inputData.getString(ExposureNotificationClient.EXTRA_TOKEN)
        Timber.d("Starting detected exposure handling for token=$token")
        return if (token == null) {
            Timber.d("Token missing")
            Result.failure()
        } else {
            val exposureNotificationClient: ExposureNotificationClient = Nearby.getExposureNotificationClient(context)

            val apiExposureConfig = fileLoader.getJsonFromApiFile(context, BuildConfig.CONFIG_URL, TEMP_CONFIG_FILE_PREFIX)?.let {
                Gson().fromJson(it, ExposureApiConfig::class.java)
            }

            val exposureSummary = exposureNotificationClient.getExposureSummary(token).await()
            if (exposureSummary.matchedKeyCount > 0 && exposureSummary.maximumRiskScore >= apiExposureConfig?.minimumRiskScore ?: 0) {
                val exposureInformation = exposureNotificationClient.getExposureInformation(token).await()

                Timber.d("Exposure summary - ${exposureSummary.matchedKeyCount} matched keys and ${exposureSummary.summationRiskScore} summation risk score")
                val stringBuilder = StringBuilder()

                exposureInformation?.filterNotNull()?.forEach {
                    stringBuilder.append("Exposure summary - attenuation value = ${it.attenuationValue}")
                    stringBuilder.append(System.lineSeparator())

                    it.attenuationDurationsInMinutes.forEach { attenuationDuration ->
                        stringBuilder.append("Exposure summary - attenuation durations in minutes = $attenuationDuration")
                    }

                    stringBuilder.append(System.lineSeparator())
                    stringBuilder.append("Exposure summary - date (millis since epoch) = ${DateTime(it.dateMillisSinceEpoch).toDateTimeISO()}")
                    stringBuilder.append(System.lineSeparator())
                    stringBuilder.append("Exposure summary - duration minutes = ${DateTime(it.durationMinutes * 1L).toDateTimeISO()}")
                    stringBuilder.append(System.lineSeparator())
                    stringBuilder.append("Exposure summary - total risk score = ${it.totalRiskScore}")
                    stringBuilder.append(System.lineSeparator())
                    stringBuilder.append("Exposure summary - transmission risk level = ${it.transmissionRiskLevel}")
                    stringBuilder.append(System.lineSeparator())
                }

                stringBuilder.append("Exposure summary - config contents ${apiExposureConfig.toString()}")

                settingsRepository.debugingLogData = stringBuilder.toString()

                showNotification()

                exposureRepository.markTokenContainsExposure(token)

                settingsRepository.exposureToken?.let {
                    exposureRepository.sendExposureSummaries(it)
                }
            }

            Timber.d("Got a summary with ${exposureSummary.matchedKeyCount} keys and sum risk score ${exposureSummary.summationRiskScore}")

            Timber.d("Done")
            Result.success()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                EXPOSURE_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = context.getString(R.string.notification_channel_description)
            val notificationManager =
                context.getSystemService(
                    NotificationManager::class.java
                )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        createNotificationChannel()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.action = ACTION_LAUNCH_FROM_EXPOSURE_NOTIFICATION
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val builder =
            NotificationCompat.Builder(
                context,
                EXPOSURE_NOTIFICATION_CHANNEL_ID
            )
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_message))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.notification_message))
                )
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat
            .from(context)
        notificationManager.notify(0, builder.build())
    }
}