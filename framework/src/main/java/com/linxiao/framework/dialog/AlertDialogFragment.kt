package com.linxiao.framework.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.linxiao.framework.R
import com.linxiao.framework.architecture.BaseDialogFragment
import com.linxiao.framework.common.ContextProvider
import com.linxiao.framework.common.getRealScreenHeight

/**
 * 提示对话框
 *
 * @author lx8421bcd
 * @since 2016-08-07
 */
class AlertDialogFragment : BaseDialogFragment() {

    companion object {
        @JvmStatic
        val FRAMEWORK_DEFAULT_LAYOUT = R.layout.dialog_alert_framework_default
        val defaultTitle = ContextProvider.get().getString(android.R.string.dialog_alert_title)
        var defaultLayoutRes = FRAMEWORK_DEFAULT_LAYOUT
    }

    // build params
    private var layoutRes = defaultLayoutRes
    private var title: CharSequence = defaultTitle
    private var message: CharSequence = ""
    private var messageHtml = ""
    private var contentLink = ""
    private var hint: CharSequence = ""
    private var positiveBtnText: String? = ContextProvider.get().getString(android.R.string.ok)
    private var negativeBtnText: String? = ContextProvider.get().getString(android.R.string.cancel)
    private var positiveListener: DialogInterface.OnClickListener? = DialogInterface.OnClickListener { _, _ -> dismiss() }
    private var negativeListener: DialogInterface.OnClickListener? = DialogInterface.OnClickListener { _, _ -> dismiss() }
    private var cancelable = true
    private var dismissListener: DialogInterface.OnDismissListener? = null

    fun setLayoutRes(@LayoutRes res: Int): AlertDialogFragment {
        layoutRes = res
        return this
    }

    fun setTitle(title: CharSequence): AlertDialogFragment {
        this.title = title
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            val tvTitle = dialog?.findViewById<TextView?>(R.id.tv_title)
            tvTitle?.let {
                it.text = title
                it.isVisible = title.isNotEmpty()
            }
        }
        return this
    }

    fun setMessage(message: CharSequence): AlertDialogFragment {
        this.message = message
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            val tvMessage = dialog?.findViewById<TextView?>(R.id.tv_message)
            tvMessage?.let {
                it.text = message
                it.isVisible = message.isNotEmpty()
            }
        }
        return this
    }

    fun setHint(hintText: CharSequence): AlertDialogFragment {
        this.hint = hintText
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            val tvHint = dialog?.findViewById<TextView?>(R.id.tv_hint)
            tvHint?.let {
                it.text = hint
                it.isVisible = hint.isNotEmpty()
            }
        }
        return this
    }

    fun setContentLink(link: String): AlertDialogFragment {
        this.contentLink = link
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            updateWebViewContent()
        }
        return this
    }

    fun setContentHtml(messageHtml: String): AlertDialogFragment {
        this.messageHtml = messageHtml
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            updateWebViewContent()
        }
        return this
    }

    @JvmOverloads
    fun setPositiveButton(
        text: String? = positiveBtnText,
        listener: DialogInterface.OnClickListener?
    ): AlertDialogFragment {
        positiveListener = listener
        positiveBtnText = text
        if (positiveListener == null) {
            positiveListener = DialogInterface.OnClickListener { _, _ -> dismiss() }
        }
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            val btnPositive = dialog?.findViewById<TextView?>(R.id.btn_positive)
            btnPositive?.let {
                it.text = positiveBtnText
                it.setOnClickListener {
                    positiveListener?.onClick(dialog, 0)
                }
                it.isVisible = !positiveBtnText.isNullOrEmpty()
            }
        }
        return this
    }

    @JvmOverloads
    fun setNegativeButton(
        text: String? = negativeBtnText,
        listener: DialogInterface.OnClickListener?
    ): AlertDialogFragment {
        negativeListener = listener
        negativeBtnText = text
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            val btnNegative = dialog?.findViewById<TextView?>(R.id.btn_negative)
            btnNegative?.let {
                it.text = negativeBtnText
                it.setOnClickListener {
                    negativeListener?.onClick(dialog, 0)
                }
                it.isVisible = !negativeBtnText.isNullOrEmpty() && negativeListener != null
            }
        }
        return this
    }

    fun setOnDismissListener(dismissListener: DialogInterface.OnDismissListener?): AlertDialogFragment {
        this.dismissListener = dismissListener
        return this
    }

    fun setDialogCancelable(cancelable: Boolean): AlertDialogFragment {
        this.cancelable = cancelable
        isCancelable = cancelable
        return this
    }

    fun show(manager: FragmentManager) {
        super.show(manager, "${tag}${hashCode()}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutRes, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(cancelable)
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveBtnText, positiveListener)
        setNegativeButton(negativeBtnText, negativeListener)
        updateWebViewContent()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(dialog)
    }

    private fun updateWebViewContent() {
        val wvContent = dialog?.findViewById<WebView?>(R.id.wv_content) ?: return
        if (contentLink.isNotEmpty()) {
            wvContent.layoutParams.height = getRealScreenHeight() / 2
            wvContent.loadUrl(contentLink)
        }
        else if (messageHtml.isNotEmpty()) {
            val htmlStr = """
                <html>
                    <body>
                        $messageHtml
                    </body>
                </html>
            """.trimIndent()
            wvContent.loadData(htmlStr, "text/html", "UTF-8")
        }
        else {
            return
        }
        wvContent.isVisible = true
        dialog?.findViewById<TextView?>(R.id.tv_message)?.isVisible = false
        dialog?.findViewById<TextView?>(R.id.tv_hint)?.isVisible = false
    }
}