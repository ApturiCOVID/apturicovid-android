package lv.spkc.apturicovid.persistance

interface PreferenceStorage {
    fun setObject(keyName: String, `object`: Any?)
    fun <T> getObject(keyName: String, type: Class<T>): T?
    fun purge()
}