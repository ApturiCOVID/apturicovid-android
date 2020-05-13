package lv.spkc.apturicovid.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.FragmentExposedNextStepsBinding
import lv.spkc.apturicovid.extension.toggleVisibility
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.settings.SettingsViewModel

class ExposedNextStepsFragment: BaseFragment() {
    private lateinit var binding: FragmentExposedNextStepsBinding

    override val viewModel by activityViewModels <SettingsViewModel> { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentExposedNextStepsBinding>(inflater, R.layout.fragment_exposed_next_steps, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            if (viewModel.contactNumberLiveData.value == null || viewModel.contactNumberLiveData.value?.isEmpty() == true) {
                numberButton.toggleVisibility(true)
                optionalToDo.toggleVisibility(false)
                contactInfoText.text = getString(R.string.covid_contact_description_no_phone)
            }

            numberButton.setOnClickListener {
                findNavController().navigate(R.id.contactFragment)
            }

            backArrowIv.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}