package com.linxiao.framework.architecture

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.linxiao.framework.architecture.ActivityResultHolderFragment.Companion.startActivityForCallback
import com.linxiao.framework.dialog.LoadingDialogFragment
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.android.RxLifecycleAndroid
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * base Fragment class of entire project
 *
 *
 * template for Fragments in the project, contains common methods.
 * extends from Android base appcompat component class, manually implemented the implementation
 * of RxLifeCycle, if you have to extends framework base class from some third sdk, just
 * change parent class is ok.
 *
 *
 * @author linxiao
 * @since 2016-12-05
 */
abstract class BaseFragment : Fragment(), LifecycleProvider<FragmentEvent> {

    @JvmField
    protected val TAG = this.javaClass.simpleName

    private val lifecycleSubject = BehaviorSubject.create<FragmentEvent>()


    private var childBackPressedEnabled = true
    private var childBackPressedCallback = object : OnBackPressedCallback(false) {

        override fun handleOnBackPressed() {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                childFragmentManager.popBackStack()
            }
        }
    }
    private val backStackChangeListener = {
        childBackPressedCallback.isEnabled =
            childBackPressedEnabled && getChildFragmentManager().backStackEntryCount > 0
    }

    protected val loadingDialog by lazy {
        LoadingDialogFragment()
    }

    fun startActivityForCallback(
        intent: Intent?,
        callback: ActivityResultCallback<ActivityResult>?
    ) {
        this.startActivityForCallback(intent, callback, null)
    }

    @CheckResult
    override fun lifecycle(): Observable<FragmentEvent> {
        return lifecycleSubject.hide()
    }

    @CheckResult
    override fun <T> bindUntilEvent(event: FragmentEvent): LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    @CheckResult
    override fun <T> bindToLifecycle(): LifecycleTransformer<T> {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleSubject.onNext(FragmentEvent.ATTACH)
        requireActivity().onBackPressedDispatcher.addCallback(childBackPressedCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleSubject.onNext(FragmentEvent.CREATE)
        childFragmentManager.addOnBackStackChangedListener(backStackChangeListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW)
    }

    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(FragmentEvent.START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(FragmentEvent.RESUME)
    }

    override fun onPause() {
        super.onPause()
        lifecycleSubject.onNext(FragmentEvent.PAUSE)
    }

    override fun onStop() {
        super.onStop()
        lifecycleSubject.onNext(FragmentEvent.STOP)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleSubject.onNext(FragmentEvent.DESTROY)
        childFragmentManager.removeOnBackStackChangedListener(backStackChangeListener)
    }

    override fun onDetach() {
        super.onDetach()
        lifecycleSubject.onNext(FragmentEvent.DETACH)
        childBackPressedCallback.isEnabled = false
        childBackPressedCallback.remove()
    }

    fun addFragmentPageToChildStack(
        fragment: Fragment,
        addToBackstack: Boolean,
        @IdRes containerId: Int = 0,
    ) {
        if (!fragment.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            return
        }
        val containerViewId = if (containerId != 0 && containerId != View.NO_ID) {
            containerId
        }
        else if (view != null) {
            if (requireView().id == View.NO_ID) {
                requireView().id = View.generateViewId()
            }
            requireView().id
        }
        else {
            return
        }
        val tag = "${fragment.javaClass.getSimpleName()}#${fragment.hashCode()}"
        val transaction = getChildFragmentManager().beginTransaction()
        if (fragment.isAdded) {
            transaction.remove(fragment)
        }
        transaction.add(containerViewId, fragment, tag)
        if (addToBackstack) {
            transaction.addToBackStack(tag)
        }
        transaction.commitAllowingStateLoss()
    }

    fun addFragmentPageToParentStack(
        fragment: Fragment,
        addToBackstack: Boolean,
        @IdRes containerId: Int = 0,
    ) {
        if (activity == null || !requireActivity().lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            return
        }
        val containerViewId = if (containerId != 0 && containerId != View.NO_ID) {
            containerId
        } else if (requireActivity().findViewById<View>(android.R.id.content) != null) {
            android.R.id.content
        } else {
            return
        }
        val tag = fragment.javaClass.getSimpleName() + fragment.hashCode()
        val transaction = parentFragmentManager.beginTransaction()
        if (fragment.isAdded) {
            transaction.remove(fragment)
        }
        transaction.add(containerViewId, fragment, tag)
        if (addToBackstack) {
            transaction.addToBackStack(tag)
        }
        transaction.commitAllowingStateLoss()
    }

    fun popAllChildFragmentPages() {
        for (i in 0 until childFragmentManager.backStackEntryCount) {
            getChildFragmentManager().popBackStack()
        }
    }

    fun popSelf() {
        parentFragmentManager.beginTransaction()
        .remove(this)
        .commitAllowingStateLoss()
    }

    /**
     * 设置当前页面内的fragment栈是否允许通过系统back键回退
     * 通常在一个Activity内存在多个fragment tab，其中某一个tab的fragment栈与activity的整个回退栈出现冲突时使用
     * 比如首页某个tab fragment拥有回退栈，但在其他tab时不需要回退栈生效，避免出现点击back无响应的情况
     */
    fun setChildBackPressedCallbackEnabled(enabled: Boolean) {
        childBackPressedEnabled = enabled
        childBackPressedCallback.isEnabled =
            childBackPressedEnabled && childFragmentManager.backStackEntryCount > 0
    }
}
