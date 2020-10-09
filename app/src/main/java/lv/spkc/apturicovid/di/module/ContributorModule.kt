package lv.spkc.apturicovid.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import lv.spkc.apturicovid.activity.MainActivity
import lv.spkc.apturicovid.activity.OnboardingActivity
import lv.spkc.apturicovid.activity.SplashActivity
import lv.spkc.apturicovid.ui.LanguageFragment
import lv.spkc.apturicovid.ui.bottomnav.BottomNavFragment
import lv.spkc.apturicovid.ui.home.DebugInfoFragment
import lv.spkc.apturicovid.ui.home.ExposedNextStepsFragment
import lv.spkc.apturicovid.ui.home.HomeFragment
import lv.spkc.apturicovid.ui.home.TermsConfirmationFragment
import lv.spkc.apturicovid.ui.information.InformationFragment
import lv.spkc.apturicovid.ui.intro.ActivationFragment
import lv.spkc.apturicovid.ui.intro.IntroFragment
import lv.spkc.apturicovid.ui.settings.ContactFragment
import lv.spkc.apturicovid.ui.settings.SettingsFragment
import lv.spkc.apturicovid.ui.settings.datatransfer.DataTransferSubmitFragment
import lv.spkc.apturicovid.ui.settings.datatransfer.DataTransferSuccessFragment
import lv.spkc.apturicovid.ui.sms.SmsRetrievalFragment
import lv.spkc.apturicovid.ui.statistics.StatisticsFragment
import lv.spkc.apturicovid.ui.widget.ConfirmationDialogFragment
import lv.spkc.apturicovid.ui.widget.ErrorDialogFragment

@Suppress("unused")
@Module
abstract class ContributorModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun contributeOnboardingActivity(): OnboardingActivity

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeInformationFragment(): InformationFragment

    @ContributesAndroidInjector
    abstract fun contributeStatisticsFragment(): StatisticsFragment

    @ContributesAndroidInjector
    abstract fun contributeIntroFragment(): IntroFragment

    @ContributesAndroidInjector
    abstract fun contributeBottomNavFragment(): BottomNavFragment

    @ContributesAndroidInjector
    abstract fun contributeDataTransferSubmitFragment(): DataTransferSubmitFragment

    @ContributesAndroidInjector
    abstract fun contributeDataTransferSuccessFragment(): DataTransferSuccessFragment

    @ContributesAndroidInjector
    abstract fun contributeContactFragment(): ContactFragment

    @ContributesAndroidInjector
    abstract fun contributeActivationFragment(): ActivationFragment

    @ContributesAndroidInjector
    abstract fun contributeConfirmationDialogFragment(): ConfirmationDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeSmsRetrievalFragment(): SmsRetrievalFragment

    @ContributesAndroidInjector
    abstract fun contributeExposedNextStepsFragment(): ExposedNextStepsFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeDebugInfoFragment(): DebugInfoFragment

    @ContributesAndroidInjector
    abstract fun contributeTermsConfirmationFragment(): TermsConfirmationFragment

    @ContributesAndroidInjector
    abstract fun contributeLanguageFragment(): LanguageFragment
}