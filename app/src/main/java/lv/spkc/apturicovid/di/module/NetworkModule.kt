package lv.spkc.apturicovid.di.module

import android.content.Context
import com.google.gson.Gson
import com.moczul.ok2curl.CurlInterceptor
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import lv.spkc.apturicovid.BuildConfig
import lv.spkc.apturicovid.di.qualifier.ApplicationContext
import lv.spkc.apturicovid.network.ApiClient
import lv.spkc.apturicovid.network.NetworkLoggingInterceptor
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
class NetworkModule {
    companion object {
        private const val HTTP_CACHE_SIZE = 20 * 1024 * 1024L

        const val API_URL = "https://${BuildConfig.API_DOMAIN}/api/v1/"
        const val CONFIG_URL = "https://${BuildConfig.FILES_DOMAIN}/exposure_configurations/v1/android.json"
        const val INDEX_URL = "https://${BuildConfig.FILES_DOMAIN}/dkfs/v1/index.txt"
        const val STATS_URL = "https://${BuildConfig.FILES_DOMAIN}/stats/v1/covid-stats.json"
    }

    @Provides
    @Singleton
    fun provideOkHttp(cache: Cache, certificatePinner: CertificatePinner): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(CurlInterceptor {
                Timber.v(it)
            })
            .cache(cache)
            .addNetworkInterceptor(NetworkLoggingInterceptor("ApturiCovid-Network"))
            .followRedirects(false)
            .followSslRedirects(false)
            .certificatePinner(certificatePinner)
            .build()
    }

    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .add(BuildConfig.API_DOMAIN, *BuildConfig.API_SSL_PINS)
            .add(BuildConfig.FILES_DOMAIN, *BuildConfig.FILES_SSL_PINS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(okHttpClient)
    }

    @Provides
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheDirName = "apturicovid_okhttp_cache"

        val cacheDirectory = context.getDir(cacheDirName, Context.MODE_PRIVATE)
        return Cache(cacheDirectory, HTTP_CACHE_SIZE)
    }

    @Provides
    fun provideDataApiRetrofit(retrofitBuilder: Retrofit.Builder): Retrofit {
        return retrofitBuilder
            .baseUrl(API_URL)
            .build()
    }

    @Provides
    fun provideDataApi(retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }
}
