package com.linxiao.framework.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.linxiao.framework.R

/**
 * 带清空按钮的EditText
 *
 * 注意此不要使用DrawableRight属性,drawableRight已被清空按钮占用,可以用自己的清空按钮覆盖
 *
 * @author linxiao
 * @version 1.0
 */
@SuppressLint("AppCompatCustomView")
class ClearButtonEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : EditText(context, attrs, defStyleAttr)  {
    private val watcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            showClearButton(s.isNotEmpty() && hasFocus() && isEnabled && isFocusable)
        }

        override fun afterTextChanged(s: Editable) {}
    }
    private var clearButtonDrawable = ContextCompat.getDrawable(context, R.drawable.ic_btn_clear)
    private var cachedDrawable = clearButtonDrawable

    init {
        val length = this.textSize.toInt()
        clearButtonDrawable?.setBounds(0, 0, length, length)
        addTextChangedListener(watcher)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        showClearButton(focused && getText().isNotEmpty())
    }

    private fun showClearButton(isShow: Boolean) {
        if (isShow) {
            setCompoundDrawables(null, null, clearButtonDrawable, null)
        } else {
            setCompoundDrawables(null, null, null, null)
        }
    }

    fun setClearButtonVisible(visible: Boolean) {
        clearButtonDrawable = if (visible) cachedDrawable else null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(paramMotionEvent: MotionEvent): Boolean {
        if (getCompoundDrawables()[2] != null && paramMotionEvent.action == 1) {
            val rBounds = clearButtonDrawable!!.getBounds()
            val i = paramMotionEvent.rawX.toInt() // 距离屏幕的距离
            // int i = (int) paramMotionEvent.getX();//距离边框的距离
            if (i > right - 3 * rBounds.width()) {
                setText("")
                paramMotionEvent.action = MotionEvent.ACTION_CANCEL
            }
        }
        return super.onTouchEvent(paramMotionEvent)
    }
}
