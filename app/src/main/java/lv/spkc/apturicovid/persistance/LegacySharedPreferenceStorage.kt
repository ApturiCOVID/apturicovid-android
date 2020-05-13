package lv.spkc.apturicovid.persistance

import android.content.Context
import android.os.StrictMode
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lv.spkc.apturicovid.extension.decrypt

@Deprecated("Use `SharedPreferenceStorage` instead. Deprecated due to migration to Jetpack's EncryptedSharedPreferences")
class LegacySharedPreferenceStorage(context: Context, storeName: String) : PreferenceStorage {
    private val backgroundPreferences = context.getSharedPreferences(storeName, 0)
    private val gson = createGson()
    override fun setObject(keyName: String, `object`: Any?) {
        throw RuntimeException("Do not use! Migrated to the new more secure `SharedPreferenceStorage`")
    }

    override fun <T> getObject(keyName: String, type: Class<T>): T {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        StrictMode.allowThreadDiskWrites()
        val result = getObjectAllowDiskAccess(keyName, type)
        StrictMode.setThreadPolicy(oldPolicy)
        return result
    }

    private fun <T> getObjectAllowDiskAccess(keyName: String, type: Class<T>): T {
        val jsonString = backgroundPreferences.getString(keyName, null)
        return gson.fromJson(jsonString?.decrypt(), type)
    }

    override fun purge() {
        backgroundPreferences.edit().clear().apply()
    }

    private fun createGson(): Gson {
        return GsonBuilder().create()
    }
}
