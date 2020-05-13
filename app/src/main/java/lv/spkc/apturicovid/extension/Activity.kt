package lv.spkc.apturicovid.extension

import android.app.Activity
import androidx.annotation.DimenRes

fun Activity.bindDimension(@DimenRes dimenId: Int) = lazy { resources.getDimension(dimenId).toInt() }