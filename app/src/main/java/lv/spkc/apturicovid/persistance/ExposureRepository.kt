package lv.spkc.apturicovid.persistance

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import lv.spkc.apturicovid.di.qualifier.ApplicationContext
import lv.spkc.apturicovid.network.ApiClient
import lv.spkc.apturicovid.network.Exposure
import lv.spkc.apturicovid.network.ExposureSummary
import lv.spkc.apturicovid.ui.settings.SettingsRepository
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject

class ExposureRepository @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val exposureCheckTokenDao: ExposureCheckTokenDao,
    @ApplicationContext private val context: Context,
    private var apiClient: ApiClient
) {
    companion object {
        const val MILLIS_PER_SECOND = 1000
        const val SECONDS_PER_MINUTE = 60
    }

    fun getExposedTokenListLiveData() = exposureCheckTokenDao.getExposedLiveData()
    private fun getExposedNotSentTokenList() = exposureCheckTokenDao.getExposedNotSent()

    private val exposureNotificationClient = Nearby.getExposureNotificationClient(context)

    suspend fun cleanExpiredData() {
        exposureCheckTokenDao.deleteOlderThan(DateTime.now().minusDays(14).millis / MILLIS_PER_SECOND)
    }

    suspend fun markTokenContainsExposure(token: String) {
        exposureCheckTokenDao.findById(token)?.let { exposureCheckToken ->
            exposureCheckToken.containsExposure = true
            exposureCheckTokenDao.update(exposureCheckToken)
        }
    }

    suspend fun sendExposureSummaries(exposureToken: String) {
        Timber.d("sendExposureSummaries")
        Mutex().withLock {
            Timber.d("sendExposureSummaries in lock")
            val exposedTokens = getExposedNotSentTokenList()

            val summaries = exposedTokens.flatMap { exposureCheckToken ->
                exposureNotificationClient.getExposureInformation(exposureCheckToken.id).await()
            }.filter {
                it.transmissionRiskLevel > 0
            }

            if (summaries.isNullOrEmpty()) { return@withLock }

            sendSummariesToApi(summaries, exposureToken)

            exposedTokens.forEach { exposureCheckToken ->
                exposureCheckToken.isSummarySent = true
                exposureCheckTokenDao.update(exposureCheckToken)
            }

            Timber.d("sendExposureSummaries end lock")
        }
        Timber.d("sendExposureSummaries end")
    }

    private suspend fun sendSummariesToApi(summaries: List<ExposureInformation>, exposureToken: String) {
        val exposureSummary = ExposureSummary(
            exposureToken = exposureToken,
            isRelativeDevice = settingsRepository.phoneIsThirdParty,
            exposures = summaries.map {
                Exposure(
                    date = (it.dateMillisSinceEpoch / MILLIS_PER_SECOND),
                    duration = it.durationMinutes * SECONDS_PER_MINUTE,
                    attenuationValue = it.attenuationValue,
                    totalRiskScore = it.totalRiskScore,
                    transmissionRiskLevel = it.transmissionRiskLevel
                )
            }
        )

        apiClient.sendExposureSummaries(exposureSummary)
    }
}
