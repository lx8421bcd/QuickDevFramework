package com.linxiao.framework.support.dialog;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.linxiao.framework.event.ShowActivityDialogEvent;

/**
 *
 * Created by LinXiao on 2016-12-12.
 */
public class AlertDialogBuilder {
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

    public AlertDialogOperator build() {
        ShowActivityDialogEvent event = new ShowActivityDialogEvent(message);
        event.setTitle(title);
        event.setIcon(icon);
        event.setPositiveText(positiveText);
        event.setNegativeText(negativeText);
        event.setPositiveListener(positiveListener);
        event.setNegativeListener(negativeListener);
        return new AlertDialogOperator(event);
    }
}
