package com.linxiao.quickdevframework.sample.frameworkapi

import android.os.Bundle
import android.view.View
import com.linxiao.framework.architecture.SimpleViewBindingDialogFragment
import com.linxiao.quickdevframework.databinding.DialogBottomSampleBinding

/**
 *
 * @author lx8421bcd
 * @since 2016-12-12
 */
class SampleBottomDialogFragment : SimpleViewBindingDialogFragment<DialogBottomSampleBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.rootView.setOnClickListener { v: View? -> dismiss() }
    }
}
