package lv.spkc.apturicovid.network

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

data class Diagnosis(
    val token: String,
    val diagnosisKeys: List<DiagnosisKey>
)

data class DiagnosisKey(
    val keyData: String,
    val rollingStartNumber: Int,
    val rollingPeriod: Int?,
    val transmissionRiskLevel: Int
)

// /phone_verifications
data class PhoneVerificationCreateBody(
    val phoneNumber: String,
    val signedAttestation: String
)

// /phone_verifications/verify
data class PhoneVerificationBody(
    val code: String,
    val token: String
)

data class PhoneVerificationResponse(
    val status: Boolean
)

// /upload_keys/verify
data class UploadPinVerificationBody(
    val code: String
)

data class ExposureSummary(
    val exposureToken: String,
    val isRelativeDevice: Boolean,
    val exposures: List<Exposure>
)

data class Exposure(
    val date: Long,
    val duration: Int,
    val totalRiskScore: Int,
    val transmissionRiskLevel: Int,
    val attenuationValue: Int
)

data class ExposureApiConfig(
    @SerializedName("minimum_risk_score")
    val minimumRiskScore: Int,
    @SerializedName("attenuation_scores")
    val attenuationScores: IntArray,
    @SerializedName("attenuation_weight")
    val attenuationWeight: Int,
    @SerializedName("days_since_last_exposure_scores")
    val daysSinceLastExposureScores: IntArray,
    @SerializedName("days_since_last_exposure_weight")
    val daysSinceLastExposureWeight: Int,
    @SerializedName("duration_scores")
    val durationScores: IntArray,
    @SerializedName("duration_weight")
    val durationWeight: Int,
    @SerializedName("transmission_risk_scores")
    val transmissionRiskScores: IntArray,
    @SerializedName("transmission_risk_weight")
    val transmissionRiskWeight: Int,
    @SerializedName("attenuation_threshold")
    val attenuationThreshold: IntArray

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExposureApiConfig

        if (minimumRiskScore != other.minimumRiskScore) return false
        if (!attenuationScores.contentEquals(other.attenuationScores)) return false
        if (attenuationWeight != other.attenuationWeight) return false
        if (!daysSinceLastExposureScores.contentEquals(other.daysSinceLastExposureScores)) return false
        if (daysSinceLastExposureWeight != other.daysSinceLastExposureWeight) return false
        if (!durationScores.contentEquals(other.durationScores)) return false
        if (durationWeight != other.durationWeight) return false
        if (!transmissionRiskScores.contentEquals(other.transmissionRiskScores)) return false
        if (transmissionRiskWeight != other.transmissionRiskWeight) return false
        if (attenuationThreshold.contentHashCode() != other.attenuationThreshold.contentHashCode()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minimumRiskScore
        result = 31 * result + attenuationScores.contentHashCode()
        result = 31 * result + attenuationWeight
        result = 31 * result + daysSinceLastExposureScores.contentHashCode()
        result = 31 * result + daysSinceLastExposureWeight
        result = 31 * result + durationScores.contentHashCode()
        result = 31 * result + durationWeight
        result = 31 * result + transmissionRiskScores.contentHashCode()
        result = 31 * result + transmissionRiskWeight
        result = 31 * result + attenuationThreshold.contentHashCode()
        return result
    }
}

// General
data class TokenResponse constructor(
    val token: String
)

data class CovidStats(
    @SerializedName("total_tests_count")
    val totalTestCount: Int,
    @SerializedName("total_infected_count")
    val totalInfectedCount: Int,
    @SerializedName("total_death_count")
    val totalDeathCount: Int,
    @SerializedName("yesterday_tests_count")
    val yesterdayTestCount: Int,
    @SerializedName("yesterday_infected_count")
    val yesterdayInfectedCount: Int,
    @SerializedName("yesterday_death_count")
    val yesterdayDeathCount: Int,
    @SerializedName("infected_tests_proportion")
    val infectedTestRatio: Float,
    @SerializedName("updated_at")
    val updatedAt: DateTime
)
