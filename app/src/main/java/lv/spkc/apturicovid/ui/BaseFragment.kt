package lv.spkc.apturicovid.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.extension.observeEvent
import java.util.*
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModel: BaseViewModel?

    protected val appStatusViewModel by activityViewModels<AppStatusViewModel>{ viewModelFactory }

    override fun onStart() {
        super.onStart()

        loadLanguage()
        connectViewModel(viewModel)
    }

    override fun onStop() {
        super.onStop()

        disconnectViewModel(viewModel)
    }

    fun connectViewModel(baseViewModel: BaseViewModel?) {
        baseViewModel?.apply {
            appStatusViewModel.addLoadingCondition(loadingLiveData)

            observeEvent(errorEventLiveData) {
                appStatusViewModel.postErrorEvent(it)
            }
        }
    }

    fun disconnectViewModel(baseViewModel: BaseViewModel?) {
        baseViewModel?.apply {
            appStatusViewModel.removeLoadingCondition(loadingLiveData)
        }
    }

    private fun loadLanguage() {
        val locale  = Locale(getLangCode())
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun getLangCode(): String {
        return appStatusViewModel.getLanguage()
    }

    fun openWebView(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}