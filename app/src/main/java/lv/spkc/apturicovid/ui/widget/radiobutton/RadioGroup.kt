package lv.spkc.apturicovid.ui.widget.radiobutton

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class RadioGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {
    private var selectedButton: RadioButton? = null

    init {
        orientation = HORIZONTAL
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        parseChild(child)
    }

    var onSelectedListener: ((RadioButton) -> Unit)? = null

    private fun parseChild(child: View) {
        if (child is RadioButton) {
            if (child.isChecked) {
                selectedButton = child
            }
            child.setOnClickListener {
                selectedButton?.isChecked = false
                child.isChecked = true
                selectedButton = child

                onSelectedListener?.invoke(child)
            }
        }
    }
}