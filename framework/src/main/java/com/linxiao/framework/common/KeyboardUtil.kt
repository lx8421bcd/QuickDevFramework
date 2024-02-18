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

fun View.hideKeyboard() {
    this.clearFocus()
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.showKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.isKeyboardActiveFor(): Boolean {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return imm.isActive(this)
}