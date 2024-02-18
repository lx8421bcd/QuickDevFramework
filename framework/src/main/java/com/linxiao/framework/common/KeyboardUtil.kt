package com.linxiao.framework.common

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 软键盘管理工具类
 *
 * @author 0x8421bcd
 * @since 2022-08-31
 */
object KeyboardUtil {

    @JvmStatic
    fun hideKeyboard(targetView: View?) {
        targetView ?: return
        targetView.clearFocus()
        val imm = targetView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(targetView.windowToken, 0)
    }

    @JvmStatic
    fun showKeyboard(targetView: View?) {
        targetView ?: return
        val imm = targetView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        targetView.requestFocus()
        imm.showSoftInput(targetView, InputMethodManager.SHOW_IMPLICIT)
    }

    @JvmStatic
    fun isSoftKeyBoardActiveFor(targetView: View?): Boolean {
        targetView ?: return false
        val imm = targetView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.isActive(targetView)
    }
}
