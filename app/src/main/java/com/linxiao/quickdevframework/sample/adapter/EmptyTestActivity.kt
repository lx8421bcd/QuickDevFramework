package com.linxiao.quickdevframework.sample.adapter

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.linxiao.framework.architecture.SimpleViewBindingActivity
import com.linxiao.framework.common.DensityHelper.dp2px
import com.linxiao.framework.list.EquidistantDecoration
import com.linxiao.quickdevframework.databinding.ActivityEmptyTestBinding
import com.linxiao.quickdevframework.databinding.LayoutEmptyViewBinding

class EmptyTestActivity : SimpleViewBindingActivity<ActivityEmptyTestBinding>() {
    
    private val mAdapter: SampleAdapter by lazy {
        SampleAdapter()
    }
    private var showEmpty = true
    private var showData = false
    private var showError = false
    private val emptyViewBinding: LayoutEmptyViewBinding by lazy {
        LayoutEmptyViewBinding.inflate(
            layoutInflater, viewBinding.rcvEmptySimple, false
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding.rcvEmptySimple.setAdapter(mAdapter)
        viewBinding.rcvEmptySimple.setItemAnimator(DefaultItemAnimator())
        // TODO bug QuickGridLayoutManager not work
//        viewBinding.rcvEmptySimple.setLayoutManager(new QuickGridLayoutManager(this, 3));
        val layoutManager = GridLayoutManager(this, 3)
        // fix method
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0 && mAdapter.items.isEmpty()) {
                    3
                } else 1
            }
        }
        viewBinding.rcvEmptySimple.setLayoutManager(layoutManager)
        viewBinding.rcvEmptySimple.addItemDecoration(EquidistantDecoration(3, dp2px(12f)))
        // 这种初始化empty view必须先设置LayoutManager
        mAdapter.isStateViewEnable = true
        mAdapter.stateView = emptyViewBinding.getRoot()
        viewBinding.btnRefresh.setOnClickListener { v: View? ->
            mAdapter.submitList(ArrayList())
            refreshData()
        }
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

    private fun refreshData() {
        emptyViewBinding.tvText.text = "loading"
        Handler().postDelayed({
            if (showEmpty) {
                showEmpty = false
                showError = true
                emptyViewBinding.tvText.text = "empty"
            } else if (showError) {
                showError = false
                showData = true
                emptyViewBinding.tvText.text = "error"
            } else if (showData) {
                showData = false
                showEmpty = true
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
        }, 1000)
    }
}
