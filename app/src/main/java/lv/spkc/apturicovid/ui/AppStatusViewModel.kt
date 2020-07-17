package lv.spkc.apturicovid.ui

import androidx.lifecycle.*
import lv.spkc.apturicovid.event.Event
import lv.spkc.apturicovid.persistance.ExposureRepository
import lv.spkc.apturicovid.ui.settings.SettingsRepository
import java.net.InetAddress
import javax.inject.Inject

class AppStatusViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    exposureRepository: ExposureRepository
) : ViewModel() {
    private val loadingLiveDataList: MutableList<LiveData<Boolean>> = mutableListOf()
    private val loadingMediator = MediatorLiveData<Boolean>()

    val isExposedLiveData: LiveData<Boolean> = Transformations.map(exposureRepository.getExposedTokenListLiveData()) { list -> list.isNotEmpty() }

    val loadingLiveData: LiveData<Boolean> = loadingMediator.map { _ ->
        loadingLiveDataList.any { it.value == true }
    }

    private var _errorEventLiveData = MutableLiveData<Event<Throwable>>()
    val errorEventLiveData: LiveData<Event<Throwable>> = _errorEventLiveData

    fun addLoadingCondition(additionalLoading: LiveData<Boolean>) {
        loadingLiveDataList.add(additionalLoading)
        loadingMediator.removeSource(additionalLoading)
        loadingMediator.addSource(additionalLoading) {
            loadingMediator.value = it
        }
    }

    fun removeLoadingCondition(loadingLiveData: LiveData<Boolean>) {
        loadingLiveDataList.remove(loadingLiveData)
        loadingMediator.removeSource(loadingLiveData)
    }

    fun resetLoading() {
        loadingLiveDataList.forEach {
            loadingMediator.removeSource(it)
        }
        loadingLiveDataList.clear()
        loadingMediator.postValue(false)
    }

    fun postErrorEvent(error: Throwable) {
        _errorEventLiveData.postValue(Event(error))
    }

    fun saveOnboardingFinished() {
        settingsRepository.isOnboardingFinished = true
    }

    fun isTrackingStateNotificationsEnabled(): Boolean {
        return settingsRepository.isTrackingStateNotificationsEnabled
    }

    fun getLanguage(): String {
        return settingsRepository.language
    }

    private fun isInternetAvailable(): Boolean = try {
        val ipAddress = InetAddress.getByName("google.com")
        ipAddress.toString().isNotEmpty()
    } catch (e: Exception) {
        false
    }
}
