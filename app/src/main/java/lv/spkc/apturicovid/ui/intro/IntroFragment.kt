package lv.spkc.apturicovid.ui.intro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.OnboardingActivity
import lv.spkc.apturicovid.databinding.FragmentIntroBinding
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.extension.setOnDebounceClickListener
import lv.spkc.apturicovid.ui.BaseFragment

class IntroFragment : BaseFragment() {
    companion object {
        const val LV_LANGUAGE_CODE = "lv"
        const val EN_LANGUAGE_CODE = "en"
        const val RU_LANGUAGE_CODE = "ru"
    }

    override val viewModel by navGraphViewModels<OnboardingViewModel>(R.id.nav_onboarding) { viewModelFactory }

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

        with(binding) {
            introAgreementCb.setOnClickListener {
                viewModel.setAcceptancesSelected(introAgreementCb.isChecked)
            }

            when (viewModel.getSelectedLocale()) {
                LV_LANGUAGE_CODE -> {
                    languageCodeLv.isSelected = true
                    viewModel.prepareHyperlinks(
                        OnboardingViewModel.HyperlinkLocale.LV,
                        introAgreementCbText,
                        this@IntroFragment,
                        getString(R.string.terms_of_use_url),
                        getString(R.string.privacy_policy_url)
                    )
                }
                EN_LANGUAGE_CODE -> {
                    languageCodeEn.isSelected = true
                    viewModel.prepareHyperlinks(
                        OnboardingViewModel.HyperlinkLocale.EN,
                        introAgreementCbText,
                        this@IntroFragment,
                        getString(R.string.terms_of_use_url),
                        getString(R.string.privacy_policy_url)
                    )
                }
                RU_LANGUAGE_CODE -> {
                    languageCodeRu.isSelected = true
                    viewModel.prepareHyperlinks(
                        OnboardingViewModel.HyperlinkLocale.RU,
                        introAgreementCbText,
                        this@IntroFragment,
                        getString(R.string.terms_of_use_url),
                        getString(R.string.privacy_policy_url)
                    )
                }
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

            nextButton.setOnDebounceClickListener {
                findNavController().navigate(R.id.activationFragment)
            }

            executePendingBindings()


            observeLiveData(viewModel.acceptancesLiveData) {
                nextButton.isEnabled = it
            }
        }

        observeLiveData(viewModel.languageChangedLiveData) {
            if (it) {
                restartActivity()
            }
        }
    }

    fun openWebView(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
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
