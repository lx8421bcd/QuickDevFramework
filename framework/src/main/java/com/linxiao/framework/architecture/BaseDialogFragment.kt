package com.linxiao.framework.architecture

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.linxiao.framework.common.getRealScreenWidth
import com.linxiao.framework.dialog.LoadingDialogFragment
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.android.RxLifecycleAndroid
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible

/**
 * base DialogFragment class of entire project
 *
 *
 * template for DialogFragments in the project, contains common methods.
 * extends from Android base appcompat component class, manually implemented the implementation
 * of RxLifeCycle, if you have to extends framework base class from some third sdk, just
 * change parent class is ok.
 *
 *
 * @author linxiao
 * @since 2016-12-05
 */
abstract class BaseDialogFragment : AppCompatDialogFragment(), LifecycleProvider<FragmentEvent> {

    private inner class BaseBottomSheetDismissCallback() : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                this@BaseDialogFragment.dismissAfterAnimation()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    companion object {
        /**
         * 应用内所有继承自BaseDialogFragment的Dialog组件的默认宽度
         */
        @JvmStatic
        val baseDialogWidth: Int = (getRealScreenWidth() * 0.8).toInt()
    }

    @JvmField
    protected val TAG = this::class.java.simpleName

    private var waitingForDismissAllowingStateLoss = false
    private val lifecycleSubject = BehaviorSubject.create<FragmentEvent>()

    protected var bottomSheetStyle = false
    protected val loadingDialog by lazy {
        LoadingDialogFragment()
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

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    @CallSuper
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        lifecycleSubject.onNext(FragmentEvent.ATTACH)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleSubject.onNext(FragmentEvent.CREATE)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = if (bottomSheetStyle) {
            BottomSheetDialog(requireContext(), theme)
        } else {
            super.onCreateDialog(savedInstanceState)
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW)
        // 默认这个宽度，如需改变，自己在子类中设置
        dialog?.window?.setLayout(
//            WindowManager.LayoutParams.MATCH_PARENT,
            baseDialogWidth,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.decorView?.background?.alpha = 0
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 去除半透明蒙板背景
//        dialog?.window?.attributes?.dimAmount = 0.0f
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(FragmentEvent.START)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(FragmentEvent.RESUME)
    }

    @CallSuper
    override fun onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE)
        super.onPause()
    }

    @CallSuper
    override fun onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP)
        super.onStop()
    }

    @CallSuper
    override fun onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW)
        super.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY)
        super.onDestroy()
    }

    @CallSuper
    override fun onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH)
        super.onDetach()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (manager.findFragmentByTag(tag) != null) {
            // which means already added, do not add again to avoid error
            return
        }
        super.show(manager, tag)
    }

    fun show(manager: FragmentManager) {
        this.show(manager, "${this.javaClass.simpleName}#${this.hashCode()}")
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        if (manager.findFragmentByTag(tag) != null) {
            // which means already added, do not add again to avoid error
            return
        }
        super.showNow(manager, tag)
    }

    fun showNow(manager: FragmentManager) {
        this.showNow(manager, "${this.javaClass.simpleName}#${this.hashCode()}")
    }

    override fun dismiss() {
        if (bottomSheetStyle) {
            if (!tryDismissWithAnimation(false)) {
                super.dismiss()
            }
        }
        super.dismiss()
    }

    override fun dismissAllowingStateLoss() {
        if (bottomSheetStyle) {
            if (!tryDismissWithAnimation(true)) {
                super.dismissAllowingStateLoss()
            }
        }
        super.dismissAllowingStateLoss()
    }

    /* functions copy from BottomSheetDialogFragment */

    private fun tryDismissWithAnimation(allowingStateLoss: Boolean): Boolean {
        val baseDialog = this.dialog
        if (baseDialog is BottomSheetDialog) {
            val behavior: BottomSheetBehavior<*> = baseDialog.behavior
            if (behavior.isHideable && baseDialog.dismissWithAnimation) {
                this.dismissWithAnimation(behavior, allowingStateLoss)
                return true
            }
        }
        return false
    }

    private fun dismissWithAnimation(behavior: BottomSheetBehavior<*>, allowingStateLoss: Boolean) {
        this.waitingForDismissAllowingStateLoss = allowingStateLoss
        if (behavior.state == 5) {
            this.dismissAfterAnimation()
        } else {
            if (this.dialog is BottomSheetDialog) {
                val privateMethod = BottomSheetDialog::class.declaredMemberFunctions.first {
                    it.name == "removeDefaultCallback"
                }
                privateMethod.isAccessible = true
                privateMethod.call(this.dialog)
            }
            behavior.addBottomSheetCallback(BaseBottomSheetDismissCallback())
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    private fun dismissAfterAnimation() {
        if (this.waitingForDismissAllowingStateLoss) {
            super.dismissAllowingStateLoss()
        } else {
            super.dismiss()
        }
    }


}
