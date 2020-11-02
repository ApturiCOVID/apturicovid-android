package lv.spkc.apturicovid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.FragmentLanguagesBinding

class LanguageFragment : BaseFragment() {
    companion object {
        const val LV_LANGUAGE_CODE = "lv"
        const val EN_LANGUAGE_CODE = "en"
        const val RU_LANGUAGE_CODE = "ru"
    }

    private lateinit var binding: FragmentLanguagesBinding

    override val viewModel by viewModels<LanguageViewModel>({ requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentLanguagesBinding>(inflater, R.layout.fragment_languages, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
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
        }
    }
}