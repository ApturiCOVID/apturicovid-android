package lv.spkc.apturicovid.utils

import android.view.View
import org.joda.time.DateTime

abstract class DebouncedInfoClickListener constructor(private var defaultInterval: Long = 10000) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View?) {
        val timeRemainingMillis = DateTime.now().millis - lastTimeClicked
        if (timeRemainingMillis < defaultInterval) {
            onError(timeRemainingMillis - defaultInterval)
            return
        }
        lastTimeClicked = DateTime.now().millis
        performClick(v)
    }

    abstract fun performClick(v: View?)
    abstract fun onError(timeRemainingMillis: Long)
}