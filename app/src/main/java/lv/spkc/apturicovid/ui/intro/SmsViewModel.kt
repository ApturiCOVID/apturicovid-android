package lv.spkc.apturicovid.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.event.Event
import lv.spkc.apturicovid.model.ContactNumber
import javax.inject.Inject

class SmsViewModel @Inject constructor(): BaseViewModel() {
    private val _smsCodeEventLiveData = MutableLiveData<Event<String>>()
    val smsCodeEventLiveData: LiveData<Event<String>> = _smsCodeEventLiveData

    private val _smsRetrievalEventLiveData = MutableLiveData<Event<Boolean>>()
    val smsRetrievalEventLiveData: LiveData<Event<Boolean>> = _smsRetrievalEventLiveData

    var numberForVerification: ContactNumber? = null

    fun postCode(code: String?) {
        _smsCodeEventLiveData.value = Event(code ?: "")
    }

    fun startSmsRetrieval() {
        _smsRetrievalEventLiveData.value = Event(true)
    }
}