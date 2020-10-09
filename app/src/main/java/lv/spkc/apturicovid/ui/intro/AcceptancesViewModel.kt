package lv.spkc.apturicovid.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import javax.inject.Inject

class AcceptancesViewModel @Inject constructor(): BaseViewModel() {
    private val _acceptancesLiveData = MutableLiveData<Boolean>()
    val acceptancesLiveData: LiveData<Boolean> = _acceptancesLiveData

    fun setAcceptancesSelected(isSelected: Boolean) {
        _acceptancesLiveData.value = isSelected
    }
}