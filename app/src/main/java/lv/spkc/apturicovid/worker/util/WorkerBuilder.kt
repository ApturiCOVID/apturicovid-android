package lv.spkc.apturicovid.worker.util

import androidx.work.WorkerFactory
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
internal abstract class WorkerBuilder {
    @Binds
    internal abstract fun bindWorkerFactory(factory: DaggerWorkerFactory): WorkerFactory
}