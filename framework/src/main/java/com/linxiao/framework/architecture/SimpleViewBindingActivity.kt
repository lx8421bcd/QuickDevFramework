package com.linxiao.framework.architecture

import android.os.Bundle
import android.view.LayoutInflater
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
abstract class SimpleViewBindingActivity<B : ViewBinding?> : BaseActivity() {
    protected var viewBinding: B? = null
        private set

    protected fun setViewBinding(bindingClass: Class<B>) {
        try {
            viewBinding = bindingClass.getMethod("inflate", LayoutInflater::class.java)
                .invoke(null, layoutInflater) as B
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setContentView(viewBinding!!.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewBinding((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<B>)
    }
}
