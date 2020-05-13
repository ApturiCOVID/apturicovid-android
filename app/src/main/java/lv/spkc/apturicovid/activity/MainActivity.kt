package lv.spkc.apturicovid.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.ActivityMainBinding
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.extension.toggleVisibility

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override var networkErrorLayout: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLanguage()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        networkErrorLayout = binding.networkErrorLayout

        navController = findNavController(R.id.main_nav_fragment)

        observeLiveData(appStatusViewModel.loadingLiveData) {
            binding.loadingProgressBar.toggleVisibility(it)
        }

        navController.addOnDestinationChangedListener { _, _, _ ->
            appStatusViewModel.resetLoading()
        }
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.dataSuccessFragment) {
            navController.popBackStack(R.id.bottomNavFragment, false)
            return
        }

        super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        checkExposureNotificationModuleStatus()
    }
}
