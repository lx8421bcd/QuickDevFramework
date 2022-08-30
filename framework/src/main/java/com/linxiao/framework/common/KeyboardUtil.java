package com.linxiao.framework.common;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘管理工具类
 *
 * @author 0x8421bcd
 * @since 2022-08-31
 */
public class KeyboardUtil {

    public static void hideKeyboard(final View targetView) {
        if (targetView == null) {
            return;
        }

        targetView.clearFocus();
        InputMethodManager imm = (InputMethodManager) targetView.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(targetView.getWindowToken(), 0);
    }

    public static void showKeyboard(final View targetView, final int retryDelayMillis) {
        if (targetView == null) {
            return;
        }

        final InputMethodManager imm = (InputMethodManager) targetView.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        new Runnable() {
            @Override
            public void run() {
                if (!targetView.requestFocus() || !imm.isActive(targetView)
                        || !imm.showSoftInput(targetView, InputMethodManager.SHOW_IMPLICIT)) {
                    targetView.postDelayed(this, retryDelayMillis);
                }
            }
        }.run();
    }

    public static boolean isSoftKeyBoardActiveFor(View targetView) {
        if (targetView == null) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager) targetView.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive(targetView);
    }
}
