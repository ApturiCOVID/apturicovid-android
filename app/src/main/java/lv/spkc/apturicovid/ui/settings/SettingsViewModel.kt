package lv.spkc.apturicovid.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.event.Event
import lv.spkc.apturicovid.model.ContactNumber
import lv.spkc.apturicovid.persistance.SharedPreferenceManager
import java.util.*
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val sharedPreferenceManager: SharedPreferenceManager
): BaseViewModel() {
    private val _contactNumberLiveData = MutableLiveData<String>()
    val contactNumberLiveData: LiveData<String> = _contactNumberLiveData

    private val _isThirdPartyNumberLiveData = MutableLiveData<Boolean>()
    val isThirdPartyNumberLiveData: LiveData<Boolean> = _isThirdPartyNumberLiveData

    private val _languageChangedLiveData = MutableLiveData<Event<Boolean>>()
    val languageChangedLiveData: LiveData<Event<Boolean>> = _languageChangedLiveData

    init {
        _contactNumberLiveData.value = settingsRepository.phoneNumber
        _isThirdPartyNumberLiveData.value = settingsRepository.phoneIsThirdParty
    }

    fun postContactNumber(contactNumber: ContactNumber?) {
        _contactNumberLiveData.value = contactNumber?.number ?: ""
        settingsRepository.phoneNumber = contactNumber?.number ?: ""
        
        if (contactNumber == null) {
            settingsRepository.exposureToken = null
        }

        _isThirdPartyNumberLiveData.value = contactNumber?.isThirdParty
        settingsRepository.phoneIsThirdParty = contactNumber?.isThirdParty ?: false
    }

    fun getSelectedLocale(): String {
        return Locale.getDefault().language
    }

    fun setLanguage(language: String) {
        sharedPreferenceManager.language = language
        _languageChangedLiveData.value = Event(true)
    }

    fun selectNotifyTrackingSwitch(isEnabled: Boolean) {
        settingsRepository.isTrackingStateNotificationsEnabled = isEnabled
    }

    fun getSwitchStatus(): Boolean {
        return settingsRepository.isTrackingStateNotificationsEnabled
    }

    fun getDebugInfo(): String {
        return settingsRepository.debugingLogData
    }
}
