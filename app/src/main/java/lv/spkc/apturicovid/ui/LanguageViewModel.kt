package lv.spkc.apturicovid.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.event.Event
import lv.spkc.apturicovid.persistance.SharedPreferenceManager
import java.util.*
import javax.inject.Inject

class LanguageViewModel @Inject constructor(private val sharedPreferenceManager: SharedPreferenceManager) : BaseViewModel() {

    private val _languageChangedLiveData = MutableLiveData<Boolean>()
    val languageChangedLiveData = _languageChangedLiveData

    private val _languageChangedEventLiveData = MutableLiveData<Event<Boolean>>()
    val languageChangedEventLiveData: LiveData<Event<Boolean>> = _languageChangedEventLiveData

    fun getSelectedLocale(): String {
        return Locale.getDefault().language
    }

    fun setLanguage(language: String) {
        sharedPreferenceManager.language = language
        _languageChangedEventLiveData.value = Event(true)
        _languageChangedLiveData.value = true
    }
}