package lv.spkc.apturicovid.ui.settings

import lv.spkc.apturicovid.persistance.SharedPreferenceManager
import javax.inject.Inject

class SettingsRepository @Inject constructor(private val sharedPreferenceManager: SharedPreferenceManager) {
    // Token used to verify the phone number
    var exposureToken: String?
        get() = sharedPreferenceManager.exposureToken
        set(value) {
            sharedPreferenceManager.exposureToken = value
        }

    var phoneNumber: String?
        get() = sharedPreferenceManager.phone
        set(value) {
            sharedPreferenceManager.phone = value
        }

    var phoneIsThirdParty: Boolean
        get() = sharedPreferenceManager.isThirdPartyNumber
        set(value) {
            sharedPreferenceManager.isThirdPartyNumber = value
        }

    var isTrackingStateNotificationsEnabled: Boolean
        get() = sharedPreferenceManager.isTrackingNotificationsEnabled
        set(value) {
            sharedPreferenceManager.isTrackingNotificationsEnabled = value
        }

    var language: String
        get() = sharedPreferenceManager.language
        set(value) {
            sharedPreferenceManager.language = value
        }

    var isOnboardingFinished: Boolean
        get() = sharedPreferenceManager.isOnboardingFinished
        set(value) {
            sharedPreferenceManager.isOnboardingFinished = value
        }

    var debugingLogData: String
        get() = sharedPreferenceManager.debuggingData ?: ""
        set(value) {
            sharedPreferenceManager.debuggingData = value
        }

    var acceptanceV2Confirmed: Boolean
        get() = sharedPreferenceManager.acceptanceV2Confirmed
        set(value) {
            sharedPreferenceManager.acceptanceV2Confirmed = value
        }
}