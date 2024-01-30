package com.linxiao.framework.common;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtil {

    public static void writeTo(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) ContextProvider.get().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("", text);
        clipboardManager.setPrimaryClip(data);
    }
}
