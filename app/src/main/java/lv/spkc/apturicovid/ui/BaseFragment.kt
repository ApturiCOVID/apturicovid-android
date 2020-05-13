package lv.spkc.apturicovid.ui

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.extension.observeEvent
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModel: BaseViewModel?

    protected val appStatusViewModel by activityViewModels<AppStatusViewModel>{ viewModelFactory }

    override fun onStart() {
        super.onStart()

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
}