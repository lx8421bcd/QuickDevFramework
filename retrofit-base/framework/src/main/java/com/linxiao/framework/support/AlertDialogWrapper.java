package com.linxiao.framework.support;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.linxiao.framework.activity.TopDialogActivity;
import com.linxiao.framework.event.ShowActivityDialogEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 应用内消息通知封装
 * Created by LinXiao on 2016-11-25.
 */
public class AlertDialogWrapper {

    public static AlertDialogBuilder buildAlertDialog() {
        return new AlertDialogBuilder();
    }

    public static void showDialog(String message) {
        buildAlertDialog()
                .setMessage(message)
                .show();
    }

    public static void showDialog(String message, DialogInterface.OnClickListener positiveListener) {
        buildAlertDialog()
                .setMessage(message)
                .setPositiveButton(positiveListener)
                .show();
    }

    public static void showDialog(String message, DialogInterface.OnClickListener positiveListener,
                                  DialogInterface.OnClickListener negativeListener) {
        buildAlertDialog()
                .setMessage(message)
                .setPositiveButton(positiveListener)
                .setNegativeButton(negativeListener)
                .show();
    }

    /**
     * 通过Activity承载Dialog的方式弹出顶级消息，一般用于异步回调提示
     * <p>如果message为空则不会显示dialog</p>
     */
    public static void showTopActivityDialog(Context context, String title, String message) {
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
    public static void showTopActivityDialog(Context context, String message) {
        showTopActivityDialog(context, null, message);
    }

    public static class AlertDialogBuilder {
        private String title;
        private String message;
        private Drawable icon;
        private String positiveText;
        private String negativeText;
        private DialogInterface.OnClickListener positiveListener;
        private DialogInterface.OnClickListener negativeListener;

        public AlertDialogBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public AlertDialogBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public AlertDialogBuilder setIcon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        public AlertDialogBuilder setPositiveText(String positiveText) {
            this.positiveText = positiveText;
            return this;
        }

        public AlertDialogBuilder setNegativeText(String negativeText) {
            this.negativeText = negativeText;
            return this;
        }

        public AlertDialogBuilder setPositiveButton(DialogInterface.OnClickListener positiveListener) {
            this.positiveListener = positiveListener;
            return this;
        }

        public AlertDialogBuilder setNegativeButton(DialogInterface.OnClickListener negativeListener) {
            this.negativeListener = negativeListener;
            return this;
        }

        public void show() {
            ShowActivityDialogEvent event = new ShowActivityDialogEvent(message);
            event.setTitle(title);
            event.setIcon(icon);
            event.setPositiveText(positiveText);
            event.setNegativeText(negativeText);
            event.setPositiveListener(positiveListener);
            event.setNegativeListener(negativeListener);
            EventBus.getDefault().post(event);
        }
    }

}
