package lv.spkc.apturicovid.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.FragmentContactBinding
import lv.spkc.apturicovid.extension.isPhoneNumber
import lv.spkc.apturicovid.extension.setOnDebounceClickListener
import lv.spkc.apturicovid.model.ContactNumber
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.intro.SmsViewModel
import lv.spkc.apturicovid.ui.widget.ConfirmationDialogFragment
import lv.spkc.apturicovid.utils.DisplayableError

class ContactFragment : BaseFragment() {
    override val viewModel by activityViewModels <SettingsViewModel> { viewModelFactory }
    private val smsViewModel by activityViewModels<SmsViewModel> { viewModelFactory }

    private lateinit var binding: FragmentContactBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentContactBinding>(inflater, R.layout.fragment_contact, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            phoneEt.setText(viewModel.contactNumberLiveData.value)
            thirdPartyContactCb.isChecked = viewModel.isThirdPartyNumberLiveData.value ?: false
            backArrowIv.setOnClickListener {
                findNavController().popBackStack()
            }

            remainAnonymousRl.setOnClickListener {
                ConfirmationDialogFragment.newInstance(
                    R.string.confirm_stay_anonymous_text,
                    R.string.confirm_stay_anonymous_no,
                    R.string.confirm_stay_anonymous_yes,
                    onAccept = {
                        phoneEt.text.clear()
                        viewModel.postContactNumber(null)
                        findNavController().popBackStack()
                    }
                ).show(childFragmentManager, null)
            }

            otherContactDescriptionTv.setOnClickListener {
                thirdPartyContactCb.isChecked = !thirdPartyContactCb.isChecked
            }

            contactWhyRl.setOnClickListener {
                findNavController().navigate(R.id.whyEnterNumberFragment)
            }

            nextButton.setOnDebounceClickListener {
                val oldPhone = viewModel.contactNumberLiveData.value
                val newPhone = phoneEt.text.toString()

                if (newPhone.length == 8 && newPhone.isPhoneNumber()) {
                    if (oldPhone != newPhone) {
                        smsViewModel.numberForVerification = ContactNumber(newPhone, thirdPartyContactCb.isChecked)
                        findNavController().navigate(R.id.SMSRetrievalFragment)
                    } else {
                        // maybe checkbox has changed? save anyways
                        viewModel.postContactNumber(ContactNumber(newPhone, thirdPartyContactCb.isChecked))
                        findNavController().popBackStack()
                    }
                } else {
                    appStatusViewModel.postErrorEvent(DisplayableError(R.string.label_wrong_number, R.string.label_ok))
                }
            }
        }
    }
}