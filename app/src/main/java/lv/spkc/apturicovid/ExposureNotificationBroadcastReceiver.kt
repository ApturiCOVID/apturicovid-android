package lv.spkc.apturicovid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import lv.spkc.apturicovid.worker.DetectedExposureHandlerWorker

class ExposureNotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val workManager = WorkManager.getInstance(context)
        if (ExposureNotificationClient.ACTION_EXPOSURE_STATE_UPDATED == action) {
            val token = intent.getStringExtra(ExposureNotificationClient.EXTRA_TOKEN)
            workManager.enqueue(
                OneTimeWorkRequest.Builder(DetectedExposureHandlerWorker::class.java)
                    .setInputData(
                        Data.Builder()
                            .putString(ExposureNotificationClient.EXTRA_TOKEN, token)
                            .build()
                    )
                    .build()
            )
        }
    }
}
