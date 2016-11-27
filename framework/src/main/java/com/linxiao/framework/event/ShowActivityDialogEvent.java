package com.linxiao.framework.event;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

/**
 * 用于发送广播通知处于Foreground状态的Activity显示AlertDialog
 * Created by LinXiao on 2016-11-25.
 */
public class ShowActivityDialogEvent {

    private String title = "";
    private String message = "";
    private Drawable icon = null;
    private String positiveText = "";
    private String negativeText = "";
    private DialogInterface.OnClickListener positiveListener = null;
    private DialogInterface.OnClickListener negativeListener = null;

    public ShowActivityDialogEvent(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setPositiveText(String positiveText) {
        this.positiveText = positiveText;
    }

    public void setNegativeText(String negativeText) {
        this.negativeText = negativeText;
    }

    public void setPositiveListener(DialogInterface.OnClickListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public void setNegativeListener(DialogInterface.OnClickListener negativeListener) {
        this.negativeListener = negativeListener;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPositiveText() {
        return positiveText;
    }

    public String getNegativeText() {
        return negativeText;
    }

    public DialogInterface.OnClickListener getPositiveListener() {
        return positiveListener;
    }

    public DialogInterface.OnClickListener getNegativeListener() {
        return negativeListener;
    }

    @Override
    public String toString() {
        return "ShowActivityDialogEvent{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
