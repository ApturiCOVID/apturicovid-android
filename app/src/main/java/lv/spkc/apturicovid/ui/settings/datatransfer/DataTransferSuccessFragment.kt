package lv.spkc.apturicovid.ui.settings.datatransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.databinding.FragmentDataTransferSuccessBinding
import lv.spkc.apturicovid.extension.setOnDebounceClickListener
import lv.spkc.apturicovid.ui.BaseFragment

class DataTransferSuccessFragment : BaseFragment() {
    private lateinit var binding: FragmentDataTransferSuccessBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentDataTransferSuccessBinding>(inflater, R.layout.fragment_data_transfer_success, container, false)
            .apply { binding = this }
            .root
    }

    override val viewModel: BaseViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextClose.setOnDebounceClickListener {
            findNavController().popBackStack(R.id.bottomNavFragment, false)
        }
    }
}