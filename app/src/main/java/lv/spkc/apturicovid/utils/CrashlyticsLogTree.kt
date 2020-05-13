package lv.spkc.apturicovid.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class CrashlyticsLogTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority == Log.ERROR || priority == Log.DEBUG) {
            FirebaseCrashlytics.getInstance().log(message)
            if (throwable != null) {
                FirebaseCrashlytics.getInstance().recordException(throwable)
            }
        } else return
    }
}