package com.linxiao.framework.support.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.linxiao.framework.activity.TopDialogActivity;

/**
 * 应用内消息通知封装
 * Created by LinXiao on 2016-11-25.
 */
public class AlertDialogWrapper {

    public static AlertDialogBuilder createAlertDialogBuilder() {
        return new AlertDialogBuilder();
    }

    /**
     * build an alert dialog with simple message and positive button
     * */
    public static void showAlertDialog(String message) {
        createAlertDialogBuilder()
        .setMessage(message)
        .build()
        .show();
    }

    /**
     * build alert dialog with simple message,
     * click event of positive button is configurable
     * */
    public static void showAlertDialog(String message, DialogInterface.OnClickListener positiveListener) {
        createAlertDialogBuilder()
        .setMessage(message)
        .setPositiveButton(positiveListener)
        .build()
        .show();
    }

    /**
     * build alert dialog with simple message,
     * click event of positive button and negative button are configurable
     * */
    public static void showAlertDialog(String message, DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener negativeListener) {
        createAlertDialogBuilder()
        .setMessage(message)
        .setPositiveButton(positiveListener)
        .setNegativeButton(negativeListener)
        .build()
        .show();
    }

    /**
     * 通过Activity承载Dialog的方式弹出顶级消息，一般用于异步回调提示
     * <p>如果message为空则不会显示dialog</p>
     */
    public static void showTopAlertDialog(Context context, String title, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        Intent intent = new Intent(context, TopDialogActivity.class);
        intent.putExtra(TopDialogActivity.KEY_DIALOG_TITLE, title);
        intent.putExtra(TopDialogActivity.KEY_DIALOG_MESSAGE, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    /**
     * 通过Activity承载Dialog的方式弹出顶级消息，一般用于异步回调提示
     * <p>如果message为空则不会显示dialog</p>
     */
    public static void showTopAlertDialog(Context context, String message) {
        showTopAlertDialog(context, null, message);
    }

}
