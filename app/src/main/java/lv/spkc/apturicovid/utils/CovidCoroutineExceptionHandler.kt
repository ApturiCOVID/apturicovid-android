package lv.spkc.apturicovid.utils

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

open class CovidCoroutineExceptionHandler(private val errorMessage: String = "", private val onErrorCustomAction: ((throwable: Throwable) -> Unit)? = null) : CoroutineExceptionHandler {
    companion object {
        var onErrorAction: ((Throwable, String) -> Unit)? = null
    }

    override val key: CoroutineContext.Key<*>
        get() = CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        onErrorAction?.invoke(exception, errorMessage)
        onErrorCustomAction?.invoke(exception)
    }
}
