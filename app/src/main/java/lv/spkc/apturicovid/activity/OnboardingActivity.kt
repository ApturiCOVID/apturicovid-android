package lv.spkc.apturicovid.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.ActivityOnboardingBinding
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.extension.toggleVisibility

class OnboardingActivity : BaseActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override var networkErrorLayout: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //This must be done after super.onCreate() and before setContentView()
        loadLanguage()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)

        networkErrorLayout = binding.networkErrorLayout

        observeLiveData(appStatusViewModel.loadingLiveData) {
            binding.loadingProgressBar.toggleVisibility(it)
        }
    }

    override fun onStart() {
        super.onStart()
        checkExposureNotificationModuleStatus()
    }
}