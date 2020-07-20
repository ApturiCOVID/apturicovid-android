package lv.spkc.apturicovid.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import lv.spkc.apturicovid.event.Event
import lv.spkc.apturicovid.utils.CovidCoroutineExceptionHandler
import lv.spkc.apturicovid.utils.HttpCoroutineExceptionHandler
import retrofit2.HttpException

open class BaseViewModel : ViewModel() {
    protected val _loadingLiveData = MutableLiveData<Boolean>()
    open val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    protected val _errorEventLiveData = MutableLiveData<Event<Throwable>>()
    val errorEventLiveData: LiveData<Event<Throwable>> = _errorEventLiveData

    private val genericExceptionHandler = CovidCoroutineExceptionHandler("Got error")

    private val httpExceptionHandler = HttpCoroutineExceptionHandler {
        _errorEventLiveData.postValue(Event(it))
    }

    val coroutineExceptionHandler = CoroutineExceptionHandler { context, exception ->
        _loadingLiveData.postValue(false)

        when (exception) {
            is HttpException -> httpExceptionHandler.handleException(context, exception)
            else -> genericExceptionHandler.handleException(context, exception)
        }
    }

    fun showErrorDialog(throwable: Throwable) {
        _errorEventLiveData.postValue(Event(throwable))
    }

    fun CoroutineExceptionHandler.withAction(alsoOnErrorDo: ((Throwable) -> Unit)) = CoroutineExceptionHandler { context, throwable ->
        this.handleException(context, throwable)
        alsoOnErrorDo.invoke(throwable)
    }
}