package lv.spkc.apturicovid.extension

import android.util.TypedValue
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import lv.spkc.apturicovid.event.Event

inline fun <T> Fragment.observeLiveData(data: LiveData<T>, crossinline onChanged: (T) -> Unit) {
    data.observe(viewLifecycleOwner, Observer {
        it?.let(onChanged)
    })
}

inline fun <T> Fragment.observeEvent(data: LiveData<Event<T>>, crossinline onEventUnhandledContent: (T) -> Unit) {
    data.observe(viewLifecycleOwner, Observer {
        it?.getContentIfNotHandled()?.let(onEventUnhandledContent)
    })
}

fun Fragment.bindDimension(@DimenRes dimenId: Int) = lazy { resources.getDimension(dimenId).toInt() }

fun Fragment.bindFloatDimension(@DimenRes dimenId: Int) = lazy {
    TypedValue().apply { resources.getValue(dimenId, this, true) }.float
}