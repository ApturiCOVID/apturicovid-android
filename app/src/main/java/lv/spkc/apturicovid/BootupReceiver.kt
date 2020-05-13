package lv.spkc.apturicovid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import lv.spkc.apturicovid.worker.DeleteExpiredDataWorker
import lv.spkc.apturicovid.worker.ExposureKeyFetchWorker
import lv.spkc.apturicovid.worker.SummarySendingWorker
import timber.log.Timber

class BootUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("BootUpReceiver initiated")
        if (intent.action?.endsWith(Intent.ACTION_BOOT_COMPLETED) == true) {
            ExposureKeyFetchWorker.scheduleWorkManager(context)
            DeleteExpiredDataWorker.scheduleWorkManager(context)
            SummarySendingWorker.scheduleWorkManager(context)
        }
    }
}