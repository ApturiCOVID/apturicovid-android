package lv.spkc.apturicovid.ui.sms

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.event.Event
import lv.spkc.apturicovid.ui.settings.SettingsRepository
import lv.spkc.apturicovid.utils.CovidCoroutineExceptionHandler
import javax.inject.Inject

class SmsRetrievalViewModel @Inject constructor(
    private val smsRepository: SmsRepository,
    private val settingsRepository: SettingsRepository
): BaseViewModel() {
    private val _codeVerificationEventLiveData = MutableLiveData<Event<Boolean>>()
    val codeVerificationEventLiveData = _codeVerificationEventLiveData

    fun sendNumber(number: String) {
        viewModelScope.launch(CovidCoroutineExceptionHandler("Failed sending phone number")) {
            settingsRepository.exposureToken = smsRepository.getVerificationToken(number)
        }
    }

    fun verifyCode(code: String) {
        viewModelScope.launch(CovidCoroutineExceptionHandler("Failed sending code verification") {
            _codeVerificationEventLiveData.value = Event(false)
            _loadingLiveData.value = false
        }) {
            val token = settingsRepository.exposureToken ?: return@launch

            _loadingLiveData.value = true
            _codeVerificationEventLiveData.value = Event(smsRepository.verifyPhone(code, token))
            _loadingLiveData.value = false
        }
    }
}