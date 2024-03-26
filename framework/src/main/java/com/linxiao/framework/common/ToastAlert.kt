package com.linxiao.framework.common

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.linxiao.framework.R
import com.linxiao.framework.common.DensityHelper.dp2px

/**
 * A powerful util of [Toast]. Must init it in your Application.
 * Typically use cases look something
 * like this:
 * <pre>
 * `
 * // show a simple toast immediately that only display a text
 * ToastAlert.show("this is a text only toast");
 *
 * // show a simple toast immediately that with a text and a icon
 * ToastAlert.show("this is toast with a icon", R.drawable.icon);
 *
 * // put a simple toast in a queue that only display text
 * ToastAlert.show("this is a text only toast");
 *
 * // put a simple toast in a queue that with a text and a icon
 * ToastAlert.enqueue("this is toast with a icon", R.drawable.icon);
` *
</pre> *
 *
 *
 * more powerful usage
 * like this:
 * <pre>
 * `
 * ToastAlert.create("this is the toast text")
 * .iconResId(R.drawable.your_icon)
 * .duration(4000)              // we use millis as time unit
 * .gravity(Gravity.TOP, 200)   // set Toast to be shown at top, the top offset is 200
 * .show();                     // show the Toast immediately, or use 'enqueue()' to put it in a queue.
` *
</pre> *
 *
 *
 * more and more powerful usage - use custom layout(please see wiki)
 *
 * @author linxiao, wangxin
 * @since 2016/11/26
 */
object ToastAlert {
    /**
     * @param context
     * @param message
     * @param timeMills
     * @see .create
     * @see .show
     * @see ToastInfo.duration
     */
    fun showToast(context: Context?, message: CharSequence?, timeMills: Int) {
        val toastAlert = create(message)
        toastAlert.duration = timeMills
        toastAlert.show()
    }

    /**
     * @param context
     * @param message
     * @see .show
     * @see .create
     */
    @JvmStatic
    fun showToast(context: Context?, message: String?) {
        showToast(context, message, Toast.LENGTH_SHORT)
    }

    //------------------------------------以下为新功能核心代码区---------------------------------------
    private val DEFAULT_TOAST_INFO_FACTORY: ToastInfoFactory = object : ToastInfoFactory {
        override fun newToastInfo(): ToastInfo {
            return ToastInfo(this)
        }

        override fun onCreateView(toastInfo: ToastInfo): View? {
            val text = toastInfo.text
            if (TextUtils.isEmpty(text) && toastInfo.iconResId == 0) return null
            val textView = TextView(globalContext)
            textView.text = text
            textView.setTextColor(Color.WHITE)
            if (toastInfo.iconResId == 0) {
                val padding = dp2px(15f)
                textView.setPadding(padding, padding, padding, padding)
            } else {
                val padding = dp2px(20f)
                textView.setPadding(padding, padding, padding, padding)
                textView.setCompoundDrawablePadding(dp2px(15f))
                textView.setCompoundDrawablesWithIntrinsicBounds(0, toastInfo.iconResId, 0, 0)
            }
            if (toastInfo.backgroundResId == 0) {
                textView.setBackgroundResource(R.drawable.toast)
            } else {
                textView.setBackgroundResource(toastInfo.backgroundResId)
            }

            // if duration is not set manually, auto adjust duration depending on the text length
            if (toastInfo.duration <= 0) {
                if (text!!.length > 50) {
                    toastInfo.duration = 5000
                } else if (text.length > 25) {
                    toastInfo.duration = 3500
                } else {
                    toastInfo.duration = 2000
                }
            }

            // if icon is set, let the Toast shown at the center of screen
            val gravity = toastInfo.gravity
            if (toastInfo.iconResId != 0) {
                toastInfo.gravity = if (gravity == Gravity.NO_GRAVITY) Gravity.CENTER else gravity
            } else {
                toastInfo.gravity = if (gravity == Gravity.NO_GRAVITY) Gravity.BOTTOM else gravity
            }
            return textView
        }
    }
    private val HANDLER = Handler(Looper.getMainLooper())
    private var toastInfoFactory = DEFAULT_TOAST_INFO_FACTORY
    fun setDefaultFactory(toastInfoFactory: ToastInfoFactory?) {
        ToastAlert.toastInfoFactory = toastInfoFactory ?: DEFAULT_TOAST_INFO_FACTORY
    }

    @JvmStatic
    fun create(cs: CharSequence?): ToastInfo {
        return toastInfoFactory.newToastInfo()
    }

    fun show(stringResId: Int) {
        show(globalContext.getString(stringResId))
    }

    fun show(stringResId: Int, iconResId: Int) {
        show(globalContext.getString(stringResId), iconResId)
    }

    fun enqueue(stringResId: Int) {
        enqueue(globalContext.getString(stringResId))
    }

    fun enqueue(stringResId: Int, iconResId: Int) {
        enqueue(globalContext.getString(stringResId), iconResId)
    }

    @JvmStatic
    fun show(cs: CharSequence?) {
        val toastInfo = toastInfoFactory.newToastInfo()
        toastInfo.text = cs
        toastInfo.show()
    }

    @JvmStatic
    fun show(cs: CharSequence?, iconResId: Int) {
        val toastInfo = toastInfoFactory.newToastInfo()
        toastInfo.text = cs
        toastInfo.iconResId = iconResId
        toastInfo.show()
    }

    fun enqueue(cs: CharSequence?) {
        val toastInfo = toastInfoFactory.newToastInfo()
        toastInfo.text = cs
        toastInfo.enqueue()
    }

    @JvmStatic
    fun enqueue(cs: CharSequence?, iconResId: Int) {
        val toastInfo = toastInfoFactory.newToastInfo()
        toastInfo.text = cs
        toastInfo.iconResId = iconResId
        toastInfo.enqueue()
    }

    interface ToastInfoFactory {
        fun newToastInfo(): ToastInfo
        fun onCreateView(toastInfo: ToastInfo): View?
    }

    interface OnStageListener {
        fun onShow(toastDelegate: ToastDelegate?)
        fun onDismiss(toastDelegate: ToastDelegate?)
    }

    open class ToastInfo(private val toastInfoFactory: ToastInfoFactory) {
        var text: CharSequence? = null
        var backgroundResId = 0
        var iconResId = 0
        var duration = 0
        var gravity = Gravity.NO_GRAVITY
        var offsetY = 200
        var onStageListener: OnStageListener? = null
        private val extra: Map<String, Any> = HashMap()

        fun build(): ToastDelegate? {
            val view = toastInfoFactory.onCreateView(this)
            return if (view == null) null else ToastDelegate(view, this)
        }

        fun show() {
            val toastDelegate = build()
            toastDelegate?.show()
        }

        fun enqueue() {
            val toastDelegate = build()
            toastDelegate?.enqueue()
        }
    }

    class ToastDelegate(view: View?, toastInfo: ToastInfo) {
        private val updateRunnable = Runnable { update() }
        private val enqueueRunnable = Runnable { enqueue() }
        private val duration: Int
        private val toastInfo: ToastInfo
        private var toast: Toast?
        private var startTimeMs: Long = 0

        init {
            val toast = Toast(globalContext)
            toast.view = view
            toast.setGravity(toastInfo.gravity, 0, toastInfo.offsetY)
            toast.setDuration(Toast.LENGTH_LONG)
            this.toast = toast
            duration = if (toastInfo.duration <= 0) 2000 else toastInfo.duration
            this.toastInfo = toastInfo
        }

        val isShowing: Boolean
            get() = toast != null && startTimeMs > 0

        private fun update() {
            if (startTimeMs == 0L) startTimeMs = currentTimestamp()
            if (currentTimestamp() - startTimeMs < duration) {
                toast!!.show()
                HANDLER.postDelayed(updateRunnable, checkInterval.toLong())
            } else {
                cancel()
            }
        }

        fun show() {
            if (lastToastDelegate != null) lastToastDelegate!!.cancel()
            lastToastDelegate = this
            update()
            if (toastInfo.onStageListener != null) {
                toastInfo.onStageListener!!.onShow(this)
            }
        }

        fun enqueue() {
            if (lastToastDelegate == null) {
                show()
                return
            }
            val timeRemaining = lastToastDelegate!!.startTimeMs
            +lastToastDelegate!!.duration - currentTimestamp()
            if (timeRemaining <= 0) {
                lastToastDelegate!!.cancel()
                show()
                return
            }
            HANDLER.postDelayed(enqueueRunnable, timeRemaining)
        }

        fun cancel() {
            if (!isShowing) return
            toast!!.cancel()
            toast = null
            HANDLER.removeCallbacks(updateRunnable)
            HANDLER.removeCallbacks(enqueueRunnable)
            if (toastInfo.onStageListener != null) {
                toastInfo.onStageListener!!.onDismiss(this)
            }
        }

        companion object {
            var checkInterval = 335
            private var lastToastDelegate: ToastDelegate? =
                null // memory leak 28 bytes, forget it!!

            private fun currentTimestamp(): Long {
                return SystemClock.uptimeMillis()
            }
        }
    }
}
