package com.linxiao.framework.list

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import java.lang.reflect.ParameterizedType
import java.util.Objects

/**
 * SingleItemAdapter with [ViewBindingRecyclerHolder]
 * use this type to avoid to write the ViewHolder creation codes.
 *
 * @author linxiao
 * @since 2020-10-20
 */
abstract class ViewBindingSingleItemAdapter<T, B : ViewBinding>: BaseQuickAdapter<T, ViewBindingRecyclerHolder<B>>() {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): ViewBindingRecyclerHolder<B> {
        // 返回一个 ViewHolder
        return ViewBindingRecyclerHolder.create(
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<B>,
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewBindingRecyclerHolder<B>, position: Int, item: T?) {
        // 设置item数据
    }

}