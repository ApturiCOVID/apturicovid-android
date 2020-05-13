package lv.spkc.apturicovid.ui.home

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.FragmentDebugInfoBinding
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.settings.SettingsViewModel

class DebugInfoFragment: BaseFragment() {
    private lateinit var binding: FragmentDebugInfoBinding

    override val viewModel by activityViewModels <SettingsViewModel> { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentDebugInfoBinding>(inflater, R.layout.fragment_debug_info, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            binding.debugInfoText.text = viewModel.getDebugInfo()
            deleteData.setOnClickListener {
                launchMissile()
            }
        }

    }

    private fun launchMissile() {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        (requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
    }
}