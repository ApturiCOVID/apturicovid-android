package lv.spkc.apturicovid.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.event.Event
import lv.spkc.apturicovid.model.ContactNumber
import lv.spkc.apturicovid.persistance.SharedPreferenceManager
import org.joda.time.DateTime
import javax.inject.Inject

class SmsViewModel @Inject constructor(
    private val sharedPreferenceManager: SharedPreferenceManager
): BaseViewModel() {
    companion object {
        const val MILLIS_IN_SECOND = 1000L
        const val MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60
    }

    private val _smsCodeEventLiveData = MutableLiveData<Event<String>>()
    val smsCodeEventLiveData: LiveData<Event<String>> = _smsCodeEventLiveData

    private val _smsRetrievalEventLiveData = MutableLiveData<Event<Boolean>>()
    val smsRetrievalEventLiveData: LiveData<Event<Boolean>> = _smsRetrievalEventLiveData

    private val _smsRemainingRequestTimeLiveData = MutableLiveData<Event<Long>>()
    val smsRemainingRequestTimeLiveData: LiveData<Event<Long>> = _smsRemainingRequestTimeLiveData

    private val _sendSmsAndShowMessageLiveData = MutableLiveData<Event<Boolean>>()
    val sendSmsAndShowMessageLiveData: LiveData<Event<Boolean>> = _sendSmsAndShowMessageLiveData
    // The Boolean Event indicates whether to show a Toast popup in SmsRetrievalFragment or not

    var numberForVerification: ContactNumber? = null

    fun postCode(code: String?) {
        _smsCodeEventLiveData.value = Event(code ?: "")
    }

    fun startSmsRetrieval() {
        _smsRetrievalEventLiveData.value = Event(true)
    }

    fun processSmsRequest() {
        val timeRemainingMillis = DateTime.now().millis - sharedPreferenceManager.lastSmsCodeRequestTime
        if (timeRemainingMillis >= MILLIS_IN_MINUTE) {
            _sendSmsAndShowMessageLiveData.value = Event(false)
        }
    }

    fun processSmsRequestAndLogTimeIfNeeded() {
        val timeRemainingMillis = DateTime.now().millis - sharedPreferenceManager.lastSmsCodeRequestTime
        if (timeRemainingMillis < MILLIS_IN_MINUTE) {
            val remainingTime = (MILLIS_IN_MINUTE - timeRemainingMillis) / MILLIS_IN_SECOND
            _smsRemainingRequestTimeLiveData.value = Event(remainingTime)
        } else {
            sharedPreferenceManager.lastSmsCodeRequestTime = DateTime.now().millis
            _sendSmsAndShowMessageLiveData.value = Event(true)
        }
    }
}