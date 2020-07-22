package lv.spkc.apturicovid.activity.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatusCodes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.event.Event
import lv.spkc.apturicovid.utils.CovidCoroutineExceptionHandler
import lv.spkc.apturicovid.utils.DisplayableError
import timber.log.Timber
import javax.inject.Inject

enum class ExposureApiState {
    Disabled,
    Starting,
    Enabled
}

class ExposureViewModel @Inject constructor(private val exposureNotificationClient: ExposureNotificationClient) : BaseViewModel() {
    private val _exposureApiState = MutableLiveData<ExposureApiState>()
    val exposureApiState: LiveData<ExposureApiState> = _exposureApiState

    private val _resolveRequest = MutableLiveData<Event<Status>>()
    val resolveRequest: LiveData<Event<Status>> = _resolveRequest

    private val _exposureState = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            updateState()
        }
    }

    fun start() {
        viewModelScope.launch(CovidCoroutineExceptionHandler("Failed starting exposure notification client") {
            if (it !is ApiException) {
                Timber.e(it, "Unknown error")
                return@CovidCoroutineExceptionHandler
            }
            when (it.statusCode) {
                ExposureNotificationStatusCodes.RESOLUTION_REQUIRED -> {
                    _exposureApiState.postValue(ExposureApiState.Starting)
                    _resolveRequest.postValue(Event(it.status))
                }
                ExposureNotificationStatusCodes.API_NOT_CONNECTED -> {
                    _errorEventLiveData.postValue(Event(DisplayableError(R.string.exposure_api_error_unavailable, R.string.label_understand)))
                    _exposureApiState.postValue(ExposureApiState.Starting)
                }
                else -> {
                    Timber.e(it, "ApiException: ${it.status} ${it.statusCode}")
                }
            }
        }) {
            if (exposureNotificationClient.isEnabled.await() && _exposureState.value == false) {
                exposureNotificationClient.stop().await()
            }
            exposureNotificationClient.start().await()

            updateState()
        }
    }

    fun stop() {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (!exposureNotificationClient.isEnabled.await()) {
                return@launch
            }

            exposureNotificationClient.stop().await()

            updateState()
        }
    }

    fun onResolutionComplete(resultCode: Int) {
        viewModelScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
            if (resultCode == Activity.RESULT_OK) {
                start()
            } else {
                updateState()
            }
        }
    }

    private suspend fun updateState() {
        val isTracingEnabled = exposureNotificationClient.isEnabled.await()
        val areServicesOperational = _exposureState.value == true

        val state = if (isTracingEnabled && areServicesOperational) ExposureApiState.Enabled else ExposureApiState.Disabled
        _exposureApiState.value = state
    }

    suspend fun changeExposureState(enabled: Boolean) {
        _exposureState.value = enabled
        updateState()
    }
}