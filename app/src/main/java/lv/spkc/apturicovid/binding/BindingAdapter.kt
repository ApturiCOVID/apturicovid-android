package lv.spkc.apturicovid.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.ui.widget.radiobutton.RadioButton
import org.joda.time.DateTime

@BindingAdapter("radioBtnText")
fun setRadioButtonText(radioButton: RadioButton, title: String) {
    radioButton.text = title
}

@BindingAdapter("refreshDateText")
fun setRefreshDateText(textView: TextView, dateTime: DateTime?) {
    dateTime ?: return
    textView.text = textView.context.getString(R.string.label_data_refreshed, dateTime.toString("dd.MM.yyyy"))
}

@BindingAdapter("floatAsPercent")
fun setFloatAsPercent(textView: TextView, percent: Float?) {
    percent ?: return
    textView.text = textView.context.getString(R.string.news_statistics_percent, percent)
}