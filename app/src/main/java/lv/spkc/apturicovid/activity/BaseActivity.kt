package lv.spkc.apturicovid.activity

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerDialogFragment
import kotlinx.coroutines.launch
import lv.spkc.apturicovid.BluetoothBroadcastReceiver
import lv.spkc.apturicovid.LocationServiceStatusBroadcastReceiver
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.SmsBroadcastReceiver
import lv.spkc.apturicovid.activity.viewmodel.ExposureViewModel
import lv.spkc.apturicovid.extension.bindDimension
import lv.spkc.apturicovid.extension.observeEvent
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.ui.AppStatusViewModel
import lv.spkc.apturicovid.ui.intro.SmsViewModel
import lv.spkc.apturicovid.ui.settings.datatransfer.DataTransferViewModel
import lv.spkc.apturicovid.ui.widget.ErrorDialogFragment
import lv.spkc.apturicovid.utils.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_START_EXPOSURE_NOTIFICATION = 12341
        private const val REQUEST_CODE_SEND_DATA = 12342
        private const val SMS_RETRIEVAL_REQUEST_CODE = 383
        private const val SWITCH_LOG_ERROR = "Failed changing switch state"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    private var bluetoothBroadcastReceiver: BluetoothBroadcastReceiver? = null
    private var locationServiceStatusBroadcastReceiver: LocationServiceStatusBroadcastReceiver? = null

    private val smsViewModel by viewModels<SmsViewModel> { viewModelFactory }

    private val exposureViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(ExposureViewModel::class.java) }

    protected val appStatusViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(AppStatusViewModel::class.java) }

    private val dataTransferViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(DataTransferViewModel::class.java) }

    private var connectionChangeReceiver: ConnectivityChangeReceiver? = null

    private val networkErrorContainerHeight by bindDimension(R.dimen.error_network_container_height)

    abstract var networkErrorLayout: View?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeLiveData(exposureViewModel.resolveRequest) { status ->
            status.getContentIfNotHandled()?.startResolutionForResult(
                this,
                REQUEST_CODE_START_EXPOSURE_NOTIFICATION
            )
        }

        observeLiveData(dataTransferViewModel.resolveRequest) { status ->
            status.getContentIfNotHandled()?.startResolutionForResult(
                this,
                REQUEST_CODE_SEND_DATA
            )
        }

        observeEvent(smsViewModel.smsRetrievalEventLiveData) {
            startSmsUserConsent()
        }

        observeEvent(appStatusViewModel.errorEventLiveData) {
            when (it) {
                is DisplayableError -> {
                    if (hasOpenedDialogs()) {
                        return@observeEvent
                    } else {
                        ErrorDialogFragment.newInstance(it.errorMessageId, it.errorDialogButtonId)
                            .show(supportFragmentManager, null)
                    }
                }
            }
        }

        checkRequiredServicesState()

        registerToLocationBroadcastReceiver()
        registerToBluetoothBroadcastReceiver()
        registerConnectionChangeReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationServiceStatusBroadcastReceiver)
        unregisterReceiver(bluetoothBroadcastReceiver)
        unregisterConnectionChangeReceiver()
    }

    override fun onStart() {
        super.onStart()

        registerToSmsBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()

        unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_START_EXPOSURE_NOTIFICATION -> {
                exposureViewModel.onResolutionComplete(resultCode)
            }
            REQUEST_CODE_SEND_DATA -> {
                dataTransferViewModel.onResolutionComplete(resultCode)
            }
            SMS_RETRIEVAL_REQUEST_CODE -> {
                if ((resultCode == Activity.RESULT_OK) && (data != null)) {
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val code = message?.let { fetchVerificationCode(it) }

                    smsViewModel.postCode(code)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun checkExposureNotificationModuleStatus() {
        if (!hasExposureNotificationModule()) {
            Timber.e("EN module missing")
        }
    }

    private fun hasExposureNotificationModule(): Boolean {
        val intent = Intent(ExposureNotificationClient.ACTION_EXPOSURE_NOTIFICATION_SETTINGS)
        return intent.resolveActivity(packageManager) != null
    }

    private fun startSmsUserConsent() {
        SmsRetriever.getClient(this).also {
            //We can add sender phone number or leave it blank
            it.startSmsUserConsent(null /* or null */)
                .addOnSuccessListener {
                    Timber.e("Started listening to sms!")
                }
                .addOnFailureListener {
                    Timber.e("Sms retrieval failed!")
                }
        }
    }

    private fun registerToSmsBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver().also {
            it.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    intent?.let { context -> startActivityForResult(context, SMS_RETRIEVAL_REQUEST_CODE) }
                }

                override fun onFailure() {
                    Timber.e("Failed to listen to SMS!")
                }
            }
        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    private fun registerToBluetoothBroadcastReceiver() {
        bluetoothBroadcastReceiver = BluetoothBroadcastReceiver().also {
            it.bluetoothBroadcastReceiverListener = object : BluetoothBroadcastReceiver.BluetoothBroadcastReceiverListener {
                override fun onTurnedOff() {
                    Timber.e("Bluetooth is not operational")
                    if (appStatusViewModel.isTrackingStateNotificationsEnabled()) {
                        BtNotificationManager.showNotification(this@BaseActivity)
                        lifecycleScope.launch(CovidCoroutineExceptionHandler(SWITCH_LOG_ERROR)) {
                            checkRequiredServicesState()
                        }
                    }
                }

                override fun onTurnedOn() {
                    BtNotificationManager.hideNotificationIfPresent(this@BaseActivity)
                    lifecycleScope.launch(CovidCoroutineExceptionHandler(SWITCH_LOG_ERROR)) {
                        checkRequiredServicesState()
                    }
                }
            }
        }

        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothBroadcastReceiver, intentFilter)
    }

    private fun registerToLocationBroadcastReceiver() {
        locationServiceStatusBroadcastReceiver = LocationServiceStatusBroadcastReceiver().also {
            it.locationServiceStatusBroadcastReceiverListener = object : LocationServiceStatusBroadcastReceiver.LocationServiceStatusBroadcastReceiverListener {
                override fun onTurnedOff() {
                    Timber.e("Location services are disabled")
                    if (appStatusViewModel.isTrackingStateNotificationsEnabled()) {
                        LocationServicesNotificationManager.showNotification(this@BaseActivity)
                        lifecycleScope.launch(CovidCoroutineExceptionHandler(SWITCH_LOG_ERROR)) {
                            checkRequiredServicesState()
                        }
                    }
                }

                override fun onTurnedOn() {
                    LocationServicesNotificationManager.hideNotificationIfPresent(this@BaseActivity)
                    lifecycleScope.launch(CovidCoroutineExceptionHandler(SWITCH_LOG_ERROR)) {
                        checkRequiredServicesState()
                    }
                }
            }
        }

        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(locationServiceStatusBroadcastReceiver, intentFilter)
    }

    private fun fetchVerificationCode(message: String): String {
        return Regex("[0-9]{8}").find(message)?.value ?: ""
    }

    private fun hasOpenedDialogs(): Boolean {
        return supportFragmentManager.fragments.any {
            it is DaggerDialogFragment
        }
    }

    private fun getLangCode(): String {
        return appStatusViewModel.getLanguage()
    }

    protected fun loadLanguage() {
        val locale  = Locale(getLangCode())
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun showNetworkError() {
        networkErrorLayout?.apply {
            animate().translationY(0f).duration = 100
        }
    }

    private fun closeNetworkError() {
        networkErrorLayout?.apply {
            animate().translationY(-networkErrorContainerHeight.toFloat()).duration = 100
        }
    }

    @Suppress("DEPRECATION") // apps should use the more versatile... bla bla bla... This one is better
    private fun registerConnectionChangeReceiver() {
        if (connectionChangeReceiver != null) return

        connectionChangeReceiver = ConnectivityChangeReceiver()
        registerReceiver(connectionChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun unregisterConnectionChangeReceiver() {
        connectionChangeReceiver?.apply {
            unregisterReceiver(this)
        }
        connectionChangeReceiver = null
    }

    private fun onNetworkAvailable(isAvailable: Boolean) {
        Timber.e("Network available: $isAvailable")
        when (isAvailable) {
            true -> closeNetworkError()
            false -> showNetworkError()
        }
    }

    private fun onNetworkChanged() {
        lifecycleScope.launch(CovidCoroutineExceptionHandler("Error checking internet connection")) {
            onNetworkAvailable(NetworkUtils.isInternetAvailable())
        }
    }

    private fun checkRequiredServicesState() {
        lifecycleScope.launch(CovidCoroutineExceptionHandler("Error checking required services state")) {
            val locationManager = (this@BaseActivity.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)

            val isOperational = locationManager?.let { LocationManagerCompat.isLocationEnabled(it) } ?: false
                    && BluetoothAdapter.getDefaultAdapter()?.isEnabled ?: false
            exposureViewModel.changeExposureState(isOperational)
        }
    }

    inner class ConnectivityChangeReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("Network changed...")
            this@BaseActivity.onNetworkChanged()
        }
    }
}

