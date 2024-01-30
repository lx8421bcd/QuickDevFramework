package com.linxiao.framework.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtil {
    fun writeTo(text: String?) {
        val clipboardManager = globalContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(data)
    }
}
