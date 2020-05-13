package lv.spkc.apturicovid.ui.sms

import lv.spkc.apturicovid.network.ApiClient
import lv.spkc.apturicovid.network.PhoneVerificationBody
import lv.spkc.apturicovid.network.PhoneVerificationCreateBody
import lv.spkc.apturicovid.network.SafetyNetService
import javax.inject.Inject

class SmsRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val safetyNetService: SafetyNetService
) {
    suspend fun getVerificationToken(number: String): String {
        val attestationResponse = safetyNetService.getSafetyNetJWSString(number)

        return apiClient
            .createPhoneVerification(PhoneVerificationCreateBody(number, attestationResponse))
            .token
    }

    suspend fun verifyPhone(code: String, verificationToken: String) = apiClient.verifyPhone(PhoneVerificationBody(code, verificationToken)).status
}
