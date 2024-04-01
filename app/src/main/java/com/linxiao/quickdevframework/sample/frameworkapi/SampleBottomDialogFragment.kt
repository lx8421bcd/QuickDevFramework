package com.linxiao.quickdevframework.sample.frameworkapi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.linxiao.framework.architecture.BaseBottomDialogFragment
import com.linxiao.quickdevframework.R

/**
 *
 * @author lx8421bcd
 * @since 2016-12-12
 */
class SampleBottomDialogFragment : BaseBottomDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_bottom_sample, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.findViewById<View>(R.id.root_view)?.setOnClickListener { v: View? -> dismiss() }
    }
}
