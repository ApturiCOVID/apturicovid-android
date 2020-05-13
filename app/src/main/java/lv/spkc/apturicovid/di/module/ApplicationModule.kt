package lv.spkc.apturicovid.di.module

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import lv.spkc.apturicovid.StopCovidApplication
import lv.spkc.apturicovid.di.qualifier.ApplicationContext
import lv.spkc.apturicovid.network.SafetyNetService
import javax.inject.Singleton

@Module
object ApplicationModule {
    @Provides
    @JvmStatic
    @ApplicationContext
    fun provideContext(application: StopCovidApplication): Context {
        return application
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideExposureApiWrapper(@ApplicationContext context: Context): ExposureNotificationClient {
        return Nearby.getExposureNotificationClient(context)
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideSafetyNetService(@ApplicationContext context: Context): SafetyNetService {
        return SafetyNetService(context)
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }
}