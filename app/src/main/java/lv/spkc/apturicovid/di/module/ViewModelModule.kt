package lv.spkc.apturicovid.di.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import lv.spkc.apturicovid.activity.viewmodel.ExposureViewModel
import lv.spkc.apturicovid.activity.viewmodel.SplashActivityViewModel
import lv.spkc.apturicovid.di.viewmodel.ViewModelKey
import lv.spkc.apturicovid.ui.AppStatusViewModel
import lv.spkc.apturicovid.ui.home.HomeViewModel
import lv.spkc.apturicovid.ui.intro.OnboardingViewModel
import lv.spkc.apturicovid.ui.intro.SmsViewModel
import lv.spkc.apturicovid.ui.settings.SettingsViewModel
import lv.spkc.apturicovid.ui.settings.datatransfer.DataTransferViewModel
import lv.spkc.apturicovid.ui.sms.SmsRetrievalViewModel
import lv.spkc.apturicovid.ui.statistics.StatisticsViewModel

@Suppress("unused")
@Module
internal abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AppStatusViewModel::class)
    abstract fun bindAppStatusViewModel(viewModel: AppStatusViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsViewModel::class)
    abstract fun bindStatisticsViewModel(viewModel: StatisticsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingViewModel::class)
    abstract fun bindIntroViewModel(viewModel: OnboardingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DataTransferViewModel::class)
    abstract fun bindDataTransferViewModel(viewModel: DataTransferViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashActivityViewModel::class)
    abstract fun bindSplashActivityViewModel(viewModel: SplashActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SmsRetrievalViewModel::class)
    abstract fun bindSmsRetrievalViewModel(viewModel: SmsRetrievalViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ExposureViewModel::class)
    abstract fun bindExposureViewModel(viewModel: ExposureViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SmsViewModel::class)
    abstract fun bindSmsViewModel(viewModel: SmsViewModel): ViewModel
}