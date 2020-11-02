package lv.spkc.apturicovid.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lv.spkc.apturicovid.persistance.SharedPreferenceManager
import javax.inject.Inject

class SplashActivityViewModel @Inject constructor(private var sharedPreferenceManager: SharedPreferenceManager): BaseViewModel() {
    private val _isOnboardingFinished = MutableLiveData<Boolean>()
    val isOnboardingFinished: LiveData<Boolean> = _isOnboardingFinished

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            _isOnboardingFinished.value = sharedPreferenceManager.isOnboardingFinished
        }
    }
}