package com.linxiao.quickdevframework.main

import com.linxiao.framework.list.ViewBindingRecyclerHolder
import com.linxiao.framework.list.ViewBindingSingleItemAdapter
import com.linxiao.quickdevframework.databinding.ItemApiSampleBinding

/**
 *
 * Created by linxiao on 2016/11/30.
 */
class ApiSampleListAdapter :
    ViewBindingSingleItemAdapter<ApiSampleObject, ItemApiSampleBinding>() {
    protected override fun onBindViewHolder(
        holder: ViewBindingRecyclerHolder<ItemApiSampleBinding>,
        position: Int,
        item: ApiSampleObject?
    ) {
        super.onBindViewHolder(holder, position, item)
        holder.viewBinding.tvApiSample.text = item!!.apiName
    }
}
