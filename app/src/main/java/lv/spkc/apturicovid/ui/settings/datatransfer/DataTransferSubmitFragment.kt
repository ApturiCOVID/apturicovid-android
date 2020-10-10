package lv.spkc.apturicovid.ui.settings.datatransfer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.FragmentDataTransferSubmitBinding
import lv.spkc.apturicovid.extension.observeEvent
import lv.spkc.apturicovid.extension.setOnDebounceClickListener
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.settings.SettingsViewModel

class DataTransferSubmitFragment : BaseFragment() {
    private val dataTransferViewModel by lazy { ViewModelProvider(requireActivity(), viewModelFactory).get(DataTransferViewModel::class.java) }

    private lateinit var binding: FragmentDataTransferSubmitBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentDataTransferSubmitBinding>(inflater, R.layout.fragment_data_transfer_submit, container, false)
            .apply { binding = this }
            .root
    }

    override val viewModel by activityViewModels <SettingsViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            codeEt.filters += InputFilter.AllCaps()

            codeEt.addTextChangedListener {
                if (errorTv.isVisible) {
                    codeEt.setPinBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_pin_number))
                    codeEt.setTextColor(ContextCompat.getColor(requireContext(), R.color.headerTextColor))
                    codeExpirationTv.visibility = View.VISIBLE
                    errorTv.visibility = View.INVISIBLE
                }
            }

            nextButton.setOnDebounceClickListener {
                val code = codeEt.text.toString()

                if (code.length == 8) {
                    dataTransferViewModel.submitCode(codeEt.text.toString())
                } else {
                    showEditTextError()
                }
            }

            countriesListBtn.setOnClickListener {
                val url = getString(R.string.list_of_countries_url)
                openWebView(url)
            }

            backArrowIv.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        observeEvent(dataTransferViewModel.successEventLiveData) {
            if (it) {
                findNavController().navigate(R.id.action_go_to_success)
            } else {
                showEditTextError()
            }
        }

        observeEvent(dataTransferViewModel.validCodeEventLiveData) {
            if (it) {
                hideKeyboard()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        connectViewModel(dataTransferViewModel)
    }

    override fun onStop() {
        super.onStop()

        disconnectViewModel(dataTransferViewModel)
    }

    private fun showEditTextError() {
        with(binding) {
            codeEt.setPinBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_pin_error))
            codeEt.setTextColor(ContextCompat.getColor(requireContext(), R.color.editTextErrorTextColor))
            codeExpirationTv.visibility = View.INVISIBLE
            errorTv.visibility = View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}