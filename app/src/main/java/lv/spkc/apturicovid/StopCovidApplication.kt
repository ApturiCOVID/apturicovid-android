package lv.spkc.apturicovid

import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import lv.spkc.apturicovid.di.component.DaggerAppComponent
import lv.spkc.apturicovid.di.module.SharedPreferenceModule
import lv.spkc.apturicovid.persistance.LegacySharedPreferenceStorage
import lv.spkc.apturicovid.persistance.SharedPreferenceManager
import lv.spkc.apturicovid.persistance.SharedPreferenceStorage
import lv.spkc.apturicovid.utils.CovidCoroutineExceptionHandler
import lv.spkc.apturicovid.utils.CrashlyticsLogTree
import lv.spkc.apturicovid.utils.HttpCoroutineExceptionHandler
import lv.spkc.apturicovid.worker.BtMonitorWorker
import lv.spkc.apturicovid.worker.DeleteExpiredDataWorker
import lv.spkc.apturicovid.worker.ExposureKeyFetchWorker
import lv.spkc.apturicovid.worker.SummarySendingWorker
import lv.spkc.apturicovid.worker.util.DaggerWorkerFactory
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import javax.inject.Inject

class StopCovidApplication: DaggerApplication() {
    companion object;

    private val onErrorAction: ((Throwable, String) -> Unit)? = { throwable, message ->
        Timber.e(throwable, "$message")
        firebaseCrashlytics.recordException(throwable)
    }

    @Inject
    lateinit var daggerWorkerFactory: DaggerWorkerFactory

    @Inject
    lateinit var firebaseCrashlytics: FirebaseCrashlytics

    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(this)

        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(daggerWorkerFactory).build())

        setupLogging()

        migrateSharedPreferencesIfNeeded()

        ExposureKeyFetchWorker.scheduleWorkManager(this)
        BtMonitorWorker.scheduleWorkManager(this)
        DeleteExpiredDataWorker.scheduleWorkManager(this)
        SummarySendingWorker.scheduleWorkManager(this)

        WorkManager.getInstance(this).pruneWork()
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.plant(CrashlyticsLogTree())

        CovidCoroutineExceptionHandler.onErrorAction = onErrorAction
        HttpCoroutineExceptionHandler.onErrorAction = onErrorAction
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }

    private fun migrateSharedPreferencesIfNeeded() {
        val newStorage = SharedPreferenceStorage(this, SharedPreferenceModule.preferenceFileName)
        val legacyStorage = LegacySharedPreferenceStorage(this, SharedPreferenceModule.legacyPreferenceFileName)

        val newSharedPreferenceManager = SharedPreferenceManager(newStorage)
        val legacySharedPreferenceManager = SharedPreferenceManager(legacyStorage)

        Timber.d("Is migrated: ${newSharedPreferenceManager.migratedToV2Storage}")
        if (!newSharedPreferenceManager.migratedToV2Storage) {
            Timber.d("Migrating")
            newSharedPreferenceManager.language = legacySharedPreferenceManager.language
            newSharedPreferenceManager.isOnboardingFinished = legacySharedPreferenceManager.isOnboardingFinished
            newSharedPreferenceManager.phone = legacySharedPreferenceManager.phone
            newSharedPreferenceManager.exposureToken = legacySharedPreferenceManager.exposureToken
            newSharedPreferenceManager.isThirdPartyNumber = legacySharedPreferenceManager.isThirdPartyNumber
            newSharedPreferenceManager.isTrackingNotificationsEnabled = legacySharedPreferenceManager.isTrackingNotificationsEnabled
            newSharedPreferenceManager.debuggingData = legacySharedPreferenceManager.debuggingData

            newSharedPreferenceManager.migratedToV2Storage = true

            legacyStorage.purge()
        }
        Timber.d("Migrating DONE")
    }
}
