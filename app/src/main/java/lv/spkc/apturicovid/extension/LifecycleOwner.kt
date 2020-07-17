package lv.spkc.apturicovid.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import lv.spkc.apturicovid.event.Event

inline fun <T> LifecycleOwner.observeLiveData(data: LiveData<T>, crossinline onChanged: (T) -> Unit) {
    data.observe(this, Observer {
        it?.let(onChanged)
    })
}

inline fun <T> LifecycleOwner.observeEvent(data: LiveData<Event<T>>, crossinline onEventUnhandledContent: (T) -> Unit) {
    data.observe(this, Observer {
        it?.getContentIfNotHandled()?.let(onEventUnhandledContent)
    })
}