package lv.spkc.apturicovid.extension

import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import lv.spkc.apturicovid.utils.AESUtils
import java.util.*

@Suppress("DEPRECATION")
fun String.fromHtml(): Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
} else {
    Html.fromHtml(this)
}

fun String.makeSectionOfTextBold(textToBold: String): SpannableStringBuilder? {
    val builder = SpannableStringBuilder()
    if (textToBold.isNotEmpty() && textToBold.trim { it <= ' ' } != "") {
        //for counting start/end indexes
        val testText = this.toLowerCase(Locale.getDefault())
        val testTextToBold = textToBold.toLowerCase(Locale.getDefault())
        val startingIndex = testText.indexOf(testTextToBold)
        val endingIndex = startingIndex + testTextToBold.length
        //for counting start/end indexes
        if (startingIndex < 0 || endingIndex < 0) {
            return builder.append(this)
        } else if (startingIndex >= 0 && endingIndex >= 0) {
            builder.append(this)
            builder.setSpan(StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0)
        }
    } else {
        return builder.append(this)
    }
    return builder
}

fun String.isPhoneNumber(): Boolean {
    return "^((2|6)[0-9]{7}|\\+371(2|6)[0-9]{7}|\\+(?!371)[0-9]{4,15})\$".toRegex().matchEntire(this) != null
}

fun String.decrypt(): String {
    return AESUtils.decrypt(this)
}