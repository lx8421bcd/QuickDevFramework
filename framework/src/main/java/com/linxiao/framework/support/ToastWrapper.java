package com.linxiao.framework.support;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Toast封装，解决快速点击重复Toast问题
 * Created by LinXiao on 2016-11-26.
 */

public class ToastWrapper {

    //用于连续Toast时的处理
    private static Toast mToast;

    public static void showToast(Context context, CharSequence message, int timeMills) {
        if (mToast == null) {
            mToast = Toast.makeText(context, message, timeMills);
            int delay = timeMills;
            if (timeMills == Toast.LENGTH_SHORT) {
                delay = 2000;
            }
            if (timeMills == Toast.LENGTH_LONG) {
                delay = 3000;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mToast = null;
                }
            }, delay);
        }
        else {
            mToast.setText(message);
            mToast.setDuration(timeMills);
        }
        mToast.show();
    }

    public static void showToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

}
