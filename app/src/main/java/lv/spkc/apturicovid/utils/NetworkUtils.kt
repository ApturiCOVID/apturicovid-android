package lv.spkc.apturicovid.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress

object NetworkUtils {
    suspend fun isInternetAvailable(): Boolean {
        return withContext(Dispatchers.IO + CovidCoroutineExceptionHandler("Error checking network!")) {
            return@withContext try {
                val ipAddress = InetAddress.getByName("google.com")
                ipAddress.toString().isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }
    }
}