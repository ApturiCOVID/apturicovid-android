package lv.spkc.apturicovid.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiClient {
    @POST("phone_verifications")
    suspend fun createPhoneVerification(@Body phoneNumber: PhoneVerificationCreateBody): TokenResponse

    @POST("phone_verifications/verify")
    suspend fun verifyPhone(@Body phoneVerificationBody: PhoneVerificationBody): PhoneVerificationResponse

    @POST("upload_keys/verify")
    suspend fun verifyUploadKeys(@Body pin: UploadPinVerificationBody): TokenResponse

    @POST("diagnosis_keys")
    suspend fun sendDiagnosisKeys(@Body diagnosis: Diagnosis): Response<Any>

    @POST("exposure_summaries")
    suspend fun sendExposureSummaries(@Body exposureSummary: ExposureSummary)
}
