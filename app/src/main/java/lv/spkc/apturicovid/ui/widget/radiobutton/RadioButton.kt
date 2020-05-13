package lv.spkc.apturicovid.ui.widget.radiobutton

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import lv.spkc.apturicovid.databinding.ViewRadioButtonBinding

class RadioButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {
    companion object {
        private const val OUTLINE_MAX_SCALE = 1.1f
    }
    private val binding = ViewRadioButtonBinding.inflate(LayoutInflater.from(context))

    var text: String? = null
        set(value) {
            field = value
            with(binding) {
                buttonTitleTv.text = value
            }
        }

    init {
        addView(binding.root)
    }

    var isChecked: Boolean = false
        set(value) {
            field = value
            val outlineScale = if (isChecked) OUTLINE_MAX_SCALE else 1f
            val selectedScale = if (isChecked) 1f else 0f
            binding.buttonTitleTv.isSelected = isChecked

            binding.radioButtonOutlineIv.animate().scaleX(outlineScale).scaleY(outlineScale)
                .duration = 150
            binding.radioButtonSelectedIv.animate().scaleX(selectedScale).scaleY(selectedScale)
                .duration = 150
        }
}