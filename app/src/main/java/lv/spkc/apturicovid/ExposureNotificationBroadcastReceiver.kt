package lv.spkc.apturicovid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import lv.spkc.apturicovid.worker.DetectedExposureHandlerWorker

class ExposureNotificationBroadcastReceiver : BroadcastReceiver() {
    companion object {
        private const val WORK_TAG = "exposure_worker_tag"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (ExposureNotificationClient.ACTION_EXPOSURE_STATE_UPDATED == action) {
            val token = intent.getStringExtra(ExposureNotificationClient.EXTRA_TOKEN)
            val exposureWorkRequest= OneTimeWorkRequest.Builder(DetectedExposureHandlerWorker::class.java)
                .setInputData(
                    Data.Builder()
                        .putString(ExposureNotificationClient.EXTRA_TOKEN, token)
                        .build()
                )
                .addTag(WORK_TAG)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_TAG, ExistingWorkPolicy.REPLACE, exposureWorkRequest)
        }
    }
}
