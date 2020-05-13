package lv.spkc.apturicovid.network

import lv.spkc.apturicovid.di.module.NetworkModule
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiClient {
    @POST("${NetworkModule.API_VERSION}/phone_verifications")
    suspend fun createPhoneVerification(@Body phoneNumber: PhoneVerificationCreateBody): TokenResponse

    @POST("${NetworkModule.API_VERSION}/phone_verifications/verify")
    suspend fun verifyPhone(@Body phoneVerificationBody: PhoneVerificationBody): PhoneVerificationResponse

    @POST("${NetworkModule.API_VERSION}/upload_keys/verify")
    suspend fun verifyUploadKeys(@Body pin: UploadPinVerificationBody): TokenResponse

    @POST("${NetworkModule.API_VERSION}/diagnosis_keys")
    suspend fun sendDiagnosisKeys(@Body diagnosis: Diagnosis): Response<Any>

    @POST("${NetworkModule.API_VERSION}/exposure_summaries")
    suspend fun sendExposureSummaries(@Body exposureSummary: ExposureSummary)
}
