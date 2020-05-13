package lv.spkc.apturicovid.network

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class FileLoader @Inject constructor(private var okHttpClient: OkHttpClient) {
    companion object {
        private const val FILE_SUFFIX = "bin"
    }

    /**
     * Get json file content from url.
     * The file is deleted when VM terminates.
     *
     * @param context Context.
     * @param fileUrl URL to server-based-file.
     * @param filePrefix distinct prefix to handle multiple file downloads.
     *
     * @return  Json content as String.
     */
    suspend fun getJsonFromApiFile(context: Context, fileUrl: String, filePrefix: String): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(fileUrl)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val outputFile: File = File.createTempFile(filePrefix, FILE_SUFFIX, context.cacheDir)

                outputFile.sink().buffer().use { sink ->
                    response.body?.source()?.let {
                        try {
                            sink.writeAll(it)
                        } catch (ex: Exception) {
                            Timber.e(ex, "Couldn't read ($filePrefix)!")
                        }
                    } ?: run {
                        Timber.i("File ($filePrefix) is empty!}")
                        return@withContext null
                    }
                }

                val json = outputFile.readText()
                outputFile.deleteOnExit()
                Timber.i("File ($filePrefix) - download successful!}")
                return@withContext json
            } else {
                Timber.e("File ($filePrefix) - download failed! ${response.code} $${response.message}")
                return@withContext null
            }
        }
    }
}