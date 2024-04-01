package com.linxiao.quickdevframework.sample.adapter

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.linxiao.framework.architecture.SimpleViewBindingActivity
import com.linxiao.framework.list.HeaderFooterAdapter
import com.linxiao.quickdevframework.R
import com.linxiao.quickdevframework.databinding.ActivityHeaderFooterBinding

class HeaderFooterActivity : SimpleViewBindingActivity<ActivityHeaderFooterBinding>() {
    
    private val mAdapter: SampleAdapter by lazy {
        SampleAdapter()
    }
    
    private val quickAdapterHelper: QuickAdapterHelper by lazy {
        QuickAdapterHelper.Builder(mAdapter).build()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter.setOnItemClickListener { baseQuickAdapter: BaseQuickAdapter<String, *>, view: View, i: Int ->
            addHeaderView()
            addFooterView()
        }
        viewBinding.rcvHeaderFooter.setLayoutManager(LinearLayoutManager(this))
        viewBinding.rcvHeaderFooter.setItemAnimator(DefaultItemAnimator())
        viewBinding.rcvHeaderFooter.setAdapter(quickAdapterHelper.adapter)
        val headerAdapter = HeaderFooterAdapter(R.layout.header_sample)
        headerAdapter.onInitView = { quickViewHolder: QuickViewHolder ->
            quickViewHolder.itemView.setOnClickListener { v: View? -> addHeaderView() }
            Unit
        }
        quickAdapterHelper.addBeforeAdapter(headerAdapter)
        val footerAdapter = HeaderFooterAdapter(R.layout.footer_sample)
        footerAdapter.onInitView = { quickViewHolder: QuickViewHolder ->
            quickViewHolder.itemView.setOnClickListener { v: View? -> addFooterView() }
            Unit
        }
        quickAdapterHelper.addAfterAdapter(footerAdapter)
        initData()
    }

    private fun initData() {
        mAdapter.submitList(
            mutableListOf(
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1"
            )
        )
    }

    private fun addHeaderView() {
        val headerAdapter = HeaderFooterAdapter(R.layout.header_added)
        headerAdapter.onInitView = { quickViewHolder: QuickViewHolder ->
            quickViewHolder.itemView.setOnClickListener { v: View? ->
                quickAdapterHelper.removeAdapter(
                    headerAdapter
                )
            }
            Unit
        }
        quickAdapterHelper.addBeforeAdapter(0, headerAdapter)
    }

    private fun addFooterView() {
        val footerAdapter = HeaderFooterAdapter(R.layout.footer_added)
        footerAdapter.onInitView = { quickViewHolder: QuickViewHolder ->
            quickViewHolder.itemView.setOnClickListener { v: View? ->
                quickAdapterHelper.removeAdapter(
                    footerAdapter
                )
            }
            Unit
        }
        quickAdapterHelper.addAfterAdapter(footerAdapter)
    }
}
