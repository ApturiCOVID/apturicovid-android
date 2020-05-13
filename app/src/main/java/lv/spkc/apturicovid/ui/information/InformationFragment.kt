package lv.spkc.apturicovid.ui.information

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.fragment_information.*
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.databinding.FragmentInformationBinding
import lv.spkc.apturicovid.extension.setOnDebounceClickListener
import lv.spkc.apturicovid.ui.BaseFragment
import net.cachapa.expandablelayout.ExpandableLayout

class InformationFragment : BaseFragment(), View.OnClickListener {
    override val viewModel: BaseViewModel? = null

    private lateinit var binding: FragmentInformationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentInformationBinding>(inflater, R.layout.fragment_information, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            //todo oh boy
            questionContainer1.setOnClickListener(this@InformationFragment)
            questionContainer2.setOnClickListener(this@InformationFragment)
            questionContainer3.setOnClickListener(this@InformationFragment)
            questionContainer4.setOnClickListener(this@InformationFragment)
            questionContainer5.setOnClickListener(this@InformationFragment)
            questionContainer6.setOnClickListener(this@InformationFragment)
            questionContainer7.setOnClickListener(this@InformationFragment)
            questionContainer8.setOnClickListener(this@InformationFragment)
            questionContainer9.setOnClickListener(this@InformationFragment)

            termsOfUseView.setOnDebounceClickListener {
                showTerms()
            }

            privacyPolicyLinkView.setOnDebounceClickListener {
                showPrivacyPolicy()
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.questionContainer1 -> processViews(expandableLayout1, questionView1, questionImage1)
            R.id.questionContainer2 -> processViews(expandableLayout2, questionView2, questionImage2)
            R.id.questionContainer3 -> processViews(expandableLayout3, questionView3, questionImage3)
            R.id.questionContainer4 -> processViews(expandableLayout4, questionView4, questionImage4)
            R.id.questionContainer5 -> processViews(expandableLayout5, questionView5, questionImage5)
            R.id.questionContainer6 -> processViews(expandableLayout6, questionView6, questionImage6)
            R.id.questionContainer7 -> processViews(expandableLayout7, questionView7, questionImage7)
            R.id.questionContainer8 -> processViews(expandableLayout8, questionView8, questionImage8)
            R.id.questionContainer9 -> processViews(expandableLayout9, questionView9, questionImage9)
        }
    }

    private fun processViews(layout: ExpandableLayout, textView: TextView, imageView : ImageView) {
        if (layout.isExpanded) {
            layout.collapse()
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryTextColor))
            imageView.setImageResource(R.drawable.faq_colapse)
        } else {
            layout.expand()
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.hyperlinkColor))
            imageView.setImageResource(R.drawable.faq_expand)
        }
    }

    private fun startBrowserIntentWithUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun showPrivacyPolicy() {
        startBrowserIntentWithUrl(getString(R.string.privacy_policy_url))
    }

    private fun showTerms() {
        startBrowserIntentWithUrl(getString(R.string.terms_of_use_url))
    }
}