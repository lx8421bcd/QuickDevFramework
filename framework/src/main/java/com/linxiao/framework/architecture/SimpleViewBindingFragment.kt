package com.linxiao.framework.architecture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 *
 *
 * class usage summary
 *
 *
 * @author linxiao
 * @since 2020-10-20
 */
@Suppress("UNCHECKED_CAST")
abstract class SimpleViewBindingFragment<B : ViewBinding?> : BaseFragment() {
    protected var viewBinding: B? = null
        private set

    protected fun setViewBinding(bindingClass: Class<B>) {
        try {
            viewBinding = bindingClass.getMethod("inflate", LayoutInflater::class.java)
                .invoke(null, layoutInflater) as B
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setViewBinding((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<B>)
        return viewBinding!!.root
    }
}
