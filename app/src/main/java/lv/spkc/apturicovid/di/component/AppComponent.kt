package lv.spkc.apturicovid.di.component

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import lv.spkc.apturicovid.StopCovidApplication
import lv.spkc.apturicovid.di.ViewModelBuilder
import lv.spkc.apturicovid.di.module.*
import lv.spkc.apturicovid.worker.util.WorkerBuilder
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        DataModule::class,
        NetworkModule::class,
        ContributorModule::class,
        ViewModelModule::class,
        ViewModelBuilder::class,
        WorkerManagerModule::class,
        WorkerBuilder::class,
        SharedPreferenceModule::class
    ]
)
interface AppComponent : AndroidInjector<StopCovidApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<StopCovidApplication>()
}