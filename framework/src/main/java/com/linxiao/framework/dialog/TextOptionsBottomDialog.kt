package com.linxiao.framework.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.linxiao.framework.architecture.SimpleViewBindingDialogFragment
import com.linxiao.framework.databinding.PopupTextOptionsBinding

/**
 * simple configurable text option dialog
 *
 * @author linxiao
 * @since 2022-12-22
 */
class TextOptionsBottomDialog : SimpleViewBindingDialogFragment<PopupTextOptionsBinding>() {

    val adapter: TextOptionsAdapter by lazy {
        return@lazy TextOptionsAdapter()
    }

    init {
        bottomSheetStyle = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setOnItemClickListener { itemView, position ->
            dismiss()
        }
        viewBinding.rcvList.layoutManager = LinearLayoutManager(context)
        viewBinding.rcvList.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        (viewBinding.root.parent as ViewGroup).setBackgroundResource(android.R.color.transparent)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

}