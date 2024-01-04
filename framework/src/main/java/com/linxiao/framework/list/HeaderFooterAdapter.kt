package com.linxiao.framework.list

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder

/**
 * Default Header and Footer holder adapter for BRVAH
 *
 * @author linxiao
 * @since 2023-09-15
 */
class HeaderFooterAdapter(
    @LayoutRes val layoutRes: Int,
    ) : BaseSingleItemAdapter<Any, QuickViewHolder>() {

    var onInitView: (QuickViewHolder) -> Unit = {}

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        // 返回一个 ViewHolder
        return QuickViewHolder(layoutRes, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, item: Any?) {
        onInitView(holder)
    }
}