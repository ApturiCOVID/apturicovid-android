package lv.spkc.apturicovid.network

import lv.spkc.apturicovid.getBody
import okhttp3.Interceptor
import timber.log.Timber
import java.io.IOException

class NetworkLoggingInterceptor @JvmOverloads constructor(private val customText: String = "INTERCEPTOR") :
    Interceptor {
    companion object {
        private const val NANOSECONDS_PER_MILLISECOND = 1e6
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        Timber.v(
            "%s --> %s",
            customText,
            originalRequest.url
        )
        val t1 = System.nanoTime()

        val response: okhttp3.Response
        try {
            response = chain.proceed(originalRequest)
        } catch (e: IOException) {
            val durationMillis = (System.nanoTime() - t1) / NANOSECONDS_PER_MILLISECOND
            Timber.v(
                "%s <-- %s exception in %.0fms (%s)",
                customText,
                originalRequest.url,
                durationMillis,
                e.localizedMessage
            )

            throw e
        }

        val durationMillis = (System.nanoTime() - t1) / NANOSECONDS_PER_MILLISECOND
        val contentLength = response.body?.contentLength() ?: -1

        Timber.v(
            "%s <-- %s%s (HTTP %d) %d bytes in %.0fms",
            customText,
            response.request.url,
            if (response.cacheResponse != null) " (cached)" else "",
            response.code,
            contentLength,
            durationMillis
        )

        if (response.code >= 400) {
            Timber.v("%s <-- %s", customText, response.getBody())
        }

        return response
    }
}