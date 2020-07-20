package lv.spkc.apturicovid

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothBroadcastReceiver : BroadcastReceiver() {
    lateinit var bluetoothBroadcastReceiverListener: BluetoothBroadcastReceiverListener

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            val action = intent.action
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)

            if (action == BluetoothAdapter.ACTION_STATE_CHANGED && state == BluetoothAdapter.STATE_OFF) {
                bluetoothBroadcastReceiverListener.onTurnedOff()
            }

            if (action == BluetoothAdapter.ACTION_STATE_CHANGED && state == BluetoothAdapter.STATE_ON) {
                bluetoothBroadcastReceiverListener.onTurnedOn()
            }
        }
    }

    interface BluetoothBroadcastReceiverListener {
        fun onTurnedOff()
        fun onTurnedOn()
    }
}