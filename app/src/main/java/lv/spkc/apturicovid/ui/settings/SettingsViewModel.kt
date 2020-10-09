package lv.spkc.apturicovid.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lv.spkc.apturicovid.BuildConfig
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.model.ContactNumber
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): BaseViewModel() {
    private val _contactNumberLiveData = MutableLiveData<String>()
    val contactNumberLiveData: LiveData<String> = _contactNumberLiveData

    private val _isThirdPartyNumberLiveData = MutableLiveData<Boolean>()
    val isThirdPartyNumberLiveData: LiveData<Boolean> = _isThirdPartyNumberLiveData

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

    fun selectNotifyTrackingSwitch(isEnabled: Boolean) {
        settingsRepository.isTrackingStateNotificationsEnabled = isEnabled
    }

    fun getSwitchStatus(): Boolean {
        return settingsRepository.isTrackingStateNotificationsEnabled
    }

    fun getDebugInfo(): String {
        return settingsRepository.debugingLogData
    }

    fun getApplicationVersionString(): String {
        return "v ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }
}
