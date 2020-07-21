package lv.spkc.apturicovid.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import lv.spkc.apturicovid.R
import org.joda.time.DateTime

@BindingAdapter("refreshDateText")
fun setRefreshDateText(textView: TextView, dateTime: DateTime?) {
    dateTime ?: return
    textView.text = textView.context.getString(R.string.label_data_refreshed, dateTime.toString("dd.MM.yyyy"))
}