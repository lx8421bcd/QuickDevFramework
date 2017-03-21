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
    private static Handler mHandler = new Handler();

    private ToastWrapper() {}

    public static void showToast(Context context, CharSequence message, int timeMills) {
        int delay = timeMills;
        if (timeMills == Toast.LENGTH_SHORT) {
            delay = 2000;
        }
        else if (timeMills == Toast.LENGTH_LONG) {
            delay = 3000;
        }
        //Toast默认只有短长两种时间，暂时还未有自定义时间的处理
        else if (timeMills >= 3000) {
            delay = 3000;
        }
        else {
            delay = 2000;
        }

        if (mToast == null) {
            mToast = Toast.makeText(context, message, timeMills);
        }
        else {
            mToast.setText(message);
            mToast.setDuration(timeMills);
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mToast = null;
            }
        }, delay);

        mToast.show();
    }

    public static void showToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

}
