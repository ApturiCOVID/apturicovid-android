package lv.spkc.apturicovid.persistance

import android.content.Context
import android.os.StrictMode
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class SharedPreferenceStorage(private val context: Context, storeName: String) : PreferenceStorage {
    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    val backgroundPreferences = EncryptedSharedPreferences
        .create(
            storeName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    private val gson = createGson()

    override fun setObject(keyName: String, `object`: Any?) {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        StrictMode.allowThreadDiskWrites()
        setObjectAllowDiskAccess(keyName, `object`)
        StrictMode.setThreadPolicy(oldPolicy)
    }

    private fun setObjectAllowDiskAccess(keyName: String, `object`: Any?) {
        val editor = backgroundPreferences.edit()
        if (`object` == null) {
            editor.remove(keyName)
        } else {
            val json = gson.toJson(`object`)
            editor.putString(keyName, json)
        }
        editor.apply()
    }

    override fun <T> getObject(keyName: String, type: Class<T>): T? {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        StrictMode.allowThreadDiskWrites()
        val result = getObjectAllowDiskAccess(keyName, type)
        StrictMode.setThreadPolicy(oldPolicy)
        return result
    }

    private fun <T> getObjectAllowDiskAccess(keyName: String, type: Class<T>): T? {
        val json = backgroundPreferences.getString(keyName, null)
        return gson.fromJson(json, type)
    }

    override fun purge() {
        backgroundPreferences.edit().clear().apply()
    }

    private fun createGson(): Gson {
        return GsonBuilder().create()
    }
}
