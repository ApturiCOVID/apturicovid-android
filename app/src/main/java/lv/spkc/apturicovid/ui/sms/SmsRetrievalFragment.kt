package lv.spkc.apturicovid.ui.sms

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter.AllCaps
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.MainActivity
import lv.spkc.apturicovid.activity.OnboardingActivity
import lv.spkc.apturicovid.databinding.FragmentSmsRetrievalBinding
import lv.spkc.apturicovid.extension.makeSectionOfTextBold
import lv.spkc.apturicovid.extension.observeEvent
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.extension.setOnDebounceClickListener
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.intro.SmsViewModel
import lv.spkc.apturicovid.ui.settings.SettingsViewModel

class SmsRetrievalFragment: BaseFragment() {
    companion object {
        const val MILLIS_IN_SECOND = 1000L
        const val MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60
    }
    override val viewModel by viewModels<SmsRetrievalViewModel> { viewModelFactory }

    private val smsViewModel by activityViewModels<SmsViewModel> { viewModelFactory }
    private val settingsViewModel by activityViewModels<SettingsViewModel> { viewModelFactory }

    private lateinit var binding: FragmentSmsRetrievalBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentSmsRetrievalBinding>(inflater, R.layout.fragment_sms_retrieval, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            handleSendNumberRequest()

            codeEt.filters += AllCaps()

            codeEt.addTextChangedListener {
                if (errorTv.isVisible) {
                    codeEt.setPinBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_pin_number))
                    codeEt.setTextColor(ContextCompat.getColor(requireContext(), R.color.headerTextColor))
                    errorTv.visibility = View.GONE
                }
            }

            smsViewModel.numberForVerification?.number?.apply {
                val formattedString = String.format(getString(R.string.sms_verification_info), this)
                dataSubmitDescriptionTv.text = formattedString.makeSectionOfTextBold( this)
            }

            nextButton.setOnDebounceClickListener {
                viewModel.verifyCode(codeEt.text.toString())
            }

            backArrowIv.setOnDebounceClickListener {
                findNavController().popBackStack()
            }

            resendCodeTv.setOnClickListener { smsViewModel.startCodeRetrievalTimer(MILLIS_IN_MINUTE, true) }
        }

        observeLiveData(smsViewModel.requestRemainingTimeShowMessagePairLiveData) {
            handleSendNumberRequest(it.first, it.second)
        }

        observeEvent(smsViewModel.smsCodeEventLiveData) {
            binding.codeEt.setText(it)
        }

        observeEvent(viewModel.codeVerificationEventLiveData) {
            if (it) {
                settingsViewModel.postContactNumber(smsViewModel.numberForVerification)
                smsViewModel.numberForVerification = null

                if (requireActivity() is OnboardingActivity) {
                    appStatusViewModel.saveOnboardingFinished()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    findNavController().popBackStack(R.id.bottomNavFragment, false)
                }
            } else {
                with(binding) {
                    codeEt.setPinBackground(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.bg_pin_error
                        )
                    )
                    codeEt.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.editTextErrorTextColor
                        )
                    )
                    errorTv.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun handleSendNumberRequest(remainingRequestTime: Long = 0L, showMessage: Boolean = false) {
        if (remainingRequestTime == 0L) {
            sendNumber()
            if (showMessage) Toast.makeText(requireContext(), getString(R.string.label_code_resent), Toast.LENGTH_SHORT).show()
        } else if (showMessage) {
            val errorText = String.format(getString(R.string.label_code_resend_error), remainingRequestTime * -1 / MILLIS_IN_SECOND)
            Toast.makeText(requireContext(), errorText, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNumber() {
        smsViewModel.numberForVerification?.let { contactNumber ->
            smsViewModel.startSmsRetrieval()
            viewModel.sendNumber(contactNumber.number)
        } ?: Toast.makeText(requireContext(), getString(R.string.label_generic_error), Toast.LENGTH_LONG).show()
    }
}
