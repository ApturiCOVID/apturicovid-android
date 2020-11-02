package lv.spkc.apturicovid.ui.home

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.FragmentTermsConfirmationBinding
import lv.spkc.apturicovid.extension.observeEvent
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.LanguageFragment
import lv.spkc.apturicovid.ui.LanguageViewModel
import lv.spkc.apturicovid.ui.intro.AcceptancesViewModel

class TermsConfirmationFragment : BaseFragment() {
    private lateinit var binding: FragmentTermsConfirmationBinding

    override val viewModel by navGraphViewModels<AcceptancesViewModel>(R.id.nav_main) { viewModelFactory }
    private val languageViewModel by viewModels<LanguageViewModel> { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentTermsConfirmationBinding>(inflater, R.layout.fragment_terms_confirmation, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appStatusViewModel.resetLoading()

        val languageFragment = LanguageFragment()
        childFragmentManager.beginTransaction().replace(R.id.language_stack, languageFragment)
            .commit()

        with(binding) {
            confirmationCbText.movementMethod = LinkMovementMethod()

            confirmationCb.setOnClickListener {
                viewModel.setAcceptancesSelected(confirmationCb.isChecked)
            }

            continueButton.setOnClickListener {
                appStatusViewModel.saveAcceptanceV2Confirmed()
                findNavController().popBackStack()
            }

            observeLiveData(viewModel.acceptancesLiveData) {
                continueButton.isEnabled = it
            }
        }

        viewLifecycleOwner.observeEvent(languageViewModel.languageChangedEventLiveData) {
            if (it) {
                requireActivity().recreate()
            }
        }
    }
}