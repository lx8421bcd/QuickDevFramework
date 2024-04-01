package com.linxiao.quickdevframework.sample.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import com.linxiao.framework.architecture.BaseFragment
import com.linxiao.framework.architecture.SimpleViewBindingFragment
import com.linxiao.quickdevframework.databinding.FragmentAdapterTestBinding

class AdapterTestFragment : SimpleViewBindingFragment<FragmentAdapterTestBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnTestEmptyView.setOnClickListener { v: View? ->
            startActivity(Intent(activity, EmptyTestActivity::class.java))
        }
        viewBinding.btnHeaderFooter.setOnClickListener { v: View? ->
            startActivity(Intent(activity, HeaderFooterActivity::class.java))
        }
    }
}
