package lv.spkc.apturicovid.utils

import lv.spkc.apturicovid.BuildConfig
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Deprecated("Deprecated in favor of Jetpack encryption")
object AESUtils {
    private const val AES = "AES"

    @Throws(Exception::class)
    fun decrypt(encrypted: String): String {
        val enc = toByte(encrypted)
        val result = decrypt(enc)
        return String(result)
    }

    @Throws(Exception::class)
    private fun decrypt(encrypted: ByteArray): ByteArray {
        val skeySpec: SecretKey = SecretKeySpec(BuildConfig.SECRET_KEY.toByteArray(), AES)
        val cipher = Cipher.getInstance(AES)
        cipher.init(Cipher.DECRYPT_MODE, skeySpec)
        return cipher.doFinal(encrypted)
    }

    private fun toByte(hexString: String): ByteArray {
        val len = hexString.length / 2
        val result = ByteArray(len)
        for (i in 0 until len) result[i] = Integer.valueOf(
            hexString.substring(2 * i, 2 * i + 2), 16
        ).toByte()
        return result
    }
}