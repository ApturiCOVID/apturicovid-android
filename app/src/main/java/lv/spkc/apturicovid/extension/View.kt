package lv.spkc.apturicovid.extension

import android.view.View
import androidx.annotation.DimenRes
import lv.spkc.apturicovid.utils.DebouncedClickListener

fun View.bindDimension(@DimenRes dimenId: Int) = lazy { resources.getDimension(dimenId).toInt() }

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.toggleVisibility(isVisible: Boolean) {
    if (isVisible) {
        visible()
    } else {
        gone()
    }
}

fun View.setOnDebounceClickListener(debounceTime: Int = 1000, onClickAction: () -> Unit) {
    setOnClickListener(object : DebouncedClickListener(debounceTime) {
        override fun performClick(v: View?) {
            onClickAction.invoke()
        }
    })
}