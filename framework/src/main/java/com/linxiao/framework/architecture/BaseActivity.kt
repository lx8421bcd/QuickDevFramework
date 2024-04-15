package com.linxiao.framework.architecture

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.linxiao.framework.architecture.ActivityResultHolderFragment.Companion.startActivityForCallback
import com.linxiao.framework.common.DensityHelper
import com.linxiao.framework.common.hideKeyboard
import com.linxiao.framework.dialog.LoadingDialogFragment
import com.linxiao.framework.language.AppLanguageHelper
import com.linxiao.framework.language.LanguageChangedEvent
import com.linxiao.framework.permission.PermissionRequestHelper
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.RxLifecycleAndroid
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * base activity class of entire project
 *
 *
 * template for activities in the project, used to define common methods of activity,
 * extends from Android base appcompat component class, manually implemented the implementation
 * of RxLifeCycle, if you have to extends framework base class from some third sdk, just
 * change parent class is ok.
 *
 *
 * @author linxiao
 * @since 2016-12-05
 */
abstract class BaseActivity : AppCompatActivity(), LifecycleProvider<ActivityEvent> {

    @JvmField
    protected val TAG = this.javaClass.simpleName

    private var printLifecycle = false
    private val lifecycleSubject = BehaviorSubject.create<ActivityEvent>()
    private val finishSubject = BehaviorSubject.create<Any>()
    private val finishSignal = Any()
    private var hideKeyboardOnTouchOutside = false

    protected val loadingDialog by lazy {
        LoadingDialogFragment()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: LanguageChangedEvent?) {
        recreate()
    }

    @CheckResult
    override fun lifecycle(): Observable<ActivityEvent> {
        return lifecycleSubject.hide()
    }

    @CheckResult
    override fun <T> bindUntilEvent(event: ActivityEvent): LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    @CheckResult
    fun <T> bindUntilFinish(): LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(finishSubject, finishSignal)
    }

    @CheckResult
    override fun <T> bindToLifecycle(): LifecycleTransformer<T> {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject)
    }

    fun printLifecycle(print: Boolean) {
        printLifecycle = print
    }

    fun setHideKeyboardOnTouchOutside(hideKeyboardOnTouchOutside: Boolean) {
        this.hideKeyboardOnTouchOutside = hideKeyboardOnTouchOutside
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleSubject.onNext(ActivityEvent.CREATE)
        if (printLifecycle) {
            Log.d(TAG, "onCreate")
        }
        EventBus.getDefault().register(this)
    }

    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(ActivityEvent.START)
        if (printLifecycle) {
            Log.d(TAG, "onStart")
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(ActivityEvent.RESUME)
        if (printLifecycle) {
            Log.d(TAG, "onResume")
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleSubject.onNext(ActivityEvent.PAUSE)
        if (printLifecycle) {
            Log.d(TAG, "onPause")
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleSubject.onNext(ActivityEvent.STOP)
        if (printLifecycle) {
            Log.d(TAG, "onStop")
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (printLifecycle) {
            Log.d(TAG, "onRestart")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleSubject.onNext(ActivityEvent.DESTROY)
        if (printLifecycle) {
            Log.d(TAG, "onDestroy")
        }
        EventBus.getDefault().unregister(this)
    }

    override fun finish() {
        super.finish()
        finishSubject.onNext(finishSignal)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionRequestHelper.current?.handleCallback(this, requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PermissionRequestHelper.current?.onActivityResult(this, requestCode, resultCode, data)
    }

    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (isTranslucent && Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                // return to avoid crash in Android O
                return
            }
        }
        super.setRequestedOrientation(requestedOrientation)
    }

    fun startActivityForCallback(
        intent: Intent?,
        callback: ActivityResultCallback<ActivityResult>?
    ) {
        this.startActivityForCallback(intent, callback, null)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (hideKeyboardOnTouchOutside) {
                val v = currentFocus
                if (shouldHideInput(v, ev)) {
                    window.decorView.hideKeyboard()
                    v!!.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun shouldHideInput(v: View?, ev: MotionEvent): Boolean {
        if (v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val right = left + v.getWidth()
            val bottom = top + v.getHeight()
            return !(ev.x > left && ev.x < right && ev.y > top && ev.y < bottom)
        }
        return false
    }

    /**
     * set activity to immersive mode without using fullscreen
     *
     *
     * in this mode, the window will extend to the status bar area,
     * but the bottom will not extend to the bottom navigation bar area.
     *
     * @param enabled enable immersive mode
     */
    protected fun setImmersiveMode(enabled: Boolean) {
        val mask = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val flags = if (enabled) mask else 0
        val window = window
        val originStatus = window.decorView.systemUiVisibility
        val deStatus = originStatus and mask.inv() or (flags and mask)
        window.decorView.systemUiVisibility = deStatus
    }

    override fun getResources(): Resources {
        val resources = super.getResources()
        // update density
        DensityHelper.onActivityGetResources(resources)
        // update language config
        AppLanguageHelper.doOnContextGetResources(resources)
        return resources
    }

    protected val isTranslucent: Boolean
        get() {
            val attributes = window.attributes
            val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            val flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            if (attributes.flags and flagTranslucentStatus == flagTranslucentStatus ||
                attributes.flags and flagTranslucentNavigation == flagTranslucentNavigation
            ) {
                return true
            }
            val background = window.decorView.background
            return background == null || background.opacity != PixelFormat.OPAQUE
        }

    /**
     * 是否允许截屏
     * <p>关闭之后调用系统截屏为黑屏</p>
     *
     * @param enabled 是否允许
     */
    fun setAllowScreenshots(enabled: Boolean) {
        if (enabled) {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        else {
            window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun addFragmentPageTo(@IdRes viewId: Int, fragment: Fragment, addToBackstack: Boolean) {
        val tag = fragment.javaClass.getSimpleName() + fragment.hashCode()
        val transaction = supportFragmentManager.beginTransaction()
        if (fragment.isAdded) {
            transaction.remove(fragment)
        }
        transaction.add(viewId, fragment, tag)
        if (addToBackstack) {
            transaction.addToBackStack(tag)
        }
        transaction.commitAllowingStateLoss()
    }

    fun popAllFragmentPages() {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }

}
