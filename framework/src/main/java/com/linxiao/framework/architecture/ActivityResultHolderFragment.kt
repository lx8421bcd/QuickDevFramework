package com.linxiao.framework.architecture

import android.content.Intent
import androidx.activity.result.ActivityResultCallback
import androidx.core.app.ActivityOptionsCompat
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * ActivityResultListener的二次封装
 *
 * @author linxiao
 * @since 2021-06-08
 */
class ActivityResultHolderFragment : Fragment() {

    companion object {

        @JvmStatic
        fun FragmentActivity.startActivityForCallback(
            intent: Intent?,
            callback: ActivityResultCallback<ActivityResult>?,
            options: ActivityOptionsCompat?
        ) {
            val holderFragment = ActivityResultHolderFragment()
            holderFragment.attach(intent, callback, options)
            this.supportFragmentManager.beginTransaction()
            .add(holderFragment, holderFragment.javaClass.simpleName + holderFragment.hashCode())
            .commitAllowingStateLoss()
        }

        @JvmStatic
        fun Fragment.startActivityForCallback(
            intent: Intent?,
            callback: ActivityResultCallback<ActivityResult>?,
            options: ActivityOptionsCompat?
        ) {
            val holderFragment = ActivityResultHolderFragment()
            holderFragment.attach(intent, callback, options)
            if (this.activity != null) {
                this.requireActivity().supportFragmentManager.beginTransaction()
                .add(holderFragment, holderFragment.javaClass.simpleName + holderFragment.hashCode())
                .commitAllowingStateLoss()
            }
        }
    }
    private val TAG = this.javaClass.simpleName
    private var startActivityIntent: Intent? = null
    private var callback: ActivityResultCallback<ActivityResult>? = null
    private var options: ActivityOptionsCompat? = null

    fun attach(
        intent: Intent?,
        callback: ActivityResultCallback<ActivityResult>?,
        options: ActivityOptionsCompat?
    ) {
        startActivityIntent = intent
        this.callback = callback
        this.options = options
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (startActivityIntent != null) {
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (callback != null) {
                    callback!!.onActivityResult(result)
                }
                removeSelf()
            }
            .launch(startActivityIntent, options)
        } else {
            Log.i(TAG,"onCreate: null start activity intent")
            removeSelf()
        }
    }

    private fun removeSelf() {
        parentFragmentManager.beginTransaction()
        .remove(this)
        .commitAllowingStateLoss()
    }
}