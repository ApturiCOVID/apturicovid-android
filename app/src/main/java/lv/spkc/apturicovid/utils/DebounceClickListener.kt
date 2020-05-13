package lv.spkc.apturicovid.utils

import android.view.View
import org.joda.time.DateTime

abstract class DebouncedClickListener constructor(private var defaultInterval: Int = 1000) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View?) {
        if (DateTime.now().millis - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = DateTime.now().millis
        performClick(v)
    }

    abstract fun performClick(v: View?)
}