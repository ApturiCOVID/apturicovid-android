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
        const val API_VERSION = "v1"
        private const val STAGING_API_PIN_1 = "sha256/psmaXsNEQMAcQikTDZYnDYdZTi2FspxEiLcwxoKXtEA="
        private const val STAGING_API_PIN_2 = "sha256/SVDc8R/k6PEHYdk/xtajSqqlO7yosaFygdu9qQb/eD8="
        private const val STAGING_API_PIN_3 = "sha256/kJSrlTzuvigY9oNV5DB1qzJLxZFU+Z2DRtOv6ra6LZ0="
        private const val STAGING_API_PIN_4 = "sha256/HD887onXp9ZkSz8QfqOzodXiJcGGmyQRRUa5fsdE9R0="

        private const val STAGING_FILES_PIN_1 = "sha256/qHG2qARLCPHRKfr0clCZwqTau+wLVJgEsnHDk7owVXs="
        private const val STAGING_FILES_PIN_2 = "sha256/m4WHKUfFpnDiMlfU4udGkHVy9Z9svzHrlOjCd/wT2A4="
        private const val STAGING_FILES_PIN_3 = "sha256/520BPZE5dS/bczUq6aU+9AqSc1O0L1jbHVR/QBccVfU="
        private const val STAGING_FILES_PIN_4 = "sha256/kNlTopCOsrEv9buAm9nBbWe4FjEn/QDaewi747PyW/4="

        private const val API_PIN_1 = "sha256/AD1268BbF/w+Hh5JXyPLDLVAdh67VIG2agjkdYjC/kI="
        private const val API_PIN_2 = "sha256/KhxI+AM5BFntDid1jc3tGAWOh/Qg8uG4ARPFF/QdXyg="
        private const val API_PIN_3 = "sha256/3LJ/K6jSPOrKBxIZSUFxI0dmVpKPGj0a5WVm7/RPh8w="
        private const val API_PIN_4 = "sha256/Mr5krFQTCZzw9aBSWDcRTiX6pD3qEpBytrjXwKt7/v8="

        private const val FILES_PIN_1 = "sha256/u4OJJH+SZhvigfdKR+e1F3/OmICvFTnUl5Fnv6myaHg="
        private const val FILES_PIN_2 = "sha256/OMEJX6dz1nbHkHom4UjDI6IV7MntvN2OyaV4xHsdRdA="
        private const val FILES_PIN_3 = "sha256/dgbAjpXEfPJsPkFbQx99pUUg7iXQgCsjSNTs1A/X8mI="
        private const val FILES_PIN_4 = "sha256/gVxo9q/ofaPErOFjlBfYGUlX4RPMYwFIcpCPKMWX3aM="

        private const val DOMAIN = "apturicovid-api.spkc.gov.lv"
        private const val DOMAIN_FILES = "apturicovid-files.spkc.gov.lv"
        private const val DOMAIN_STAGING = "apturicovid-staging-api.spkc.gov.lv"
        private const val DOMAIN_STAGING_FILES = "apturicovid-staging-files.spkc.gov.lv"
        private const val STAGING = "staging"
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
        return when(BuildConfig.FLAVOR) {
            STAGING -> {
                CertificatePinner.Builder()
                    .add(DOMAIN_STAGING, STAGING_API_PIN_1, STAGING_API_PIN_2, STAGING_API_PIN_3, STAGING_API_PIN_4)
                    .add(DOMAIN_STAGING_FILES, STAGING_FILES_PIN_1, STAGING_FILES_PIN_2, STAGING_FILES_PIN_3, STAGING_FILES_PIN_4)
                    .build()
            }
            else -> {
                CertificatePinner.Builder()
                    .add(DOMAIN, API_PIN_1, API_PIN_2, API_PIN_3, API_PIN_4)
                    .add(DOMAIN_FILES, FILES_PIN_1, FILES_PIN_2, FILES_PIN_3, FILES_PIN_4)
                    .build()
            }
        }
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
            .baseUrl(BuildConfig.API_URL)
            .build()
    }

    @Provides
    fun provideDataApi(retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }
}
