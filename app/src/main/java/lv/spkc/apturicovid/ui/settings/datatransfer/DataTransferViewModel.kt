package lv.spkc.apturicovid.ui.settings.datatransfer

import android.app.Activity
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatusCodes
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.event.Event
import lv.spkc.apturicovid.network.ApiClient
import lv.spkc.apturicovid.network.Diagnosis
import lv.spkc.apturicovid.network.DiagnosisKey
import lv.spkc.apturicovid.network.UploadPinVerificationBody
import lv.spkc.apturicovid.utils.CovidCoroutineExceptionHandler
import lv.spkc.apturicovid.utils.DisplayableError
import timber.log.Timber
import javax.inject.Inject

class DataTransferViewModel @Inject constructor(
    private val apiClient: ApiClient,
    private val exposureNotificationClient: ExposureNotificationClient
): BaseViewModel() {
    private val _successEventLiveData = MutableLiveData<Event<Boolean>>()
    val successEventLiveData: LiveData<Event<Boolean>> = _successEventLiveData

    private val _validCodeEventLiveData = MutableLiveData<Event<Boolean>>()
    val validCodeEventLiveData: LiveData<Event<Boolean>> = _validCodeEventLiveData

    private val _resolveRequest = MutableLiveData<Event<Status>>()
    val resolveRequest: LiveData<Event<Status>> = _resolveRequest

    private var token: String? = null
    private var keys: List<TemporaryExposureKey>? = null

    fun submitCode(code: String) {
        viewModelScope.launch(CovidCoroutineExceptionHandler("Failed sending code") {
            _successEventLiveData.postValue(Event(false))
            _loadingLiveData.value = false
        }) {
            _loadingLiveData.value = true

            token = apiClient.verifyUploadKeys(UploadPinVerificationBody(code.toUpperCase())).token

            _validCodeEventLiveData.value = Event(true)

            Timber.v("Got token: $token")

            getKeys()
        }
    }

    private fun getKeys() {
        viewModelScope.launch(CovidCoroutineExceptionHandler("Failed retrieving codes") {
            if (it !is ApiException) {
                _loadingLiveData.value = false
                Timber.e(it, "Unknown error")
                return@CovidCoroutineExceptionHandler
            }
            when (it.statusCode) {
                ExposureNotificationStatusCodes.RESOLUTION_REQUIRED -> {
                    _resolveRequest.value = Event(it.status)
                }
                ExposureNotificationStatusCodes.DEVELOPER_ERROR -> {
                    _loadingLiveData.value = false
                    _errorEventLiveData.postValue(Event(DisplayableError(R.string.exposure_api_error_not_enabled, R.string.label_understand)))
                }
                else -> {
                    _loadingLiveData.value = false

                    Timber.e(it, "ApiException: ${it.status} ${it.statusCode}")
                }
            }
        }) {
            keys = exposureNotificationClient.temporaryExposureKeyHistory.await()

            Timber.v("Got keys: $keys")

            uploadKeys()
        }
    }

    private suspend fun uploadKeys() {
        val keys = keys ?: return
        val token = token ?: return

        val uploadResponse = apiClient.sendDiagnosisKeys(Diagnosis(token, keys.map {
            DiagnosisKey(
                Base64.encodeToString(it.keyData, Base64.NO_CLOSE).replace("\n", ""),
                it.rollingStartIntervalNumber,
                if (it.rollingPeriod > 0) it.rollingPeriod else null,
                it.transmissionRiskLevel
            )
        }))

        this.token = null
        this.keys = null

        Timber.v("Got upload response: $uploadResponse")

        _loadingLiveData.value = false
        _successEventLiveData.value = Event(true)
    }

    fun onResolutionComplete(resultCode: Int) {
        viewModelScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
            if (resultCode == Activity.RESULT_OK) {
                getKeys()
            }
        }
    }
}