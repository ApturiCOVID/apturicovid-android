package lv.spkc.apturicovid.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.ViewModelProvider
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.viewmodel.SplashActivityViewModel
import lv.spkc.apturicovid.extension.observeLiveData
import timber.log.Timber

class SplashActivity : BaseActivity() {
    companion object {
        const val SPLASH_VISIBILITY_TIME_SECONDS = 1000L * 3
    }

    private val splashActivityViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(SplashActivityViewModel::class.java) }
    override var networkErrorLayout: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        observeLiveData(splashActivityViewModel.isOnboardingFinished) {
            Timber.d("Onboarding is finished? = $it")
            Handler().postDelayed({
                if (it) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, OnboardingActivity::class.java))
                }
            }, SPLASH_VISIBILITY_TIME_SECONDS)
        }
    }
}