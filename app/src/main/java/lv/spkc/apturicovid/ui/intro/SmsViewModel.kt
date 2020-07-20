package lv.spkc.apturicovid.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.event.Event
import lv.spkc.apturicovid.model.ContactNumber
import org.joda.time.DateTime
import javax.inject.Inject

class SmsViewModel @Inject constructor(): BaseViewModel() {
    private val _smsCodeEventLiveData = MutableLiveData<Event<String>>()
    val smsCodeEventLiveData: LiveData<Event<String>> = _smsCodeEventLiveData

    private val _smsRetrievalEventLiveData = MutableLiveData<Event<Boolean>>()
    val smsRetrievalEventLiveData: LiveData<Event<Boolean>> = _smsRetrievalEventLiveData

    private var lastCodeRequestTime: Long = 0L

    private val _requestRemainingTimeShowMessagePairLiveData = MutableLiveData<Pair<Long, Boolean>>()
    val requestRemainingTimeShowMessagePairLiveData: LiveData<Pair<Long, Boolean>> = _requestRemainingTimeShowMessagePairLiveData

    var numberForVerification: ContactNumber? = null

    fun postCode(code: String?) {
        _smsCodeEventLiveData.value = Event(code ?: "")
    }

    fun startSmsRetrieval() {
        _smsRetrievalEventLiveData.value = Event(true)
    }

    fun startCodeRetrievalTimer(interval: Long = 1000, showMessage: Boolean = false) {
        val timeRemainingMillis = DateTime.now().millis - (lastCodeRequestTime)
        if (timeRemainingMillis < interval) {
            _requestRemainingTimeShowMessagePairLiveData.value = Pair(timeRemainingMillis - interval, showMessage)
        } else {
            lastCodeRequestTime = DateTime.now().millis
            _requestRemainingTimeShowMessagePairLiveData.value = Pair(0, showMessage)
        }
    }
}