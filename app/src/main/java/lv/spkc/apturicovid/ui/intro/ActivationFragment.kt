package lv.spkc.apturicovid.ui.intro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.MainActivity
import lv.spkc.apturicovid.activity.viewmodel.ExposureApiState
import lv.spkc.apturicovid.activity.viewmodel.ExposureViewModel
import lv.spkc.apturicovid.databinding.FragmentActivationBinding
import lv.spkc.apturicovid.extension.*
import lv.spkc.apturicovid.model.ContactNumber
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.settings.SettingsViewModel
import lv.spkc.apturicovid.ui.widget.ConfirmationDialogFragment
import lv.spkc.apturicovid.utils.DisplayableError

class ActivationFragment : BaseFragment() {
    private lateinit var binding: FragmentActivationBinding

    override val viewModel by viewModels<SettingsViewModel> { viewModelFactory }

    private val smsViewModel by activityViewModels<SmsViewModel> { viewModelFactory }

    private val exposureViewModel by lazy { ViewModelProvider(requireActivity(), viewModelFactory).get(ExposureViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentActivationBinding>(inflater, R.layout.fragment_activation, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            backArrowIv.setOnClickListener {
                findNavController().popBackStack()
            }

            activateSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    exposureViewModel.start()
                } else {
                    exposureViewModel.stop()
                }
            }

            contactWhyLl.setOnClickListener {
                findNavController().navigate(R.id.whyEnterNumberFragmentIntro)
            }

            nextButton.setOnDebounceClickListener {
                with(phoneEt) {
                    if (activateSwitch.isChecked && exposureViewModel.exposureApiState.value == ExposureApiState.Enabled) {
                        if (text.length == 8 && text.toString().isPhoneNumber()) {
                            smsViewModel.startSmsRetrieval()
                            smsViewModel.numberForVerification = ContactNumber(phoneEt.text.toString(), thirdPartyContactCb.isChecked)
                            findNavController().navigate(R.id.smsVerification)
                        } else if (text.isEmpty()) {
                            viewModel.showErrorDialog(DisplayableError(R.string.label_empty_number, R.string.label_ok))
                        } else {
                            appStatusViewModel.postErrorEvent(DisplayableError(R.string.label_wrong_number, R.string.label_ok))
                        }
                    } else {
                        ConfirmationDialogFragment.newInstance(
                            R.string.confirm_stay_anonymous_no_tracing_text,
                            R.string.confirm_stay_anonymous_no,
                            R.string.confirm_stay_anonymous_yes,
                            onAccept = {
                                viewModel.postContactNumber(null)
                                saveAndContinue()
                            }
                        ).show(childFragmentManager, null)
                    }
                }
            }

            remainAnonymousLl.setOnClickListener {
                ConfirmationDialogFragment.newInstance(
                    R.string.confirm_stay_anonymous_text,
                    R.string.confirm_stay_anonymous_no,
                    R.string.confirm_stay_anonymous_yes,
                    onAccept = {
                        saveAndContinue()
                    }
                ).show(childFragmentManager, null)
            }

            observeEvent(exposureViewModel.errorEventLiveData) {
                viewModel.showErrorDialog(it)
            }

            observeLiveData(exposureViewModel.exposureApiState) {
                val isChecked = it == ExposureApiState.Enabled

                activateSwitch.isChecked = isChecked
                specifyNumberContainerCl.toggleVisibility(isChecked)
            }
        }
    }

    private fun saveAndContinue() {
        appStatusViewModel.saveOnboardingFinished()
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }
}