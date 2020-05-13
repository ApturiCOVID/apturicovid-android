package lv.spkc.apturicovid.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import lv.spkc.apturicovid.di.qualifier.ApplicationContext
import lv.spkc.apturicovid.persistance.SharedPreferenceManager
import lv.spkc.apturicovid.persistance.SharedPreferenceStorage
import javax.inject.Singleton

@Module
class SharedPreferenceModule {
    companion object {
        const val legacyPreferenceFileName = "settingsPreferences"
        const val preferenceFileName = "settingsPreferencesV2"
    }

    @Provides
    @Singleton
    fun provideSharedPreferenceStorage(@ApplicationContext context: Context): SharedPreferenceStorage {
        return SharedPreferenceStorage(context, preferenceFileName)
    }

    @Provides
    @Singleton
    fun provideSharedPreferenceManager(sharedPreferenceStorage: SharedPreferenceStorage): SharedPreferenceManager {
        return SharedPreferenceManager(sharedPreferenceStorage)
    }
}