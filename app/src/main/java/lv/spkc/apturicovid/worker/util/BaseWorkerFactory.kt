package lv.spkc.apturicovid.worker.util

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface BaseWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): ListenableWorker
}