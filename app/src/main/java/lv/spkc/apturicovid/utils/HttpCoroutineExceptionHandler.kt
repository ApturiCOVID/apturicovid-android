package lv.spkc.apturicovid.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineExceptionHandler
import retrofit2.HttpException
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

open class HttpCoroutineExceptionHandler(
        private val onErrorsReceived: (ApiError) -> Unit
) : CoroutineExceptionHandler {
    companion object {
        var onErrorAction: ((Throwable, String) -> Unit)? = null
    }

    override val key: CoroutineContext.Key<*>
        get() = CoroutineExceptionHandler

    private fun handleError(exception: HttpException) {
        exception.response()?.errorBody()?.apply {
            val errorBody = string()
            if (errorBody.isNotEmpty()) {
                val error: ApiError = try {
                    val gson = GsonBuilder().create()
                    val parser = JsonParser()
                    val json = parser.parse(errorBody)
                    val errors = gson.fromJson<List<ApiError>>(
                        json.asJsonObject.get("error_messages")?.asJsonArray, object : TypeToken<List<ApiError>>() {}.type
                    )

                    errors[0].apply {
                        httpCode = exception.code()
                    }
                } catch (e: Exception) {
                    Timber.e(e)

                    ApiError("Unknown error type here!")
                }

                onErrorAction?.invoke(exception, error.getErrorMessage())
                onErrorsReceived.invoke(error)

                Timber.e(error.getErrorMessage())
            }
        }
    }

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        Timber.e(exception, "HttpCoroutineHandler exception")
        if (exception !is HttpException) return

        handleError(exception)
    }
}
