package com.linxiao.framework.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import com.linxiao.framework.architecture.BaseDialogFragment
import com.linxiao.framework.databinding.DialogLoadingBinding

/**
 * common loading dialog
 *
 * @author linxiao
 * @since 2022-11-14
 */
class LoadingDialogFragment : BaseDialogFragment() {
    
    private val viewBinding by lazy {
        return@lazy DialogLoadingBinding.inflate(layoutInflater)
    }
    
    private var title = ""
    private var text = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onStart() {
        super.onStart()
        (viewBinding.root.parent as ViewGroup).setBackgroundResource(android.R.color.transparent)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun initViews() {
        setTitle(title)
        setText(text)
    }
    
    fun setTitle(title: String): LoadingDialogFragment {
        this.title = title
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            viewBinding.tvTitle.text = text
            viewBinding.tvTitle.isVisible = !TextUtils.isEmpty(text)
        }
        return this
    }

    fun setText(text: String): LoadingDialogFragment {
        this.text = text
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            viewBinding.tvText.text = text
            viewBinding.tvText.isVisible = !TextUtils.isEmpty(text)
        }
        return this
    }
}