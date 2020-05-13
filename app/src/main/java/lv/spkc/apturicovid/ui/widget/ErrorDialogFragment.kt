package lv.spkc.apturicovid.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import dagger.android.support.DaggerDialogFragment
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.DialogFragmentErrorBinding

open class ErrorDialogFragment : DaggerDialogFragment() {
    companion object {
        private const val DESCRIPTION_ID = "description"
        private const val CANCEL_ID = "cancel"
        fun newInstance(descriptionId: Int, cancelButtonId: Int) = ErrorDialogFragment().apply {
            arguments = bundleOf(DESCRIPTION_ID to descriptionId, CANCEL_ID to cancelButtonId)
        }
    }
    private lateinit var binding: DialogFragmentErrorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<DialogFragmentErrorBinding>(inflater, R.layout.dialog_fragment_error, container, false)
                .apply { binding = this }
                .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.apply {
            isCancelable = false
            window?.setBackgroundDrawableResource(R.color.backgroundTransparent)
        }

        with(binding) {
            descriptionTv.text = arguments?.getInt(DESCRIPTION_ID)?.let(::getText)
            cancelButton.text = arguments?.getInt(CANCEL_ID)?.let(::getText)
            cancelButton.setOnClickListener { dismiss() }

            executePendingBindings()
        }
    }
}