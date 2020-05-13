package lv.spkc.apturicovid.network

import android.content.Context
import com.google.android.gms.safetynet.SafetyNet
import kotlinx.coroutines.tasks.await
import lv.spkc.apturicovid.BuildConfig
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import java.util.*

class SafetyNetService(private val context: Context) {
    private val random: Random = SecureRandom()

    /*
    The nonce is returned as part of the response from the
    SafetyNet API. Here we append the string to a number of random bytes to ensure it larger
    than the minimum 16 bytes required.
    Read out this value and verify it against the original request to ensure the
    response is correct and genuine.
    NOTE: A nonce must only be used once and a different nonce should be used for each request.
    As a more secure option, you can obtain a nonce from your own server using a secure
    connection. Here in this sample, we generate a String and append random bytes, which is not
    very secure. Follow the tips on the Security Tips page for more information:
    https://developer.android.com/training/articles/security-tips.html#Crypto
     */
    suspend fun getSafetyNetJWSString(nonce: String): String {
        Timber.i("Sending SafetyNet API request.")

        /*
        Call the SafetyNet API asynchronously.
        The result is returned through the success or failure listeners.
        First, get a SafetyNetClient for the foreground Activity.
        Next, make the call to the attestation API. The API key is specified in the gradle build
        configuration and read from the gradle.properties file.
        */
        val client = SafetyNet.getClient(context)
        try {
            val attestationResponse = client
                .attest(generateRequestNonce(nonce), BuildConfig.SAFETY_NET_API_KEY)
                .await()
                .jwsResult

            Timber.v("attestationResponse: $attestationResponse")

            return attestationResponse
        } catch (exception: Throwable) {
            Timber.e(exception)

            throw exception
        }
    }

    private fun generateRequestNonce(data: String): ByteArray {
        val byteStream = ByteArrayOutputStream()
        val bytes = ByteArray(24)
        random.nextBytes(bytes)

        return byteStream.use { it.toByteArray() }
    }
}