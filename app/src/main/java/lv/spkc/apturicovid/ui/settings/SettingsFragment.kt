package lv.spkc.apturicovid.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.FragmentSettingsBinding
import lv.spkc.apturicovid.extension.observeEvent
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.LanguageFragment
import lv.spkc.apturicovid.ui.LanguageViewModel

class SettingsFragment: BaseFragment() {

    override val viewModel by activityViewModels <SettingsViewModel> { viewModelFactory }
    private val languageViewModel by viewModels<LanguageViewModel> { viewModelFactory }

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentSettingsBinding>(inflater, R.layout.fragment_settings, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languageFragment = LanguageFragment()
        childFragmentManager.beginTransaction().replace(R.id.language_selector, languageFragment).commit()

        with(binding) {
            observeLiveData(viewModel.contactNumberLiveData) {
                specifyContactView.contactNr = it
            }

            notifySwitch.isChecked = viewModel.getSwitchStatus()

            versionNr.text = viewModel.getApplicationVersionString()


            codeButton.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.main_nav_fragment).navigate(R.id.toDataTransferWizard)
            }

            specifyContactView.changeNumberClickListener = {
                Navigation.findNavController(requireActivity(), R.id.main_nav_fragment).navigate(R.id.contactFragment)
            }

            observeEvent(languageViewModel.languageChangedEventLiveData) {
                if (it) {
                    requireActivity().recreate()
                }
            }

            notifySwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.selectNotifyTrackingSwitch(isChecked)
            }
        }
    }
}
