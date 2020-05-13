package lv.spkc.apturicovid.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.ViewSpecifyContactBinding
import lv.spkc.apturicovid.extension.gone
import lv.spkc.apturicovid.extension.visible

class SpecifyContactView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = DataBindingUtil.inflate<ViewSpecifyContactBinding>(LayoutInflater.from(context), R.layout.view_specify_contact, this, true)

    var changeNumberClickListener: (() -> Unit)? = null

    var contactNr: String = ""
        set(value) {
            field = value
            with(binding) {
                if (value.isEmpty()) {
                    missingContactContainerCl.visible()
                    contactContainerCl.gone()
                } else {
                    contactNumberTv.text = value
                    missingContactContainerCl.gone()
                    contactContainerCl.visible()
                }
            }
        }

    init {
        with(binding) {
            contactSpecifyBtnTv.setOnClickListener {
                changeNumberClickListener?.invoke()
            }

            contactChangeBtnTv.setOnClickListener {
                changeNumberClickListener?.invoke()
            }
        }
    }
}