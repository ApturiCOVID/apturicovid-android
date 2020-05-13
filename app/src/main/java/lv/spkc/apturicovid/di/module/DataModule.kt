package lv.spkc.apturicovid.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import lv.spkc.apturicovid.di.qualifier.ApplicationContext
import lv.spkc.apturicovid.persistance.ExposureCheckTokenDao
import lv.spkc.apturicovid.persistance.MainDatabase
import javax.inject.Singleton

@Module
object DataModule {
    @Provides
    @Singleton
    fun provideExposureCheckTokenDao(@ApplicationContext context: Context) : ExposureCheckTokenDao {
        return MainDatabase.getInstance(context).exposureCheckTokenDao()
    }
}