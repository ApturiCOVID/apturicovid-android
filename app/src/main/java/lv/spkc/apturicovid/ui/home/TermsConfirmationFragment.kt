package lv.spkc.apturicovid.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.MainActivity
import lv.spkc.apturicovid.databinding.FragmentTermsConfirmationBinding
import lv.spkc.apturicovid.extension.makeLinks
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.LanguageFragment
import lv.spkc.apturicovid.ui.LanguageViewModel
import lv.spkc.apturicovid.ui.intro.AcceptancesViewModel
import lv.spkc.apturicovid.utils.DebouncedClickListener

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
            confirmationCbText.makeLinks(
                Pair(
                    getString(R.string.v2_terms_link),
                    object : DebouncedClickListener() {
                        override fun performClick(v: View?) {
                            val url = getString(R.string.terms_of_use_url) + languageViewModel.getSelectedLocale()
                            openWebView(url)
                        }
                    }),
                Pair(
                    getString(R.string.v2_privacy_link),
                    object : DebouncedClickListener() {
                        override fun performClick(v: View?) {
                            val url = getString(R.string.privacy_policy_url) + languageViewModel.getSelectedLocale()
                            openWebView(url)
                        }
                    })
            )

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

        viewLifecycleOwner.observeLiveData(languageViewModel.languageChangedLiveData) {
            if (it) {
                restartActivity()
            }
        }
    }

    private fun openWebView(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun restartActivity() {
        with(requireActivity()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)
        }
    }
}