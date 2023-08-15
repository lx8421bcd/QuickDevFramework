package com.linxiao.framework.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException
import java.util.Objects

/**
 *
 *
 * RecyclerViewHolder built with ViewBinding
 * the constructor method is private and use [.create] method
 * to create ViewHolder.
 * use holder.getViewBinding.someView to get reference of View
 *
 *
 * @author linxiao
 * @since 2020-10-20
 */
class ViewBindingRecyclerHolder<B : ViewBinding> constructor(
    bindingClass: Class<B>,
    parent: ViewGroup
) : RecyclerView.ViewHolder(inflateItemView(bindingClass, parent)) {

    val viewBinding by lazy {
        initViewBinding(bindingClass)
    }

    @Suppress("UNCHECKED_CAST")
    private fun initViewBinding(bindingClass: Class<B>): B {
        return bindingClass.getMethod("bind", View::class.java).invoke(null, itemView) as B
    }

    companion object {
        fun <B : ViewBinding> create(
            bindingClass: Class<B>,
            parent: ViewGroup
        ): ViewBindingRecyclerHolder<B> {
            return ViewBindingRecyclerHolder(bindingClass, parent)
        }

        @Suppress("UNCHECKED_CAST")
        private fun <B : ViewBinding?> inflateItemView(
            bindingClass: Class<B>,
            parent: ViewGroup
        ): View {
            var binding: B? = null
            try {
                binding = bindingClass.getMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.javaPrimitiveType
                ).invoke(null, LayoutInflater.from(parent.context), parent, false) as B
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }
            return binding!!.root
        }
    }
}