package lv.spkc.apturicovid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class PackageUpdatedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("PackageUpdatedReceiver initiated with action ${intent.action}")

        if (intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) { return }

        // Used to migrate `SharedPreferences`, but some devices doesn't trigger the
        // `ACTION_MY_PACKAGE_REPLACED` on update so the migration code is moved to
        // `StopCovidApplication.onCreate()` method.
        //
        // The Receiver is kept to trigger `StopCovidApplication` initialization right after update
        // for the devices that do support `ACTION_MY_PACKAGE_REPLACED`
    }
}
