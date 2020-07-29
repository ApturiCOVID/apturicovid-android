package lv.spkc.apturicovid.persistance

class SharedPreferenceManager(private val preferenceStorage: PreferenceStorage) {
    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_ONBOARDING = "onboarding"
        private const val KEY_PHONE = "phone"
        private const val KEY_EXPOSURE_TOKEN = "exposure_token"
        private const val KEY_IS_THIRD_PARTY = "phone_is_third_party"
        private const val KEY_IS_TACKING_NOTIFICATIONS_ENABLED = "is_tracking_notifications_enabled"
        private const val DEBUG_DATA = "debug_data"
        private const val HAS_MIGRATED_TO_V2_STORAGE = "migratedToV2Storage"
        private const val KET_LAST_SMS_REQUEST_TIME = "last_sms_request_time"
    }

    var language: String
        get() = preferenceStorage.getObject(KEY_LANGUAGE, String::class.java) ?: "lv"
        set(language) = preferenceStorage.setObject(KEY_LANGUAGE, language)

    var isOnboardingFinished: Boolean
        get() = preferenceStorage.getObject(KEY_ONBOARDING, Boolean::class.java) ?: false
        set(value) = preferenceStorage.setObject(KEY_ONBOARDING, value)

    var phone: String?
        get() = preferenceStorage.getObject(KEY_PHONE, String::class.java)
        set(value) = preferenceStorage.setObject(KEY_PHONE, value)

    var exposureToken: String?
        get() = preferenceStorage.getObject(KEY_EXPOSURE_TOKEN, String::class.java)
        set(value) = preferenceStorage.setObject(KEY_EXPOSURE_TOKEN, value)

    var isThirdPartyNumber: Boolean
        get() = preferenceStorage.getObject(KEY_IS_THIRD_PARTY, Boolean::class.java) ?: false
        set(value) = preferenceStorage.setObject(KEY_IS_THIRD_PARTY, value)

    var isTrackingNotificationsEnabled: Boolean
        get() = preferenceStorage.getObject(KEY_IS_TACKING_NOTIFICATIONS_ENABLED, Boolean::class.java) ?: true
        set(value) = preferenceStorage.setObject(KEY_IS_TACKING_NOTIFICATIONS_ENABLED, value)

    var debuggingData: String?
        get() = preferenceStorage.getObject(DEBUG_DATA, String::class.java)
        set(value) = preferenceStorage.setObject(DEBUG_DATA, value)

    var migratedToV2Storage: Boolean
        get() = preferenceStorage.getObject(HAS_MIGRATED_TO_V2_STORAGE, Boolean::class.java) ?: false
        set(value) = preferenceStorage.setObject(HAS_MIGRATED_TO_V2_STORAGE, value)

    var lastSmsCodeRequestTime: Long
        get() = preferenceStorage.getObject(KET_LAST_SMS_REQUEST_TIME, Long::class.java) ?: 0
        set(value) = preferenceStorage.setObject(KET_LAST_SMS_REQUEST_TIME, value)
}
