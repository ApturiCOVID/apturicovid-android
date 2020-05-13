package lv.spkc.apturicovid.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.ui.statistics.StatisticsRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val statisticsRepository: StatisticsRepository): BaseViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}