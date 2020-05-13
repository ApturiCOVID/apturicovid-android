package lv.spkc.apturicovid.ui.bottomnav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.databinding.FragmentBottomNavBinding
import lv.spkc.apturicovid.ui.BaseFragment

class BottomNavFragment : BaseFragment() {
    override val viewModel: BaseViewModel? = null

    private lateinit var binding: FragmentBottomNavBinding

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentBottomNavBinding>(inflater, R.layout.fragment_bottom_nav, container, false)
                .apply { binding = this }
                .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController(requireActivity(), R.id.bottom_nav_fragment)

        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, _, _ ->
            appStatusViewModel.resetLoading()
        }
    }
}