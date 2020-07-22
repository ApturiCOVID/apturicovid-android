package lv.spkc.apturicovid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat

class LocationServiceStatusBroadcastReceiver : BroadcastReceiver() {
    lateinit var locationServiceStatusBroadcastReceiverListener: LocationServiceStatusBroadcastReceiverListener

    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            if (isLocationServiceEnabled(context)) locationServiceStatusBroadcastReceiverListener.onTurnedOn() else locationServiceStatusBroadcastReceiverListener.onTurnedOff()
        }
    }

    private fun isLocationServiceEnabled(context: Context?): Boolean {
        return (context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)
            ?.let { LocationManagerCompat.isLocationEnabled(it) } ?: false
    }

    interface LocationServiceStatusBroadcastReceiverListener {
        fun onTurnedOff()
        fun onTurnedOn()
    }
}