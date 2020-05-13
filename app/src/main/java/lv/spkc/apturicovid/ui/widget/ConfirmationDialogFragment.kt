package lv.spkc.apturicovid.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import dagger.android.support.DaggerDialogFragment
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.DialogFragmentConfirmationBinding

open class ConfirmationDialogFragment : DaggerDialogFragment() {
    companion object {
        private const val DESCRIPTION_ID = "description"
        private const val ACCEPT_ID = "accept_id"
        private const val CANCEL_ID = "cancel_id"
        private const val ON_ACCEPT = "on_accept"
        private const val ON_CANCEL = "on_cancel"

        fun newInstance(
            descriptionId: Int,
            cancelButtonTextId: Int,
            acceptButtonTextId: Int,
            onAccept: () -> Unit,
            onCancel: (() -> Unit)? = null
        ) = ConfirmationDialogFragment().apply {
            arguments = bundleOf(
                DESCRIPTION_ID to descriptionId,
                CANCEL_ID to cancelButtonTextId,
                ACCEPT_ID to acceptButtonTextId,
                ON_ACCEPT to onAccept,
                ON_CANCEL to onCancel
            )
        }
    }
    private lateinit var binding: DialogFragmentConfirmationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<DialogFragmentConfirmationBinding>(inflater, R.layout.dialog_fragment_confirmation, container, false)
                .apply { binding = this }
                .root
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.apply {
            isCancelable = false
            window?.setBackgroundDrawableResource(R.color.backgroundTransparent)
        }

        val onAcceptAction = arguments?.getSerializable(ON_ACCEPT) as? () -> Unit
        val onCancelAction = arguments?.getSerializable(ON_CANCEL) as? () -> Unit

        with(binding) {
            descriptionTv.text = arguments?.getInt(DESCRIPTION_ID)?.let(::getText)
            cancelButton.text = arguments?.getInt(CANCEL_ID)?.let(::getText)
            acceptButton.text = arguments?.getInt(ACCEPT_ID)?.let(::getText)
            cancelButton.setOnClickListener {
                onCancelAction?.invoke()
                dismiss()
            }
            acceptButton.setOnClickListener {
                onAcceptAction?.invoke()
                dismiss()
            }

            executePendingBindings()
        }
    }

    override fun onPause() {
        super.onPause()

        dismiss()
    }
}