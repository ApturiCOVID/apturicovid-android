package lv.spkc.apturicovid.di.module

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import lv.spkc.apturicovid.worker.*
import lv.spkc.apturicovid.worker.util.BaseWorkerFactory
import lv.spkc.apturicovid.worker.util.WorkerKey

@Module
internal abstract class WorkerManagerModule {
    @Binds
    @IntoMap
    @WorkerKey(ExposureKeyFetchWorker::class)
    abstract fun bindExposureKeyFetchWorker(worker: ExposureKeyFetchWorker.Factory): BaseWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(DetectedExposureHandlerWorker::class)
    abstract fun bindDetectedExposureHandlerWorker(worker: DetectedExposureHandlerWorker.Factory): BaseWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(SummarySendingWorker::class)
    abstract fun bindSummarySendingWorker(worker: SummarySendingWorker.Factory): BaseWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(BtMonitorWorker::class)
    abstract fun bindBtMonitorWorker(worker: BtMonitorWorker.Factory): BaseWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(DeleteExpiredDataWorker::class)
    abstract fun bindDeleteExpiredDataWorker(worker: DeleteExpiredDataWorker.Factory): BaseWorkerFactory
}
