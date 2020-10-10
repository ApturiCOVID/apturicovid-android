package lv.spkc.apturicovid.ui.intro

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
import lv.spkc.apturicovid.activity.OnboardingActivity
import lv.spkc.apturicovid.databinding.FragmentIntroBinding
import lv.spkc.apturicovid.extension.makeLinks
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.extension.setOnDebounceClickListener
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.LanguageFragment
import lv.spkc.apturicovid.ui.LanguageViewModel
import lv.spkc.apturicovid.utils.DebouncedClickListener

class IntroFragment : BaseFragment() {

    override val viewModel by navGraphViewModels<AcceptancesViewModel>(R.id.nav_onboarding) { viewModelFactory }
    private val languageViewModel by viewModels<LanguageViewModel> { viewModelFactory }

    private lateinit var binding: FragmentIntroBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentIntroBinding>(
            inflater,
            R.layout.fragment_intro,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languageFragment = LanguageFragment()
        childFragmentManager.beginTransaction().replace(R.id.language_stack, languageFragment).commit()

        with(binding) {
            introAgreementCb.setOnClickListener {
                viewModel.setAcceptancesSelected(introAgreementCb.isChecked)
            }

            introAgreementCbText.makeLinks(
                Pair(
                    getString(R.string.terms_link),
                    object : DebouncedClickListener() {
                        override fun performClick(v: View?) {
                            val url =
                                getString(R.string.terms_of_use_url) + languageViewModel.getSelectedLocale()
                            openWebView(url)
                        }
                    }),
                Pair(
                    getString(R.string.privacy_link),
                    object : DebouncedClickListener() {
                        override fun performClick(v: View?) {
                            val url =
                                getString(R.string.privacy_policy_url) + languageViewModel.getSelectedLocale()
                            openWebView(url)
                        }
                    })
            )

            nextButton.setOnDebounceClickListener {
                findNavController().navigate(R.id.activationFragment)
            }

            executePendingBindings()

            observeLiveData(viewModel.acceptancesLiveData) {
                nextButton.isEnabled = it
            }
        }

        observeLiveData(languageViewModel.languageChangedLiveData) {
            if (it) {
                restartActivity()
            }
        }
    }

    private fun restartActivity() {
        with(requireActivity()) {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)
        }
    }
}
