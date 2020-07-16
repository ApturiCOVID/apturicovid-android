package lv.spkc.apturicovid.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.FragmentSettingsBinding
import lv.spkc.apturicovid.extension.observeEvent
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.ui.BaseFragment

class SettingsFragment: BaseFragment() {
    companion object {
        const val LV_LANGUAGE_CODE = "lv"
        const val EN_LANGUAGE_CODE = "en"
        const val RU_LANGUAGE_CODE = "ru"
    }

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentSettingsBinding>(inflater, R.layout.fragment_settings, container, false)
            .apply { binding = this }
            .root
    }

    override val viewModel by activityViewModels <SettingsViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            observeLiveData(viewModel.contactNumberLiveData) {
                specifyContactView.contactNr = it
            }

            notifySwitch.isChecked = viewModel.getSwitchStatus()

            versionNr.text = viewModel.getApplicationVersionString()

            when (viewModel.getSelectedLocale()) {
                LV_LANGUAGE_CODE -> languageCodeLv.isSelected = true
                EN_LANGUAGE_CODE -> languageCodeEn.isSelected = true
                RU_LANGUAGE_CODE -> languageCodeRu.isSelected = true
            }

            languageCodeLv.setOnClickListener {
                if (it.isSelected) return@setOnClickListener

                it.isSelected = true
                viewModel.setLanguage(LV_LANGUAGE_CODE)
                languageCodeEn.isSelected = false
                languageCodeRu.isSelected = false
            }

            languageCodeEn.setOnClickListener {
                if (it.isSelected) return@setOnClickListener

                it.isSelected = true
                viewModel.setLanguage(EN_LANGUAGE_CODE)
                languageCodeLv.isSelected = false
                languageCodeRu.isSelected = false
            }

            languageCodeRu.setOnClickListener {
                if (it.isSelected) return@setOnClickListener

                it.isSelected = true
                viewModel.setLanguage(RU_LANGUAGE_CODE)
                languageCodeLv.isSelected = false
                languageCodeEn.isSelected = false
            }

            codeButton.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.main_nav_fragment).navigate(R.id.toDataTransferWizard)
            }

            specifyContactView.changeNumberClickListener = {
                Navigation.findNavController(requireActivity(), R.id.main_nav_fragment).navigate(R.id.contactFragment)
            }

            observeEvent(viewModel.languageChangedLiveData) {
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
