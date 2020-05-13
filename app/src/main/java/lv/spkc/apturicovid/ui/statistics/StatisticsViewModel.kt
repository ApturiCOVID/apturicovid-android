package lv.spkc.apturicovid.ui.statistics

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lv.spkc.apturicovid.activity.viewmodel.BaseViewModel
import lv.spkc.apturicovid.network.CovidStats
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(private val statisticsRepository: StatisticsRepository): BaseViewModel() {
    private val _covidStatsLiveData = MutableLiveData<CovidStats>()
    val covidStatsLiveData = _covidStatsLiveData

    fun refreshData(context: Context) {
        _loadingLiveData.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            _covidStatsLiveData.value = statisticsRepository.getStats(context)
            _loadingLiveData.value = false
        }
    }
}