package lv.spkc.apturicovid.ui.intro

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.extension.makeLinks
import lv.spkc.apturicovid.persistance.SharedPreferenceManager
import lv.spkc.apturicovid.utils.DebouncedClickListener
import java.util.*
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(private val sharedPreferenceManager: SharedPreferenceManager) : BaseViewModel() {
    enum class HyperlinkLocale {
        LV, EN, RU;

        fun privacyPolicyString(): String {
            return when (this) {
                LV -> "Privātuma politiku"
                EN -> "Privacy policy"
                RU -> "Политикой конфиденциальности"
            }
        }

        fun termsOfUseString(): String {
            return when (this) {
                LV -> "Lietotnes noteikumiem"
                EN -> "App Terms of Use"
                RU -> "Правилами использования приложения"
            }
        }
    }

    private val _acceptancesLiveData = MutableLiveData<Boolean>()
    val acceptancesLiveData: LiveData<Boolean> = _acceptancesLiveData

    private val _languageChangedLiveData = MutableLiveData<Boolean>()
    val languageChangedLiveData = _languageChangedLiveData

    fun getSelectedLocale(): String {
        return Locale.getDefault().language
    }

    fun setLanguage(language: String) {
        sharedPreferenceManager.language = language
        _languageChangedLiveData.value = true
    }

    fun prepareHyperlinks(
        hyperlinkLocale: HyperlinkLocale,
        textView: TextView,
        fragment: IntroFragment,
        termsOfUseUrl: String,
        privacyPolicyUrl: String
    ) {
        textView.makeLinks(
            Pair(
                hyperlinkLocale.termsOfUseString(),
                object : DebouncedClickListener() {
                    override fun performClick(v: View?) {
                        fragment.openWebView(termsOfUseUrl)
                    }
                }),
            Pair(
                hyperlinkLocale.privacyPolicyString(),
                object : DebouncedClickListener() {
                    override fun performClick(v: View?) {
                        fragment.openWebView(privacyPolicyUrl)
                    }
                })
        )
    }

    fun setAcceptancesSelected(isSelected: Boolean) {
        _acceptancesLiveData.value = isSelected
    }
}