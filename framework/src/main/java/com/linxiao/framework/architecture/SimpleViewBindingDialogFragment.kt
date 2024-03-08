package com.linxiao.framework.architecture

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
abstract class SimpleViewBindingDialogFragment<B : ViewBinding> : BaseDialogFragment() {
    protected lateinit var viewBinding: B
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
        return viewBinding.root
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
